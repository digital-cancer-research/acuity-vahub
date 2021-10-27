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

import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;

public class VitalFilterSummaryStatistics implements FilterSummaryStatistics<Vital> {

    private VitalFilters vitalFilters = new VitalFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Vital vitalEvent = (Vital) value;

        vitalFilters.getAnalysisVisit().completeWithValue(vitalEvent.getEvent().getAnalysisVisit());
        vitalFilters.getAnatomicalLocations().completeWithValue(vitalEvent.getEvent().getAnatomicalLocation());
        vitalFilters.getBaseline().completeWithValue(vitalEvent.getEvent().getBaseline());
        vitalFilters.getBaselineFlags().completeWithValue(vitalEvent.getEvent().getBaselineFlag());
        vitalFilters.getChangeFromBaseline().completeWithValue(vitalEvent.getChangeFromBaseline());
        vitalFilters.getClinicallySignificant().completeWithValue(vitalEvent.getEvent().getClinicallySignificant());
        vitalFilters.getDaysSinceFirstDose().completeWithValue(vitalEvent.getDaysSinceFirstDose());
        vitalFilters.getLastDoseAmounts().completeWithValue(vitalEvent.getEvent().getLastDoseAmount());
        vitalFilters.getLastDoseDate().completeWithValue(vitalEvent.getEvent().getLastDoseDate());
        vitalFilters.getMeasurementDate().completeWithValue(vitalEvent.getEvent().getMeasurementDate());
        vitalFilters.getPercentageChangeFromBaseline().completeWithValue(vitalEvent.getPercentChangeFromBaseline());
        vitalFilters.getPhysicalPositions().completeWithValue(vitalEvent.getEvent().getPhysicalPosition());
        vitalFilters.getPlannedTimePoints().completeWithValue(vitalEvent.getEvent().getPlannedTimePoint());
        vitalFilters.getResultValue().completeWithValue(vitalEvent.getEvent().getResultValue());
        vitalFilters.getScheduleTimepoints().completeWithValue(vitalEvent.getEvent().getScheduleTimepoint());
        vitalFilters.getSidesOfInterest().completeWithValue(vitalEvent.getEvent().getSidesOfInterest());
        vitalFilters.getStudyPeriods().completeWithValue(vitalEvent.getEvent().getStudyPeriod());
        vitalFilters.getUnits().completeWithValue(vitalEvent.getEvent().getUnit());
        vitalFilters.getVisitNumber().completeWithValue(vitalEvent.getEvent().getVisitNumber());
        vitalFilters.getVitalsMeasurements().completeWithValue(vitalEvent.getEvent().getVitalsMeasurement());

        vitalFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Vital> other) {
        VitalFilterSummaryStatistics otherVitalEvent = (VitalFilterSummaryStatistics) other;

        vitalFilters.getAnalysisVisit().complete(otherVitalEvent.vitalFilters.getAnalysisVisit());
        vitalFilters.getAnatomicalLocations().complete(otherVitalEvent.vitalFilters.getAnatomicalLocations());
        vitalFilters.getBaseline().complete(otherVitalEvent.vitalFilters.getBaseline());
        vitalFilters.getBaselineFlags().complete(otherVitalEvent.vitalFilters.getBaselineFlags());
        vitalFilters.getChangeFromBaseline().complete(otherVitalEvent.vitalFilters.getChangeFromBaseline());
        vitalFilters.getClinicallySignificant().complete(otherVitalEvent.vitalFilters.getClinicallySignificant());
        vitalFilters.getDaysSinceFirstDose().complete(otherVitalEvent.vitalFilters.getDaysSinceFirstDose());
        vitalFilters.getLastDoseAmounts().complete(otherVitalEvent.vitalFilters.getLastDoseAmounts());
        vitalFilters.getLastDoseDate().complete(otherVitalEvent.vitalFilters.getLastDoseDate());
        vitalFilters.getMeasurementDate().complete(otherVitalEvent.vitalFilters.getMeasurementDate());
        vitalFilters.getPercentageChangeFromBaseline().complete(otherVitalEvent.vitalFilters.getPercentageChangeFromBaseline());
        vitalFilters.getPhysicalPositions().complete(otherVitalEvent.vitalFilters.getPhysicalPositions());
        vitalFilters.getPlannedTimePoints().complete(otherVitalEvent.vitalFilters.getPlannedTimePoints());
        vitalFilters.getResultValue().complete(otherVitalEvent.vitalFilters.getResultValue());
        vitalFilters.getScheduleTimepoints().complete(otherVitalEvent.vitalFilters.getScheduleTimepoints());
        vitalFilters.getSidesOfInterest().complete(otherVitalEvent.vitalFilters.getSidesOfInterest());
        vitalFilters.getStudyPeriods().complete(otherVitalEvent.vitalFilters.getStudyPeriods());
        vitalFilters.getUnits().complete(otherVitalEvent.vitalFilters.getUnits());
        vitalFilters.getVisitNumber().complete(otherVitalEvent.vitalFilters.getVisitNumber());
        vitalFilters.getVitalsMeasurements().complete(otherVitalEvent.vitalFilters.getVitalsMeasurements());

        vitalFilters.setMatchedItemsCount(count);
    }

    @Override
    public VitalFilters getFilters() {
        return vitalFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Vital> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Vital> newStatistics() {
        return new VitalFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{vitalFilters=%s}",
                this.getClass().getSimpleName(),
                this.vitalFilters.toString());
    }
}
