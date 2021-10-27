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

package com.acuity.visualisations.rawdatamodel.vo.timeline.aes;

import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class AeDetailEvent implements HasStartEndDate, Serializable {
    private String id;
    private Integer severityGradeNum;
    private String severityGrade;
    private String pt;
    private String serious;
    private String causality;
    private String actionTaken;
    private Integer duration;
    private DateDayHour start;
    private DateDayHour end;
    private boolean ongoing;
    private boolean imputedEndDate;
    private AeSeverityRaw.AeEndType endType;
    private String lastVisitNumber;

    @JsonIgnore
    private List<String> specialInterestGroups;

    @Override
    public Date getEndDate() {
        return end == null ? null : end.getDate();
    }

    @Override
    public Date getStartDate() {
        return start == null ? null : start.getDate();
    }
}
