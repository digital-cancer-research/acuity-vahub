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

package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Status summary of lung function (visits) for a subject, which consists of a list of LungFunctionSummaryEvent.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SubjectLungFunctionSummary extends SubjectSummary implements Serializable {
    private String sex;
    private DateDayHour baseline;

    private List<LungFunctionSummaryEvent> events;

    @Builder
    private SubjectLungFunctionSummary(String subjectId, String subject,
                                       List<LungFunctionSummaryEvent> events,
                                       String sex,
                                       DateDayHour baseline) {
        super(subjectId, subject);
        this.events = events;
        this.sex = sex;
        this.baseline = baseline;
    }
}
