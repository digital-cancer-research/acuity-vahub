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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class NicotineFilters extends Filters<Nicotine> {

    private SetFilter<String> category = new SetFilter<>();
    private SetFilter<String> type = new SetFilter<>();
    private SetFilter<String> useOccurrence = new SetFilter<>();
    private SetFilter<String> otherTypeSpec = new SetFilter<>();
    private SetFilter<String> subTypeUseOccurrence = new SetFilter<>();
    private SetFilter<String> currentUseSpec = new SetFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private DateRangeFilter endDate = new DateRangeFilter();
    private RangeFilter<Integer> consumption = new RangeFilter<>();
    private SetFilter<String> frequencyInterval = new SetFilter<>();
    private RangeFilter<Integer> numberPackYears = new RangeFilter<>();

    public static NicotineFilters empty() {
        return new NicotineFilters();
    }

    @Override
    public Query<Nicotine> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Nicotine> cqb = new CombinedQueryBuilder<>(Nicotine.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Nicotine.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(Nicotine.Attributes.CATEGORY, this.category))
                .add(getFilterQuery(Nicotine.Attributes.TYPE, this.type))
                .add(getFilterQuery(Nicotine.Attributes.USE_OCCURRENCE, this.useOccurrence))
                .add(getFilterQuery(Nicotine.Attributes.OTHER_TYPE_SPEC, this.otherTypeSpec))
                .add(getFilterQuery(Nicotine.Attributes.SUB_TYPE_USE_OCCURRENCE, this.subTypeUseOccurrence))
                .add(getFilterQuery(Nicotine.Attributes.CURRENT_USE_SPEC, this.currentUseSpec))
                .add(getFilterQuery(Nicotine.Attributes.START_DATE, this.startDate))
                .add(getFilterQuery(Nicotine.Attributes.END_DATE, this.endDate))
                .add(getFilterQuery(Nicotine.Attributes.CONSUMPTION, this.consumption))
                .add(getFilterQuery(Nicotine.Attributes.FREQUENCY_INTERVAL, this.frequencyInterval))
                .add(getFilterQuery(Nicotine.Attributes.NUMBER_PACK_YEARS, this.numberPackYears))
                .build();
    }
}
