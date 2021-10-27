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

package com.acuity.visualisations.rawdatamodel.service.timeline.common.vo;

import org.junit.Test;

import static com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DayHourConvertor.getDayHourAsString;
import static org.assertj.core.api.Assertions.assertThat;

public class WhenConvertingDayHoursToString {

    @Test
    public void shouldConvertDaysHoursCorrectly() {

        assertThat(getDayHourAsString(1.70069)).isEqualTo("1d 16:49");
        assertThat(getDayHourAsString(1.5)).isEqualTo("1d 12:00");
        assertThat(getDayHourAsString(1.5)).isEqualTo("1d 12:00");
        assertThat(getDayHourAsString(1.0)).isEqualTo("1d 00:00");
        assertThat(getDayHourAsString(null)).isNull();
        assertThat(getDayHourAsString(6.99)).isEqualTo("6d 23:45");
        assertThat(getDayHourAsString(2.25)).isEqualTo("2d 06:00");
        assertThat(getDayHourAsString(3.75)).isEqualTo("3d 18:00");
        assertThat(getDayHourAsString(3.15)).isEqualTo("3d 03:36");
        assertThat(getDayHourAsString(3.67)).isEqualTo("3d 16:04");
        assertThat(getDayHourAsString(1.99999)).isEqualTo("1d 23:59");
        assertThat(getDayHourAsString(0.4589)).isEqualTo("0d 11:00");
        assertThat(getDayHourAsString(17.99999)).isEqualTo("17d 23:59");
    }
}
