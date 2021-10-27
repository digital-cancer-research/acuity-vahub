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

package com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.JSON_TIMESTAMP_FORMAT;

@Data
@NoArgsConstructor
public class DateDayHour implements Comparable<DateDayHour>, Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = JSON_TIMESTAMP_FORMAT, timezone = DaysUtil.GMT_TIMEZONE)
    private Date date;
    protected Double dayHour;
    protected Double doseDayHour;

    private String dayHourAsString;
    private String studyDayHourAsString;

    public DateDayHour(Date date, Double dayHour) {
        this.date = date;
        this.dayHour = dayHour;
    }

    @JsonIgnore
    public boolean isValid() {
        return (date != null && dayHour != null);
    }

    @Override
    public int compareTo(DateDayHour o) {
        return date.compareTo(o.date);
    }
}
