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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.math3.util.Precision;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Date utils
 *
 * @author Glen
 */
public final class DaysUtil {

    public static final long MILLISECONDS_IN_DAY = (long) 24 * 60 * 60 * 1000;
    public static final long MILLISECONDS_IN_HOUR = (long) 60 * 60 * 1000;
    public static final String GMT_TIMEZONE = "GMT";
    public static final String JSON_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YMD = "yyyy-MM-dd";
    public static final String YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String YMD_T_HMS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DMMMY = "dd-MMM-yy";
    public static final String MMM_YY = "MMM-yy";
    public static final String YYYY = "yyyy";
    private static final FastDateFormat YMD_PARSER = FastDateFormat.getInstance(YMD, TimeZone.getTimeZone(GMT_TIMEZONE), Locale.ENGLISH);
    private static final FastDateFormat YMD_HM_PARSER = FastDateFormat.getInstance(YMD_HM, TimeZone.getTimeZone(GMT_TIMEZONE), Locale.ENGLISH);
    private static final FastDateFormat YMD_T_HMS_PARSER = FastDateFormat.getInstance(YMD_T_HMS, TimeZone.getTimeZone(GMT_TIMEZONE), Locale.ENGLISH);

    private DaysUtil() {

    }

    /**
     * Returns days between to dates
     *
     * @param dateFrom start date
     * @param dateTo   end date
     * @return number of days
     */
    public static OptionalInt daysBetween(Date dateFrom, Date dateTo) {
        if (dateFrom == null || dateTo == null) { // hack need to handle null end dates
            return OptionalInt.empty();
        }
        long timeFrom = truncLocalTime(dateFrom.getTime());
        long timeTo = truncLocalTime(dateTo.getTime());
        long durationMillis = timeTo - timeFrom;
        long daysBetween = TimeUnit.MILLISECONDS.toDays(durationMillis);
        return OptionalInt.of((int) daysBetween);
    }

