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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EcgDetailEvent extends EcgEvent implements Serializable {
    private Double baselineValue;
    private Double valueRaw;
    private String unitRaw;
    private Double valueChangeFromBaseline;
    private String unitChangeFromBaseline;
    private Double valuePercentChangeFromBaseline;
    private String unitPercentChangeFromBaseline;
    private boolean baselineFlag;

    @Builder
    public EcgDetailEvent(DateDayHour start, Double visitNumber, String abnormality, String significant,
                          Double baselineValue, Double valueRaw, String unitRaw, Double valueChangeFromBaseline,
                          String unitChangeFromBaseline, Double valuePercentChangeFromBaseline,
                          String unitPercentChangeFromBaseline, boolean baselineFlag) {
        super(start, visitNumber, abnormality, significant);
        this.baselineValue = baselineValue;
        this.valueRaw = valueRaw;
        this.unitRaw = unitRaw;
        this.valueChangeFromBaseline = valueChangeFromBaseline;
        this.unitChangeFromBaseline = unitChangeFromBaseline;
        this.valuePercentChangeFromBaseline = valuePercentChangeFromBaseline;
        this.unitPercentChangeFromBaseline = unitPercentChangeFromBaseline;
        this.baselineFlag = baselineFlag;
    }
}
