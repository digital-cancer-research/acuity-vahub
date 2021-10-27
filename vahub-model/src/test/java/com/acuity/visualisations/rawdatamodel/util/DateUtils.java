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
