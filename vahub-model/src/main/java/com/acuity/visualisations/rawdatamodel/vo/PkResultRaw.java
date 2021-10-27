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
@Builder(toBuilder = true)
@AcuityEntity(version = 9)
public class PkResultRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;
    @Column(columnName = "parameter", order = 5, displayName = "Parameter")
    private String parameter;
    @Column(columnName = "parameterValue", order = 6, displayName = "Value")
    private Double parameterValue;
    @Column(columnName = "parameterUnit", order = 7, displayName = "Unit")
    private String parameterUnit;
    @Column(columnName = "analyte", order = 1, displayName = "Analyte")
    private String analyte;
    private Date visitDate;
    private Integer visitNumber;
    @Column(columnName = "treatment", order = 8, displayName = "Nominal dose")
    private Double treatment;
    @Column(columnName = "cycle", order = 2, displayName = "Cycle")
    private String treatmentCycle;
    @Column(columnName = "protocolScheduleStartDay", order = 3, displayName = "Nominal day")
    private String protocolScheduleStartDay;
    @Column(columnName = "visit", order = 4, displayName = "Visit")
    private String visit;
    @Column(columnName = "actualDose", order = 9, displayName = "Actual administered dose")
    private Double actualDose;
    private String bestOverallResponse;
}
