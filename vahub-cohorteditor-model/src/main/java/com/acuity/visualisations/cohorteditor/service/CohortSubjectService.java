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

package com.acuity.visualisations.cohorteditor.service;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

@Service
public class CohortSubjectService {
    @Autowired
    private PopulationService populationService;
    @Autowired
    private AeService aeService;


    public List<String> getDistinctSubjectIds(
            Datasets datasets,
            List<Filters> filters,
            SavedFilter.Operator operator) {
        // Get subject ids for each filters
        Set<Set<String>> filteredSubjectsLists = newHashSet();

        filters.forEach(f -> {
            List<String> subjects;
            if (f instanceof PopulationFilters) {
                subjects = populationService.getFilteredData(datasets, (PopulationFilters) f)
                        .stream()
                        .map(e -> e.getSubject().getSubjectCode())
                        .distinct()
                        .collect(toList());
            } else if (f instanceof AeFilters) {
                subjects = aeService.getFilteredData(datasets, (AeFilters) f, PopulationFilters.empty())
                        .stream()
                        .map(e -> e.getSubject().getSubjectCode())
                        .distinct()
                        .collect(toList());
            } else {
                throw new IllegalStateException("Unknown filters type " + f.getClass() + ". " + f);
            }

            filteredSubjectsLists.add(newHashSet(subjects));
        });

        BinaryOperator<Set<String>> op = (operator == SavedFilter.Operator.OR) ? Sets::union : Sets::intersection;

        return filteredSubjectsLists.stream().reduce(op).get().stream().collect(toList());
    }
}
