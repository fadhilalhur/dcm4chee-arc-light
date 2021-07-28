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
 * Portions created by the Initial Developer are Copyright (C) 2019
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

package org.dcm4chee.arc.stgcmt.rs;

import org.dcm4che3.conf.api.ConfigurationException;
import org.dcm4che3.conf.api.IApplicationEntityCache;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.IDWithIssuer;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.service.QueryRetrieveLevel2;
import org.dcm4chee.arc.conf.ArchiveDeviceExtension;
import org.dcm4chee.arc.entity.ExpirationState;
import org.dcm4chee.arc.entity.Patient;
import org.dcm4chee.arc.keycloak.HttpServletRequestInfo;
import org.dcm4chee.arc.query.Query;
import org.dcm4chee.arc.query.QueryContext;
import org.dcm4chee.arc.query.QueryService;
import org.dcm4chee.arc.query.RunInTransaction;
import org.dcm4chee.arc.query.util.QueryAttributes;
import org.dcm4chee.arc.stgcmt.StgCmtSCU;
import org.dcm4chee.arc.validation.constraints.InvokeValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Mar 2019
 */
@RequestScoped
@Path("aets/{aet}/rs")
@InvokeValidate(type = StgCmtSCUMatchingRS.class)
public class StgCmtSCUMatchingRS {

    private static final Logger LOG = LoggerFactory.getLogger(StgCmtSCUMatchingRS.class);

    @PathParam("aet")
    private String aet;

    @Context
    private UriInfo uriInfo;

    @Inject
    private Device device;

    @Inject
    private StgCmtSCU stgCmtSCU;

    @Inject
    private IApplicationEntityCache aeCache;

    @Inject
    private QueryService queryService;

    @Inject
    private RunInTransaction runInTx;

    @Context
    private HttpServletRequest request;

    @QueryParam("fuzzymatching")
    @Pattern(regexp = "true|false")

    private String fuzzymatching;
    @QueryParam("incomplete")
    @Pattern(regexp = "true|false")

    private String incomplete;
    @QueryParam("retrievefailed")
    @Pattern(regexp = "true|false")

    private String retrievefailed;
    @QueryParam("storageVerificationFailed")
    @Pattern(regexp = "true|false")

    private String storageVerificationFailed;
    @QueryParam("metadataUpdateFailed")
    @Pattern(regexp = "true|false")

    private String metadataUpdateFailed;
    @QueryParam("compressionfailed")
    @Pattern(regexp = "true|false")

    private String compressionfailed;
    @QueryParam("ExternalRetrieveAET")
    private String externalRetrieveAET;

    @QueryParam("ExternalRetrieveAET!")
    private String externalRetrieveAETNot;

    @QueryParam("patientVerificationStatus")
    @Pattern(regexp = "UNVERIFIED|VERIFIED|NOT_FOUND|VERIFICATION_FAILED")
    private String patientVerificationStatus;

    @QueryParam("ExpirationDate")
    private String expirationDate;

    @QueryParam("storageID")
    private String storageID;

    @QueryParam("storageClustered")
    @Pattern(regexp = "true|false")
    private String storageClustered;

    @QueryParam("storageExported")
    @Pattern(regexp = "true|false")
    private String storageExported;

    @QueryParam("allOfModalitiesInStudy")
    @Pattern(regexp = "true|false")
    private String allOfModalitiesInStudy;

    @QueryParam("StudySizeInKB")
    @Pattern(regexp = "\\d{1,9}(-\\d{0,9})?|-\\d{1,9}")
    private String studySizeInKB;

    @QueryParam("ExpirationState")
    @Pattern(regexp = "UPDATEABLE|FROZEN|REJECTED|EXPORT_SCHEDULED|FAILED_TO_EXPORT|FAILED_TO_REJECT")
    private String expirationState;

    @QueryParam("batchID")
    private String batchID;

    private static Boolean parseBoolean(String s) {
        return s != null ? Boolean.valueOf(s) : null;
    }

