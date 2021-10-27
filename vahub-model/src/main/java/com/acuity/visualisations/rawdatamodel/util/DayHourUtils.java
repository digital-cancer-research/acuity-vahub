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

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.vo.DaysWithFraction;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;

import java.util.Date;
import java.util.Optional;

import static java.lang.Math.floor;

public final class DayHourUtils {

    private DayHourUtils() {
    }


    public static DateDayHour extractDaysHours(Subject subject,
                                               DayZeroType dayZeroType,
                                               String dayZeroOption,
                                               Date eventDate) {
        DaysWithFraction doseDays = DaysWithFraction.from(subject.getFirstTreatmentDate(), eventDate);
        DaysWithFraction days;
        switch (dayZeroType) {
            case DAYS_SINCE_FIRST_DOSE:
                days = doseDays;
                break;
            case DAYS_SINCE_STUDY_DAY:
                days = DaysWithFraction.from(DaysUtil.addDays(subject.getFirstTreatmentDate(), -1), eventDate);
                break;
            case DAYS_SINCE_RANDOMISATION:
                days = DaysWithFraction.from(subject.getDateOfRandomisation(), eventDate);
                break;
            case DAYS_SINCE_FIRST_TREATMENT:
                days = DaysWithFraction.from(subject.getDateOfFirstDoseOfDrug(dayZeroOption), eventDate);
                break;
            default:
                throw new IllegalArgumentException();
        }

        DateDayHour dateDayHour = new DateDayHour();
        dateDayHour.setDate(eventDate);
        if (days != null) {
            dateDayHour.setDayHour(days.toDouble());
            dateDayHour.setDayHourAsString(days.toString());
        }
        if (doseDays != null) {
            dateDayHour.setDoseDayHour(doseDays.toDouble());
            dateDayHour.setStudyDayHourAsString(doseDays.toStringPlusOneDayIfPositive());
        }
        return dateDayHour;
    }

    public static Integer getDuration(DateDayHour start, DateDayHour end) {
        if (isValid(start, end)) {
            return (int) (floor(end.getDayHour()) - floor(start.getDayHour())) + 1;
        } else {
            return null;
        }
    }

    public static boolean isValid(DateDayHour start, DateDayHour end) {
        return Optional.ofNullable(end).filter(DateDayHour::isValid).isPresent()
                && Optional.ofNullable(start).filter(DateDayHour::isValid).isPresent()
                && start.getDayHour() <= end.getDayHour();
    }
}
