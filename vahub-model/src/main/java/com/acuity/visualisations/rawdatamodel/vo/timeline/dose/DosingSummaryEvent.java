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

package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DosingSummaryEvent extends EventInterval implements Serializable {

    private PercentChange percentChange = PercentChange.inactive();

    private PeriodType periodType;

    private PeriodType subsequentPeriodType;

    private List<DoseAndFrequency> drugDoses;

    @JsonIgnore
    public boolean isActive() {
        return (periodType == PeriodType.ACTIVE) || (periodType == null && percentChange != null && percentChange.isActive());
    }
}
