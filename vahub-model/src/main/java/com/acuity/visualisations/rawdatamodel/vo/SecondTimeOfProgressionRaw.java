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

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 0)
public class SecondTimeOfProgressionRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 1, displayName = "Visit date", type = Column.Type.SSV)
    private Date visitDate;
    @Column(order = 2, displayName = "Assessement performed", type = Column.Type.SSV)
    private String assessmentPerformed;
    @Column(order = 3, displayName = "Reason assessment not performed", type = Column.Type.SSV)
    private String reason;
    @Column(order = 4, displayName = "Date of scan", type = Column.Type.SSV)
    private Date scanDate;
    @Column(order = 5, displayName = "Investigator asmt of patient response", type = Column.Type.SSV)
    private String investigatorAsmt;
    @Column(order = 6, displayName = "Type of progression", type = Column.Type.SSV)
    private String progressionType;
    @Column(order = 7, displayName = "Type of progression (meor)", type = Column.Type.SSV)
    private String progressionMeor;
    @Column(order = 8, displayName = "Other", type = Column.Type.SSV)
    private String other;
}
