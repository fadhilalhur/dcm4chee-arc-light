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
 * Portions created by the Initial Developer are Copyright (C) 2015
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

package org.dcm4chee.arc.delete.impl;

import org.dcm4che3.audit.AuditMessages;
import org.dcm4che3.data.Code;
import org.dcm4che3.data.IDWithIssuer;
import org.dcm4che3.net.Device;
import org.dcm4chee.arc.code.CodeCache;
import org.dcm4chee.arc.conf.ArchiveDeviceExtension;
import org.dcm4chee.arc.delete.StudyDeleteContext;
import org.dcm4chee.arc.entity.*;
import org.dcm4chee.arc.patient.PatientMgtContext;
import org.dcm4chee.arc.patient.PatientService;
import org.dcm4chee.arc.query.QueryService;
import org.dcm4chee.arc.store.impl.StoreServiceEJB;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Gunter Zeilinger <gunterze@gmail.com>
 * @author Vrinda Nayak <vrinda.nayak@j4care.com>
 * @since Oct 2015
 */
@Stateless
public class DeletionServiceEJB {

    public static final int MAX_LOCATIONS_PER_INSTANCE = 2;

    @PersistenceContext(unitName = "dcm4chee-arc")
    private EntityManager em;

    @Inject
    private CodeCache codeCache;

    @Inject
    private Device device;

    @Inject
    private PatientService patientService;

    @Inject
    private StoreServiceEJB storeEjb;

    @Inject
    private QueryService queryService;

