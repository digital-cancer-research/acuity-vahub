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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DeathFilters extends Filters<Death> {

    public static DeathFilters empty() {
        return new DeathFilters();
    }

    protected SetFilter<String> deathCause = new SetFilter<>();
    protected SetFilter<String> autopsyPerformed = new SetFilter<>();
    protected SetFilter<String> designation = new SetFilter<>();
    protected SetFilter<String> deathRelatedToDisease = new SetFilter<>();
    protected SetFilter<String> hlt = new SetFilter<>();
    protected SetFilter<String> llt = new SetFilter<>();
    protected SetFilter<String> pt = new SetFilter<>();
    protected SetFilter<String> soc = new SetFilter<>();
    protected RangeFilter<Integer> daysFromFirstDoseToDeath = new RangeFilter<>();

    @Override
    public Query<Death> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Death> cqb = new CombinedQueryBuilder<>(Death.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Death.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(Death.Attributes.DEATH_CAUSE, this.deathCause))
                .add(getFilterQuery(Death.Attributes.AUTOPSY_PERFORMED, this.autopsyPerformed))
                .add(getFilterQuery(Death.Attributes.DESIGNATION, this.designation))
                .add(getFilterQuery(Death.Attributes.DEATH_RELATED_TO_DISEASE, this.deathRelatedToDisease))
                .add(getFilterQuery(Death.Attributes.HLT, this.hlt))
                .add(getFilterQuery(Death.Attributes.LLT, this.llt))
                .add(getFilterQuery(Death.Attributes.PT, this.pt))
                .add(getFilterQuery(Death.Attributes.SOC, this.soc))
                .add(getFilterQuery(Death.Attributes.DAYS_FROM_FIRST_DOSE_TO_DEATH, this.daysFromFirstDoseToDeath))
                .build();
    }
}
