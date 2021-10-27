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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CvotEndpointFilters extends Filters<CvotEndpoint> {

    @JsonIgnore
    public static CvotEndpointFilters empty() {
        return new CvotEndpointFilters();
    }

    private SetFilter<String> studyId = new SetFilter<>();
    private SetFilter<String> studyPart = new SetFilter<>();
    private SetFilter<String> subjectId = new SetFilter<>();
    private SetFilter<String> aeNumber = new SetFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private SetFilter<String> term = new SetFilter<>();
    private SetFilter<String> category1 = new SetFilter<>();
    private SetFilter<String> category2 = new SetFilter<>();
    private SetFilter<String> category3 = new SetFilter<>();
    private SetFilter<String> description1 = new SetFilter<>();
    private SetFilter<String> description2 = new SetFilter<>();
    private SetFilter<String> description3 = new SetFilter<>();

    @Override
    public Query<CvotEndpoint> getQuery() {
        return getQuery(Collections.emptyList());
    }

    @Override
    public Query<CvotEndpoint> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<CvotEndpoint> cqb = new CombinedQueryBuilder<>(CvotEndpoint.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(CvotEndpoint.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.
                add(getFilterQuery(CvotEndpoint.Attributes.STUDY_ID, studyId)).
                add(getFilterQuery(CvotEndpoint.Attributes.STUDY_PART, studyPart)).
                add(getFilterQuery(CvotEndpoint.Attributes.TERM, term)).
                add(getFilterQuery(CvotEndpoint.Attributes.START_DATE, startDate)).
                add(getFilterQuery(CvotEndpoint.Attributes.AE_NUMBER, aeNumber)).
                add(getFilterQuery(CvotEndpoint.Attributes.CATEGORY_1, category1)).
                add(getFilterQuery(CvotEndpoint.Attributes.CATEGORY_2, category2)).
                add(getFilterQuery(CvotEndpoint.Attributes.CATEGORY_3, category3)).
                add(getFilterQuery(CvotEndpoint.Attributes.DESCRIPTION_1, description1)).
                add(getFilterQuery(CvotEndpoint.Attributes.DESCRIPTION_2, description2)).
                add(getFilterQuery(CvotEndpoint.Attributes.DESCRIPTION_3, description3)).
                build();
    }
}
