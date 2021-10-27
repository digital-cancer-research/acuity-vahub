package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;

public class DiseaseExtentFilterSummaryStatistics implements FilterSummaryStatistics<DiseaseExtent> {

    private DiseaseExtentFilters diseaseExtentFilters = new DiseaseExtentFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        DiseaseExtent pathology = (DiseaseExtent) value;

        diseaseExtentFilters.getRecentProgressionDate().completeWithValue(pathology.getEvent().getRecentProgressionDate());
        diseaseExtentFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<DiseaseExtent> other) {

        DiseaseExtentFilterSummaryStatistics otherPathology = (DiseaseExtentFilterSummaryStatistics) other;
        count += otherPathology.count;

        diseaseExtentFilters.getRecentProgressionDate().complete(otherPathology.diseaseExtentFilters.getRecentProgressionDate());
        diseaseExtentFilters.setMatchedItemsCount(count);
    }

    @Override
    public final DiseaseExtentFilters getFilters() {
        return diseaseExtentFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public DiseaseExtentFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<DiseaseExtent> newStatistics() {
        return new DiseaseExtentFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{diseaseExtentFilters=%s}",
                this.getClass().getSimpleName(),
                this.diseaseExtentFilters.toString());
    }
}
