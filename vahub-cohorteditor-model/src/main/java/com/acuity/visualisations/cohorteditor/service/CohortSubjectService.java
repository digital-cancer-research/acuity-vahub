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