    public List<Location> findLocationsToDelete(String storageID, int limit) {
        return em.createNamedQuery(Location.FIND_BY_STORAGE_ID_AND_STATUS, Location.class)
                .setParameter(1, storageID)
                .setParameter(2, Location.Status.TO_DELETE)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Metadata> findMetadataToDelete(String storageID, int limit) {
        return em.createNamedQuery(Metadata.FIND_BY_STORAGE_ID_AND_STATUS, Metadata.class)
                .setParameter(1, storageID)
                .setParameter(2, Metadata.Status.TO_DELETE)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Long> findStudiesForDeletionOnStorage(String storageID, int limit) {
        return em.createNamedQuery(Study.FIND_PK_BY_STORAGE_ID_ORDER_BY_ACCESS_TIME, Long.class)
                .setParameter(1, storageID)
                .setMaxResults(limit)
                .getResultList();
    }

    public int instancesNotStoredOnBoth(Long studyPk, String storageID, String otherStorageID) {
        return ((BigInteger) em.createNamedQuery(Location.COUNT_INSTANCES_OF_STUDY_NOT_ON_BOTH_STORAGE)
                .setParameter(1, studyPk)
                .setParameter(2, storageID)
                .setParameter(3, otherStorageID)
                .getSingleResult()).intValue();
    }

    public List<Long> findStudiesForDeletionOnStorageWithExternalRetrieveAET(String storageID, String aet, int limit) {
        return em.createNamedQuery(Study.FIND_PK_BY_STORAGE_ID_AND_EXT_RETR_AET, Long.class)
                .setParameter(1, storageID)
                .setParameter(2, aet)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Metadata> findMetadataOfSeriesWithPurgedInstances(Long studyPk) {
        return em.createNamedQuery(Series.FIND_BY_STUDY_PK_AND_INSTANCE_PURGE_STATE, Metadata.class)
                .setParameter(1, studyPk)
                .setParameter(2, Series.InstancePurgeState.PURGED)
                .getResultList();
    }

    public void failedToDelete(Location location) {
        location.setStatus(Location.Status.FAILED_TO_DELETE);
        em.merge(location);
    }

    public void failedToDelete(Metadata metadata) {
        metadata.setStatus(Metadata.Status.FAILED_TO_DELETE);
        em.merge(metadata);
    }

    public void removeLocation(Location location) {
        em.remove(em.merge(location));
    }

    public void removeMetadata(Metadata metadata) {
        em.remove(em.merge(metadata));
    }

    public Study deleteStudy(StudyDeleteContext ctx) {
        Long studyPk = ctx.getStudyPk();
        List<Location> locations = em.createNamedQuery(Location.FIND_BY_STUDY_PK, Location.class)
                .setParameter(1, studyPk)
                .getResultList();
        return deleteStudy(removeOrMarkToDelete(locations, Integer.MAX_VALUE, false), ctx);
    }

    public Study deleteObjectsOfStudy(Long studyPk, String storageID) {
        List<Location> locations = em.createNamedQuery(Location.FIND_BY_STUDY_PK_AND_STORAGE_ID, Location.class)
                .setParameter(1, studyPk)
                .setParameter(2, storageID)
                .getResultList();
        Collection<Instance> insts = removeOrMarkToDelete(locations, Integer.MAX_VALUE, false);
        Set<Long> seriesPks = new HashSet<>();
        for (Instance inst : insts) {
            Series series = inst.getSeries();
            if (seriesPks.add(series.getPk())
                    && series.getMetadataScheduledUpdateTime() == null
                    && series.getMetadata() != null)
                scheduleMetadataUpdate(series.getPk());
        }
        Study study = insts.iterator().next().getSeries().getStudy();
        study.removeStorageID(storageID);
        return study;
    }

    public int deleteRejectedInstancesOrRejectionNotesBefore(
            String queryName, Code rejectionCode, Date before, int limit) {
        CodeEntity codeEntity = codeCache.findOrCreate(rejectionCode);
        TypedQuery<Location> query = em.createNamedQuery(queryName, Location.class).setParameter(1, codeEntity);
        if (before != null)
            query.setParameter(2, before);

        List<Location> locations = query.setMaxResults(limit).getResultList();
        if (!locations.isEmpty())
            deleteInstances(removeOrMarkToDelete(locations, limit, true));
        return locations.size();
    }

    public void deleteEmptyStudy(StudyDeleteContext ctx) {
        Study study = ctx.getStudy();
        em.remove(em.contains(study) ? study : em.merge(study));
    }

    public void deleteMWLItemsOfPatient(PatientMgtContext ctx) {
        List<MWLItem> mwlItems = em.createNamedQuery(MWLItem.FIND_BY_PATIENT, MWLItem.class)
                .setParameter(1, ctx.getPatient()).getResultList();
        for (MWLItem mwlItem : mwlItems)
            em.remove(mwlItem);
    }

    private Collection<Instance> removeOrMarkToDelete(List<Location> locations, int limit, boolean resetSize) {
        int size = locations.size();
        int initialCapacity = size * 4 / 3;
        HashMap<Long, Instance> insts = new HashMap<>(initialCapacity);
        HashMap<Long, UIDMap> uidMaps = new HashMap<>();
        Instance prev = null;
        int n = limit - (MAX_LOCATIONS_PER_INSTANCE - 1);
        for (Location location : locations) {
            Instance inst = location.getInstance();
            if (n-- <= 0 && prev != inst) // ensure to detach all locations of returned instances
                break;
            insts.put(inst.getPk(), prev = inst);
            UIDMap uidMap = location.getUidMap();
            if (uidMap != null)
                uidMaps.put(uidMap.getPk(), uidMap);

            if (resetSize) {
                Series series = inst.getSeries();
                series.resetSize();
                series.getStudy().resetSize();
            }
            storeEjb.removeOrMarkToDelete(location);
        }
        for (UIDMap uidMap : uidMaps.values())
            storeEjb.removeOrphaned(uidMap);
        return insts.values();
    }

    private void deleteInstances(Collection<Instance> insts) {
        HashMap<Long, Series> series = new HashMap<>();
        for (Instance inst : insts) {
            Series ser = inst.getSeries();
            if (!series.containsKey(ser.getPk())) {
                series.put(ser.getPk(), ser);
                deleteSeriesQueryAttributes(ser);
            }
            em.remove(inst);
        }
        HashMap<Long, Study> studies = new HashMap<>();
        for (Series ser : series.values()) {
            Study study = ser.getStudy();
            if (!studies.containsKey(study.getPk())) {
                studies.put(study.getPk(), study);
                deleteStudyQueryAttributes(study);
            }
            if (countInstancesOfSeries(ser) == 0) {
                if (ser.getMetadata() != null)
                    ser.getMetadata().setStatus(Metadata.Status.TO_DELETE);
                em.remove(ser);
            } else {
                studies.put(study.getPk(), null);
                if (ser.getRejectionState() == RejectionState.PARTIAL && !hasRejectedInstances(ser))
                    ser.setRejectionState(RejectionState.NONE);
            }
        }
        for (Study study : studies.values()) {
            if (study == null)
                continue;

            if (countSeriesOfStudy(study) == 0) {
                em.remove(study);
            } else {
                if (study.getRejectionState() == RejectionState.PARTIAL
                        && !hasSeriesWithOtherRejectionState(study, RejectionState.NONE))
                    study.setRejectionState(RejectionState.NONE);
            }
        }
    }

    private Study deleteStudy(Collection<Instance> insts, StudyDeleteContext ctx) {
        Patient patient = null;
        Study study = null;
        HashMap<Long, Series> series = new HashMap<>();
        for (Instance inst : insts) {
            Series ser = inst.getSeries();
            if (!series.containsKey(ser.getPk())) {
                series.put(ser.getPk(), ser);
            }
            ctx.addInstance(inst);
            em.remove(inst);
        }
        for (Series ser : series.values()) {
            if (study == null) {
                ctx.setStudy(study = ser.getStudy());
                ctx.setPatient(patient = study.getPatient());
            }
            if (ser.getMetadata() != null)
                ser.getMetadata().setStatus(Metadata.Status.TO_DELETE);
            em.remove(ser);
        }
        em.remove(study);
        if (ctx.isDeletePatientOnDeleteLastStudy() && countStudiesOfPatient(patient) == 0) {
            PatientMgtContext patMgtCtx = patientService.createPatientMgtContextScheduler();
            patMgtCtx.setPatient(patient);
            patMgtCtx.setEventActionCode(AuditMessages.EventActionCode.Delete);
            patMgtCtx.setAttributes(patient.getAttributes());
            patMgtCtx.setPatientID(IDWithIssuer.pidOf(patient.getAttributes()));
            patientService.deletePatientIfHasNoMergedWith(patMgtCtx);
        }
        return study;
    }

    private boolean hasRejectedInstances(Series series) {
        return em.createNamedQuery(Instance.COUNT_REJECTED_INSTANCES_OF_SERIES, Long.class)
                .setParameter(1, series)
                .getSingleResult() > 0;
    }

    private boolean hasSeriesWithOtherRejectionState(Study study, RejectionState rejectionState) {
        return em.createNamedQuery(Series.COUNT_SERIES_OF_STUDY_WITH_OTHER_REJECTION_STATE, Long.class)
                .setParameter(1, study)
                .setParameter(2, rejectionState)
                .getSingleResult() > 0;
    }

    private long countStudiesOfPatient(Patient patient) {
        return em.createNamedQuery(Study.COUNT_STUDIES_OF_PATIENT, Long.class).setParameter(1, patient)
                .getSingleResult();
    }

    private long countSeriesOfStudy(Study study) {
        return em.createNamedQuery(Series.COUNT_SERIES_OF_STUDY, Long.class).setParameter(1, study)
                .getSingleResult();
    }

    private long countInstancesOfSeries(Series series) {
        return em.createNamedQuery(Instance.COUNT_INSTANCES_OF_SERIES, Long.class).setParameter(1, series).
                getSingleResult();
    }

    private int deleteStudyQueryAttributes(Study study) {
        return em.createNamedQuery(StudyQueryAttributes.DELETE_FOR_STUDY).setParameter(1, study).executeUpdate();
    }

    private int deleteSeriesQueryAttributes(Series series) {
        return em.createNamedQuery(SeriesQueryAttributes.DELETE_FOR_SERIES).setParameter(1, series).executeUpdate();
    }

    public List<Series.MetadataUpdate> findSeriesToPurgeInstances(int fetchSize) {
        return em.createNamedQuery(Series.SCHEDULED_PURGE_INSTANCES, Series.MetadataUpdate.class)
                .setMaxResults(fetchSize)
                .getResultList();
    }

    public boolean purgeInstanceRecordsOfSeries(Long seriesPk, Map<String, List<Location>> locationsFromMetadata) {
        Series series = em.find(Series.class, seriesPk);
        List<Location> locations = em.createNamedQuery(Location.FIND_BY_SERIES_PK, Location.class)
                .setParameter(1, seriesPk)
                .getResultList();
        if (!verifyMetadata(locationsFromMetadata, locations)) {
            Date now = new Date();
            series.setMetadataScheduledUpdateTime(now);
            series.setInstancePurgeTime(now);
            return false;
        }
        calculateMissingSeriesQueryAttributes(seriesPk);
        long size = 0L;
        for (Location location : locations) {
            switch (location.getObjectType()) {
                case DICOM_FILE:
                    size += location.getSize();
                    em.remove(location);
                    em.remove(location.getInstance());
                    break;
                case METADATA:
                    location.setInstance(null);
                    location.setStatus(Location.Status.TO_DELETE);
                    break;
            }
        }
        series.setSize(size);
        series.setInstancePurgeTime(null);
        series.setInstancePurgeState(Series.InstancePurgeState.PURGED);
        return true;
    }

    private boolean verifyMetadata(Map<String, List<Location>> locationsFromMetadata, List<Location> locations) {
        for (Location location : locations) {
            if (location.getObjectType() == Location.ObjectType.DICOM_FILE) {
                if (!verifyMetadata(locationsFromMetadata, location))
                    return false;
            }
        }
        return locationsFromMetadata.isEmpty();
    }

    private boolean verifyMetadata(Map<String, List<Location>> locationsFromMetadata, Location location) {
        String iuid = location.getInstance().getSopInstanceUID();
        List<Location> locations = locationsFromMetadata.get(iuid);
        if (locations == null || !locations.removeIf(l ->
            Objects.equals(location.getStorageID(), l.getStorageID())
                    && Objects.equals(location.getStoragePath(), l.getStoragePath())
                    && Objects.equals(location.getDigestAsHexString(), l.getDigestAsHexString())
                    && Objects.equals(location.getSize(), l.getSize())))
            return false;

        if (locations.isEmpty())
            locationsFromMetadata.remove(iuid);
        return true;
    }

    public boolean claimPurgeInstanceRecordsOfSeries(Long seriesPk) {
        Series series = em.find(Series.class, seriesPk);
        if (series.getInstancePurgeTime() == null)
            return false;

        series.setInstancePurgeTime(null);
        return true;
    }

    public boolean scheduleMetadataUpdate(Long seriesPk) {
        return em.createNamedQuery(Series.SCHEDULE_METADATA_UPDATE_FOR_SERIES)
                .setParameter(1, seriesPk)
                .executeUpdate() > 0;
    }

    public boolean updateInstancePurgeState(
            Long seriesPk, Series.InstancePurgeState from, Series.InstancePurgeState to) {
        return em.createNamedQuery(Series.UPDATE_INSTANCE_PURGE_STATE)
                .setParameter(1, seriesPk)
                .setParameter(2, from)
                .setParameter(3, to)
                .executeUpdate() > 0;
    }

    private void calculateMissingSeriesQueryAttributes(Long seriesPk) {
        ArchiveDeviceExtension arcDev = device.getDeviceExtension(ArchiveDeviceExtension.class);
        Set<String> viewIDs = new HashSet<>(arcDev.getQueryRetrieveViewIDs());
        viewIDs.removeAll(em.createNamedQuery(SeriesQueryAttributes.VIEW_IDS_FOR_SERIES_PK, String.class)
                .setParameter(1, seriesPk)
                .getResultList());
        for (String viewID : viewIDs) {
            queryService.calculateSeriesQueryAttributes(seriesPk, arcDev.getQueryRetrieveView(viewID));
        }
    }

    public void updateStudyAccessTime(Long studyPk) {
        em.createNamedQuery(Study.UPDATE_ACCESS_TIME)
                .setParameter(1, studyPk)
                .executeUpdate();
    }
}
