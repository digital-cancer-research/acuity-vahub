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

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 7)
public class CIEventRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(columnName = "startDate", order = 3, displayName = "Start date")
    private Date startDate;
    @Column(columnName = "term", order = 5, displayName = "Term")
    private String term;
    private int aeNumber;
    @Column(columnName = "ischemicSymptoms", order = 7, displayName = "Ischemic symptoms")
    private String ischemicSymptoms;
    @Column(columnName = "cieSymptomsDuration", order = 8, displayName = "Cie symptoms duration")
    private String cieSymptomsDuration;
    @Column(columnName = "symptPromptUnschedHospit", order = 9, displayName = "Sympt prompt unsched hospit")
    private String symptPromptUnschedHospit;
    @Column(columnName = "eventSuspDueToStentThromb", order = 10, displayName = "Event susp due to stent thromb")
    private String eventSuspDueToStentThromb;
    @Column(columnName = "previousEcgAvailable", order = 11, displayName = "Previous ecg available")
    private String previousEcgAvailable;
    @Column(columnName = "previousEcgDate", order = 12, displayName = "Previous ecg date")
    private Date previousEcgDate;
    @Column(columnName = "ecgAtTheEventTime", order = 13, displayName = "Ecg at the event time")
    private String ecgAtTheEventTime;
    @Column(columnName = "noEcgAtTheEventTime", order = 14, displayName = "No ecg at the event time")
    private String noEcgAtTheEventTime;
    @Column(columnName = "localCardiacBiomarkersDrawn", order = 15, displayName = "Local cardiac biomarkers drawn")
    private String localCardiacBiomarkersDrawn;
    @Column(columnName = "coronaryAngiography", order = 16, displayName = "Coronary angiography")
    private String coronaryAngiography;
    @Column(columnName = "angiographyDate", order = 17, displayName = "Angiography date")
    private Date angiographyDate;
    @Column(columnName = "finalDiagnosis", order = 1, displayName = "Final diagnosis")
    private String finalDiagnosis;
    @Column(columnName = "otherDiagnosis", order = 2, displayName = "Other diagnosis")
    private String otherDiagnosis;
    @Column(columnName = "description1", order = 18, displayName = "Description 1")
    private String description1;
    @Column(columnName = "description2", order = 19, displayName = "Description 2")
    private String description2;
    @Column(columnName = "description3", order = 20, displayName = "Description 3")
    private String description3;
    @Column(columnName = "description4", order = 21, displayName = "Description 4")
    private String description4;
    @Column(columnName = "description5", order = 22, displayName = "Description 5")
    private String description5;
}
