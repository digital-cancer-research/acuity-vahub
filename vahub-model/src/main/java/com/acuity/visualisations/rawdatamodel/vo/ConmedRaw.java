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

import java.io.Serializable;
import java.util.Date;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 4)
public class ConmedRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Medication name", type = Column.Type.SSV)
    @Column(order = 1, displayName = "Medication Name", type = Column.Type.DOD)
    private String medicationName;

    @Column(order = 2, displayName = "Atc Code", type = Column.Type.DOD)
    @Column(order = 2, displayName = "Atc code", type = Column.Type.SSV)
    private String atcCode;
    @Column(order = 3, displayName = "Dose", type = Column.Type.DOD)
    private Double dose;

    @Column(order = 4, displayName = "Dose Units", type = Column.Type.DOD)
    private String doseUnits;

    @Column(order = 6, displayName = "Dose frequency", type = Column.Type.DOD)
    @Column(order = 4, displayName = "Dose frequency", type = Column.Type.SSV)
    private String doseFrequency;

    @Column(order = 9, displayName = "Start Date", type = Column.Type.DOD, defaultSortBy = true, defaultSortOrder = 1)
    @Column(order = 5, displayName = "Start date", type = Column.Type.SSV)
    private Date startDate;

    @Column(order = 10, displayName = "End Date", type = Column.Type.DOD)
    @Column(order = 7, displayName = "End date", type = Column.Type.SSV)
    private Date endDate;

    private String atcText;

    @Column(order = 18, columnName = "treatmentReason", displayName = "Treatment reason", type = Column.Type.DOD)
    @Column(order = 9, displayName = "Reason", type = Column.Type.SSV)
    private String treatmentReason;

    @Column(order = 19, displayName = "AE PT", type = Column.Type.DOD)
    @Column(order = 9.1, displayName = "AE PT", type = Column.Type.SSV)
    private String aePt;

    @Column(order = 20, displayName = "AE Number", type = Column.Type.DOD)
    @Column(order = 9.2, displayName = "AE Number", type = Column.Type.SSV)
    private Integer aeNum;

    // fields not for DoD (for filters, grouping etc)
    private String doseUnitsOther;
    private String frequencyOther;
    private Double doseTotal;
    private String route;
    private String therapyReason;
    private String therapyReasonOther;
    private String otherProphylaxisSpec;
    private String infectionBodySystem;
    private String infectionBodySystemOther;
    private String reasonForTreatmentStop;
    private String reasonForTreatmentStopOther;
    private String medicationClass;
    private String activeIngredient1;
    private String activeIngredient2;
    private String studyPeriods;
}
