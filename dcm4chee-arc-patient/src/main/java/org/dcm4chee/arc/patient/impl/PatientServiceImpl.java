/*
 * ** BEGIN LICENSE BLOCK *****
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
 * Portions created by the Initial Developer are Copyright (C) 2015-2017
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
 * ** END LICENSE BLOCK *****
 */

package org.dcm4chee.arc.patient.impl;

import org.dcm4che3.audit.AuditMessages;
import org.dcm4che3.data.IDWithIssuer;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.hl7.HL7Application;
import org.dcm4che3.net.hl7.UnparsedHL7Message;
import org.dcm4che3.util.AttributesFormat;
import org.dcm4chee.arc.conf.ArchiveDeviceExtension;
import org.dcm4chee.arc.entity.Patient;
import org.dcm4chee.arc.entity.PatientID;
import org.dcm4chee.arc.entity.Study;
import org.dcm4chee.arc.keycloak.HttpServletRequestInfo;
import org.dcm4chee.arc.patient.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Jul 2015
 */
@ApplicationScoped
public class PatientServiceImpl implements PatientService {

    @Inject
    private PatientServiceEJB ejb;

    @Inject
    private Device device;

    @Inject
    private Event<PatientMgtContext> patientMgtEvent;

    @Override
    public PatientMgtContext createPatientMgtContextDIMSE(Association as) {
        PatientMgtContextImpl ctx = new PatientMgtContextImpl(device);
        ctx.setAssociation(as);
        return ctx;
    }

    @Override
    public PatientMgtContext createPatientMgtContextWEB(HttpServletRequestInfo httpRequest) {
        PatientMgtContextImpl ctx = new PatientMgtContextImpl(device);
        ctx.setHttpServletRequestInfo(httpRequest);
        return ctx;
    }

    @Override
    public PatientMgtContext createPatientMgtContextHL7(HL7Application hl7App, Socket socket, UnparsedHL7Message msg) {
        PatientMgtContextImpl ctx = new PatientMgtContextImpl(device);
        ctx.setSocket(socket);
        ctx.setUnparsedHL7Message(msg);
        ctx.setHL7Application(hl7App);
        return ctx;
    }

    @Override
    public PatientMgtContext createPatientMgtContextScheduler() {
        return new PatientMgtContextImpl(device);
    }

    @Override
    public Collection<Patient> findPatients(Collection<IDWithIssuer> pids) {
        return ejb.findPatients(pids);
    }

    @Override
    public Patient findPatient(Collection<IDWithIssuer> pids) {
        return ejb.findPatient(pids);
    }

