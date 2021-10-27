package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class VitalFilters extends Filters<Vital> {

    @JsonIgnore
    public static VitalFilters empty() {
        return new VitalFilters();
    }

    protected SetFilter<String> vitalsMeasurements = new SetFilter<>();
    protected SetFilter<String> plannedTimePoints = new SetFilter<>();
    protected DateRangeFilter measurementDate = new DateRangeFilter();
    protected RangeFilter<Integer> daysSinceFirstDose = new RangeFilter<>();
    protected RangeFilter<Double> visitNumber = new RangeFilter<>();
    protected SetFilter<String> scheduleTimepoints = new SetFilter<>();
    protected SetFilter<String> units = new SetFilter<>();
    protected RangeFilter<Double> percentageChangeFromBaseline = new RangeFilter<>();
    protected RangeFilter<Double> changeFromBaseline = new RangeFilter<>();
    protected RangeFilter<Double> baseline = new RangeFilter<>();
    protected SetFilter<String> baselineFlags = new SetFilter<>();
    protected RangeFilter<Double> resultValue = new RangeFilter<>();
    protected RangeFilter<Double> analysisVisit = new RangeFilter<>();
    protected SetFilter<String> studyPeriods = new SetFilter<>();
    protected DateRangeFilter lastDoseDate = new DateRangeFilter();
    protected SetFilter<String> lastDoseAmounts = new SetFilter<>();
    protected SetFilter<String> anatomicalLocations = new SetFilter<>();
    protected SetFilter<String> sidesOfInterest = new SetFilter<>();
    protected SetFilter<String> physicalPositions = new SetFilter<>();
    protected SetFilter<String> clinicallySignificant = new SetFilter<>();

    @Override
    public Query<Vital> getQuery() {
        return getQuery(newArrayList());
    }

    @Override
    public Query<Vital> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Vital> cqb = new CombinedQueryBuilder<>(Vital.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Vital.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.
                add(getFilterQuery(Vital.Attributes.MEASUREMENT, vitalsMeasurements)).
                add(getFilterQuery(Vital.Attributes.PLANNED_TIMEPOINT, plannedTimePoints)).
                add(getFilterQuery(Vital.Attributes.MEASUREMENT_DATE, measurementDate)).
                add(getFilterQuery(Vital.Attributes.DAYS_SINCE_FIRST_DOSE, daysSinceFirstDose)).
                add(getFilterQuery(Vital.Attributes.VISIT_NUMBER, VisitNumber.wrapVisitNumberFilter(visitNumber))).
                add(getFilterQuery(Vital.Attributes.SCHEDULE_TIMEPOINT, scheduleTimepoints)).
                add(getFilterQuery(Vital.Attributes.UNIT, units)).
                add(getFilterQuery(Vital.Attributes.PERCENTAGE_CHANGE_FROM_BASELINE, percentageChangeFromBaseline)).
                add(getFilterQuery(Vital.Attributes.CHANGE_FROM_BASELINE, changeFromBaseline)).
                add(getFilterQuery(Vital.Attributes.BASELINE, baseline)).
                add(getFilterQuery(Vital.Attributes.BASELINE_FLAG, baselineFlags)).
                add(getFilterQuery(Vital.Attributes.RESULT_VALUE, resultValue)).
                add(getFilterQuery(Vital.Attributes.ANALYSIS_VISIT, analysisVisit)).
                add(getFilterQuery(Vital.Attributes.STUDY_PERIOD, studyPeriods)).
                add(getFilterQuery(Vital.Attributes.LAST_DOSE_DATE, lastDoseDate)).
                add(getFilterQuery(Vital.Attributes.LAST_DOSE_AMOUNT, lastDoseAmounts)).
                add(getFilterQuery(Vital.Attributes.ANATOMICAL_LOCATION, anatomicalLocations)).
                add(getFilterQuery(Vital.Attributes.SIDES_OF_INTEREST, sidesOfInterest)).
                add(getFilterQuery(Vital.Attributes.PHYSICAL_POSITION, physicalPositions)).
                add(getFilterQuery(Vital.Attributes.CLINICALLY_SIGNIFICANT, clinicallySignificant)).
                build();
    }
}
