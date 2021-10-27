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
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesCell;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesCohortCount;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesRow;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class AeSummariesMstCmnService extends AeSummariesService {
    private static final String ANY_AE = "Any AE";

    @Override
    public List<AeSummariesTable> getAesSummariesTable(Datasets datasets) {
        log.debug("start getAesSummariesMostCommonTable");
        FilterResult<Ae> filtered = getFilteredData(datasets, AeFilters.empty(), PopulationFilters.empty(), null);
        Collection<Subject> subjects = filtered.getPopulationFilterResult().getFilteredEvents();
        Collection<Ae> aes = getAcceptableAes(filtered.getFilteredEvents());

        Set<AeSummariesGroupingData> aeSummariesGroupingData = getAeSummariesGroupings(subjects);
        List<String> pt = new ArrayList<>();
        pt.add(ANY_AE);
        pt.addAll(getPreferredTerms(aes, aeSummariesGroupingData));

        List<AeSummariesTable> summariesTables = new ArrayList<>();
        aeSummariesGroupingData
                .stream()
                .map(ds -> createSummaryTable(ds, aes, pt))
                .forEach(summariesTables::add);
        summariesTables.add(createTotalTable(aes, pt, subjects.size()));
        return summariesTables;
    }

    private List<String> getPreferredTerms(Collection<Ae> aes, Set<AeSummariesGroupingData> aeSummariesGroupingData) {
        return aes.stream()
                .map(a -> a.getEvent().getPt())
                .filter(Objects::nonNull)
                .distinct()
                .filter(pt -> hasEnoughFrequency(pt, aeSummariesGroupingData, aes))
                .collect(Collectors.toList());

    }

    private boolean hasEnoughFrequency(String pt, Set<AeSummariesGroupingData> datasetCohortSubjectsMap, Collection<Ae> aes) {
        return datasetCohortSubjectsMap
                .stream()
                .flatMap(v -> v.getCohortCountSubjectsMap().values()
                        .stream()
                        .map(
                                a -> ((double) aes
                                        .stream()
                                        .filter(s ->
                                                pt.equals(s.getEvent().getPt())
                                                        && a.contains(s.getSubject()))
                                        .map(SubjectAwareWrapper::getSubject).distinct().count()) / a.size() * 100))
                .anyMatch(a -> a > 5);
    }

    private AeSummariesTable createTotalTable(Collection<Ae> aes, List<String> pts, int subjectCount) {
        List<AeSummariesRow> rows = new ArrayList<>();
        AeSummariesTable table = AeSummariesTable
                .builder()
                .datasetName("")
                .cohortCounts(Collections.singleton(
                        new AeSummariesCohortCount("TOTAL", "",
                                AeSummariesTable.GroupingType.NONE, "", subjectCount)))
                .rows(rows).build();
        table.setRows(rows);
        pts
                .stream()
                .map(pt -> {
                    long affected = aes.stream().filter(ae -> ANY_AE.equals(pt) && ae.getEvent().getPt() != null
                            || pt.equals(ae.getEvent().getPt())).map(SubjectAwareWrapper::getSubject).distinct().count();
                    return AeSummariesRow
                            .builder()
                            .rowDescription(pt)
                            .cells(Collections.singletonList(new AeSummariesCell("TOTAL", "", (int) affected,
                                    (double) affected / subjectCount * 100, AeSummariesTable.GroupingType.NONE, "")))
                            .build();
                }).forEach(rows::add);
        return table;
    }


    private AeSummariesTable createSummaryTable(AeSummariesGroupingData groupingData, Collection<Ae> aes, List<String> pts) {
        return AeSummariesTable
                .builder()
                .datasetName(groupingData.getStudyName())
                .countDosedSubject(groupingData
                        .getCohortCountSubjectsMap()
                        .values()
                        .stream()
                        .flatMap(Collection::stream).distinct().count()
                )
                .cohortCounts(groupingData.getCohortCountSubjectsMap().keySet())
                .rows(pts.stream().map(pt -> {
                    List<AeSummariesCell> cells = new ArrayList<>();
                    groupingData.getCohortCountSubjectsMap()
                            .forEach((key, value) -> {
                                int size = value.size();
                                long affected = aes.stream().filter(ae -> ANY_AE.equals(pt) && ae.getEvent().getPt() != null
                                        || pt.equals(ae.getEvent().getPt())).map(SubjectAwareWrapper::getSubject).distinct()
                                        .filter(value::contains).count();
                                cells.add(new AeSummariesCell(key.getCohort(), key.getGrouping(), (int) affected,
                                        (double) affected / size * 100, key.getGroupingType(),
                                        key.getStudyPart() == null ? "(Empty)" : key.getStudyPart()));
                            });

                    return AeSummariesRow.builder()
                            .cells(cells)
                            .rowDescription(pt)
                            .build();
                }).collect(toList()))
                .build();
    }

}
