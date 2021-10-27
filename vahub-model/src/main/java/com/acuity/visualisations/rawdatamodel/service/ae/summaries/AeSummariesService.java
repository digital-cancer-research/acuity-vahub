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

import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesCohortCount;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class AeSummariesService extends BaseEventService<AeRaw, Ae, AeGroupByOptions> {
    public abstract List<AeSummariesTable> getAesSummariesTable(Datasets datasets);

    protected Set<AeSummariesGroupingData> getAeSummariesGroupings(Collection<Subject> subjects) {
        Map<AeSummariesGroupingData, Map<AeSummariesCohortCount, List<Subject>>> datasetDoseCohortSubjectsMap = subjects.stream()
                .filter(s -> s.getDoseCohort() != null && s.getDoseGrouping() != null).collect(
                        Collectors.groupingBy(s1 -> new AeSummariesGroupingData(s1.getClinicalStudyCode(), s1.getStudyPart()),
                                Collectors.groupingBy(subj ->
                                        new AeSummariesCohortCount(
                                                subj.getDoseCohort(), subj.getDoseGrouping(),
                                                AeSummariesTable.GroupingType.DOSE,
                                                subj.getStudyPart() == null ? "(Empty)" : subj.getStudyPart()))));

        Map<AeSummariesGroupingData, Map<AeSummariesCohortCount, List<Subject>>> result = subjects.stream()
                .filter(s -> s.getOtherGrouping() != null && s.getOtherCohort() != null).collect(
                        Collectors.groupingBy(s1 -> new AeSummariesGroupingData(
                                        s1.getClinicalStudyCode(), s1.getStudyPart()),
                                Collectors.groupingBy(subj ->
                                        new AeSummariesCohortCount(
                                                subj.getOtherCohort(), subj.getOtherGrouping(), AeSummariesTable.GroupingType.NONE,
                                                subj.getStudyPart() == null ? "(Empty)" : subj.getStudyPart()))));


        for (Map.Entry<AeSummariesGroupingData, Map<AeSummariesCohortCount, List<Subject>>> e : result.entrySet()) {
            e.getValue().putAll(datasetDoseCohortSubjectsMap.getOrDefault(e.getKey(), new HashMap<>()));
            datasetDoseCohortSubjectsMap.remove(e.getKey());
        }

        for (Map.Entry<AeSummariesGroupingData, Map<AeSummariesCohortCount, List<Subject>>> e : datasetDoseCohortSubjectsMap.entrySet()) {
            result.putIfAbsent(e.getKey(), new HashMap<>());
            result.get(e.getKey()).putAll(e.getValue());
        }
        for (Map.Entry<AeSummariesGroupingData, Map<AeSummariesCohortCount, List<Subject>>> e : result.entrySet()) {
            for (Map.Entry<AeSummariesCohortCount, List<Subject>> e1 : e.getValue().entrySet()) {
                e1.getKey().setCount(e1.getValue().size());
            }
            e.getKey().setCohortCountSubjectsMap(e.getValue());
        }

        return result.keySet();
    }

    protected List<Ae> getAcceptableAes(Collection<Ae> aes) {
        return aes.stream()
                .filter(a -> a.getStartDate() != null
                        && a.getSubject().getFirstTreatmentDate() != null
                        && !a.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS)
                        .isBefore(a.getSubject().getFirstTreatmentDate().toInstant().truncatedTo(ChronoUnit.DAYS)))
                .filter(a -> (a.getSubject().getLastTreatmentDate() == null
                        || !a.getSubject().getLastTreatmentDate().toInstant().plus(28, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS)
                        .isBefore(a.getStartDate().toInstant().truncatedTo(ChronoUnit.DAYS))))
                .collect(Collectors.toList());
    }

    protected List<AeSummariesTable.AeSummariesCell> getCells(List<Subject> filteredSubjects,
                                                            Map<AeSummariesCohortCount, List<Subject>> cohortSubjects) {

        Map<AeSummariesCohortCount, Long> cohortFilteredSubjectCount = cohortSubjects.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().filter(filteredSubjects::contains).count()));

        return cohortFilteredSubjectCount.entrySet().stream().map(entry -> new AeSummariesTable.AeSummariesCell(
                entry.getKey().getCohort(),
                entry.getKey().getGrouping(),
                entry.getValue().intValue(),
                ((double) entry.getValue()) * 100 / cohortSubjects.get(entry.getKey()).size(),
                entry.getKey().getGroupingType(),
                entry.getKey().getStudyPart())
        ).collect(toList());
    }

    @Data
    @Getter
    @EqualsAndHashCode(of = {"studyName", "studyPart"})
    public class AeSummariesGroupingData {
        private String studyName;
        private String studyPart;
        @Setter
        private Map<AeSummariesCohortCount, List<Subject>> cohortCountSubjectsMap = new HashMap<>();

        public AeSummariesGroupingData(String studyName, String studyPart) {
            this.studyName = studyName;
            this.studyPart = studyPart;
        }
    }
}