    @POST
    @Path("/studies/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingStudyStorageCommit(@PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingStudyStorageCommit",
                QueryRetrieveLevel2.STUDY, null, null);
    }

    @POST
    @Path("/series/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingSeriesStorageCommit(@PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingSeriesStorageCommit",
                QueryRetrieveLevel2.SERIES, null, null);
    }

    @POST
    @Path("/studies/{StudyInstanceUID}/series/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingSeriesOfStudyStorageCommit(
            @PathParam("StudyInstanceUID") String studyUID,
            @PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingSeriesOfStudyStorageCommit",
                QueryRetrieveLevel2.SERIES, studyUID, null);
    }

    @POST
    @Path("/instances/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingInstancesStorageCommit(@PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingInstancesStorageCommit",
                QueryRetrieveLevel2.IMAGE, null, null);
    }

    @POST
    @Path("/studies/{StudyInstanceUID}/instances/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingInstancesOfStudyStorageCommit(
            @PathParam("StudyInstanceUID") String studyUID,
            @PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingInstancesOfStudyStorageCommit",
                QueryRetrieveLevel2.IMAGE, studyUID, null);
    }

    @POST
    @Path("/studies/{StudyInstanceUID}/series/{SeriesInstanceUID}/instances/stgcmt/{stgcmtscp}")
    @Produces("application/json")
    public Response matchingInstancesOfSeriesStorageCommit(
            @PathParam("StudyInstanceUID") String studyUID,
            @PathParam("SeriesInstanceUID") String seriesUID,
            @PathParam("stgcmtscp") String stgcmtscp) {
        return storageCommitMatching(aet, stgcmtscp,"matchingInstancesOfSeriesStorageCommit",
                QueryRetrieveLevel2.IMAGE, studyUID, seriesUID);
    }

    @Override
    public String toString() {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        return queryString == null ? requestURI : requestURI + '?' + queryString;
    }

    public void validate() {
        logRequest();
        new QueryAttributes(uriInfo, null);
    }

    Response storageCommitMatching(String aet, String stgcmtscp, String method, QueryRetrieveLevel2 qrlevel,
                                   String studyUID, String seriesUID) {
        ApplicationEntity ae = device.getApplicationEntity(aet, true);
        if (ae == null || !ae.isInstalled())
            return errResponse("No such Application Entity: " + aet, Response.Status.NOT_FOUND);

        try {
            aeCache.findApplicationEntity(stgcmtscp);
            QueryContext ctx = queryContext(method, qrlevel, studyUID, seriesUID, ae);
            String warning;
            int count;
            Response.Status status = Response.Status.ACCEPTED;
            try (Query query = queryService.createQuery(ctx)) {
                int queryMaxNumberOfResults = ctx.getArchiveAEExtension().queryMaxNumberOfResults();
                if (queryMaxNumberOfResults > 0 && !ctx.containsUniqueKey()
                        && query.fetchCount() > queryMaxNumberOfResults)
                    return errResponse("Request entity too large", Response.Status.BAD_REQUEST);

                StgCmtSCUMatchingObjects stgCmtSCUMatchingObjects = new StgCmtSCUMatchingObjects(
                                                                            aet, stgcmtscp, qrlevel, query, status);
                runInTx.execute(stgCmtSCUMatchingObjects);
                count = stgCmtSCUMatchingObjects.getCount();
                status = stgCmtSCUMatchingObjects.getStatus();
                warning = stgCmtSCUMatchingObjects.getWarning();
            }
            Response.ResponseBuilder builder = Response.status(status);
            if (warning != null) {
                LOG.warn("Response {} caused by {}", status, warning);
                builder.header("Warning", warning);
            }
            return builder.entity("{\"count\":" + count + '}').build();
        } catch (IllegalStateException | ConfigurationException e) {
            return errResponse(e.getMessage(), Response.Status.NOT_FOUND);
        } catch (Exception e) {
            return errResponseAsTextPlain(exceptionAsString(e), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private QueryContext queryContext(String method, QueryRetrieveLevel2 qrlevel, String studyUID,
                                      String seriesUID, ApplicationEntity ae) {
        QueryContext ctx = queryService.newQueryContextQIDO(
                HttpServletRequestInfo.valueOf(request), method, ae, queryParam(ae));
        ctx.setQueryRetrieveLevel(qrlevel);
        QueryAttributes queryAttrs = new QueryAttributes(uriInfo, null);
        Attributes keys = queryAttrs.getQueryKeys();
        IDWithIssuer idWithIssuer = IDWithIssuer.pidOf(keys);
        if (idWithIssuer != null)
            ctx.setPatientIDs(idWithIssuer);
        if (studyUID != null)
            keys.setString(Tag.StudyInstanceUID, VR.UI, studyUID);
        if (seriesUID != null)
            keys.setString(Tag.SeriesInstanceUID, VR.UI, seriesUID);
        ctx.setQueryKeys(keys);
        Attributes returnKeys = new Attributes(3);
        returnKeys.setNull(Tag.StudyInstanceUID, VR.UI);
        switch (qrlevel) {
            case IMAGE:
                returnKeys.setNull(Tag.SOPInstanceUID, VR.UI);
            case SERIES:
                returnKeys.setNull(Tag.SeriesInstanceUID, VR.UI);
        }
        ctx.setReturnKeys(returnKeys);
        return ctx;
    }

    private org.dcm4chee.arc.query.util.QueryParam queryParam(ApplicationEntity ae) {
        org.dcm4chee.arc.query.util.QueryParam queryParam = new org.dcm4chee.arc.query.util.QueryParam(ae);
        queryParam.setCombinedDatetimeMatching(true);
        queryParam.setFuzzySemanticMatching(Boolean.parseBoolean(fuzzymatching));
        queryParam.setAllOfModalitiesInStudy(Boolean.parseBoolean(allOfModalitiesInStudy));
        queryParam.setIncomplete(Boolean.parseBoolean(incomplete));
        queryParam.setRetrieveFailed(Boolean.parseBoolean(retrievefailed));
        queryParam.setStorageVerificationFailed(Boolean.parseBoolean(storageVerificationFailed));
        queryParam.setMetadataUpdateFailed(Boolean.parseBoolean(metadataUpdateFailed));
        queryParam.setCompressionFailed(Boolean.parseBoolean(compressionfailed));
        queryParam.setExternalRetrieveAET(externalRetrieveAET);
        queryParam.setExternalRetrieveAETNot(externalRetrieveAETNot);
        queryParam.setExpirationDate(expirationDate);
        queryParam.setStudySizeRange(studySizeInKB);
        if (patientVerificationStatus != null)
            queryParam.setPatientVerificationStatus(Patient.VerificationStatus.valueOf(patientVerificationStatus));
        if (storageID != null)
            queryParam.setStudyStorageIDs(device.getDeviceExtensionNotNull(ArchiveDeviceExtension.class)
                    .getStudyStorageIDs(storageID, parseBoolean(storageClustered), parseBoolean(storageExported)));
        if (expirationState != null)
            queryParam.setExpirationState(ExpirationState.valueOf(expirationState));
        return queryParam;
    }

    private void logRequest() {
        LOG.info("Process {} {} from {}@{}",
                request.getMethod(),
                toString(),
                request.getRemoteUser(),
                request.getRemoteHost());
    }

    private Response errResponse(String msg, Response.Status status) {
        return errResponseAsTextPlain("{\"errorMessage\":\"" + msg + "\"}", status);
    }

    private Response errResponseAsTextPlain(String errorMsg, Response.Status status) {
        LOG.warn("Response {} caused by {}", status, errorMsg);
        return Response.status(status)
                .entity(errorMsg)
                .type("text/plain")
                .build();
    }

    private String exceptionAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    class StgCmtSCUMatchingObjects implements Runnable {
        private int count;
        private final String aet;
        private final String stgcmtscp;
        private final QueryRetrieveLevel2 qrLevel;
        private final Query query;
        private Response.Status status;
        private String warning;

        StgCmtSCUMatchingObjects(
                String aet, String stgcmtscp, QueryRetrieveLevel2 qrLevel, Query query, Response.Status status) {
            this.aet = aet;
            this.stgcmtscp = stgcmtscp;
            this.qrLevel = qrLevel;
            this.query = query;
            this.status = status;
        }

        int getCount() {
            return count;
        }

        Response.Status getStatus() {
            return status;
        }

        String getWarning() {
            return warning;
        }

        @Override
        public void run() {
            try {
                query.executeQuery(device.getDeviceExtensionNotNull(ArchiveDeviceExtension.class).getQueryFetchSize());
                while (query.hasMoreMatches()) {
                    Attributes match = query.nextMatch();
                    if (match == null)
                        continue;

                    stgCmtSCU.scheduleStorageCommit(aet, stgcmtscp, match, batchID, qrLevel);
                    count++;
                }
            } catch (Exception e) {
                warning = e.getMessage();
                status = Response.Status.INTERNAL_SERVER_ERROR;
            }
        }
    }
}
