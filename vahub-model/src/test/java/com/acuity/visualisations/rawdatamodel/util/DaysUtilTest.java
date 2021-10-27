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

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

import java.util.Date;

import static java.lang.Math.round;
import static org.assertj.core.api.Assertions.assertThat;

public class DaysUtilTest {

    private static final Date DATE_1 = DaysUtil.toDate("2011-11-02");
    private static final Date DATE_2 = DaysUtil.toDate("2011-12-24");
    private static final Date DATE_3 = DaysUtil.toDate("2011-12-29");
    private static final Date DATE_4 = DaysUtil.toDate("2012-01-13");

    @Test
    public void testDaysBetweenTwoSameDatesAndTime() {
        Date date1 = DaysUtil.toDate("1970-01-05 00:00");
        Date date2 = DaysUtil.toDate("1970-01-05 00:00");
        int daysBetween = DaysUtil.daysBetween(date1, date2).getAsInt();
        assertThat(daysBetween).isEqualTo(0);
    }
    
    @Test
    public void testDaysBetweenTwoDates() {
        Date date1 = DaysUtil.toDate("1970-01-05 04:00");
        Date date2 = DaysUtil.toDate("1970-01-08 00:00");
        int daysBetween = DaysUtil.daysBetween(date1, date2).orElse(0);
        assertThat(daysBetween).isEqualTo(3);
        int daysBetweenNeg = DaysUtil.daysBetween(date2, date1).orElse(0);
        assertThat(daysBetweenNeg).isEqualTo(-3);
    }

    @Test
    public void testWeeksBetween() {

        int weeksBetween = DaysUtil.weeksBetween(DATE_1, DATE_2).orElse(0);
        assertThat(weeksBetween).isEqualTo(7);
    }

    @Test
    public void testWeeksBetweenNegative() {

        int weeksBetween = DaysUtil.weeksBetween(DATE_2, DATE_1).orElse(0);
        assertThat(weeksBetween).isEqualTo(-8);
    }

    @Test
    public void testWeeksBetweenTimeIsTruncated() {

        Date dateTime1 = DaysUtil.toDate("2011-11-02 23:00");
        Date dateTime2 = DaysUtil.toDate("2011-11-09 09:00");

        int weeksBetween = DaysUtil.weeksBetween(dateTime1, dateTime2).orElse(0);
        assertThat(weeksBetween).isEqualTo(1);
    }

    @Test
    public void testWeeksBetweenSameDateIsZero() {

        Date dateTime1 = DaysUtil.toDate("2011-11-02 08:00");
        Date dateTime2 = DaysUtil.toDate("2011-11-02 09:00");

        int weeksBetween = DaysUtil.weeksBetween(dateTime1, dateTime2).orElse(0);
        assertThat(weeksBetween).isEqualTo(0);
    }

    @Test
    public void testWeeksBetweenWithDaylightSavings() {

        Date dateTime1 = DaysUtil.toDate("2014-12-31 11:00");
        Date dateTime2 = DaysUtil.toDate("2015-06-17 10:59");

        int weeksBetween = DaysUtil.weeksBetween(dateTime1, dateTime2).orElse(0);
        assertThat(weeksBetween).isEqualTo(24);
    }

    @Test
    public void testAddDays() {

        Date dateTime = DaysUtil.toDate("2014-12-31 11:00");

        final Date nextDay = DaysUtil.addDays(dateTime, 1);
        assertThat(nextDay).isEqualTo(DaysUtil.toDate("2015-01-01 11:00"));
    }


    @Test
    public void testTruncTime() {

        Date dateTime = DaysUtil.toDate("2014-12-31 11:00");

        final Date nextDay = DaysUtil.truncLocalTime(dateTime);
        assertThat(nextDay).isEqualTo(DaysUtil.toDate("2014-12-31 00:00"));
    }


