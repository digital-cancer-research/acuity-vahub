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

package com.acuity.visualisations.rawdatamodel.axes;

import lombok.Getter;

public enum CountType {
    COUNT_OF_SUBJECTS(CountBase.SUBJECT),
    COUNT_OF_EVENTS(CountBase.EVENT),
    PERCENTAGE_OF_ALL_SUBJECTS(CountBase.SUBJECT),
    PERCENTAGE_OF_ALL_EVENTS(CountBase.EVENT),
    PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT(CountBase.SUBJECT),
    PERCENTAGE_OF_EVENTS_WITHIN_PLOT(CountBase.EVENT),
    PERCENTAGE_OF_EVENTS_100_STACKED(CountBase.EVENT),
    PERCENTAGE_OF_SUBJECTS_100_STACKED(CountBase.SUBJECT),
    PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED(CountBase.SUBJECT),
    CUMULATIVE_COUNT_OF_SUBJECTS(CountBase.SUBJECT),
    CUMULATIVE_COUNT_OF_EVENTS(CountBase.EVENT);

    @Getter
    private final CountBase countBase;

    CountType(CountBase countBase) {
        this.countBase = countBase;
    }

    public boolean isCumulativeType() {
        return this == CUMULATIVE_COUNT_OF_EVENTS || this == CUMULATIVE_COUNT_OF_SUBJECTS;
    }

    public enum CountBase {
        SUBJECT,
        EVENT
    }
}
