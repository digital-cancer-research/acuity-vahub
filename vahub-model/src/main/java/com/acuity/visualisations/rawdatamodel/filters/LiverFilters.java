package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LiverFilters extends Filters<Liver> {

    @JsonIgnore
    public static LiverFilters empty() {
        return new LiverFilters();
    }

    protected DateRangeFilter measurementTimePoint = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudy = new RangeFilter<>();
    protected RangeFilter<Double> visitNumber = new RangeFilter<>();
    protected RangeFilter<Double> labValue = new RangeFilter<>();
    protected RangeFilter<Double> baselineValue = new RangeFilter<>();
    protected SetFilter<String> baselineFlag = new SetFilter<>();
    protected RangeFilter<Double> changeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Double> percentageChangeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Double> refRangeNormValue = new RangeFilter<>();
    protected RangeFilter<Double> labValueOverUpperRefValue = new RangeFilter<>();
    protected RangeFilter<Double> labValueOverLowerRefValue = new RangeFilter<>();
    protected RangeFilter<Double> lowerRefValue = new RangeFilter<>();
    protected RangeFilter<Double> upperRefValue = new RangeFilter<>();

    @Override
    public Query<Liver> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Liver> cqb = new CombinedQueryBuilder<>(Liver.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Liver.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(Liver.Attributes.LAB_VALUE, labValue))
                .add(getFilterQuery(Liver.Attributes.MEASUREMENT_TIME_POINT, measurementTimePoint))
                .add(getFilterQuery(Liver.Attributes.DAYS_ON_STUDY, daysOnStudy))
                .add(getFilterQuery(Liver.Attributes.VISIT_NUMBER, visitNumber))
                .add(getFilterQuery(Liver.Attributes.BASELINE_VALUE, baselineValue))
                .add(getFilterQuery(Liver.Attributes.BASELINE_FLAG, baselineFlag))
                .add(getFilterQuery(Liver.Attributes.CHANGE_FROM_BASELINE, changeFromBaselineValue))
                .add(getFilterQuery(Liver.Attributes.PERCENT_CHANGE_FROM_BASELINE, percentageChangeFromBaselineValue))
                .add(getFilterQuery(Liver.Attributes.REF_RANGE_NORM_VALUE, refRangeNormValue))
                .add(getFilterQuery(Liver.Attributes.TIMES_UPPER_REF, labValueOverUpperRefValue))
                .add(getFilterQuery(Liver.Attributes.TIMES_LOWER_REF, labValueOverLowerRefValue))
                .add(getFilterQuery(Liver.Attributes.UPPER_REF_RANGE, upperRefValue))
                .add(getFilterQuery(Liver.Attributes.LOWER_REF_RANGE, lowerRefValue))
                .build();
    }

}
