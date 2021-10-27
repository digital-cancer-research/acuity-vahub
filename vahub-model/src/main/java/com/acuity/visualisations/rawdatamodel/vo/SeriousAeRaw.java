/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YMD;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 2)
public class SeriousAeRaw implements HasStringId, HasSubjectId, HasStartDate, HasEndDate {
    private String id;
    private String subjectId;
    @Column(order = 1, displayName = "Event", type = Column.Type.SSV)
    @Column(order = 2, displayName = "Adverse event", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String ae;
    @Column(order = 13, displayName = "Preferred term", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String pt;
    @Column(order = 18, displayName = "Primary cause of death", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String primaryDeathCause;
    @Column(order = 19, displayName = "Secondary cause of death", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String secondaryDeathCause;
    @Column(order = 26, displayName = "Other medication", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String otherMedication;
    @Column(order = 27, displayName = "AE caused by other medication", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String causedByOtherMedication;
    @Column(order = 28, displayName = "Study procedure(s)", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String studyProcedure;
    @Column(order = 29, displayName = "AE caused by study procedure(s)", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String causedByStudy;
    @Column(order = 17, displayName = "AE description", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String description;
    @Column(order = 5, displayName = "Results in death", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String resultInDeath;
    @Column(order = 6, displayName = "Requires or prolongs hospitalization", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String hospitalizationRequired;
    @Column(order = 7, displayName = "Congenital anomaly or birth defect", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String congenitalAnomaly;
    @Column(order = 8, displayName = "Life threatening", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String lifeThreatening;
    @Column(order = 9, displayName = "Persist. or sign. disability/incapacity", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String disability;
    @Column(order = 10, displayName = "Other medically important serious event", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String otherSeriousEvent;
    @Column(order = 20, displayName = "Additional Drug", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String ad;
    @Column(order = 21, displayName = "AE Caused by Additional Drug", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String causedByAD;
    @Column(order = 22, displayName = "Additional Drug 1", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String ad1;
    @Column(order = 23, displayName = "AE Caused by Additional Drug 1", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String causedByAD1;
    @Column(order = 24, displayName = "Additional Drug 2", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String ad2;
    @Column(order = 25, displayName = "AE Caused by Additional Drug 2", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String causedByAD2;
    @Column(order = 3, columnName = "startDate", displayName = "AE start date",
        type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY, defaultSortBy = true)
    private Date startDate;
    @Column(order = 4, displayName = "AE end date", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date endDate;
    @Column(order = 0, displayName = "Date", type = Column.Type.SSV, dateFormat = YMD)
    @Column(order = 14, displayName = "Date AE met criteria for serious AE", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date becomeSeriousDate;
    @Column(order = 16, displayName = "Date investigator aware of serious AE", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date findOutDate;
    @Column(order = 11, displayName = "Date of hospitalization", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date hospitalizationDate;
    @Column(order = 12, displayName = "Date of discharge", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date dischargeDate;

    @Column(order = 1, displayName = "AE Number", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Integer num;

    public static class SeriousAeRawBuilder implements HasStringId {
        @Override
        public String getId() {
            return id;
        }
    }

    @Value
    @Builder
    public static class SeriousAeSeverityStartEndDates {
        private String seriousAeId;
        private Date severityStartDate;
        private Date severityEndDate;
    }
}
