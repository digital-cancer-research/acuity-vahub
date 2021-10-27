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

package com.acuity.visualisations.rawdatamodel.service.ae.summaries;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesCohortCount;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesRow;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@Service
public class AeSummariesSaeLeadToDiscService extends AeSummariesService {
    private static final String ANY_AE = "Any AE";
    private static final String SUBTOTAL = "Subtotal";
    private static final List<String> ACTION_TAKEN_LIST = Arrays.asList("DRUG PERMANENTLY DISCONTINUED", "DRUG WITHDRAWN", "PERMANENTLY STOPPED");
    private static final List<String> SERIOUS_LIST = Arrays.asList("1", "AESER", "YES", "Y");

    @Override
    public List<AeSummariesTable> getAesSummariesTable(Datasets datasets) {
        FilterResult<Ae> filtered = getFilteredData(datasets, AeFilters.empty(), PopulationFilters.empty(), null);
        Collection<Subject> subjects = filtered.getPopulationFilterResult().getFilteredEvents();
        Collection<Ae> aes = getAcceptableAes(filtered.getFilteredEvents());

        return getAeSummariesGroupings(subjects).stream()
                .map(g -> {
                    List<Ae> currentStudyAes = aes.stream()
                            .filter(ae -> isSerious(ae) && verifyActionTaken(ae)
                                    && g.getStudyName().equals(ae.getClinicalStudyCode())).collect(toList());

                    List<AeSummariesRow> rows = StreamEx.of(getAesSocPtGrouping(currentStudyAes))
                            .map(gr -> createRow(gr.getAes(), gr.getSoc(), gr.getPt(), gr.getDrug(), g.getCohortCountSubjectsMap()))
                            .prepend(createRow(currentStudyAes, ANY_AE, ANY_AE, getDrugsForAnyAe(currentStudyAes), g.getCohortCountSubjectsMap()))
                            .collect(toList());
                    return AeSummariesTable.builder()
                            .datasetName(g.getStudyName())
                            .cohortCounts(g.getCohortCountSubjectsMap().keySet())
                            .countDosedSubject(g.getCohortCountSubjectsMap()
                                    .values().stream()
                                    .flatMap(Collection::stream)
                                    .distinct()
                                    .count())
                            .rows(rows)
                            .build();
                })
                .collect(toList());
    }

    @Data
    @Builder
    @EqualsAndHashCode(of = {"pt", "soc", "drug"})
    private static class AeGrouping {
        private String soc;
        private String pt;
        private List<Ae> aes;
        private String drug;
    }

    private String getDrugsForAnyAe(List<Ae> currentStudyAes) {
        return currentStudyAes.stream()
                .flatMap(a -> a.getEvent().getDrugsActionTaken()
                        .entries()
                        .stream()
                        .filter(e -> ACTION_TAKEN_LIST.contains(e.getValue().toUpperCase())).map(Map.Entry::getKey))
                .distinct()
                .collect(joining(", "));
    }

    private boolean verifyActionTaken(Ae ae) {
        return ae.getEvent()
                .getDrugsActionTaken()
                .entries()
                .stream()
                .filter(e -> e.getValue() != null)
                .anyMatch(e -> ACTION_TAKEN_LIST.contains(e.getValue().toUpperCase()));
    }

    private boolean isSerious(Ae ae) {
        return Objects.nonNull(ae.getEvent().getSerious())
                && SERIOUS_LIST.contains(ae.getEvent().getSerious().toUpperCase());
    }


    private List<AeGrouping> getAesSocPtGrouping(List<Ae> aes) {
        Map<String, Map<String, Map<String, List<Ae>>>> socPtAesMap = aes.stream()
                .collect(Collectors.groupingBy(ae -> {
                            if (ae.getEvent().getSoc() == null) {
                                return "(Empty)";
                            }
                            return ae.getEvent().getSoc();
                        },
                        Collectors.groupingBy(ae -> {
                                    if (ae.getEvent().getPt() == null) {
                                        return "(Empty)";
                                    }
                                    return ae.getEvent().getPt();
                                },
                                Collectors.groupingBy(ae -> ae.getEvent().getDrugsActionTaken()
                                        .entries()
                                        .stream()
                                        .filter(e -> ACTION_TAKEN_LIST.contains(e.getValue().toUpperCase())).map(Map.Entry::getKey).collect(joining(", "))))));

        for (Map.Entry<String, Map<String, Map<String, List<Ae>>>> entry : socPtAesMap.entrySet()) {
            HashMap<String, List<Ae>> subtotal = new HashMap<>();
            subtotal.put("", entry.getValue().values().stream().flatMap(val -> val.values().stream()).flatMap(Collection::stream).collect(toList()));
            entry.getValue().put(SUBTOTAL, subtotal);
        }

        return socPtAesMap.entrySet().stream()
                .flatMap(e ->
                        e.getValue().entrySet().stream()
                                .flatMap(e1 -> e1.getValue().entrySet().stream()
                                        .map(e2 -> AeGrouping.builder()
                                                .soc(e.getKey())
                                                .pt(e1.getKey())
                                                .drug(Objects.isNull(e2.getKey()) ? "" : e2.getKey())
                                                .aes(e2.getValue())
                                                .build())))
                .collect(toList());
    }

    private AeSummariesRow createRow(List<Ae> aes, String soc, String pt, String drug,
                                     Map<AeSummariesCohortCount, List<Subject>> cohortSubjects) {
        List<Subject> filteredSubjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        return AeSummariesRow.builder()
                .soc(soc)
                .pt(pt)
                .rowDescription("")
                .drug(drug)
                .cells(getCells(filteredSubjects, cohortSubjects))
                .build();
    }
}
