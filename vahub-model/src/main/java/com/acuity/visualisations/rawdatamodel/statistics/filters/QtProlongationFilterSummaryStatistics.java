package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;

public class QtProlongationFilterSummaryStatistics implements FilterSummaryStatistics<QtProlongation> {
    private QtProlongationFilters qtProlongationFilters = new QtProlongationFilters();
    private int count = 0;


    @Override
    public void accept(Object value) {
        count++;
        qtProlongationFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<QtProlongation> other) {
        QtProlongationFilterSummaryStatistics otherQtProlongation = (QtProlongationFilterSummaryStatistics) other;
        count += otherQtProlongation.count;
    }

    @Override
    public QtProlongationFilters getFilters() {
        return qtProlongationFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public QtProlongationFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<QtProlongation> newStatistics() {
        return new QtProlongationFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{qtProlongationFilters=%s}",
                this.getClass().getSimpleName(),
                this.qtProlongationFilters.toString());
    }
}
