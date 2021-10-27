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

package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;

public class CardiacFilterSummaryStatistics implements FilterSummaryStatistics<Cardiac> {

    private CardiacFilters cardiacFilters = new CardiacFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Cardiac cardiac = (Cardiac) value;

        cardiacFilters.getMeasurementName().completeWithValue(cardiac.getEvent().getMeasurementName());
        cardiacFilters.getMeasurementCategory().completeWithValue(cardiac.getEvent().getMeasurementCategory());
        cardiacFilters.getResultValue().completeWithValue(cardiac.getEvent().getResultValue());
        cardiacFilters.getResultUnit().completeWithValue(cardiac.getEvent().getResultUnit());
        cardiacFilters.getClinicallySignificant().completeWithValue(cardiac.getEvent().getClinicallySignificant());
        cardiacFilters.getMeasurementTimePoint().completeWithValue(cardiac.getAccessibleMeasurementTimePoint());
        cardiacFilters.getDaysOnStudy().completeWithValue(cardiac.getDaysSinceFirstDose());
        cardiacFilters.getStudyPeriods().completeWithValue(cardiac.getEvent().getStudyPeriods());
        cardiacFilters.getAnalysisVisit().completeWithValue(cardiac.getEvent().getAnalysisVisit());
        cardiacFilters.getVisitNumber().completeWithValue(cardiac.getEvent().getVisitNumber());
        cardiacFilters.getBaselineValue().completeWithValue(cardiac.getEvent().getBaselineValue());
        cardiacFilters.getBaselineFlag().completeWithValue(cardiac.getEvent().getBaselineFlag());
        cardiacFilters.getChangeFromBaselineValue().completeWithValue(cardiac.getChangeFromBaseline());
        cardiacFilters.getPercentageChangeFromBaselineValue().completeWithValue(cardiac.getPercentChangeFromBaseline());

        cardiacFilters.getBeatGroupNumber().completeWithValue(cardiac.getEvent().getBeatGroupNumber());
        cardiacFilters.getBeatNumberWithinBeatGroup().completeWithValue(cardiac.getEvent().getBeatNumberWithinBeatGroup());
        cardiacFilters.getNumberOfBeatsInAverageBeat().completeWithValue(cardiac.getEvent().getNumberOfBeatsInAverageBeat());
        cardiacFilters.getBeatGroupLengthInSec().completeWithValue(cardiac.getEvent().getBeatGroupLengthInSec());

        cardiacFilters.getDateOfLastDose().completeWithValue(cardiac.getEvent().getDateOfLastDose());
        cardiacFilters.getLastDoseAmount().completeWithValue(cardiac.getEvent().getLastDoseAmount());

