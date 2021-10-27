package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;

public class PkResultFilterSummaryStatistics implements FilterSummaryStatistics<PkResult> {

    private PkResultFilters pkResultFilters = new PkResultFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        PkResult pkResult = (PkResult) value;
        pkResultFilters.setMatchedItemsCount(count);
        pkResultFilters.getAnalyte().completeWithValue(pkResult.getEvent().getAnalyte());
    }

    @Override
    public void combine(FilterSummaryStatistics<PkResult> other) {

        PkResultFilterSummaryStatistics otherPkResult = (PkResultFilterSummaryStatistics) other;
        count += otherPkResult.count;

        pkResultFilters.getAnalyte().complete(otherPkResult.pkResultFilters.getAnalyte());
    }

    @Override
    public final PkResultFilters getFilters() {
        return pkResultFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PkResultFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<PkResult> newStatistics() {
        return new PkResultFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{pkResultFilters=%s}",
                this.getClass().getSimpleName(),
                this.pkResultFilters.toString());
    }
}