    @Test
    public void testPeriodsOverlapped() {

        assertThat(DaysUtil.periodsOverlapped(DATE_1, DATE_2, DATE_3, DATE_4)).isFalse();
        assertThat(DaysUtil.periodsOverlapped(DATE_1, DATE_3, DATE_2, DATE_4)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(DATE_1, DATE_4, DATE_2, DATE_3)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(null, DATE_3, DATE_2, DATE_4)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(null, DATE_2, DATE_3, DATE_4)).isFalse();
        assertThat(DaysUtil.periodsOverlapped(DATE_3, DATE_4, null, DATE_2)).isFalse();
        assertThat(DaysUtil.periodsOverlapped(null, DATE_1, null, DATE_2)).isTrue();

        assertThat(DaysUtil.periodsOverlapped(DATE_1, null, DATE_2, DATE_3)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(DATE_1, DATE_2, DATE_3, null)).isFalse();
        assertThat(DaysUtil.periodsOverlapped(null, DATE_2, null, DATE_3)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(DATE_1, null, DATE_2, null)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(null, null, null, null)).isTrue();
        assertThat(DaysUtil.periodsOverlapped(DATE_1, DATE_2, DATE_2, DATE_3)).isTrue();
    }

    @Test
    public void testPeriodsOverlappedWithShift() {

        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_1, DATE_2, DATE_3, DATE_4, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_1, DATE_2, DATE_3, DATE_4, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, DATE_4, DATE_1, DATE_2, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, DATE_4, DATE_1, DATE_2, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(null, DATE_2, DATE_3, DATE_4, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(null, DATE_2, DATE_3, DATE_4, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, DATE_4, null, DATE_2, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, DATE_4, null, DATE_2, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_1, DATE_2, DATE_3, null, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_1, DATE_2, DATE_3, null, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, null, DATE_1, DATE_2, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(DATE_3, null, DATE_1, DATE_2, 5)).isTrue();

        assertThat(DaysUtil.periodsOverlappedWithShift(null, DATE_2, DATE_3, null, 4)).isFalse();
        assertThat(DaysUtil.periodsOverlappedWithShift(null, DATE_2, DATE_3, null, 5)).isTrue();
    }

    @Test
    public void testSortEventsByStartDateOrdered() {
        final MutablePair<Date, Date> event1 = MutablePair.of(DATE_1, DATE_2);
        final MutablePair<Date, Date> event2 = MutablePair.of(DATE_3, DATE_4);
        testSort(event1, event2);
    }

    @Test
    public void testSortEventsByStartDateNotOrdered() {
        final MutablePair<Date, Date> event1 = MutablePair.of(DATE_3, DATE_4);
        final MutablePair<Date, Date> event2 = MutablePair.of(DATE_1, DATE_2);
        testSort(event1, event2);
    }

    private void testSort(MutablePair<Date, Date> event1, MutablePair<Date, Date> event2) {
        DaysUtil.sortEventsByStartDate(event1, event2);
        Date startFirst = event1.getLeft();
        Date startLast = event2.getLeft();
        assertThat(startFirst.before(startLast));
        assertThat(event1.getLeft().equals(DATE_1));
        assertThat(event1.getRight().equals(DATE_2));
        assertThat(event2.getLeft().equals(DATE_3));
        assertThat(event2.getRight().equals(DATE_4));
    }

    @Test
    public void testSortEventsByStartDateOrderedNullStart() {
        final MutablePair<Date, Date> event1 = MutablePair.of(null, DATE_2);
        final MutablePair<Date, Date> event2 = MutablePair.of(DATE_3, DATE_4);
        DaysUtil.sortEventsByStartDate(event1, event2);
        Date startFirst = event1.getLeft();
        assertThat(event1.getLeft()).isEqualTo(null);
        assertThat(event1.getRight().equals(DATE_2));
        assertThat(event2.getLeft().equals(DATE_3));
        assertThat(event2.getRight().equals(DATE_4));
    }

    @Test
    public void testSortEventsByStartDateNotOrderedNullStart() {
        final MutablePair<Date, Date> event1 = MutablePair.of(DATE_3, DATE_4);
        final MutablePair<Date, Date> event2 = MutablePair.of(null, DATE_2);
        DaysUtil.sortEventsByStartDate(event1, event2);
        assertThat(event1.getLeft()).isEqualTo(null);
        assertThat(event1.getRight().equals(DATE_2));
        assertThat(event2.getLeft().equals(DATE_3));
        assertThat(event2.getRight().equals(DATE_4));
    }



    @Test
    public void testGetMinDate() {
        assertThat(DaysUtil.getMinDate(DATE_1, DATE_2)).isEqualTo(DATE_1);
        assertThat(DaysUtil.getMinDate(DATE_2, DATE_1)).isEqualTo(DATE_1);
    }

    @Test
    public void testDayHoursBetweenSameDates() {
        Date date1 = DaysUtil.toDate("1970-01-05 00:00");
        Date date2 = DaysUtil.toDate("1970-01-05 03:00");
        double dayHoursBetween = DaysUtil.dayHoursBetween(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(0.125);
    }

    @Test
    public void testDayHoursBetweenNextDay() {
        Date date1 = DaysUtil.toDate("1970-01-05 15:00");
        Date date2 = DaysUtil.toDate("1970-01-06 00:00");
        double dayHoursBetween = DaysUtil.dayHoursBetween(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(0.375);
    }

    @Test
    public void testDayHoursBetweenNextDay2() {
        Date date1 = DaysUtil.toDate("1970-01-05 15:00");
        Date date2 = DaysUtil.toDate("1970-01-07");
        double dayHoursBetween = DaysUtil.dayHoursBetween(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(1.375);
    }

    @Test
    public void testDayHoursBetweenOneDay() {
        Date date1 = DaysUtil.toDate("1970-01-05");
        Date date2 = DaysUtil.toDate("1970-01-06");
        double dayHoursBetween = DaysUtil.dayHoursBetween(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(1.0);
    }

    @Test
    public void testDayHoursNegative() {
        Date date1 = DaysUtil.toDate("1970-01-05 06:00");
        Date date2 = DaysUtil.toDate("1970-01-04");
        double dayHoursBetween = DaysUtil.dayHoursBetween(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(-1.25);
    }

    @Test
    public void testDayHoursSinceDateNextDay() {
        Date date1 = DaysUtil.toDate("1970-01-05 15:00");
        Date date2 = DaysUtil.toDate("1970-01-06 06:00");
        double dayHoursBetween = DaysUtil.dayHoursSinceDate(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(1.25);
    }

    @Test
    public void testDayHoursSinceDateNextDayTestPrecision() {
        Date date1 = DaysUtil.toDate("1970-01-05 15:00");

        for (int i = 0; i < 60; i++) {
            String min = i < 10 ? "0" + i : "" + i;
            Date date2 = DaysUtil.toDate("1970-01-06 06:" + min);
            double dayHoursBetween = DaysUtil.dayHoursSinceDate(date1, date2).getAsDouble();
            int minutes = getMinutes(dayHoursBetween);
            assertThat(minutes).isEqualTo(i);
        }
    }

    private int getMinutes(double dayHoursBetween) {
        long seconds = round((dayHoursBetween - (int)dayHoursBetween) * 86400);
        long hours = seconds / 3600;
        return (int) (seconds - hours * 3600) / 60;
    }

    @Test
    public void testDayHoursSinceDateNegative() {
        Date date1 = DaysUtil.toDate("1970-01-05 15:00");
        Date date2 = DaysUtil.toDate("1970-01-04 06:00");
        double dayHoursBetween = DaysUtil.dayHoursSinceDate(date1, date2).getAsDouble();
        assertThat(dayHoursBetween).isEqualTo(-0.75);
    }
}
