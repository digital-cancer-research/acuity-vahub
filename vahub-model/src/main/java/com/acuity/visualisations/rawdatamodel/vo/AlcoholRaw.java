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
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 0)
public class AlcoholRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Substance category", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceCategory;
    @Column(order = 2, displayName = "Substance use occurrence", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceUseOccurrence;
    @Column(order = 3, displayName = "Type of substance", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceType;
    @Column(order = 4, displayName = "Other substance type specification", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String otherSubstanceTypeSpec;
    @Column(order = 5, displayName = "Substance type use occurrence", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceTypeUseOccurrence;
    @Column(order = 7, displayName = "Frequency", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String frequency;
    @Column(order = 6, displayName = "Substance consumption", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Double substanceConsumption;
    @Column(order = 8, columnName = "startDate", displayName = "Start date", type = Column.Type.DOD,
            datasetType = Column.DatasetType.ACUITY, defaultSortBy = true)
    private Date startDate;
    @Column(order = 9, displayName = "End date", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date endDate;

}
