/*
 * *** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * J4Care.
 * Portions created by the Initial Developer are Copyright (C) 2013-2021
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * *** END LICENSE BLOCK *****
 */

package org.dcm4chee.arc.patient;

import org.dcm4che3.data.*;
import org.dcm4chee.arc.conf.AttributeFilter;

import java.util.*;

/**
 * @author Gunter Zeilinger (gunterze@protonmail.com)
 * @since May 2023
 */
public class PatientServiceUtils {
    public static boolean updatePatientAttrs(Attributes attrs, Attributes.UpdatePolicy updatePolicy,
                                              Attributes newAttrs, Attributes modified, AttributeFilter filter) {
        int[] selection = without(filter.getSelection(false),
                Tag.PatientID,
                Tag.IssuerOfPatientID,
                Tag.TypeOfPatientID,
                Tag.IssuerOfPatientIDQualifiersSequence,
                Tag.OtherPatientIDsSequence);
        int pidUpdated = 0;
        Sequence otherPIDs = attrs.getSequence(Tag.OtherPatientIDsSequence);
        Set<IDWithIssuer> updatedPids = IDWithIssuer.pidsOf(newAttrs);
        if (mergeIssuer(attrs, updatedPids)) pidUpdated++;
        if (otherPIDs != null)
            for (Attributes item : otherPIDs) {
                if (mergeIssuer(item, updatedPids)) pidUpdated++;
            }
        Set<IDWithIssuer> pids = IDWithIssuer.pidsOf(attrs);
        if (addOtherPID(attrs, newAttrs, pids)) pidUpdated++;
        Sequence newOtherPIDs = newAttrs.getSequence(Tag.OtherPatientIDsSequence);
        if (newOtherPIDs != null)
            for (Attributes item : newOtherPIDs) {
                if (addOtherPID(attrs, item, pids)) pidUpdated++;
            }
        return attrs.updateSelected(updatePolicy, newAttrs, modified, selection) || pidUpdated > 0;
    }

    private static boolean addOtherPID(Attributes attrs, Attributes newAttrs, Set<IDWithIssuer> pids) {
        IDWithIssuer newPID = IDWithIssuer.pidOf(newAttrs);
        if (newPID == null) return false;
        for (IDWithIssuer pid : pids) {
            if (pid.matches(newPID, true, true)) return false;
        }
        attrs.ensureSequence(Tag.OtherPatientIDsSequence, 1).add(
                new Attributes(newAttrs,
                        Tag.PatientID,
                        Tag.IssuerOfPatientID,
                        Tag.TypeOfPatientID,
                        Tag.IssuerOfPatientIDQualifiersSequence ));
        return true;
    }

    private static int[] without(int[] src, int... tags) {
        int[] index = new int[tags.length];
        int d = 0;
        for (int i = 0; i < index.length; i++) {
            if ((index[i] = Arrays.binarySearch(src, tags[i])) >= 0) d++;
        }
        if (d == 0) return src;
        int[] dest = new int[src.length-d];
        int srcPos = 0;
        int destPos = 0;
        for (int i : index) {
            if (i < 0) continue;
            int length = i - srcPos;
            System.arraycopy(src, srcPos, dest, destPos, length);
            srcPos = i+1;
            destPos += length;
        }
        System.arraycopy(src, srcPos, dest, destPos, dest.length - destPos);
        return dest;
    }

    private static boolean mergeIssuer(Attributes attrs, Set<IDWithIssuer> updatedPids) {
        IDWithIssuer pid = IDWithIssuer.pidOf(attrs);
        if (pid != null) {
            for (IDWithIssuer updatedPid : updatedPids) {
                if (pid.matches(updatedPid, true, true)) {
                    if (updatedPid.getIssuer() == null) return false;
                    Issuer issuer = pid.getIssuer();
                    if (issuer == null)
                        issuer = updatedPid.getIssuer();
                    else if (!issuer.merge(updatedPid.getIssuer()))
                        return false;
                    issuer.toIssuerOfPatientID(attrs);
                    return true;
                }
            }
        }
        return false;
    }
}
