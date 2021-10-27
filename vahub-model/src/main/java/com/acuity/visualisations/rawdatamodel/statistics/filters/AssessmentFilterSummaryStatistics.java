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
