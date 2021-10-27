package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;

public class LiverDiagFilterSummaryStatistics implements FilterSummaryStatistics<LiverDiag> {
    private LiverDiagFilters liverDiagFilters = new LiverDiagFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        LiverDiag liverDiag = (LiverDiag) value;

        liverDiagFilters.getLiverDiagInv().completeWithValue(liverDiag.getEvent().getLiverDiagInv());
        liverDiagFilters.getLiverDiagInvSpec().completeWithValue(liverDiag.getEvent().getLiverDiagInvSpec());
        liverDiagFilters.getLiverDiagInvDate().completeWithValue(liverDiag.getEvent().getLiverDiagInvDate());
        liverDiagFilters.getStudyDayLiverDiagInv().completeWithValue(liverDiag.getStudyDayLiverDiagInv());
        liverDiagFilters.getLiverDiagInvResult().completeWithValue(liverDiag.getEvent().getLiverDiagInvResult());
        liverDiagFilters.getPotentialHysLawCaseNum().completeWithValue(liverDiag.getEvent().getPotentialHysLawCaseNum());

        liverDiagFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<LiverDiag> other) {
        LiverDiagFilters otherFilters = (LiverDiagFilters) other.getFilters();

        liverDiagFilters.getLiverDiagInv().complete(otherFilters.getLiverDiagInv());
        liverDiagFilters.getLiverDiagInvSpec().complete(otherFilters.getLiverDiagInvSpec());
        liverDiagFilters.getLiverDiagInvDate().complete(otherFilters.getLiverDiagInvDate());
        liverDiagFilters.getStudyDayLiverDiagInv().complete(otherFilters.getStudyDayLiverDiagInv());
        liverDiagFilters.getLiverDiagInvResult().complete(otherFilters.getLiverDiagInvResult());
        liverDiagFilters.getPotentialHysLawCaseNum().complete(otherFilters.getPotentialHysLawCaseNum());

        count += other.count();
        liverDiagFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<LiverDiag> getFilters() {
        return liverDiagFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<LiverDiag> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<LiverDiag> newStatistics() {
        return new LiverDiagFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{filters=%s}",
                this.getClass().getSimpleName(),
                this.liverDiagFilters.toString());
    }
}
