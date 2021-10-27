package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
/**
 * Copied from javas IntegerSummaryStatistics but to allow nulls and have a hasNulls attribute
 */
public class ExposureFilterSummaryStatistics implements FilterSummaryStatistics<Exposure> {

    private ExposureFilters exposureFilters = new ExposureFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Exposure exposure = (Exposure) value;

        exposureFilters.getAnalyte().completeWithValue(exposure.getEvent().getCycle().getAnalyte());
        exposureFilters.getAnalyteConcentration().completeWithValue(exposure.getEvent().getAnalyteConcentration());
        exposureFilters.getAnalyteUnit().completeWithValue(exposure.getEvent().getAnalyteUnit());
        exposureFilters.getTimeFromAdministration().completeWithValue(exposure.getEvent().getTimeFromAdministration());
        exposureFilters.getTreatment().completeWithValue(exposure.getEvent().getTreatment());
        exposureFilters.getTreatmentCycle().completeWithValue(exposure.getEvent().getCycle().getTreatmentCycle());
        exposureFilters.getVisit().completeWithValue(exposure.getEvent().getCycle().getVisit());
        exposureFilters.getDay().completeWithValue(exposure.getEvent().getProtocolScheduleDay());
        exposureFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Exposure> other) {

        ExposureFilterSummaryStatistics otherExposure = (ExposureFilterSummaryStatistics) other;
        count += otherExposure.count;

        exposureFilters.getAnalyte().complete(otherExposure.exposureFilters.getAnalyte());
        exposureFilters.getAnalyteConcentration().complete(otherExposure.exposureFilters.getAnalyteConcentration());
        exposureFilters.getAnalyteUnit().complete(otherExposure.exposureFilters.getAnalyteUnit());
        exposureFilters.getTimeFromAdministration().complete(otherExposure.exposureFilters.getTimeFromAdministration());
        exposureFilters.getTreatment().complete(otherExposure.exposureFilters.getTreatment());
        exposureFilters.getTreatmentCycle().complete(otherExposure.exposureFilters.getTreatmentCycle());
        exposureFilters.getVisit().complete(otherExposure.exposureFilters.getVisit());
        exposureFilters.getDay().complete(otherExposure.exposureFilters.getDay());
        exposureFilters.setMatchedItemsCount(count);
    }

    @Override
    public final ExposureFilters getFilters() {
        return exposureFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public ExposureFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Exposure> newStatistics() {
        return new ExposureFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{exposureFilters=%s}",
                this.getClass().getSimpleName(),
                this.exposureFilters.toString());
    }
}
