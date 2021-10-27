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

import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;

public class LabFilterSummaryStatistics implements FilterSummaryStatistics<Lab> {

    private LabFilters labFilters = new LabFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Lab lab = (Lab) value;
        labFilters.getLabcode().completeWithValue(lab.getEvent().getLabCode());
        labFilters.getLabCategory().completeWithValue(lab.getEvent().getCategory());
        labFilters.getLabValue().completeWithValue(lab.getEvent().getValue());
        labFilters.getLabUnit().completeWithValue(lab.getEvent().getUnit());
        labFilters.getBaselineFlag().completeWithValue(lab.getEvent().getBaselineFlag());
        labFilters.getBaselineValue().completeWithValue(lab.getEvent().getBaseline());
        labFilters.getChangeFromBaselineValue().completeWithValue(lab.getChangeFromBaseline());
        labFilters.getPercentageChangeFromBaselineValue().completeWithValue(lab.getPercentChangeFromBaseline());
        labFilters.getUpperRefValue().completeWithValue(lab.getEvent().getRefHigh());
        labFilters.getLowerRefValue().completeWithValue(lab.getEvent().getRefLow());
        labFilters.getOutOfRefRange().completeWithValue(lab.getOutOfRefRange());
        labFilters.getRefRangeNormValue().completeWithValue(lab.getReferenceRangeNormalisedValue());
        labFilters.getLabValueOverUpperRefValue().completeWithValue(lab.getTimesUpperReferenceRange());
        labFilters.getLabValueOverLowerRefValue().completeWithValue(lab.getTimesLowerReferenceRange());

        labFilters.getMeasurementTimePoint().completeWithValue(lab.getEvent().getMeasurementTimePoint());
        labFilters.getDaysOnStudy().completeWithValue(lab.getDaysSinceFirstDose());
        labFilters.getVisitNumber().completeWithValue(lab.getEvent().getVisitNumber());
        labFilters.getAnalysisVisit().completeWithValue(lab.getEvent().getAnalysisVisit());

        labFilters.getProtocolScheduleTimepoint().completeWithValue(lab.getEvent().getProtocolScheduleTimepoint());
        labFilters.getValueDipstick().completeWithValue(lab.getEvent().getValueDipstick());
        labFilters.getStudyPeriods().completeWithValue(lab.getEvent().getStudyPeriods());
        labFilters.getSourceType().completeWithValue(lab.getEvent().getSourceType());

        labFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Lab> other) {
        LabFilterSummaryStatistics otherLab = (LabFilterSummaryStatistics) other;

        labFilters.getLabcode().complete(otherLab.labFilters.getLabcode());
        labFilters.getLabCategory().complete(otherLab.labFilters.getLabCategory());
        labFilters.getLabValue().complete(otherLab.labFilters.getLabValue());
        labFilters.getLabUnit().complete(otherLab.labFilters.getLabUnit());
        labFilters.getBaselineFlag().complete(otherLab.labFilters.getBaselineFlag());
        labFilters.getBaselineValue().complete(otherLab.labFilters.getBaselineValue());
        labFilters.getChangeFromBaselineValue().complete(otherLab.labFilters.getChangeFromBaselineValue());
        labFilters.getPercentageChangeFromBaselineValue().complete(otherLab.labFilters.getPercentageChangeFromBaselineValue());
        labFilters.getUpperRefValue().complete(otherLab.labFilters.getUpperRefValue());
        labFilters.getLowerRefValue().complete(otherLab.labFilters.getLowerRefValue());
        labFilters.getOutOfRefRange().complete(otherLab.labFilters.getOutOfRefRange());
        labFilters.getRefRangeNormValue().complete(otherLab.labFilters.getRefRangeNormValue());
        labFilters.getLabValueOverUpperRefValue().complete(otherLab.labFilters.getLabValueOverUpperRefValue());
        labFilters.getLabValueOverLowerRefValue().complete(otherLab.labFilters.getLabValueOverLowerRefValue());

        labFilters.getMeasurementTimePoint().complete(otherLab.labFilters.getMeasurementTimePoint());
        labFilters.getDaysOnStudy().complete(otherLab.labFilters.getDaysOnStudy());
        labFilters.getVisitNumber().complete(otherLab.labFilters.getVisitNumber());
        labFilters.getAnalysisVisit().complete(otherLab.labFilters.getAnalysisVisit());

        labFilters.getProtocolScheduleTimepoint().complete(otherLab.labFilters.getProtocolScheduleTimepoint());
        labFilters.getValueDipstick().complete(otherLab.labFilters.getValueDipstick());
        labFilters.getSourceType().complete(otherLab.labFilters.getSourceType());

        labFilters.setMatchedItemsCount(count);
    }

    @Override
    public LabFilters getFilters() {
        return labFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Lab> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Lab> newStatistics() {
        return new LabFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{labFilters=%s}",
                this.getClass().getSimpleName(),
                this.labFilters.toString());
    }
}
