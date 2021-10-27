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

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 7)
public class RadiotherapyRaw implements HasStringId, HasSubjectId, HasStartEndDate, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 1, displayName = "Radiotherapy start date", type = Column.Type.SSV)
    private Date startDate;
    @Column(order = 2, displayName = "Radiotherapy end date", type = Column.Type.SSV)
    private Date endDate;
    private String visit;
    private Date visitDate;
    private String given;
    @Column(order = 3, displayName = "Site or region", type = Column.Type.SSV)
    private String siteOrRegion;
    @Column(order = 7, displayName = "Treatment status", type = Column.Type.SSV)
    private String treatmentStatus;
    @Column(order = 4, displayName = "Dose per fraction (Gy)", type = Column.Type.SSV)
    private Double dose;
    @Column(order = 5, displayName = "Number of fraction doses", type = Column.Type.SSV)
    private Integer numOfDoses;
    private String timeStatus;
    private String concomitantChemoRadiotherapy;

    @Column(order = 8, columnName = "concomitantChemoRadiotherapy", displayName = "Concomitant chemoradiotherapy", type = Column.Type.SSV)
    public String getConcomitantChemoRadiotherapy() {
        return NOT_IMPLEMENTED;
    }
}
