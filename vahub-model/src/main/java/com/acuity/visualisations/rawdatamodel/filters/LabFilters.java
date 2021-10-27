/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LabFilters extends UsedInTflFilters<Lab> {

    private static final String SOURCE_TYPE_NAME = "sourceType";

    @JsonIgnore
    public static LabFilters empty() {
        return new LabFilters();
    }

    protected SetFilter<String> labcode = new SetFilter<>();
    protected SetFilter<String> labCategory = new SetFilter<>();
    protected RangeFilter<Double> labValue = new RangeFilter<>();
    protected SetFilter<String> labUnit = new SetFilter<>();
    protected RangeFilter<Double> baselineValue = new RangeFilter<>();
    protected SetFilter<String> baselineFlag = new SetFilter<>();
    protected RangeFilter<Double> changeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Double> percentageChangeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Double> lowerRefValue = new RangeFilter<>();
    protected RangeFilter<Double> upperRefValue = new RangeFilter<>();
    protected SetFilter<String> outOfRefRange = new SetFilter<>();
    protected RangeFilter<Double> refRangeNormValue = new RangeFilter<>();
    protected RangeFilter<Double> labValueOverUpperRefValue = new RangeFilter<>();
    protected RangeFilter<Double> labValueOverLowerRefValue = new RangeFilter<>();

    protected DateRangeFilter measurementTimePoint = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudy = new RangeFilter<>();
    protected RangeFilter<Double> visitNumber = new RangeFilter<>();
    protected RangeFilter<Double> analysisVisit = new RangeFilter<>();

    protected SetFilter<String> studyPeriods = new SetFilter<>();

    protected SetFilter<String> protocolScheduleTimepoint = new SetFilter<>();
    protected SetFilter<String> valueDipstick = new SetFilter<>();
    protected SetFilter<String> sourceType = new SetFilter<>();

    @Override
    public Query<Lab> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Lab> cqb = new CombinedQueryBuilder<>(Lab.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Lab.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(Lab.Attributes.LAB_CODE, labcode))
                .add(getFilterQuery(Lab.Attributes.LAB_CATEGORY, labCategory))
                .add(getFilterQuery(Lab.Attributes.LAB_VALUE, labValue))
                .add(getFilterQuery(Lab.Attributes.LAB_UNIT, labUnit))
                .add(getFilterQuery(Lab.Attributes.BASELINE_FLAG, baselineFlag))
                .add(getFilterQuery(Lab.Attributes.BASELINE_VALUE, baselineValue))
                .add(getFilterQuery(Lab.Attributes.CHANGE_FROM_BASELINE, changeFromBaselineValue))
                .add(getFilterQuery(Lab.Attributes.PERCENT_CHANGE_FROM_BASELINE, percentageChangeFromBaselineValue))
                .add(getFilterQuery(Lab.Attributes.UPPER_REF_RANGE, upperRefValue))
                .add(getFilterQuery(Lab.Attributes.LOWER_REF_RANGE, lowerRefValue))
                .add(getFilterQuery(Lab.Attributes.OUT_OF_REF_RANGE, outOfRefRange))
                .add(getFilterQuery(Lab.Attributes.REF_RANGE_NORM_VALUE, refRangeNormValue))
                .add(getFilterQuery(Lab.Attributes.TIMES_UPPER_REF, labValueOverUpperRefValue))
                .add(getFilterQuery(Lab.Attributes.TIMES_LOWER_REF, labValueOverLowerRefValue))

                .add(getFilterQuery(Lab.Attributes.MEASUREMENT_TIME_POINT, measurementTimePoint))
                .add(getFilterQuery(Lab.Attributes.DAYS_ON_STUDY, daysOnStudy))
                .add(getFilterQuery(Lab.Attributes.VISIT_NUMBER, VisitNumber.wrapVisitNumberFilter(visitNumber)))
                .add(getFilterQuery(Lab.Attributes.ANALYSIS_VISIT, analysisVisit))

                .add(getFilterQuery(Lab.Attributes.PROTOCOL_SCHEDULE_TIMEPOINT, protocolScheduleTimepoint))
                .add(getFilterQuery(Lab.Attributes.VALUE_DIPSTICK, valueDipstick))
                .add(getFilterQuery(Lab.Attributes.STUDY_PERIOD, studyPeriods))

                .add(getFilterQuery(Lab.Attributes.USED_IN_TFL, usedInTfl))
                .add(getFilterQuery(Lab.Attributes.SOURCE_TYPE, sourceType))
                .build();
    }

    @Override
    public List<String> getEmptyFilterNames() {
        List<String> emptyFieldNames = super.getEmptyFilterNames();

        if (this.sourceType.getValues().size() < 2 && !emptyFieldNames.contains(SOURCE_TYPE_NAME)) {
            emptyFieldNames.add(SOURCE_TYPE_NAME);
        }
        return emptyFieldNames;
    }
}
