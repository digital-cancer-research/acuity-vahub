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

import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;

public class LungFunctionFilterSummaryStatistics implements FilterSummaryStatistics<LungFunction> {

    private LungFunctionFilters filters = new LungFunctionFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        accept((LungFunction) value);
    }

    private void accept(LungFunction value) {
        count++;
        filters.getMeasurementName().completeWithValue(value.getMeasurementName());
        filters.getMeasurementTimePoint().completeWithValue(value.getAccessibleMeasurementTimePoint());
        filters.getDaysOnStudy().completeWithValue(value.getDaysOnStudy());
        filters.getProtocolScheduleTimepoint().completeWithValue(value.getProtocolScheduleTimepoint());
        filters.getVisitNumber().completeWithValue(value.getEvent().getVisit());
        filters.getResultValue().completeWithValue(value.getResultValue());
        filters.getResultUnit().completeWithValue(value.getResultUnit());
        filters.getBaselineValue().completeWithValue(value.getBaselineValue());
        filters.getChangeFromBaseline().completeWithValue(value.getChangeFromBaseline());
        filters.getPercentChangeFromBaseline().completeWithValue(value.getPercentChangeFromBaseline());
        filters.getBaselineFlag().completeWithValue(value.getEvent().getBaselineFlag());

        filters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<LungFunction> other) {
        combine((LungFunctionFilterSummaryStatistics) other);
    }

    private void combine(LungFunctionFilterSummaryStatistics other) {
        filters.getMeasurementName().complete(other.filters.getMeasurementName());
        filters.getMeasurementTimePoint().complete(other.filters.getMeasurementTimePoint());
        filters.getDaysOnStudy().complete(other.filters.getDaysOnStudy());
        filters.getProtocolScheduleTimepoint().complete(other.filters.getProtocolScheduleTimepoint());
        filters.getVisitNumber().complete(other.filters.getVisitNumber());
        filters.getResultValue().complete(other.filters.getResultValue());
        filters.getResultUnit().complete(other.filters.getResultUnit());
        filters.getBaselineValue().complete(other.filters.getBaselineValue());
        filters.getChangeFromBaseline().complete(other.filters.getChangeFromBaseline());
        filters.getPercentChangeFromBaseline().complete(other.filters.getPercentChangeFromBaseline());
        filters.getBaselineFlag().complete(other.filters.getBaselineFlag());
        count += other.count();
        filters.setMatchedItemsCount(count);
    }

    @Override
    public LungFunctionFilters getFilters() {
        return filters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<LungFunction> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<LungFunction> newStatistics() {
        return new LungFunctionFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{filters=%s}",
                this.getClass().getSimpleName(),
                this.filters.toString());
    }
}
