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
import com.acuity.visualisations.rawdatamodel.vo.exposure.Cycle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 10)
public class ExposureRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(columnName = "analyteConcentration", order = 9, displayName = "Analyte concentration")
    private Double analyteConcentration;
    @Column(columnName = "analyteUnit", order = 10, displayName = "Analyte concentration unit")
    private String analyteUnit;
    @Column(columnName = "nominalDose", order = 5, displayName = "Nominal dose")
    private String treatment;
    private Double timeFromAdministration;
    private Integer protocolScheduleDay;
    private Cycle cycle = new Cycle();
    @Column(columnName = "analyte", order = 1, displayName = "Analyte")
    private String analyte;
    @Column(columnName = "visitNumber", order = 2, displayName = "Visit number")
    private Integer visitNumber;
    @Column(columnName = "visitDate", order = 3, displayName = "Visit date")
    private Date visitDate;
    @Column(columnName = "nominalDay", order = 6, displayName = "Nominal day")
    private Integer nominalDay;
    @Column(columnName = "nominalHour", order = 7, displayName = "Nominal hour")
    private Double nominalHour;
    @Column(columnName = "nominalMinute", order = 8, displayName = "Nominal minute")
    private Integer nominalMinute;
    @Column(columnName = "cycle", order = 4, displayName = "Cycle")
    private String treatmentCycle;
    @Column(columnName = "LLOQ", order = 11, displayName = "LLOQ")
    private Double lowerLimit;
    @Column(columnName = "actualSamplingDate", order = 12, displayName = "Actual sampling date")
    private Date actualSamplingDate;
    private Date drugAdministrationDate;
}
