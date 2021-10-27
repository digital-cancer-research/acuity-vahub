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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static com.acuity.visualisations.rawdatamodel.util.Constants.MISSING;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 11)
public class AssessedTargetLesionRaw implements HasStringId, HasSubjectId, Serializable {

    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String subjectId;

    private String response;
    private String bestResponse;

    private TargetLesionRaw targetLesionRaw;
    private AssessmentRaw assessmentRaw;
    private NonTargetLesionRaw nonTargetLesionRaw;
    private AssessedTargetLesionRaw bestResponseEvent;
    // whether non-target lesions are present per subject in the dataset or not. Possible values: "Yes", "No"
    private String nonTargetLesionsPresent;

    // placed not in assessmentRaw because assessmentRaw can be null ('No assessment')
    private Integer assessmentFrequency;

    public Date getLesionDate() {
        return targetLesionRaw.getLesionDate();
    }

    public String getLesionNumber() {
        return targetLesionRaw.getLesionNumber();
    }

    public String getAssessmentMethod() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 1, columnName = "visitNumber", displayName = "Visit number", type = Column.Type.DOD, defaultSortBy = true, defaultSortOrder = 1)
    public Integer getVisitNumber() {
        return targetLesionRaw.getVisitNumber();
    }

    public Integer getLesionDiameter() {
        return targetLesionRaw.getLesionDiameter();
    }

    public Double getSumPercentageChangeFromBaseline() {
        return targetLesionRaw.getSumPercentageChangeFromBaseline();
    }

    public Double getSumBestPercentageChangeFromBaseline() {
        return targetLesionRaw.getSumBestPercentageChangeFromBaseline();
    }

    public boolean isBaseline() {
        return targetLesionRaw.isBaseline();
    }

    public int getBaselineLesionsDiameter() {
        return targetLesionRaw.getSumBaselineDiameter();
    }

    public boolean isBestPercentageChange() {
        return targetLesionRaw.isBestPercentageChange();
    }

    public Integer getLesionsDiameterPerAssessment() {
        return targetLesionRaw.getLesionsDiameterPerAssessment();
    }

    public int getLesionsCountPerAssessment() {
        return targetLesionRaw.getLesionCountAtVisit();
    }

    public int getLesionsCountAtBaseline() {
        return targetLesionRaw.getLesionCountAtBaseline();
    }

    @Column(order = 2, columnName = "ntlResponse", displayName = "NTL response", type = Column.Type.DOD)
    public String getNtlResponse() {
        return nonTargetLesionRaw == null || nonTargetLesionRaw.getResponseShort() == null
                || nonTargetLesionRaw.getResponseShort().isEmpty()
                ? MISSING : nonTargetLesionRaw.getResponseShort();
    }

    @Column(order = 3, columnName = "newLesions", displayName = "New lesions", type = Column.Type.DOD)
    public String getNewLesions() {
        return assessmentRaw == null || assessmentRaw.getNewLesionResponse() == null || assessmentRaw.getNewLesionResponse().isEmpty()
                ? MISSING : assessmentRaw.getNewLesionResponse();
    }

    @Column(order = 4, columnName = "overallResponse", displayName = "Overall response", type = Column.Type.DOD)
    public String getOverallResponse() {
        return assessmentRaw == null || assessmentRaw.getResponseShort() == null || assessmentRaw.getResponseShort().isEmpty()
                ? MISSING : assessmentRaw.getResponseShort();
    }
}
