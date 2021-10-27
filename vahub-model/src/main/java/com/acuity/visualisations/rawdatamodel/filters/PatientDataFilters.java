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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.MEASUREMENT_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.MEASUREMENT_NAME;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.REPORT_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.SOURCE_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.UNIT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData.Attributes.VALUE;

/**
 * PatientData filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PatientDataFilters extends Filters<PatientData> {

    @JsonIgnore
    public static PatientDataFilters empty() {
        return new PatientDataFilters();
    }

    protected SetFilter<String> measurementName = new SetFilter<>();
    protected RangeFilter<Double> value = new RangeFilter<>();
    protected SetFilter<String> unit = new SetFilter<>();
    protected DateRangeFilter measurementDate = new DateRangeFilter();
    protected DateRangeFilter reportDate = new DateRangeFilter();
    protected SetFilter<String> sourceType = new SetFilter<>();

    @Override
    public Query<PatientData> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<PatientData> cqb = new CombinedQueryBuilder<>(PatientData.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(PatientData.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(MEASUREMENT_NAME, measurementName))
                .add(getFilterQuery(VALUE, value))
                .add(getFilterQuery(UNIT, unit))
                .add(getFilterQuery(MEASUREMENT_DATE, measurementDate))
                .add(getFilterQuery(REPORT_DATE, reportDate))
                .add(getFilterQuery(SOURCE_TYPE, sourceType))
                .build();
    }
}
