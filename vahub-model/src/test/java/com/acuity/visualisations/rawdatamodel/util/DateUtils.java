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

package com.acuity.visualisations.rawdatamodel.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by knml167 on 6/15/2017.
 */
public final class DateUtils {

    private DateUtils() {
    }

    private static String[] dateFormats = {
            "dd.MM.yyyy",
            "MM/dd/yyyy"
    };
    private static String[] dateTimeFormats = {
            "dd.MM.yyyy HH:mm",
            "dd.MM.yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm",
            "MM/dd/yyyy HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss"
    };

    public static Date toDate(String value) {
        for (String fmt : dateFormats) {
            DateFormat df = new SimpleDateFormat(fmt);
            df.setTimeZone(TimeZone.getTimeZone(DaysUtil.GMT_TIMEZONE));
            Date startDate;
            try {
                startDate = df.parse(value);
                return new Date(startDate.getTime());
            } catch (ParseException ignored) {
            }
        }
        throw new RuntimeException("Cannot parse date");

    }
    public static Date toDateTime(String value) {
        for (String fmt : dateTimeFormats) {
            DateFormat df = new SimpleDateFormat(fmt);
            df.setTimeZone(TimeZone.getTimeZone(DaysUtil.GMT_TIMEZONE));
            Date startDate;
            try {
                startDate = df.parse(value);
                return new Date(startDate.getTime());
            } catch (ParseException ignored) {
            }
        }
        throw new RuntimeException("Cannot parse date");

    }
}
