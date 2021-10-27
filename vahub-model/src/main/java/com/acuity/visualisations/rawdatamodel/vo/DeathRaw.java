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

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YMD;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 2)
public class DeathRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 2, displayName = "Reason", type = Column.Type.SSV)
    @Column(order = 1, displayName = "Cause of death", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String deathCause;
    @Column(order = 1, displayName = "Date", type = Column.Type.SSV, dateFormat = YMD)
    @Column(order = 2, columnName = "dateOfDeath", displayName = "Date of death", type = Column.Type.DOD,
            datasetType = Column.DatasetType.ACUITY, defaultSortBy = true)
    private Date dateOfDeath;
    // 3rd is in Death (wrapper)
    @Column(order = 4, displayName = "Autopsy performed", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String autopsyPerformed;
    @Column(order = 3, displayName = "Designation", type = Column.Type.SSV)
    @Column(order = 5, displayName = "Designation", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String designation;
    @Column(order = 6, displayName = "Death related to disease under investigation", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String diseaseUnderInvestigationDeath;
    @Column(order = 7, displayName = "MedDRA HLT", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String hlt;
    @Column(order = 8, displayName = "MedDRA LLT", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String llt;
    @Column(order = 9, displayName = "MedDRA PT", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String preferredTerm;
    @Column(order = 10, displayName = "MedDRA SOC", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String soc;
}