    @Override
    public Patient createPatient(PatientMgtContext ctx) {
        try {
            return ejb.createPatient(ctx);
        } catch (RuntimeException e) {
            ctx.setException(e);
            throw e;
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public Patient updatePatient(PatientMgtContext ctx)
            throws NonUniquePatientException, PatientMergedException {
        try {
            Patient patient;
            do {
                patient = ejb.updatePatient(ctx);
            } while (deleteDuplicateCreatedPatient(ctx, patient));
            return patient;
        } catch (RuntimeException e) {
            ctx.setException(e);
            throw e;
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public void updatePatientIDs(Patient pat, Collection<IDWithIssuer> patientIDs) {
        ejb.updatePatientIDs(pat, patientIDs);
    }

    private boolean deleteDuplicateCreatedPatient(PatientMgtContext ctx, Patient patient) {
        if (ctx.getEventActionCode() == AuditMessages.EventActionCode.Create) {
            if (deleteDuplicateCreatedPatient(ctx.getPatientIDs(), patient, null)) {
                ctx.setEventActionCode(AuditMessages.EventActionCode.Read);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteDuplicateCreatedPatient(
            Collection<IDWithIssuer> pids, Patient createdPatient, Study createdStudy) {
        return ejb.deleteDuplicateCreatedPatient(pids, createdPatient, createdStudy);
    }

    @Override
    public Patient mergePatient(PatientMgtContext ctx)
            throws NonUniquePatientException, PatientMergedException, CircularPatientMergeException {
        if (ctx.getPatientIDs().stream().anyMatch(pid ->
                ctx.getPreviousPatientIDs().stream().anyMatch(other -> pid.matches(other, true, true))))
            throw new CircularPatientMergeException("PriorPatientID same as target PatientID");

        try {
            return ejb.mergePatient(ctx);
        } catch (RuntimeException e) {
            ctx.setException(e);
            throw e;
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public boolean unmergePatient(PatientMgtContext ctx) throws NonUniquePatientException, PatientUnmergedException {
        try {
            return ejb.unmergePatient(ctx);
        } catch (RuntimeException e) {
            ctx.setException(e);
            throw e;
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public Patient changePatientID(PatientMgtContext ctx)
            throws NonUniquePatientException, PatientMergedException, PatientTrackingNotAllowedException {
        if (device.getDeviceExtensionNotNull(ArchiveDeviceExtension.class).isHL7TrackChangedPatientID()) {
            checkForMatchingPatientIDs(ctx);
            createPatient(ctx);
            return mergePatient(ctx);
        }
        try {
            return ejb.changePatientID(ctx);
        } catch (RuntimeException e) {
            ctx.setException(e);
            throw e;
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    private void checkForMatchingPatientIDs(PatientMgtContext ctx) {
        Collection<IDWithIssuer> prevPatientIDs = ctx.getPreviousPatientIDs();
        Collection<IDWithIssuer> newPatientIDs = ctx.getPatientIDs();
        for (IDWithIssuer prevPatientID : prevPatientIDs) {
            for (IDWithIssuer newPatientID : newPatientIDs) {
                if (newPatientID.matches(prevPatientID, true, true)) {
                        throw newPatientID.equals(prevPatientID)
                                ? new CircularPatientMergeException(
                                        "PriorPatientID same as target PatientID")
                                : new PatientTrackingNotAllowedException(
                                        "Previous Patient ID: \"" + prevPatientID
                                                + "\" matches new Patient ID: \"" + newPatientID
                                                + " and change patient id tracking is enabled. "
                                                + "Disable change patient id tracking feature and retry update");
                }
            }
        }
    }

    @Override
    public Patient findPatient(PatientMgtContext ctx) {
        return ejb.findPatient(ctx);
    }

    @Override
    public void deletePatient(PatientMgtContext ctx) {
        ejb.deletePatient(ctx.getPatient());
        patientMgtEvent.fire(ctx);
    }

    @Override
    public Patient updatePatientStatus(PatientMgtContext ctx) {
        try {
            return ejb.updatePatientStatus(ctx);
        } finally {
            patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public List<String> studyInstanceUIDsOf(Patient patient) {
        return ejb.studyInstanceUIDsOf(patient);
    }

    @Override
    public boolean supplementIssuer(PatientMgtContext ctx, PatientID patientID, IDWithIssuer idWithIssuer,
                                    Map<IDWithIssuer, Long> ambiguous) {
        try {
            return ejb.supplementIssuer(ctx, patientID, idWithIssuer, ambiguous);
        } finally {
            if (ctx.getEventActionCode() != null)
                patientMgtEvent.fire(ctx);
        }
    }

    @Override
    public <T> List<T> queryWithOffsetAndLimit(CriteriaQuery<T> query, int offset, int limit) {
        return ejb.queryWithOffsetAndLimit(query, offset, limit);
    }

    @Override
    public <T> T merge(T entity) {
        return ejb.merge(entity);
    }

    @Override
    public void testSupplementIssuers(CriteriaQuery<PatientID> query, int fetchSize, Set<IDWithIssuer> success,
                                      Map<IDWithIssuer, Long> ambiguous, AttributesFormat issuer) {
        ejb.testSupplementIssuers(query, fetchSize, success, ambiguous, issuer);
    }
}
