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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StudyInfo implements Serializable {
    public static final StudyInfo EMPTY = StudyInfo.builder().build();

    public enum DatasetType {
        ACUITY, DETECT;
    }

    private Date lastUpdatedDate;
    private boolean limitXAxisToVisitNumber;
    private boolean medicalMonitoringRespiratory;
    private String sentriStudyId;
    private String ctcaeVersion;
    private DatasetType datasetType;

    public boolean isAcuity() {
        return datasetType == DatasetType.ACUITY;
    }

    public boolean isDetect() {
        return datasetType == DatasetType.DETECT;
    }

    private String datasetName;
    private String studyName;
    private String drugProject;

    private Date dataCutoffDate;
    private Date lastEventDate; //this may need to be calculated

    private boolean blinded;
    private boolean randomised;
    private boolean regulatory;
    //will add all study-related data to this class when needed (like blinded, randomized, custom lookups, etc)

    private boolean randomisedPopulation;

    private long numberOfDosedSubjects;
}
