package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;

public class MedicalHistoryFiltersSummaryStatistics implements FilterSummaryStatistics<MedicalHistory> {
    private MedicalHistoryFilters medicalHistoryFilters = new MedicalHistoryFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        MedicalHistory medicalHistory = (MedicalHistory) value;
        medicalHistoryFilters.getTerm().completeWithValue(medicalHistory.getEvent().getTerm());
        medicalHistoryFilters.getConditionStatus().completeWithValue(medicalHistory.getEvent().getConditionStatus());
        medicalHistoryFilters.getCategory().completeWithValue(medicalHistory.getEvent().getCategory());
        medicalHistoryFilters.getStart().completeWithValue(medicalHistory.getStartDate());
        medicalHistoryFilters.getEnd().completeWithValue(medicalHistory.getEndDate());
        medicalHistoryFilters.getCurrentMedication().completeWithValue(medicalHistory.getEvent().getCurrentMedication());
        medicalHistoryFilters.getSoc().completeWithValue(medicalHistory.getEvent().getSoc());
        medicalHistoryFilters.getHlt().completeWithValue(medicalHistory.getEvent().getHlt());
        medicalHistoryFilters.getPreferredTerm().completeWithValue(medicalHistory.getEvent().getPreferredTerm());

        medicalHistoryFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<MedicalHistory> other) {
        MedicalHistoryFiltersSummaryStatistics otherMedicalHistory = (MedicalHistoryFiltersSummaryStatistics) other;
        medicalHistoryFilters.getConditionStatus().complete(otherMedicalHistory.medicalHistoryFilters.getConditionStatus());
        medicalHistoryFilters.getTerm().complete(otherMedicalHistory.medicalHistoryFilters.getTerm());
        medicalHistoryFilters.getCategory().complete(otherMedicalHistory.medicalHistoryFilters.getCategory());
        medicalHistoryFilters.getStart().complete(otherMedicalHistory.medicalHistoryFilters.getStart());
        medicalHistoryFilters.getEnd().complete(otherMedicalHistory.medicalHistoryFilters.getEnd());
        medicalHistoryFilters.getCurrentMedication().complete(otherMedicalHistory.medicalHistoryFilters.getCurrentMedication());
        medicalHistoryFilters.getSoc().complete(otherMedicalHistory.medicalHistoryFilters.getSoc());
        medicalHistoryFilters.getPreferredTerm().complete(otherMedicalHistory.medicalHistoryFilters.getPreferredTerm());
        medicalHistoryFilters.getHlt().complete(otherMedicalHistory.medicalHistoryFilters.getHlt());

        count += other.count();
        medicalHistoryFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<MedicalHistory> getFilters() {
        return medicalHistoryFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<MedicalHistory> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<MedicalHistory> newStatistics() {
        return new MedicalHistoryFiltersSummaryStatistics();
    }
}
