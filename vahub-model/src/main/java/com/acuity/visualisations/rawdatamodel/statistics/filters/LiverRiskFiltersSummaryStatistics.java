package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;

public class LiverRiskFiltersSummaryStatistics implements FilterSummaryStatistics<LiverRisk> {

    private LiverRiskFilters liverRiskFilters = new LiverRiskFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        LiverRisk liverRisk = (LiverRisk) value;
        liverRiskFilters.getValue().completeWithValue(liverRisk.getEvent().getValue());
        liverRiskFilters.getOccurrence().completeWithValue(liverRisk.getEvent().getOccurrence());
        liverRiskFilters.getDetails().completeWithValue(liverRisk.getEvent().getDetails());
        liverRiskFilters.getComment().completeWithValue(liverRisk.getEvent().getComment());
        liverRiskFilters.getPotentialHysLawCaseNum().completeWithValue(liverRisk.getEvent().getPotentialHysLawCaseNum());
        liverRiskFilters.getReferencePeriod().completeWithValue(liverRisk.getEvent().getReferencePeriod());
        liverRiskFilters.getStartDate().completeWithValue(liverRisk.getStartDate());
        liverRiskFilters.getStopDate().completeWithValue(liverRisk.getEvent().getStopDate());
        liverRiskFilters.getStudyDayAtStart().completeWithValue(liverRisk.getStudyDayAtStart());
        liverRiskFilters.getStudyDayAtStop().completeWithValue(liverRisk.getStudyDayAtStop());

        liverRiskFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<LiverRisk> other) {
        LiverRiskFiltersSummaryStatistics otherLiverRisk = (LiverRiskFiltersSummaryStatistics) other;
        liverRiskFilters.getValue().complete(otherLiverRisk.liverRiskFilters.getValue());
        liverRiskFilters.getComment().complete(otherLiverRisk.liverRiskFilters.getComment());
        liverRiskFilters.getReferencePeriod().complete(otherLiverRisk.liverRiskFilters.getReferencePeriod());
        liverRiskFilters.getOccurrence().complete(otherLiverRisk.liverRiskFilters.getOccurrence());
        liverRiskFilters.getDetails().complete(otherLiverRisk.liverRiskFilters.getDetails());
        liverRiskFilters.getStartDate().complete(otherLiverRisk.liverRiskFilters.getStartDate());
        liverRiskFilters.getStopDate().complete(otherLiverRisk.liverRiskFilters.getStopDate());
        liverRiskFilters.getStudyDayAtStop().complete(otherLiverRisk.liverRiskFilters.getStudyDayAtStop());
        liverRiskFilters.getStudyDayAtStart().complete(otherLiverRisk.liverRiskFilters.getStudyDayAtStart());
        liverRiskFilters.getPotentialHysLawCaseNum().complete(otherLiverRisk.liverRiskFilters.getPotentialHysLawCaseNum());

        count += other.count();
        liverRiskFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<LiverRisk> getFilters() {
        return liverRiskFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<LiverRisk> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<LiverRisk> newStatistics() {
        return new LiverRiskFiltersSummaryStatistics();
    }
}
