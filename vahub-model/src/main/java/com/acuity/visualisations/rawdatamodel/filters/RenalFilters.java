package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RenalFilters extends Filters<Renal> {

    @JsonIgnore
    public static RenalFilters empty() {
        return new RenalFilters();
    }

    protected SetFilter<String> measurementName = new SetFilter<>();
    protected DateRangeFilter measurementTimePoint = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudy = new RangeFilter<>();
    protected RangeFilter<Double> analysisVisit = new RangeFilter<>();
    protected RangeFilter<Double> visitNumber = new RangeFilter<>();
    protected RangeFilter<Double> labValue = new RangeFilter<>();
    protected SetFilter<String> labUnit = new SetFilter<>();
    protected RangeFilter<Double> upperRefValue = new RangeFilter<>();
    protected RangeFilter<Double> labValueOverUpperRefValue = new RangeFilter<>();
    protected SetFilter<String> ckdStage = new SetFilter<>();
    protected SetFilter<String> studyPeriods = new SetFilter<>();

    @Override
    public Query<Renal> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Renal> cqb = new CombinedQueryBuilder<>(Renal.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Renal.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(Renal.Attributes.LAB_CODE, measurementName))
                .add(getFilterQuery(Renal.Attributes.LAB_UNIT, labUnit))
                .add(getFilterQuery(Renal.Attributes.LAB_VALUE, labValue))
                .add(getFilterQuery(Renal.Attributes.MEASUREMENT_TIME_POINT, measurementTimePoint))
                .add(getFilterQuery(Renal.Attributes.DAYS_ON_STUDY, daysOnStudy))
                .add(getFilterQuery(Renal.Attributes.VISIT_NUMBER, VisitNumber.wrapVisitNumberFilter(visitNumber)))
                .add(getFilterQuery(Renal.Attributes.ANALYSIS_VISIT, analysisVisit))
                .add(getFilterQuery(Renal.Attributes.TIMES_UPPER_REF, labValueOverUpperRefValue))
                .add(getFilterQuery(Renal.Attributes.UPPER_REF_RANGE, upperRefValue))
                .add(getFilterQuery(Renal.Attributes.STUDY_PERIOD, studyPeriods))
                .add(getFilterQuery(Renal.Attributes.CKD_STAGE_NAME, ckdStage))
                .build();
    }
}
