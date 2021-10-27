package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;


public class ConmedFiltersSummaryStatistics implements FilterSummaryStatistics<Conmed> {

    private ConmedFilters conmedFilters = new ConmedFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Conmed conmed = (Conmed) value;
        conmedFilters.getAtcCode().completeWithValue(conmed.getEvent().getAtcCode());
        conmedFilters.getAtcText().completeWithValue(conmed.getEvent().getAtcText());
        conmedFilters.getDose().completeWithValue(conmed.getEvent().getDose());
        conmedFilters.getDoseUnits().completeWithValue(conmed.getEvent().getDoseUnits());
        conmedFilters.getDoseFrequency().completeWithValue(conmed.getEvent().getDoseFrequency());
        conmedFilters.getOngoing().completeWithValue(conmed.getConmedTreatmentOngoing());
        conmedFilters.getTreatmentReason().completeWithValue(conmed.getEvent().getTreatmentReason());
        conmedFilters.getStartDate().completeWithValue(conmed.getStartDate());
        conmedFilters.getEndDate().completeWithValue(conmed.getEndDate());
        conmedFilters.getDuration().completeWithValue(conmed.getDuration());
        conmedFilters.getStudyDayAtConmedStart().completeWithValue(conmed.getStudyDayAtConmedStart());
        conmedFilters.getStudyDayAtConmedEnd().completeWithValue(conmed.getStudyDayAtConmedEnd());
        conmedFilters.getStartPriorToRandomisation().completeWithValue(conmed.getConmedStartPriorToRandomisation());
        conmedFilters.getEndPriorToRandomisation().completeWithValue(conmed.getConmedEndPriorToRandomisation());
        conmedFilters.getMedicationName().completeWithValue(conmed.getEvent().getMedicationName());

        conmedFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Conmed> other) {
        ConmedFiltersSummaryStatistics otherConmed = (ConmedFiltersSummaryStatistics) other;
        conmedFilters.getAtcCode().complete(otherConmed.conmedFilters.getAtcCode());
        conmedFilters.getAtcText().complete(otherConmed.conmedFilters.getAtcText());
        conmedFilters.getDose().complete(otherConmed.conmedFilters.getDose());
        conmedFilters.getDoseUnits().complete(otherConmed.conmedFilters.getDoseUnits());
        conmedFilters.getDoseFrequency().complete(otherConmed.conmedFilters.getDoseFrequency());
        conmedFilters.getOngoing().complete(otherConmed.conmedFilters.getOngoing());
        conmedFilters.getTreatmentReason().complete(otherConmed.conmedFilters.getTreatmentReason());
        conmedFilters.getStartDate().complete(otherConmed.conmedFilters.getStartDate());
        conmedFilters.getEndDate().complete(otherConmed.conmedFilters.getEndDate());
        conmedFilters.getDuration().complete(otherConmed.conmedFilters.getDuration());
        conmedFilters.getStudyDayAtConmedStart().complete(otherConmed.conmedFilters.getStudyDayAtConmedStart());
        conmedFilters.getStudyDayAtConmedEnd().complete(otherConmed.conmedFilters.getStudyDayAtConmedEnd());
        conmedFilters.getStartPriorToRandomisation().complete(otherConmed.conmedFilters.getStartPriorToRandomisation());
        conmedFilters.getEndPriorToRandomisation().complete(otherConmed.conmedFilters.getEndPriorToRandomisation());
        conmedFilters.getMedicationName().complete(otherConmed.conmedFilters.getMedicationName());
        count += other.count();
        conmedFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Conmed> getFilters() {
        return conmedFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Conmed> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Conmed> newStatistics() {
        return new ConmedFiltersSummaryStatistics();
    }
}
