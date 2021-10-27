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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

/**
 * SubjectExt filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SubjectExtFilters extends Filters<SubjectExt> {

    @JsonIgnore
    public static SubjectExtFilters empty() {
        return new SubjectExtFilters();
    }

    protected DateRangeFilter diagnosisDate = new DateRangeFilter(); // pathology
    protected RangeFilter<Integer> daysFromDiagnosisDate = new RangeFilter<>(); // pathology
    protected DateRangeFilter recentProgressionDate = new DateRangeFilter(); // disease extent

    @Override
    public Query<SubjectExt> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SubjectExt> cqb = new CombinedQueryBuilder<>(SubjectExt.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SubjectExt.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(SubjectExt.Attributes.DIAGNOSIS_DATE, diagnosisDate))
                .add(getFilterQuery(SubjectExt.Attributes.DAYS_FROM_DIAGNOSIS_DATE, daysFromDiagnosisDate))
                .add(getFilterQuery(SubjectExt.Attributes.RECENT_PROGRESSION_DATE, recentProgressionDate))
                .build();
    }
}

