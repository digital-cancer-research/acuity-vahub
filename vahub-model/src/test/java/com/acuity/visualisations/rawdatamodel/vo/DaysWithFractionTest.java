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

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


public class DaysWithFractionTest {

    private final Date dateZero = DaysUtil.toDate("2015-05-11 13:00");

    private final Date dateBeforeZeroInTheSameDay = DaysUtil.toDate("2015-05-11 12:00");
    private final Date dateAfterZeroInTheSameDay = DaysUtil.toDate("2015-05-11 18:00");

    private final Date dateLessThanOneDayBeforeTruncatedZero = DaysUtil.toDate("2015-05-10 18:00");

    private final Date date1Day6HoursBeforeZero = DaysUtil.toDate("2015-05-09 18:00");
    private final Date date1Day18HoursBeforeZero = DaysUtil.toDate("2015-05-09 06:00");

    private final Date getDateMoreThanOneDayAfterZero = DaysUtil.toDate("2015-05-14 06:00");


    @Test
    public void shouldPlaceAllDatesInTheSameDateAsZeroIntoZeroDay() {
        assertThat(DaysWithFraction.from(dateZero, dateBeforeZeroInTheSameDay).toDouble()).isEqualTo(0.50000);
        assertThat(DaysWithFraction.from(dateZero, dateBeforeZeroInTheSameDay).toString()).isEqualTo("0d 12:00");
        assertThat(DaysWithFraction.from(dateZero, dateBeforeZeroInTheSameDay).toStringPlusOneDayIfPositive()).isEqualTo("1d 12:00");

        assertThat(DaysWithFraction.from(dateZero, dateAfterZeroInTheSameDay).toDouble()).isEqualTo(0.75000);
        assertThat(DaysWithFraction.from(dateZero, dateAfterZeroInTheSameDay).toString()).isEqualTo("0d 18:00");
        assertThat(DaysWithFraction.from(dateZero, dateAfterZeroInTheSameDay).toStringPlusOneDayIfPositive()).isEqualTo("1d 18:00");
    }

    @Test
    public void shouldPlaceDatesOnDatePreviousBeforeZeroIntoMinusOneDay() {
        assertThat(DaysWithFraction.from(dateZero, dateLessThanOneDayBeforeTruncatedZero).toDouble()).isEqualTo(-0.25000);
        assertThat(DaysWithFraction.from(dateZero, dateLessThanOneDayBeforeTruncatedZero).toString()).isEqualTo("-0d 06:00");
        assertThat(DaysWithFraction.from(dateZero, dateLessThanOneDayBeforeTruncatedZero).toStringPlusOneDayIfPositive()).isEqualTo("-0d 06:00");
    }

    @Test
    public void shouldKeepCorrectOrderAndTimeForDatesBeforeZero() {
        assertThat(DaysWithFraction.from(dateZero, date1Day6HoursBeforeZero).toDouble()).isEqualTo(-1.25000);
        assertThat(DaysWithFraction.from(dateZero, date1Day6HoursBeforeZero).toString()).isEqualTo("-1d 06:00");
        assertThat(DaysWithFraction.from(dateZero, date1Day6HoursBeforeZero).toStringPlusOneDayIfPositive()).isEqualTo("-1d 06:00");

        assertThat(DaysWithFraction.from(dateZero, date1Day18HoursBeforeZero).toDouble()).isEqualTo(-1.75000);
        assertThat(DaysWithFraction.from(dateZero, date1Day18HoursBeforeZero).toString()).isEqualTo("-1d 18:00");
        assertThat(DaysWithFraction.from(dateZero, date1Day18HoursBeforeZero).toStringPlusOneDayIfPositive()).isEqualTo("-1d 18:00");
    }

    @Test
    public void shouldPlaceDatesLongAfterZeroToCorrespondingPlaces() {
        assertThat(DaysWithFraction.from(dateZero, getDateMoreThanOneDayAfterZero).toDouble()).isEqualTo(3.25000);
        assertThat(DaysWithFraction.from(dateZero, getDateMoreThanOneDayAfterZero).toString()).isEqualTo("3d 06:00");
        assertThat(DaysWithFraction.from(dateZero, getDateMoreThanOneDayAfterZero).toStringPlusOneDayIfPositive()).isEqualTo("4d 06:00");
    }
}
