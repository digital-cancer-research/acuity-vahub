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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.CHEMOTHERAPY_BEST_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.NUMBER_OF_CHEMOTHERAPY_CYCLES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.PREFERRED_MED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.REASON_FOR_CHEMOTHERAPY_FAILURE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.THERAPY_CLASS;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy.Attributes.THERAPY_STATUS;

/**
 * Chemotherapy filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ChemotherapyFilters extends Filters<Chemotherapy> {

    @JsonIgnore
    public static ChemotherapyFilters empty() {
        return new ChemotherapyFilters();
    }

    protected SetFilter<String> therapyStatus = new SetFilter<>();
    protected SetFilter<String> preferredMed = new SetFilter<>();
    protected SetFilter<String> therapyClass = new SetFilter<>();
    protected SetFilter<String> reasonForChemotherapyFailure = new SetFilter<>();
    protected SetFilter<String> chemotherapyBestResponse = new SetFilter<>();
    protected RangeFilter<Integer> numberOfChemotherapyCycles = new RangeFilter<>();

    @Override
    public Query<Chemotherapy> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Chemotherapy> cqb = new CombinedQueryBuilder<>(Chemotherapy.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Chemotherapy.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(THERAPY_STATUS, therapyStatus))
                .add(getFilterQuery(PREFERRED_MED, preferredMed))
                .add(getFilterQuery(THERAPY_CLASS, therapyClass))
                .add(getFilterQuery(REASON_FOR_CHEMOTHERAPY_FAILURE, reasonForChemotherapyFailure))
                .add(getFilterQuery(CHEMOTHERAPY_BEST_RESPONSE, chemotherapyBestResponse))
                .add(getFilterQuery(NUMBER_OF_CHEMOTHERAPY_CYCLES, numberOfChemotherapyCycles))
                .build();
    }
}
