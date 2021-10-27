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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker.Attributes.GENE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker.Attributes.MUTATION;

/**
 * Biomarkers filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BiomarkerFilters extends Filters<Biomarker> {

    @JsonIgnore
    public static BiomarkerFilters empty() {
        return new BiomarkerFilters();
    }

    private SetFilter<String> gene = new SetFilter<>();
    private SetFilter<String> mutation = new SetFilter<>();
    private RangeFilter<Integer> genePercentage = new RangeFilter<>();

    @Override
    public Query<Biomarker> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Biomarker> cqb = new CombinedQueryBuilder<>(Biomarker.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Biomarker.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(GENE, gene)).
                add(getFilterQuery(MUTATION, mutation)).
                build();
    }
}
