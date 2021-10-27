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

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 2)
public class PathologyRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Original diagnosis date", type = Column.Type.SSV)
    private Date date;
    @Column(order = 3, displayName = "Histology type", type = Column.Type.SSV)
    private String hisType;
    private String hisTypeDetails;
    @Column(order = 7, displayName = "Tumour grade", type = Column.Type.SSV)
    private String tumourGrade;
    @Column(order = 10, displayName = "Stage classification", type = Column.Type.SSV)
    private String stage;
    @Column(order = 5, displayName = "Primary tumour location", type = Column.Type.SSV)
    private String tumourLocation;
    @Column(order = 6, displayName = "Primary tumour", type = Column.Type.SSV)
    private String primTumour;
    @Column(order = 8, displayName = "Regional lymph node", type = Column.Type.SSV)
    private String nodesStatus;
    @Column(order = 9, displayName = "Distant metastases", type = Column.Type.SSV)
    private String metastasesStatus;
    @Column(order = 2, displayName = "Method of diagnosis", type = Column.Type.SSV)
    private String determMethod;
    private String otherMethods;
    private String tumourType;

    @Column(order = 4, columnName = "tumourType", displayName = "Primary tumour type", type = Column.Type.SSV)
    public String getTumourType() {
        return NOT_IMPLEMENTED;
    }
}
