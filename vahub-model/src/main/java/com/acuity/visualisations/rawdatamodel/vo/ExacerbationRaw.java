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

import static com.acuity.visualisations.rawdatamodel.util.Column.Type.DOD;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 6)
public class ExacerbationRaw implements HasStringId, HasSubjectId, HasStartDate, HasEndDate, Serializable {
    private String id;
    private String subjectId;

    // TODO: remove studyPeriods from everywhere
    private String studyPeriods; // Possibly unused
    @Column(order = 3, displayName = "Exacerbation Classification")
    private String exacerbationClassification;
    @Column(columnName = "startDate", order = 4, displayName = "Start Date", type = DOD, defaultSortBy = true)
    private Date startDate;
    @Column(order = 5, displayName = "End Date")
    private Date endDate;
    @Column(order = 6, displayName = "Days On Study At Start")
    private Integer daysOnStudyAtStart;
    @Column(order = 7, displayName = "Days On Study At End")
    private Integer daysOnStudyAtEnd;
    private Integer numberOfDosesReceived;
    private String reportedClinicalEvent;
    @Column(order = 8, displayName = "Duration")
    private Integer duration;
    @Column(order = 9, displayName = "Start Prior To Randomisation")
    private String startPriorToRandomisation;
    @Column(order = 10, displayName = "End Prior To Randomisation")
    private String endPriorToRandomisation;
    @Column(order = 11, displayName = "Hospitalisation")
    private String hospitalisation;
    @Column(order = 12, displayName = "Emergency Room Visit")
    private String emergencyRoomVisit;
    @Column(order = 13, displayName = "Antibiotics Treatment")
    private String antibioticsTreatment;
    @Column(order = 14, displayName = "Depot Corticosteroid Treatment")
    private String depotCorticosteroidTreatment;
    @Column(order = 15, displayName = "Systemic Corticosteroid Treatment")
    private String systemicCorticosteroidTreatment;
    @Column(order = 16, displayName = "Increased Inhaled Corticosteroid Treatment")
    private String increasedInhaledCorticosteroidTreatment;
}