    public static OptionalInt monthsBetween(Date dateFrom, Date dateTo) {
        if (dateFrom == null || dateTo == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of((int) ChronoUnit.MONTHS.between(LocalDateTime.ofInstant(dateFrom.toInstant(), ZoneId.systemDefault()).toLocalDate(),
                LocalDateTime.ofInstant(dateTo.toInstant(), ZoneId.systemDefault()).toLocalDate()));
    }

    /**
     * Truncates dateFrom and calculate number of days to dateTo, including dateTo's hours
     * For example, dateFrom is 01 Jan 2000 18:00 to 02 Jan 2000 12:00 will return 1.5
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public static OptionalDouble dayHoursSinceDate(Date dateFrom, Date dateTo) {
        long timeFrom = 0;
        return fromTo(dateFrom, dateTo, timeFrom);
    }

  private static long getLocalTime(long time) {
    return time % (MILLISECONDS_IN_DAY);
  }

    public static OptionalDouble dayHoursBetween(Date dateFrom, Date dateTo) {
        long timeFrom = getLocalTime(dateFrom.getTime());
        return fromTo(dateFrom, dateTo, timeFrom);
    }

    private static OptionalDouble fromTo(Date dateFrom, Date dateTo, long timeFrom) {
        if (dateFrom == null || dateTo == null) { // hack need to handle null end dates
            return OptionalDouble.empty();
        }
        long timeTo = getLocalTime(dateTo.getTime());
        return getDaysHours(dateFrom, dateTo, timeFrom, timeTo);
    }

    public static OptionalInt weeksBetween(Date dateFrom, Date dateTo) {
        final OptionalInt daysBetween = DaysUtil.daysBetween(dateFrom, dateTo);
        return daysBetween.isPresent() ? OptionalInt.of((int) Math.floor(
                daysBetween.getAsInt() / 7.0)) : OptionalInt.empty();
    }

    /**
     * Returns years between two dates.
     * If to date is missing, than current date will be used for calculations instead!
     *
     * @param from start date
     * @param to   end date
     * @return number of years
     */
    public static double yearsBetweenUsingCurrentDate(Date from, Date to) {
        return Precision.round(
            DaysUtil.daysBetween(from, Optional.ofNullable(to).orElse(new Date())).orElse(0) / 365.0,
            2);
    }

    /**
     * Truncates localate time to specified unit.
     * Pay your attention that math for dates before Jan. 1, 1970 and after is different.
     *
     * @param time time in millisec
     * @param unitInMilliSec unit (day, min or sec in millisec
     * @return truncated date in millisec
     */
    private static long truncTo(long time, long unitInMilliSec) {
        long modulo = time % unitInMilliSec;
        return modulo == 0 ? time : time < 0 ? time - (unitInMilliSec + modulo) : time - modulo;
    }

    /**
     * Truncates localate time to millisec.
     *
     * @param time time in millisec
     * @return truncated date in millisec
     */
    public static long truncLocalTime(long time) {
        return truncTo(time, MILLISECONDS_IN_DAY);
    }

    /**
     * Truncates localate time to hours.
     *
     * @param date date
     * @return truncated date
     */
    public static Date truncToHours(Date date) {
        return date == null ? null : new Date(truncTo(date.getTime(), MILLISECONDS_IN_HOUR));
    }

    /**
     * Truncates date
     *
     * @param time time in millisec
     * @return date only with millisec
     */
    public static Date truncLocalTime(Date time) {
        return new Date(truncTo(time.getTime(), MILLISECONDS_IN_DAY));
    }

    /**
     * Adds days to date
     *
     * @param date   date to add days
     * @param amount amount of days
     * @return date with added days
     */
    public static Date addDays(final Date date, final long amount) {
        if (date == null) {
            return null;
        }
        long time = date.getTime();
        time = time + (MILLISECONDS_IN_DAY * amount);
        return new Date(time);
    }

    /**
     * Converts string to date
     *
     * @param str string value for conversion
     * @return date
     */
    public static Date toDate(String str) {
        try {
            if (str.length() == YMD.length()) {
                return YMD_PARSER.parse(str);
            }
            if (str.length() == YMD_HM.length()) {
                return YMD_HM_PARSER.parse(str);
            }
            if (str.length() == YMD_T_HMS.length() - 2) {
                return YMD_T_HMS_PARSER.parse(str);
            }
            throw new IllegalArgumentException("No parser defined for <" + str + ">.");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing <" + str + ">.", e);
        }
    }

    public static Date toDate(String date, String format) {
        try {
            return FastDateFormat.getInstance(format, TimeZone.getTimeZone(GMT_TIMEZONE), Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing <" + date + ">.", e);
        }
    }

    /**
     * Converts date to string
     *
     * @param date date to convert
     * @return string
     */
    public static String toString(Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(GMT_TIMEZONE));
        return simpleDateFormat.format(date);
    }

    public static String toDisplayString(Date date) {
        if (date == null) {
            return "";
        }
        return toString(date);
    }

    public static String toDateTimeString(Date date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD_T_HMS);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(GMT_TIMEZONE));
        return simpleDateFormat.format(date);
    }

    /**
     * Method checks if periods overlapped with a shift.
     * Shift meaning: 11.01.2018-13.01.2018 and 15.01.2018-17.01.2018 are literally not overlapped, but the are with shift 2 and more
     *
     * @param start1 can not be NULL
     * @param end1   can be NULL - means now finished yet
     * @param start2 can not be NULL
     * @param end2   can be NULL - means now finished yet
     * @param shift  maximum number of days between end of the first event and start of the second that allows
     *               to consider events as overlapped. To count only exact overlapping use shift = 0.
     */
    public static boolean periodsOverlappedWithShift(Date start1, Date end1, Date start2, Date end2, int shift) {

        final MutablePair<Date, Date> event1 = MutablePair.of(start1, end1);
        final MutablePair<Date, Date> event2 = MutablePair.of(start2, end2);
        sortEventsByStartDate(event1, event2);
        Date startFirst = event1.getLeft();
        Date endFirst = event1.getRight();
        Date startLast = event2.getLeft();
        Date endLast = event2.getRight();

        boolean overlapped = periodsOverlapped(startFirst, endFirst, startLast, endLast);
        if (!overlapped) {
            overlapped = daysBetween(endFirst, startLast).orElseThrow(() -> new IllegalStateException("Impossible")) <= shift;
        }
        return overlapped;
    }

    /**
     * Method checks if periods are overlapped.
     *
     * @param startDate1 can be NULL - means that start date is unknown
     * @param endDate1   can be NULL - means that period is not finished yet
     * @param startDate2 can be NULL - means that start date is unknown
     * @param endDate2   can be NULL - means that period is not finished yet
     */
    public static boolean periodsOverlapped(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {

        final MutablePair<Date, Date> event1 = MutablePair.of(startDate1, endDate1);
        final MutablePair<Date, Date> event2 = MutablePair.of(startDate2, endDate2);
        sortEventsByStartDate(event1, event2);
        Date endFirst = event1.getRight();
        Date startLast = event2.getLeft();
        return endFirst == null || startLast == null || !endFirst.before(startLast);
    }

    /**
     * Swap events if event1 starts after event2. null is considered before any date value.
     * This method mutates input parameters
     *
     * @param event1 - MutablePair that contains start and end date of the first event
     * @param event2 - MutablePair that contains start and end date of the second event
     */
    public static void sortEventsByStartDate(MutablePair<Date, Date> event1, MutablePair<Date, Date> event2) {
        Date start1 = event1.getLeft();
        Date end1 = event1.getRight();
        Date start2 = event2.getLeft();
        Date end2 = event2.getRight();

        boolean needSwap = (start1 != null && start2 == null) || start1 != null && start1.after(start2);
        if (needSwap) {
            event1.setLeft(start2);
            event1.setRight(end2);
            event2.setLeft(start1);
            event2.setRight(end1);
        }
    }

    /**
     * Gets min date
     *
     * @param date1 first date
     * @param date2 second date
     * @return min date
     */
    public static Date getMinDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        return date1.compareTo(date2) > 0 ? date2 : date1;
    }

    /**
     * Gets max date
     *
     * @param date1 first date
     * @param date2 second date
     * @return max date
     */
    public static Date getMaxDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null || date2 == null) {
            return date1 == null ? date2 : date1;
        } else {
            return date1.compareTo(date2) > 0 ? date1 : date2;
        }
    }

  private static OptionalDouble getDaysHours(Date dateFrom, Date dateTo, long timeFrom, long timeTo) {
    long timeDurationMillis = timeTo - timeFrom;
    int daysBetween = daysBetween(dateFrom, dateTo).orElse(0);
    // need 5 decimal digits precision to handle time up to minutes correctly. It's faster than BigDecimal.
    // the code below is the same as Math.round(timeDurationMillis * 100000.0 / 86400000) / 100000.0
    // 86400000 is number of milliseconds in a day
    return OptionalDouble.of(daysBetween + Math.round(timeDurationMillis / 864.) / 100000.0);
  }

    // Extracted from ExacerbationsTimelineService
    // and now we apply that weird logic to round end date for Detect... 00:00:00 and 23:59:59 are considered as next midnight.
    public static Date adjustEndDate(Date date) {
        if (date == null) {
            return null;
        }
        if (date.getTime() == DaysUtil.truncLocalTime(date).getTime()) {
            date = DaysUtil.addDays(date, 1);
        } else {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            if (calendar.get(Calendar.HOUR) == 23
                && calendar.get(Calendar.MINUTE) == 59
                && calendar.get(Calendar.SECOND) == 59) {
                date = DaysUtil.addDays(date, 1);
            }
        }
        return date;
    }

    public static Date getGreatestDate(Date... dates) {
        return Stream.of(dates).filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(Date.from(Instant.EPOCH));
    }

    /**
     * Converts date to string using format
     *
     * @param date       date for conversion
     * @param dateFormat dateFormat that is used for conversion
     * @return string value
     */
    public static String toString(Date date, String dateFormat) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(GMT_TIMEZONE));
        return simpleDateFormat.format(date);
    }

    public static Date truncNullableDate(Date date) {
        return date == null ? null : truncLocalTime(date);
    }
}
