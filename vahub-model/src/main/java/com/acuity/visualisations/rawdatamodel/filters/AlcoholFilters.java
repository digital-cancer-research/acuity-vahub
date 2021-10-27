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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import com.googlecode.cqengine.query.Query;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class AlcoholFilters extends Filters<Alcohol> {

    private SetFilter<String> substanceCategory = new SetFilter<>();
    private SetFilter<String> substanceUseOccurrence = new SetFilter<>();
    private SetFilter<String> substanceType = new SetFilter<>();
    private SetFilter<String> otherSubstanceTypeSpec = new SetFilter<>();
    private SetFilter<String> frequency = new SetFilter<>();
    private SetFilter<String> substanceTypeUseOccurrence = new SetFilter<>();
    private RangeFilter<Double> substanceConsumption = new RangeFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private DateRangeFilter endDate = new DateRangeFilter();

    public static AlcoholFilters empty() {
        return new AlcoholFilters();
    }

    @Override
    public Query<Alcohol> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Alcohol> cqb = new CombinedQueryBuilder<>(Alcohol.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Alcohol.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(Alcohol.Attributes.SUBSTANCE_CATEGORY, this.substanceCategory))
                .add(getFilterQuery(Alcohol.Attributes.SUBSTANCE_USE_OCCURRENCE, this.substanceUseOccurrence))
                .add(getFilterQuery(Alcohol.Attributes.SUBSTANCE_TYPE, this.substanceType))
                .add(getFilterQuery(Alcohol.Attributes.OTHER_SUBSTANCE_TYPE_SPEC, this.otherSubstanceTypeSpec))
                .add(getFilterQuery(Alcohol.Attributes.FREQUENCY, this.frequency))
                .add(getFilterQuery(Alcohol.Attributes.SUBSTANCE_TYPE_USE_OCCURRENCE, this.substanceTypeUseOccurrence))
                .add(getFilterQuery(Alcohol.Attributes.SUBSTANCE_CONSUMPTION, this.substanceConsumption))
                .add(getFilterQuery(Alcohol.Attributes.START_DATE, this.startDate))
                .add(getFilterQuery(Alcohol.Attributes.END_DATE, this.endDate))
                .build();
    }
}
