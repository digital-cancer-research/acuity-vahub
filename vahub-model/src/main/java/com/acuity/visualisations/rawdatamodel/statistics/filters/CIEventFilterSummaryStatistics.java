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

import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;

public class CIEventFilterSummaryStatistics implements FilterSummaryStatistics<CIEvent> {

    private CIEventFilters ciEventFilters = new CIEventFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        CIEvent ciEvent = (CIEvent) value;
        ciEventFilters.getPreviousEcgAvailable().completeWithValue(ciEvent.getEvent().getPreviousEcgAvailable());
        ciEventFilters.getPreviousEcgDate().completeWithValue(ciEvent.getEvent().getPreviousEcgDate());
        ciEventFilters.getIschemicSymptoms().completeWithValue(ciEvent.getEvent().getIschemicSymptoms());
        ciEventFilters.getAeNumber().completeWithValue(ciEvent.getAeNumber());
        ciEventFilters.getEventTerm().completeWithValue(ciEvent.getEvent().getTerm());
        ciEventFilters.getCieSymptomsDuration().completeWithValue(ciEvent.getEvent().getCieSymptomsDuration());
        ciEventFilters.getCoronaryAngiography().completeWithValue(ciEvent.getEvent().getCoronaryAngiography());
        ciEventFilters.getAngiographyDate().completeWithValue(ciEvent.getEvent().getAngiographyDate());
        ciEventFilters.getEventStartDate().completeWithValue(ciEvent.getEvent().getStartDate());
        ciEventFilters.getEcgAtTheEventTime().completeWithValue(ciEvent.getEvent().getEcgAtTheEventTime());
        ciEventFilters.getEventSuspToBeDueToStentTromb().completeWithValue(ciEvent.getEvent().getEventSuspDueToStentThromb());
        ciEventFilters.getFinalDiagnosis().completeWithValue(ciEvent.getEvent().getFinalDiagnosis());
        ciEventFilters.getOtherDiagnosis().completeWithValue(ciEvent.getEvent().getOtherDiagnosis());
        ciEventFilters.getLocalCardiacBiomarkersDrawn().completeWithValue(ciEvent.getEvent().getLocalCardiacBiomarkersDrawn());
        ciEventFilters.getNoEcgAtTheEventTime().completeWithValue(ciEvent.getEvent().getNoEcgAtTheEventTime());
        ciEventFilters.getSympPromtUnsHosp().completeWithValue(ciEvent.getEvent().getSymptPromptUnschedHospit());
        ciEventFilters.getDescription1().completeWithValue(ciEvent.getEvent().getDescription1());
        ciEventFilters.getDescription2().completeWithValue(ciEvent.getEvent().getDescription2());
        ciEventFilters.getDescription3().completeWithValue(ciEvent.getEvent().getDescription3());
        ciEventFilters.getDescription4().completeWithValue(ciEvent.getEvent().getDescription4());
        ciEventFilters.getDescription5().completeWithValue(ciEvent.getEvent().getDescription5());
        ciEventFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<CIEvent> other) {
        CIEventFilterSummaryStatistics otherCIEvent = (CIEventFilterSummaryStatistics) other;
        ciEventFilters.getAeNumber().complete(otherCIEvent.ciEventFilters.getAeNumber());
        ciEventFilters.getCieSymptomsDuration().complete(otherCIEvent.ciEventFilters.getCieSymptomsDuration());
        ciEventFilters.getCoronaryAngiography().complete(otherCIEvent.ciEventFilters.getCoronaryAngiography());
        ciEventFilters.getAngiographyDate().complete(otherCIEvent.ciEventFilters.getAngiographyDate());
        ciEventFilters.getDescription1().complete(otherCIEvent.ciEventFilters.getDescription1());
        ciEventFilters.getDescription2().complete(otherCIEvent.ciEventFilters.getDescription2());
        ciEventFilters.getDescription3().complete(otherCIEvent.ciEventFilters.getDescription3());
        ciEventFilters.getDescription4().complete(otherCIEvent.ciEventFilters.getDescription4());
        ciEventFilters.getDescription5().complete(otherCIEvent.ciEventFilters.getDescription5());
        ciEventFilters.getSympPromtUnsHosp().complete(otherCIEvent.ciEventFilters.getEcgAtTheEventTime());
        ciEventFilters.getEcgAtTheEventTime().complete(otherCIEvent.ciEventFilters.getEcgAtTheEventTime());
        ciEventFilters.getEventStartDate().complete(otherCIEvent.ciEventFilters.getEventStartDate());
        ciEventFilters.getEventSuspToBeDueToStentTromb().complete(otherCIEvent.ciEventFilters.getEventSuspToBeDueToStentTromb());
        ciEventFilters.getEventTerm().complete(otherCIEvent.ciEventFilters.getEventTerm());
        ciEventFilters.getFinalDiagnosis().complete(otherCIEvent.ciEventFilters.getFinalDiagnosis());
        ciEventFilters.getOtherDiagnosis().complete(otherCIEvent.ciEventFilters.getOtherDiagnosis());
        ciEventFilters.getNoEcgAtTheEventTime().complete(otherCIEvent.ciEventFilters.getNoEcgAtTheEventTime());
        ciEventFilters.getIschemicSymptoms().complete(otherCIEvent.ciEventFilters.getIschemicSymptoms());
        ciEventFilters.getPreviousEcgAvailable().complete(otherCIEvent.ciEventFilters.getPreviousEcgAvailable());
        ciEventFilters.getPreviousEcgDate().complete(otherCIEvent.ciEventFilters.getPreviousEcgDate());
        ciEventFilters.getLocalCardiacBiomarkersDrawn().complete(otherCIEvent.ciEventFilters.getLocalCardiacBiomarkersDrawn());

        ciEventFilters.setMatchedItemsCount(count);
    }

    @Override
    public CIEventFilters getFilters() {
        return ciEventFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<CIEvent> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<CIEvent> newStatistics() {
        return new CIEventFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{ciEventFilters=%s}",
                this.getClass().getSimpleName(),
                this.ciEventFilters.toString());
    }
}
