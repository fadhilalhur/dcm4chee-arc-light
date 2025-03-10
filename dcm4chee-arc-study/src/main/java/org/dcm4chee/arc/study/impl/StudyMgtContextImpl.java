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
 * Portions created by the Initial Developer are Copyright (C) 2015-2019
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

package org.dcm4chee.arc.study.impl;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.hl7.UnparsedHL7Message;
import org.dcm4che3.soundex.FuzzyStr;
import org.dcm4che3.util.ReverseDNS;
import org.dcm4chee.arc.conf.ArchiveAEExtension;
import org.dcm4chee.arc.conf.ArchiveDeviceExtension;
import org.dcm4chee.arc.conf.AttributeFilter;
import org.dcm4chee.arc.conf.Entity;
import org.dcm4chee.arc.entity.Patient;
import org.dcm4chee.arc.entity.Study;
import org.dcm4chee.arc.keycloak.HttpServletRequestInfo;
import org.dcm4chee.arc.study.StudyMgtContext;

import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Jun 2016
 */
public class StudyMgtContextImpl implements StudyMgtContext {
    private final AttributeFilter studyAttributeFilter;
    private final AttributeFilter seriesAttributeFilter;
    private final FuzzyStr fuzzyStr;
    private HttpServletRequestInfo httpRequest;
    private ArchiveAEExtension arcAE;
    private Socket socket;
    private UnparsedHL7Message msg;
    private Study study;
    private Attributes attributes;
    private List<Attributes> requestAttrs;
    private Patient patient;
    private String studyInstanceUID;
    private String eventActionCode;
    private Exception exception;
    private LocalDate expirationDate;
    private String seriesInstanceUID;
    private String expirationExporterID;
    private boolean freezeExpirationDate;
    private boolean unfreezeExpirationDate;
    private String accessControlID;

    private String reasonForModification;

    private String sourceOfPreviousValues;

    StudyMgtContextImpl(Device device) {
        ArchiveDeviceExtension arcDev = device.getDeviceExtension(ArchiveDeviceExtension.class);
        this.studyAttributeFilter = arcDev.getAttributeFilter(Entity.Study);
        this.seriesAttributeFilter = arcDev.getAttributeFilter(Entity.Series);
        this.fuzzyStr = arcDev.getFuzzyStr();
    }

    StudyMgtContextImpl withApplicationEntity(ApplicationEntity ae) {
        this.arcAE = ae.getAEExtension(ArchiveAEExtension.class);
        return this;
    }

    StudyMgtContextImpl withHttpRequest(HttpServletRequestInfo httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    StudyMgtContextImpl withSocket(Socket socket) {
        this.socket = socket;
        return this;
    }

    StudyMgtContextImpl withUnparsedHL7Message(UnparsedHL7Message msg) {
        this.msg = msg;
        return this;
    }

    public AttributeFilter getStudyAttributeFilter() {
        return studyAttributeFilter;
    }

    @Override
    public FuzzyStr getFuzzyStr() {
        return fuzzyStr;
    }

    @Override
    public HttpServletRequestInfo getHttpRequest() {
        return httpRequest;
    }

    @Override
    public UnparsedHL7Message getUnparsedHL7Message() {
        return msg;
    }

    @Override
    public String getRemoteHostName() {
        return httpRequest != null
                ? httpRequest.requesterHost
                : socket != null
                ? ReverseDNS.hostNameOf(socket.getInetAddress()) : null;
    }

    @Override
    public String getLocalHostName() {
        return httpRequest != null
                ? httpRequest.localHost
                : socket != null
                ? ReverseDNS.hostNameOf(socket.getLocalAddress()) : null;
    }

    @Override
    public ApplicationEntity getApplicationEntity() {
        return arcAE.getApplicationEntity();
    }

    @Override
    public ArchiveAEExtension getArchiveAEExtension() {
        return arcAE;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Attributes attrs) {
        this.attributes = attrs;
        this.studyInstanceUID = attrs.getString(Tag.StudyInstanceUID);
        this.seriesInstanceUID = attrs.getString(Tag.SeriesInstanceUID);
    }

    @Override
    public Patient getPatient() {
        return patient;
    }

    @Override
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Override
    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    @Override
    public void setStudyInstanceUID(String studyUID) {
        this.studyInstanceUID = studyUID;
    }

    @Override
    public Study getStudy() {
        return study;
    }

    @Override
    public void setStudy(Study study) {
        this.study = study;
    }

    @Override
    public String getEventActionCode() {
        return eventActionCode;
    }

    @Override
    public void setEventActionCode(String eventActionCode) {
        this.eventActionCode = eventActionCode;
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String getSeriesInstanceUID() {
        return seriesInstanceUID;
    }

    @Override
    public void setSeriesInstanceUID(String seriesUID) {
        this.seriesInstanceUID = seriesUID;
    }

    @Override
    public String getExpirationExporterID() {
        return expirationExporterID;
    }

    @Override
    public void setExpirationExporterID(String expirationExporterID) {
        this.expirationExporterID = expirationExporterID;
    }

    @Override
    public boolean isFreezeExpirationDate() {
        return freezeExpirationDate;
    }

    @Override
    public void setFreezeExpirationDate(boolean freezeExpirationDate) {
        this.freezeExpirationDate = freezeExpirationDate;
    }

    @Override
    public boolean isUnfreezeExpirationDate() {
        return unfreezeExpirationDate;
    }

    @Override
    public void setUnfreezeExpirationDate(boolean unfreezeExpirationDate) {
        this.unfreezeExpirationDate = unfreezeExpirationDate;
    }

    @Override
    public String getAccessControlID() {
        return accessControlID;
    }

    @Override
    public void setAccessControlID(String accessControlID) {
        this.accessControlID = accessControlID;
    }

    @Override
    public AttributeFilter getSeriesAttributeFilter() {
        return seriesAttributeFilter;
    }

    @Override
    public List<Attributes> getRequestAttributes() {
        return requestAttrs;
    };

    @Override
    public void setRequestAttributes(List<Attributes> requestAttrs) {
        this.requestAttrs = requestAttrs;
    };

    @Override
    public String getReasonForModification() {
        return reasonForModification;
    }

    @Override
    public void setReasonForModification(String reasonForModification) {
        this.reasonForModification = reasonForModification;
    }

    @Override
    public String getSourceOfPreviousValues() {
        return sourceOfPreviousValues;
    }

    @Override
    public void setSourceOfPreviousValues(String sourceOfPreviousValues) {
        this.sourceOfPreviousValues = sourceOfPreviousValues;
    }
}
