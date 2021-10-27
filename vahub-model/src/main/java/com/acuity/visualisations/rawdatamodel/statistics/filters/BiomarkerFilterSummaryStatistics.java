package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;

public class BiomarkerFilterSummaryStatistics implements FilterSummaryStatistics<Biomarker> {

    private BiomarkerFilters biomarkerFilters = new BiomarkerFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Biomarker biomarker = (Biomarker) value;

        biomarkerFilters.getGene().completeWithValue(biomarker.getEvent().getGene());
        biomarkerFilters.getMutation().completeWithValue(biomarker.getEvent().getMutation());
        biomarkerFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Biomarker> other) {

        BiomarkerFilterSummaryStatistics otherExposure = (BiomarkerFilterSummaryStatistics) other;
        count += otherExposure.count;

        biomarkerFilters.getGene().complete(otherExposure.biomarkerFilters.getGene());
        biomarkerFilters.getMutation().complete(otherExposure.biomarkerFilters.getMutation());
        biomarkerFilters.setMatchedItemsCount(count);
    }

    @Override
    public final BiomarkerFilters getFilters() {
        return biomarkerFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public BiomarkerFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Biomarker> newStatistics() {
        return new BiomarkerFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{biomarkerFilters=%s}",
                this.getClass().getSimpleName(),
                this.biomarkerFilters.toString());
    }
}
