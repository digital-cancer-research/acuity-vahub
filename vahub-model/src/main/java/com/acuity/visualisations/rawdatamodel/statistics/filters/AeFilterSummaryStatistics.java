package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;

public class AeFilterSummaryStatistics implements FilterSummaryStatistics<Ae> {

    private AeFilters aeFilters = new AeFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Ae aeEvent = (Ae) value;
        
        aeFilters.getPt().completeWithValue(aeEvent.getEvent().getPt());
        aeFilters.getHlt().completeWithValue(aeEvent.getEvent().getHlt());
        aeFilters.getSoc().completeWithValue(aeEvent.getEvent().getSoc());
        aeFilters.getSeverity().completeWithValue(aeEvent.getEvent().getMaxAeSeverity());
        aeFilters.getSerious().completeWithValue(aeEvent.getEvent().getSerious());
        aeFilters.getStartDate().completeWithValue(aeEvent.getEvent().getMinStartDate());
        aeFilters.getEndDate().completeWithValue(aeEvent.getEvent().getMaxEndDate());
        aeFilters.getDaysOnStudyAtStart().completeWithValue(aeEvent.getDaysOnStudyAtStart());
        aeFilters.getDaysOnStudyAtEnd().completeWithValue(aeEvent.getDaysOnStudyAtEnd());
        aeFilters.getDuration().completeWithValue(aeEvent.getDuration());
        
        aeFilters.getDaysFromPrevDoseToStart().completeWithValue(aeEvent.getEvent().getDaysFromPrevDoseToStart());        
        aeFilters.getDescription().completeWithValue(aeEvent.getEvent().getText());
        aeFilters.getComment().completeWithValue(aeEvent.getEvent().getComment());
        aeFilters.getActionTaken().completeWithValue(aeEvent.getEvent().getActionTaken());
        aeFilters.getCausality().completeWithValue(aeEvent.getEvent().getCausality());
        aeFilters.getDrugsActionTaken().completeWithValue(aeEvent.getEvent().getDrugsActionTaken());
        aeFilters.getDrugsCausality().completeWithValue(aeEvent.getEvent().getDrugsCausality());
        aeFilters.getAeEndPriorToRandomisation().completeWithValue(aeEvent.getEndDatePriorToRandomisation());
        aeFilters.getAeStartPriorToRandomisation().completeWithValue(aeEvent.getStartDatePriorToRandomisation());
        aeFilters.getAeNumber().completeWithValue(aeEvent.getAeNumber());
        aeFilters.getAeOfSpecialInterest().completeWithValue(aeEvent.getEvent().getAeOfSpecialInterest());

        aeFilters.getOutcome().completeWithValue(aeEvent.getEvent().getOutcome());
        aeFilters.getRequiredTreatment().completeWithValue(aeEvent.getEvent().getRequiredTreatment());
        aeFilters.getTreatmentEmergent().completeWithValue(aeEvent.getEvent().getTreatmentEmergent());
        aeFilters.getRequiresHospitalisation().completeWithValue(aeEvent.getEvent().getRequiresHospitalisation());
        aeFilters.getCausedSubjectWithdrawal().completeWithValue(aeEvent.getEvent().getCausedSubjectWithdrawal());
        aeFilters.getDoseLimitingToxicity().completeWithValue(aeEvent.getEvent().getDoseLimitingToxicity());
        aeFilters.getTimePointDoseLimitingToxicity().completeWithValue(aeEvent.getEvent().getTimepoint());
        aeFilters.getImmuneMediated().completeWithValue(aeEvent.getEvent().getImmuneMediated());
        aeFilters.getInfusionReaction().completeWithValue(aeEvent.getEvent().getInfusionReaction());
        aeFilters.getUsedInTfl().completeWithValue(aeEvent.getEvent().getUsedInTfl());
        aeFilters.getStudyPeriods().completeWithValue(aeEvent.getEvent().getStudyPeriod());
        aeFilters.getSpecialInterestGroup().completeWithValues(aeEvent.getEvent().getSpecialInterestGroups());
        aeFilters.getSuspectedEndpointCategory().completeWithValue(aeEvent.getEvent().getSuspectedEndpointCategory());
        aeFilters.getSuspectedEndpoint().completeWithValue(aeEvent.getEvent().getSuspectedEndpoint());

        aeFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Ae> other) {
        AeFilterSummaryStatistics otherAeEvent = (AeFilterSummaryStatistics) other;
        
        aeFilters.getPt().complete(otherAeEvent.aeFilters.getPt());
        aeFilters.getHlt().complete(otherAeEvent.aeFilters.getHlt());
        aeFilters.getSoc().complete(otherAeEvent.aeFilters.getSoc());
        aeFilters.getSeverity().complete(otherAeEvent.aeFilters.getSeverity());
        aeFilters.getSerious().complete(otherAeEvent.aeFilters.getSerious());
        aeFilters.getStartDate().complete(otherAeEvent.aeFilters.getStartDate());
        aeFilters.getEndDate().complete(otherAeEvent.aeFilters.getEndDate());
        aeFilters.getDaysOnStudyAtStart().complete(otherAeEvent.aeFilters.getDaysOnStudyAtStart());
        aeFilters.getDaysOnStudyAtEnd().complete(otherAeEvent.aeFilters.getDaysOnStudyAtEnd());
        aeFilters.getDuration().complete(otherAeEvent.aeFilters.getDuration());
        aeFilters.getDaysFromPrevDoseToStart().complete(otherAeEvent.aeFilters.getDaysFromPrevDoseToStart());
        
        aeFilters.getDescription().complete(otherAeEvent.aeFilters.getDescription());
        aeFilters.getComment().complete(otherAeEvent.aeFilters.getComment());
        aeFilters.getActionTaken().complete(otherAeEvent.aeFilters.getActionTaken());
        aeFilters.getCausality().complete(otherAeEvent.aeFilters.getCausality());
        aeFilters.getDrugsActionTaken().complete(otherAeEvent.aeFilters.getDrugsActionTaken());
        aeFilters.getDrugsCausality().complete(otherAeEvent.aeFilters.getDrugsCausality());
        aeFilters.getAeEndPriorToRandomisation().complete(otherAeEvent.aeFilters.getAeEndPriorToRandomisation());
        aeFilters.getAeStartPriorToRandomisation().complete(otherAeEvent.aeFilters.getAeStartPriorToRandomisation());
        aeFilters.getAeNumber().complete(otherAeEvent.aeFilters.getAeNumber());
        aeFilters.getAeOfSpecialInterest().complete(otherAeEvent.aeFilters.getAeOfSpecialInterest());
        
        aeFilters.getOutcome().complete(otherAeEvent.aeFilters.getOutcome());
        aeFilters.getRequiredTreatment().complete(otherAeEvent.aeFilters.getRequiredTreatment());
        aeFilters.getTreatmentEmergent().complete(otherAeEvent.aeFilters.getTreatmentEmergent());
        aeFilters.getRequiresHospitalisation().complete(otherAeEvent.aeFilters.getRequiresHospitalisation());
        aeFilters.getCausedSubjectWithdrawal().complete(otherAeEvent.aeFilters.getCausedSubjectWithdrawal());
        aeFilters.getDoseLimitingToxicity().complete(otherAeEvent.aeFilters.getDoseLimitingToxicity());
        aeFilters.getTimePointDoseLimitingToxicity().complete(otherAeEvent.aeFilters.getTimePointDoseLimitingToxicity());
        aeFilters.getImmuneMediated().complete(otherAeEvent.aeFilters.getImmuneMediated());
        aeFilters.getInfusionReaction().complete(otherAeEvent.aeFilters.getInfusionReaction());
        aeFilters.getUsedInTfl().complete(otherAeEvent.aeFilters.getUsedInTfl());
        aeFilters.getStudyPeriods().complete(otherAeEvent.aeFilters.getStudyPeriods());
        aeFilters.getSpecialInterestGroup().complete(otherAeEvent.aeFilters.getSpecialInterestGroup());
        aeFilters.getSuspectedEndpointCategory().complete(otherAeEvent.aeFilters.getSuspectedEndpointCategory());
        aeFilters.getSuspectedEndpoint().complete(otherAeEvent.aeFilters.getSuspectedEndpoint());
        
        aeFilters.setMatchedItemsCount(count);
    }

    @Override
    public AeFilters getFilters() {
        return aeFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Ae> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Ae> newStatistics() {
        return new AeFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{aeFilters=%s}",
                this.getClass().getSimpleName(),
                this.aeFilters.toString());
    }
}
