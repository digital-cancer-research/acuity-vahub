package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;

import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.ONLY_TRACKED_MUTATIONS;

public class CtDnaFilterSummaryStatistics implements FilterSummaryStatistics<CtDna> {

    private CtDnaFilters ctDnaFilters = new CtDnaFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        CtDna ctDna = (CtDna) value;
        ctDnaFilters.getGene().completeWithValue(ctDna.getEvent().getGene());
        ctDnaFilters.getMutation().completeWithValue(ctDna.getEvent().getMutation());
        if (YES.equals(ctDna.getEvent().getTrackedMutation())) {
            ctDnaFilters.getTrackedMutation().completeWithValue(ONLY_TRACKED_MUTATIONS);
        }
        ctDnaFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<CtDna> other) {
        CtDnaFilterSummaryStatistics otherCtDna = (CtDnaFilterSummaryStatistics) other;
        count += otherCtDna.count;
        ctDnaFilters.getGene().complete(otherCtDna.ctDnaFilters.getGene());
        ctDnaFilters.getMutation().complete(otherCtDna.ctDnaFilters.getMutation());
        ctDnaFilters.setMatchedItemsCount(count);
    }

    @Override
    public final CtDnaFilters getFilters() {
        return ctDnaFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public CtDnaFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<CtDna> newStatistics() {
        return new CtDnaFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{ctDnaFilters=%s}",
                this.getClass().getSimpleName(),
                this.ctDnaFilters.toString());
    }
}
