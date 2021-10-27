package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;

public class LiverFilterSummaryStatistics implements FilterSummaryStatistics<Liver> {

    private LiverFilters liverFilters = new LiverFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Liver liver = (Liver) value;

        liverFilters.getMeasurementTimePoint().completeWithValue(liver.getAccessibleMeasurementTimePoint());
        liverFilters.getDaysOnStudy().completeWithValue(liver.getDaysSinceFirstDose());
        liverFilters.getVisitNumber().completeWithValue(liver.getEvent().getVisitNumber());
        liverFilters.getLabValue().completeWithValue(liver.getEvent().getValue());
        liverFilters.getBaselineValue().completeWithValue(liver.getEvent().getBaselineValue());
        liverFilters.getBaselineFlag().completeWithValue(liver.getEvent().getBaselineFlag());
        liverFilters.getChangeFromBaselineValue().completeWithValue(liver.getChangeFromBaseline());
        liverFilters.getPercentageChangeFromBaselineValue().completeWithValue(liver.getPercentChangeFromBaseline());
        liverFilters.getRefRangeNormValue().completeWithValue(liver.getReferenceRangeNormalisedValue());
        liverFilters.getLabValueOverUpperRefValue().completeWithValue(liver.getTimesUpperReferenceRange());
        liverFilters.getLabValueOverLowerRefValue().completeWithValue(liver.getTimesLowerReferenceRange());
        liverFilters.getLowerRefValue().completeWithValue(liver.getEvent().getRefLow());
        liverFilters.getUpperRefValue().completeWithValue(liver.getEvent().getRefHigh());

        liverFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Liver> other) {
        LiverFilters otherFilters = (LiverFilters) other.getFilters();

        liverFilters.getMeasurementTimePoint().complete(otherFilters.getMeasurementTimePoint());
        liverFilters.getDaysOnStudy().complete(otherFilters.getDaysOnStudy());
        liverFilters.getVisitNumber().complete(otherFilters.getVisitNumber());
        liverFilters.getLabValue().complete(otherFilters.getLabValue());
        liverFilters.getBaselineValue().complete(otherFilters.getBaselineValue());
        liverFilters.getBaselineFlag().complete(otherFilters.getBaselineFlag());
        liverFilters.getChangeFromBaselineValue().complete(otherFilters.getChangeFromBaselineValue());
        liverFilters.getPercentageChangeFromBaselineValue().complete(otherFilters.getPercentageChangeFromBaselineValue());
        liverFilters.getRefRangeNormValue().complete(otherFilters.getRefRangeNormValue());
        liverFilters.getLabValueOverUpperRefValue().complete(otherFilters.getLabValueOverUpperRefValue());
        liverFilters.getLabValueOverLowerRefValue().complete(otherFilters.getLabValueOverLowerRefValue());
        liverFilters.getLowerRefValue().complete(otherFilters.getLowerRefValue());
        liverFilters.getUpperRefValue().complete(otherFilters.getUpperRefValue());
        count += other.count();
        liverFilters.setMatchedItemsCount(count);
    }

    @Override
    public LiverFilters getFilters() {
        return liverFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Liver> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Liver> newStatistics() {
        return new LiverFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{liverFilters=%s}",
                this.getClass().getSimpleName(),
                this.liverFilters.toString());
    }
}
