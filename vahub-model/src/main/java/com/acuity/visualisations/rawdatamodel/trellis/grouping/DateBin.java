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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

import java.util.Date;

@EqualsAndHashCode(callSuper = false, of = {"start", "end"})
public final class DateBin extends Bin<Date> {

    public static final Date DATE_ZERO = DaysUtil.truncLocalTime(DaysUtil.toDate("1970-01-01"));

    private DateBin(@NonNull Date start, @NonNull Date end) {
        this(
                DaysUtil.daysBetween(DATE_ZERO, DaysUtil.truncLocalTime(start)).getAsInt(),
                DaysUtil.daysBetween(DATE_ZERO, DaysUtil.truncLocalTime(end)).getAsInt());
    }
    private DateBin(@NonNull int start, @NonNull int end) {
        this.start = start;
        this.end = end;
        this.dateStart = DaysUtil.addDays(DATE_ZERO, start);
        this.dateEnd = DaysUtil.addDays(DATE_ZERO, end);
    }

    static Bin newInstance(Date date, Integer binSize) {
        if (binSize == null || binSize == 1) {
            return new DateBin(date, date);
        }
        Validate.isTrue(binSize != 0, "Bin size must not be 0");


        int valueDaysSince = DaysUtil.daysBetween(DATE_ZERO, date).orElse(0);

        int binStartDaysSince = (int) (Math.floor(valueDaysSince * 1.0 / binSize) * binSize);
        int binEndDaysSince = binStartDaysSince + binSize - 1;

        return new DateBin(DaysUtil.addDays(DATE_ZERO, binStartDaysSince),
                DaysUtil.addDays(DATE_ZERO, binEndDaysSince));
    }

    private int start;
    private int end;

    private Date dateStart;
    private Date dateEnd;

    @Override
    public Date getStart() {
        return dateStart;
    }

    @Override
    public Date getEnd() {
        return dateEnd;
    }

    @Getter(lazy = true)
    private final String oneArgString = DaysUtil.toString(getEnd());

    @Getter(lazy = true)
    private final String twoArgsString = String.format("%s - %s", DaysUtil.toString(getStart()), DaysUtil.toString(getEnd()));

    @Override
    public <B extends Bin<Date>> B getNextBin() {
        int nextStart = end + 1;
        int nextEnd = end + getSize();
        return (B) new DateBin(nextStart, nextEnd);
    }

    @Override
    public int getSize() {
        return Long.valueOf(end - start + (long) 1).intValue();
    }
}

