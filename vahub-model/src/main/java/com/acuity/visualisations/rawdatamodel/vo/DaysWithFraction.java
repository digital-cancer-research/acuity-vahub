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

package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Precision;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@AllArgsConstructor
public class DaysWithFraction {

    private final Duration duration;

    private static final ZoneId GMT_ZONE = ZoneId.of("GMT");
    private static final int SECONDS_IN_DAY = 60 * 60 * 24;

    private static final String DURATION_FORMAT = "%s%dd %02d:%02d";

    public static DaysWithFraction from(Date zeroDate, Date eventDate) {
        if (zeroDate == null || eventDate == null) {
            return null;
        }

        ZonedDateTime zeroTemporalTruncated = zeroDate.toInstant().atZone(GMT_ZONE).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime eventTemporal = eventDate.toInstant().atZone(GMT_ZONE);

        Duration duration = Duration.between(zeroTemporalTruncated, eventTemporal);
        return new DaysWithFraction(duration);
    }

    /**
     * @return interval in double format
     */
    public Double toDouble() {
        return Precision.round((double) duration.getSeconds() / SECONDS_IN_DAY, 5);
    }

    /**
     * @return interval in "Nd HH:mm" format
     */
    public String toString() {
        return composeStringPresentation(false);
    }

    public String toStringPlusOneDayIfPositive() {
        return composeStringPresentation(true);
    }

    private String composeStringPresentation(boolean incrementIfPositive) {
        Duration betweenAbs = duration.abs();

        return String.format(DURATION_FORMAT,
                duration.isNegative() ? "-" : "",
                incrementIfPositive && !duration.isNegative()
                        ? betweenAbs.toDays() + 1
                        : betweenAbs.toDays(),
                betweenAbs.toHours() % 24,
                betweenAbs.toMinutes() % 60);
    }
}
