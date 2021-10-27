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

package com.acuity.visualisations.rawdatamodel.vo.timeline;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static java.lang.Math.floor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInterval implements Serializable {
    private DateDayHour start;
    private DateDayHour end;
    private boolean ongoing;
    private boolean imputedEndDate;

    /**
     * Duration of days of the event.
     * <p>
     * Monday to Monday, is duration of 1.
     * Monday to Tuesday, is duration of 2.
     * Monday to Wednesday, is duration of 3.
     * <p>
     * Hence,  7.2 - 7.5 = 7 - 7 + 1 = 1
     * Hence,  7.2 - 8.5 = 8 - 7 + 1 = 2
     * Hence,  7.2 - 9.9 = 9 - 7 + 1 = 3
     * Hence,  9.001 - 9.99999 = 9 - 9 + 1 = 1
     *
     * @return
     */
    public Integer getDuration() {

        if (end != null && end.getDayHour() != null
                && start != null && start.getDayHour() != null
                && (start.getDayHour() <= end.getDayHour())) {
            return (int) (floor(end.getDayHour()) - floor(start.getDayHour())) + 1;
        } else {
            return null;
        }
    }
}
