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

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.lang.Math.round;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.apache.commons.lang3.time.DateUtils.truncate;

/**
 *
 * @author ksnd199
 */
public final class DayHourConvertor {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("HH:mm", TimeZone.getDefault(), Locale.ENGLISH);
    private static final int SECONDS_IN_DAY = 60 * 60 * 24; //86400

    private DayHourConvertor() {
    }

    /**
     * Returns a string format of []d.HH:mm.
     *
     * <code>
     * ie 1.5  would be 1d.12:00
     * 2.25  would be 2d.06:00
     * 4.75  would be 4d.18:00
     * </code>
     *
     * @param dayHour ie 1.5
     * @return a string format of []d.HH:mm
     */
    public static String getDayHourAsString(Double dayHour) {
        if (dayHour == null) {
            return null;
        }
        int integerPart = dayHour.intValue();
        return String.format("%dd %s", integerPart, getTimeAsString(dayHour));
    }

    public static String getDayAsString(Double dayHour) {
        if (dayHour == null) {
            return null;
        }
        int integerPart = dayHour.intValue();
        return String.format("%dd", integerPart);
    }

    /**
     * 86400 seconds in a day. Times that by the decimal part of the double (ie 1.5 = 0.5, 1.34 = 0.34) to get the number of seconds represented by the decimal.
     * Then create a new date, remove all hours, minutes, seconds, add the seconds previously generated and format it as HH:mm. Most probably an api somewhere
     * but couldnt find one.
     *
     * @param dayHour ie 1.5
     * @return hour part as string, 1.5 would be 12:00
     */
    private static String getTimeAsString(Double dayHour) {
        int integerPart = dayHour.intValue();
        double decimal = Math.abs(dayHour - integerPart);
        double seconds = (double) decimal * SECONDS_IN_DAY;
        return DATE_FORMAT.format(addSeconds(truncate(new Date(), Calendar.MONTH), (int) round(seconds)));
    }
}
