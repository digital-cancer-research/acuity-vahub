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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.Attributes.RADIATION_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.Attributes.THERAPY_STATUS;
import static com.googlecode.cqengine.query.QueryFactory.none;

/**
 * Radiotherapy filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
public class RadiotherapyFilters extends Filters<Radiotherapy> {

    @JsonIgnore
    public static RadiotherapyFilters empty() {
        return new RadiotherapyFilters();
    }

    @JsonIgnore
    public RadiotherapyFilters(boolean enabled) {
        this();
        this.isRadiotherapyEnabled = enabled;

    }

    protected SetFilter<String> therapyStatus = new SetFilter<>();
    protected RangeFilter<Double> radiationDose = new RangeFilter<>();

    @JsonIgnore
    private boolean isRadiotherapyEnabled = true;

    @Override
    public Query<Radiotherapy> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Radiotherapy> cqb = new CombinedQueryBuilder<>(Radiotherapy.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Radiotherapy.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        if (!isRadiotherapyEnabled) {
            return cqb.add(none(Radiotherapy.class)).build();
        }
        return cqb
                .add(getFilterQuery(RADIATION_DOSE, radiationDose))
                .add(getFilterQuery(THERAPY_STATUS, therapyStatus))
                .build();
    }
}
