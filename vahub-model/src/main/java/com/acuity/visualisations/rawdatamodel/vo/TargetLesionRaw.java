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

import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 5)
public class TargetLesionRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 5.5, displayName = "Site of target lesion", type = Column.Type.SSV)
    private String lesionSite;
    @Column(order = 8, displayName = "Lesion diameter (mm)", type = Column.Type.SSV)
    private Integer lesionDiameter;
    @Column(order = 4.5, displayName = "Lesion number", type = Column.Type.SSV)
    private String lesionNumber;
    @Column(order = 1, displayName = "Date assessed", type = Column.Type.SSV)
    private Date lesionDate;
    private Date visitDate;
    @Column(order = 3, displayName = "Visit number", type = Column.Type.SSV)
    private Integer visitNumber;

    private String baselineLesionSite;
    private boolean baseline;
    private int baselineLesionDiameter;
    private Double lesionPercentageChangeFromBaseline;
    private int sumBaselineDiameter;

    private boolean missingsPresent;
    private boolean missingsAtVisitPresent;
    private Integer lesionCountAtVisit;
    private Integer lesionCountAtBaseline;

    @Column(order = 11, displayName = "Sum of diameters (mm)", type = Column.Type.SSV)
    private Integer lesionsDiameterPerAssessment;
    @Column(order = 12, displayName = "% change from baseline", type = Column.Type.SSV)
    private Double sumPercentageChangeFromBaseline;
    private boolean bestPercentageChange;
    private Double sumBestPercentageChangeFromBaseline;
    @Column(order = 13, displayName = "% change from minimum", type = Column.Type.SSV)
    private Double sumPercentageChangeFromMinimum;
    @Column(order = 14, displayName = "Absolute change from minimum", type = Column.Type.SSV)
    private Integer sumChangeFromMinimum;

    @Column(order = 5, columnName = "methodOfAssessment", displayName = "Method of assessment", type = Column.Type.SSV)
    public String getMethodOfAssessment() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 7, columnName = "locationWithinSiteSpecification", displayName = "Location within site specification", type = Column.Type.SSV)
    public String getLocationWithinSiteSpecification() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 9, columnName = "lesionNoLongerMeasurable", displayName = "Lesion no longer measurable", type = Column.Type.SSV)
    public String getLesionNoLongerMeasurable() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 10, columnName = "lesionIntervention", displayName = "Lesion intervention", type = Column.Type.SSV)
    public String getLesionIntervention() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 15, columnName = "calculatedResponse", displayName = "ACUITY calculated target lesion visit response", type = Column.Type.SSV)
    public String getCalculatedResponse() {
        return NOT_IMPLEMENTED;
    }
}
