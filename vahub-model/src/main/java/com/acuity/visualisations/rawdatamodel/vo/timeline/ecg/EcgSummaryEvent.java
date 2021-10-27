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

package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class EcgSummaryEvent extends EcgEvent implements Serializable {

    private Double maxValuePercentChange;
    private String qtcfUnit;
    private Double qtcfValue;
    // change from baseline
    private Double qtcfChange;


    @Builder
    public EcgSummaryEvent(DateDayHour start, Double visitNumber, String abnormality, String significant,
                           Double maxValuePercentChange, String qtcfUnit, Double qtcfValue, Double qtcfChange) {
        super(start, visitNumber, abnormality, significant);
        this.maxValuePercentChange = maxValuePercentChange;
        this.qtcfChange = qtcfChange;
        this.qtcfValue = qtcfValue;
        this.qtcfUnit = qtcfUnit;
    }
}
