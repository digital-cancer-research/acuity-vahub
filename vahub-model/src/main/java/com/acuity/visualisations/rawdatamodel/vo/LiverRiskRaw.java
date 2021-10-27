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
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@AcuityEntity(version = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiverRiskRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 2, displayName = "Liver risk factor",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String value;

    @Column(order = 3, displayName = "Liver risk factor occurrence",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String occurrence;

    @Column(order = 4, displayName = "Liver risk factor reference period",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String referencePeriod;

    @Column(order = 5, displayName = "Liver risk factor details",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String details;

    @Column(order = 6, displayName = "Start date", columnName = "startDate", defaultSortBy = true,
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date startDate;

    @Column(order = 7, displayName = "Stop date", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date stopDate;

    @Column(order = 10, displayName = "Liver risk factor comment",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String comment;

    @Column(order = 1, displayName = "Potential Hy's law case number",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Integer potentialHysLawCaseNum;
}
