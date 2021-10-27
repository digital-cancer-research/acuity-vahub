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
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@EqualsAndHashCode()
@ToString(callSuper = true)
@Builder
public class AeMaxCtcEvent implements Serializable {

    private int maxSeverityGradeNum;
    private String maxSeverityGrade;
    private int numberOfEvents;
    private boolean ongoing;
    private boolean imputedEndDate;
    private Integer duration;
    private DateDayHour start;
    private DateDayHour end;
    private AeSeverityRaw.AeEndType endType;
    private String lastVisitNumber;

    private Set<String> pts;
}
