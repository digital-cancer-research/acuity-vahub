package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ConmedFilters extends Filters<Conmed> {
    private SetFilter<String> medicationName = new SetFilter<>();
    private SetFilter<String> atcCode = new SetFilter<>();
    private RangeFilter<Double> dose = new RangeFilter<>();
    private SetFilter<String> doseUnits = new SetFilter<>();
    private SetFilter<String> doseFrequency = new SetFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private DateRangeFilter endDate = new DateRangeFilter();
    private RangeFilter<Integer> duration = new RangeFilter<>();
    private SetFilter<String> ongoing = new SetFilter<>();
    private RangeFilter<Integer> studyDayAtConmedStart = new RangeFilter<>();
    private RangeFilter<Integer> studyDayAtConmedEnd = new RangeFilter<>();
    private SetFilter<String> startPriorToRandomisation = new SetFilter<>();
    private SetFilter<String> endPriorToRandomisation = new SetFilter<>();
    private SetFilter<String> treatmentReason = new SetFilter<>();
    private SetFilter<String> atcText = new SetFilter<>();

    public static ConmedFilters empty() {
        return new ConmedFilters();
    }

    @Override
    public Query<Conmed> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Conmed> cqb = new CombinedQueryBuilder<>(Conmed.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Conmed.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(Conmed.Attributes.MEDICATION_NAME, this.medicationName))
                .add(getFilterQuery(Conmed.Attributes.ATC_CODE, this.atcCode))
                .add(getFilterQuery(Conmed.Attributes.DOSE, this.dose))
                .add(getFilterQuery(Conmed.Attributes.DOSE_UNITS, this.doseUnits))
                .add(getFilterQuery(Conmed.Attributes.DOSE_FREQUENCY, this.doseFrequency))
                .add(getFilterQuery(Conmed.Attributes.START_DATE, this.startDate))
                .add(getFilterQuery(Conmed.Attributes.END_DATE, this.endDate))
                .add(getFilterQuery(Conmed.Attributes.DURATION, this.duration))
                .add(getFilterQuery(Conmed.Attributes.ONGOING, this.ongoing))
                .add(getFilterQuery(Conmed.Attributes.STUDY_DAY_AT_START, this.studyDayAtConmedStart))
                .add(getFilterQuery(Conmed.Attributes.STUDY_DAY_AT_END, this.studyDayAtConmedEnd))
                .add(getFilterQuery(Conmed.Attributes.START_PRIOR_TO_RANDOMISATION, this.startPriorToRandomisation))
                .add(getFilterQuery(Conmed.Attributes.END_PRIOR_TO_RANDOMISATION, this.endPriorToRandomisation))
                .add(getFilterQuery(Conmed.Attributes.TREATMENT_REASON, this.treatmentReason))
                .add(getFilterQuery(Conmed.Attributes.ATC_TEXT, this.atcText))
                .build();
    }
}
