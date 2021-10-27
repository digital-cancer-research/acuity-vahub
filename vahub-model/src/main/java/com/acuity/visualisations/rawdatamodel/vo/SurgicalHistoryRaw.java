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

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 4)
public class SurgicalHistoryRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Preferred term", type = Column.Type.SSV)
    @Column(order = 4, displayName = "PT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String preferredTerm;

    @Column(order = 2, displayName = "Surgical procedure", type = Column.Type.SSV)
    @Column(order = 1, displayName = "Medical history term", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String surgicalProcedure;

    @Column(order = 2, displayName = "Current medication", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String currentMedication;

    @Column(order = 3, displayName = "Date of surgery", type = Column.Type.SSV)
    @Column(order = 3,
            displayName = "Start date",
            defaultSortBy = true,
            defaultSortOrder = 1,
            type = Column.Type.DOD,
            datasetType = Column.DatasetType.ACUITY)
    private Date start;

    private String pt;

    @Column(order = 5, displayName = "HLT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String hlt;

    @Column(order = 6, displayName = "SOC name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String soc;
}
