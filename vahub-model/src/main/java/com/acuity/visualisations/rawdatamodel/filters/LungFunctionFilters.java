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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LungFunctionFilters extends Filters<LungFunction> {

    private SetFilter<String> measurementName = new SetFilter<>();
    private DateRangeFilter measurementTimePoint = new DateRangeFilter();
    private RangeFilter<Integer> daysOnStudy = new RangeFilter<>();
    private SetFilter<String> protocolScheduleTimepoint = new SetFilter<>();
    private RangeFilter<Double> visitNumber = new RangeFilter<>();
    private RangeFilter<Double> resultValue = new RangeFilter<>();
    private SetFilter<String> resultUnit = new SetFilter<>();
    private RangeFilter<Double> baselineValue = new RangeFilter<>();
    private RangeFilter<Double> changeFromBaseline = new RangeFilter<>();
    private RangeFilter<Double> percentChangeFromBaseline = new RangeFilter<>();
    private SetFilter<String> baselineFlag = new SetFilter<>();

    public static LungFunctionFilters empty() {
        return new LungFunctionFilters();
    }

    @Override
    public Query<LungFunction> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<LungFunction> cqb = new CombinedQueryBuilder<>(LungFunction.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            final SetFilter<String> stringSetFilter = new SetFilter<>(subjectIds);
            cqb.add(getFilterQuery(LungFunction.Attributes.SUBJECT_ID, stringSetFilter));
        }
        return cqb
                .add(getFilterQuery(Attributes.MEASUREMENT_NAME, this.getMeasurementName()))
                .add(getFilterQuery(Attributes.MEASUREMENT_TIME_POINT, this.getMeasurementTimePoint()))
                .add(getFilterQuery(Attributes.DAYS_ON_STUDY, this.getDaysOnStudy()))
                .add(getFilterQuery(Attributes.PROTOCOL_SCHEDULE_TIMEPOINT, this.getProtocolScheduleTimepoint()))
                .add(getFilterQuery(Attributes.VISIT_NUMBER, VisitNumber.wrapVisitNumberFilter(visitNumber)))
                .add(getFilterQuery(Attributes.RESULT_VALUE, this.getResultValue()))
                .add(getFilterQuery(Attributes.RESULT_UNIT, this.getResultUnit()))
                .add(getFilterQuery(Attributes.BASELINE_VALUE, this.getBaselineValue()))
                .add(getFilterQuery(Attributes.CHANGE_FROM_BASELINE, this.getChangeFromBaseline()))
                .add(getFilterQuery(Attributes.PERCENT_CHANGE_FROM_BASELINE, this.getPercentChangeFromBaseline()))
                .add(getFilterQuery(Attributes.BASELINE_FLAG, this.getBaselineFlag()))
                .build();
    }
}
