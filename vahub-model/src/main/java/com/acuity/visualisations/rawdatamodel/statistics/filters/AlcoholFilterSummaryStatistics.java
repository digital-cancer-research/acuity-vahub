package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;

public class AlcoholFilterSummaryStatistics implements FilterSummaryStatistics<Alcohol> {
    private AlcoholFilters alcoholFilters = new AlcoholFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Alcohol alcohol = (Alcohol) value;

        alcoholFilters.getSubstanceCategory().completeWithValue(alcohol.getEvent().getSubstanceCategory());
        alcoholFilters.getSubstanceUseOccurrence().completeWithValue(alcohol.getEvent().getSubstanceUseOccurrence());
        alcoholFilters.getSubstanceType().completeWithValue(alcohol.getEvent().getSubstanceType());
        alcoholFilters.getOtherSubstanceTypeSpec().completeWithValue(alcohol.getEvent().getOtherSubstanceTypeSpec());
        alcoholFilters.getFrequency().completeWithValue(alcohol.getEvent().getFrequency());
        alcoholFilters.getSubstanceTypeUseOccurrence().completeWithValue(alcohol.getEvent().getSubstanceTypeUseOccurrence());
        alcoholFilters.getSubstanceConsumption().completeWithValue(alcohol.getEvent().getSubstanceConsumption());
        alcoholFilters.getStartDate().completeWithValue(alcohol.getEvent().getStartDate());
        alcoholFilters.getEndDate().completeWithValue(alcohol.getEvent().getEndDate());

        alcoholFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Alcohol> other) {
        AlcoholFilters otherFilters = (AlcoholFilters) other;

        this.alcoholFilters.getSubstanceCategory().complete(otherFilters.getSubstanceCategory());
        this.alcoholFilters.getSubstanceUseOccurrence().complete(otherFilters.getSubstanceUseOccurrence());
        this.alcoholFilters.getSubstanceType().complete(otherFilters.getSubstanceType());
        this.alcoholFilters.getOtherSubstanceTypeSpec().complete(otherFilters.getOtherSubstanceTypeSpec());
        this.alcoholFilters.getFrequency().complete(otherFilters.getFrequency());
        this.alcoholFilters.getSubstanceTypeUseOccurrence().complete(otherFilters.getSubstanceTypeUseOccurrence());
        this.alcoholFilters.getSubstanceConsumption().complete(otherFilters.getSubstanceConsumption());
        this.alcoholFilters.getStartDate().complete(otherFilters.getStartDate());
        this.alcoholFilters.getEndDate().complete(otherFilters.getEndDate());

        count += other.count();
        this.alcoholFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Alcohol> getFilters() {
        return alcoholFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Alcohol> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Alcohol> newStatistics() {
        return new AlcoholFilterSummaryStatistics();
    }
}
