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
public class NonTargetLesionRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 1, columnName = "lesionDate", displayName = "Date assessed", type = Column.Type.SSV)
    private Date lesionDate;
    @Column(order = 3, columnName = "visitNumber", displayName = "Visit number", type = Column.Type.SSV)
    private Integer visitNumber;
    private Date visitDate;
    @Column(order = 4, columnName = "lesionSite", displayName = "Site of lesion", type = Column.Type.SSV)
    private String lesionSite;
    private String assessmentMethod;
    @Column(order = 7.5, columnName = "response", displayName = "Investigator non-target lesion visit response", type = Column.Type.SSV)
    private String response;
    private String responseShort;
    private Date baselineDate;

    @Column(order = 5, columnName = "assessmentMethod", displayName = "Method of assessment", type = Column.Type.SSV)
    public String getAssessmentMethod() {
        return NOT_IMPLEMENTED;
    }
}
