package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;

public class PathologyFilterSummaryStatistics implements FilterSummaryStatistics<Pathology> {

    private PathologyFilters pathologyFilters = new PathologyFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Pathology pathology = (Pathology) value;

        pathologyFilters.getDiagnosisDate().completeWithValue(pathology.getEvent().getDate());
        pathologyFilters.getDaysFromOriginalDiagnosis().completeWithValue(pathology.getDaysFromOriginalDiagnosis());
        pathologyFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Pathology> other) {

        PathologyFilterSummaryStatistics otherPathology = (PathologyFilterSummaryStatistics) other;
        count += otherPathology.count;

        pathologyFilters.getDiagnosisDate().complete(otherPathology.pathologyFilters.getDiagnosisDate());
        pathologyFilters.getDaysFromOriginalDiagnosis().complete(otherPathology.pathologyFilters.getDaysFromOriginalDiagnosis());
        pathologyFilters.setMatchedItemsCount(count);
    }

    @Override
    public final PathologyFilters getFilters() {
        return pathologyFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PathologyFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Pathology> newStatistics() {
        return new PathologyFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{pathologyFilters=%s}",
                this.getClass().getSimpleName(),
                this.pathologyFilters.toString());
    }
}
