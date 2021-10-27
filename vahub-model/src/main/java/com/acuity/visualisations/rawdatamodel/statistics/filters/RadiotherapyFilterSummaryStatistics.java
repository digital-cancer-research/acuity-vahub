package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;

public class RadiotherapyFilterSummaryStatistics implements FilterSummaryStatistics<Radiotherapy> {

    private RadiotherapyFilters radiotherapyFilters = new RadiotherapyFilters(false);
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Radiotherapy radiotherapy = (Radiotherapy) value;

        radiotherapyFilters.getTherapyStatus().completeWithValue(radiotherapy.getEvent().getTreatmentStatus());
        radiotherapyFilters.getRadiationDose().completeWithValue(radiotherapy.getRadiationDose());
        radiotherapyFilters.setMatchedItemsCount(count);
        radiotherapyFilters.setRadiotherapyEnabled(true);
    }

    @Override
    public void combine(FilterSummaryStatistics<Radiotherapy> other) {

        RadiotherapyFilterSummaryStatistics otherRadiotherapy = (RadiotherapyFilterSummaryStatistics) other;
        count += otherRadiotherapy.count;

        radiotherapyFilters.getTherapyStatus().complete(otherRadiotherapy.radiotherapyFilters.getTherapyStatus());
        radiotherapyFilters.getRadiationDose().complete(otherRadiotherapy.radiotherapyFilters.getRadiationDose());
        radiotherapyFilters.setMatchedItemsCount(count);
        radiotherapyFilters.setRadiotherapyEnabled(radiotherapyFilters.isRadiotherapyEnabled()
                && otherRadiotherapy.radiotherapyFilters.isRadiotherapyEnabled());
    }

    @Override
    public final RadiotherapyFilters getFilters() {
        return radiotherapyFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public RadiotherapyFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Radiotherapy> newStatistics() {
        return new RadiotherapyFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{radiotherapyFilters=%s}",
                this.getClass().getSimpleName(),
                this.radiotherapyFilters.toString());
    }
}
