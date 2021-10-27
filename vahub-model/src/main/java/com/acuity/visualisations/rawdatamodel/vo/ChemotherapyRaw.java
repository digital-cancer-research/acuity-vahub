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
import com.google.common.base.Strings;
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
@Builder
@AllArgsConstructor
@AcuityEntity(version = 8)
public class ChemotherapyRaw implements HasStringId, HasSubjectId, HasStartEndDate, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 1, displayName = "Start date", type = Column.Type.SSV)
    private Date startDate;
    @Column(order = 2, displayName = "End date", type = Column.Type.SSV)
    private Date endDate;
    private String preferredMed;
    private String timeStatus;
    @Column(order = 3, displayName = "Reason for therapy", type = Column.Type.SSV)
    private String therapyReason;
    @Column(order = 4, displayName = "Cancer therapy agent", type = Column.Type.SSV)
    private String agent;
    @Column(order = 5, displayName = "Therapy class", type = Column.Type.SSV)
    private String therapyClass;
    @Column(order = 6, displayName = "Treatment status", type = Column.Type.SSV)
    private String treatmentStatus;
    @Column(order = 7, displayName = "№ of cycles", type = Column.Type.SSV)
    private Integer numOfCycles;
    @Column(order = 8, displayName = "Route of administration", type = Column.Type.SSV)
    private String route;
    @Column(order = 9, displayName = "Best response", type = Column.Type.SSV)
    private String bestResponse;
    @Column(order = 10, displayName = "Reason for therapy failure", type = Column.Type.SSV)
    private String failureReason;

    public String getPreferredMedOrEmpty() {
        return Strings.isNullOrEmpty(preferredMed) ? "(empty)" : preferredMed;
    }
}