        cardiacFilters.getProtocolScheduleTimepoint().completeWithValue(cardiac.getEvent().getProtocolScheduleTimepoint());
        cardiacFilters.getComment().completeWithValue(cardiac.getEvent().getComment());
        cardiacFilters.getWave().completeWithValue(cardiac.getEvent().getWave());
        cardiacFilters.getMethod().completeWithValue(cardiac.getEvent().getMethod());
        cardiacFilters.getReasonAbnormalConduction().completeWithValue(cardiac.getEvent().getReasonAbnormalConduction());
        cardiacFilters.getSttChanges().completeWithValue(cardiac.getEvent().getSttChanges());
        cardiacFilters.getStSegment().completeWithValue(cardiac.getEvent().getStSegment());
        cardiacFilters.getHeartRhythm().completeWithValue(cardiac.getEvent().getHeartRhythm());
        cardiacFilters.getHeartRhythmOther().completeWithValue(cardiac.getEvent().getHeartRhythmOther());
        cardiacFilters.getExtraSystoles().completeWithValue(cardiac.getEvent().getExtraSystoles());
        cardiacFilters.getSpecifyExtraSystoles().completeWithValue(cardiac.getEvent().getSpecifyExtraSystoles());
        cardiacFilters.getConduction().completeWithValue(cardiac.getEvent().getConduction());
        cardiacFilters.getTypeOfConduction().completeWithValue(cardiac.getEvent().getTypeOfConduction());
        cardiacFilters.getSinusRhythm().completeWithValue(cardiac.getEvent().getSinusRhythm());
        cardiacFilters.getReasonNoSinusRhythm().completeWithValue(cardiac.getEvent().getReasonNoSinusRhythm());
        cardiacFilters.getAtrialFibrillation().completeWithValue(cardiac.getEvent().getAtrialFibrillation());
        cardiacFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Cardiac> other) {
        CardiacFilters otherFilters = (CardiacFilters) other.getFilters();

        cardiacFilters.getMeasurementName().complete(otherFilters.getMeasurementName());
        cardiacFilters.getMeasurementCategory().complete(otherFilters.getMeasurementCategory());
        cardiacFilters.getResultValue().complete(otherFilters.getResultValue());
        cardiacFilters.getResultUnit().complete(otherFilters.getResultUnit());
        cardiacFilters.getClinicallySignificant().complete(otherFilters.getClinicallySignificant());
        cardiacFilters.getMeasurementTimePoint().complete(otherFilters.getMeasurementTimePoint());
        cardiacFilters.getDaysOnStudy().complete(otherFilters.getDaysOnStudy());
        cardiacFilters.getStudyPeriods().complete(otherFilters.getStudyPeriods());
        cardiacFilters.getAnalysisVisit().complete(otherFilters.getAnalysisVisit());
        cardiacFilters.getVisitNumber().complete(otherFilters.getVisitNumber());
        cardiacFilters.getBaselineValue().complete(otherFilters.getBaselineValue());
        cardiacFilters.getBaselineFlag().complete(otherFilters.getBaselineFlag());
        cardiacFilters.getChangeFromBaselineValue().complete(otherFilters.getChangeFromBaselineValue());
        cardiacFilters.getPercentageChangeFromBaselineValue().complete(otherFilters.getPercentageChangeFromBaselineValue());

        cardiacFilters.getBeatGroupNumber().complete(otherFilters.getBeatGroupNumber());
        cardiacFilters.getBeatNumberWithinBeatGroup().complete(otherFilters.getBeatNumberWithinBeatGroup());
        cardiacFilters.getNumberOfBeatsInAverageBeat().complete(otherFilters.getNumberOfBeatsInAverageBeat());
        cardiacFilters.getBeatGroupLengthInSec().complete(otherFilters.getBeatGroupLengthInSec());

        cardiacFilters.getDateOfLastDose().complete(otherFilters.getDateOfLastDose());
        cardiacFilters.getLastDoseAmount().complete(otherFilters.getLastDoseAmount());

        cardiacFilters.getProtocolScheduleTimepoint().complete(otherFilters.getProtocolScheduleTimepoint());
        cardiacFilters.getComment().complete(otherFilters.getComment());
        cardiacFilters.getWave().complete(otherFilters.getWave());
        cardiacFilters.getMethod().complete(otherFilters.getMethod());
        cardiacFilters.getReasonAbnormalConduction().complete(otherFilters.getReasonAbnormalConduction());
        cardiacFilters.getSttChanges().complete(otherFilters.getSttChanges());
        cardiacFilters.getStSegment().complete(otherFilters.getStSegment());
        cardiacFilters.getHeartRhythm().complete(otherFilters.getHeartRhythm());
        cardiacFilters.getHeartRhythmOther().complete(otherFilters.getHeartRhythmOther());
        cardiacFilters.getExtraSystoles().complete(otherFilters.getExtraSystoles());
        cardiacFilters.getSpecifyExtraSystoles().complete(otherFilters.getSpecifyExtraSystoles());
        cardiacFilters.getConduction().complete(otherFilters.getConduction());
        cardiacFilters.getTypeOfConduction().complete(otherFilters.getTypeOfConduction());
        cardiacFilters.getSinusRhythm().complete(otherFilters.getSinusRhythm());
        cardiacFilters.getReasonNoSinusRhythm().complete(otherFilters.getReasonNoSinusRhythm());
        cardiacFilters.getAtrialFibrillation().complete(otherFilters.getAtrialFibrillation());

        count += other.count();
        cardiacFilters.setMatchedItemsCount(count);
    }

    @Override
    public CardiacFilters getFilters() {
        return cardiacFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Cardiac> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Cardiac> newStatistics() {
        return new CardiacFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{cardiacFilters=%s}",
                this.getClass().getSimpleName(),
                this.cardiacFilters.toString());
    }
}
