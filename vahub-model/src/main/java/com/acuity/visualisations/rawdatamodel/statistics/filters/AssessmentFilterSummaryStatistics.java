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

import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;

public class AssessmentFilterSummaryStatistics implements FilterSummaryStatistics<Assessment> {

    private AssessmentFilters assessmentFilters = new AssessmentFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Assessment assessment = (Assessment) value;

        assessmentFilters.getResponse().completeWithValue(assessment.getEvent().getResponse());
        assessmentFilters.getLesionSite().completeWithValue(assessment.getEvent().getLesionSite());
        assessmentFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Assessment> other) {

        AssessmentFilterSummaryStatistics otherAssessment = (AssessmentFilterSummaryStatistics) other;
        count += otherAssessment.count;

        assessmentFilters.getResponse().complete(otherAssessment.assessmentFilters.getResponse());
        assessmentFilters.getLesionSite().complete(otherAssessment.assessmentFilters.getLesionSite());
        assessmentFilters.setMatchedItemsCount(count);
    }

    @Override
    public final AssessmentFilters getFilters() {
        return assessmentFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public AssessmentFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Assessment> newStatistics() {
        return new AssessmentFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{assessmentFilters=%s}",
                this.getClass().getSimpleName(),
                this.assessmentFilters.toString());
    }
}
