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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_CAUSALLY_RELATED_TO;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_LEADING_TO_DISC;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_LEADING_TO_DISC_CAUSALLY_RELATED_TO;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_OF_CTC_GRADE_3_OR_HIGHER;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_OF_CTC_GRADE_3_OR_HIGHER_RELATED_TO;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_WITH_DEATH_OUTCOME;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.AE_WITH_DEATH_OUTCOME_CAUSALLY_RELATED;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.ANY_AE;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.SAE_LEADING_TO_DISC;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.SAE_LEADING_TO_DISC_CAUSALLY_RELATED;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.SAE_WITH_DEATH_OUTCOME;
import static com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService.RowDescription.SAE_WITH_DEATH_OUTCOME_AND_CAUSALLY_RELATED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Service
public class AeSummariesAnyService extends AeSummariesService {
    private static final List<String> CAUSALITY_VALUES = asList("1", "PROBABLY RELATED", "POSSIBLE", "RELATED", "YES", "POSSIBLY RELATED",
            "DEFINITELY RELATED");
    private static final List<String> SERIOUS_VALUES = asList("1", "Y", "AESER", "YES");
    private static final List<String> ACTION_TAKEN_VALUES = asList("DRUG PERMANENTLY DISCONTINUED", "DRUG WITHDRAWN", "PERMANENTLY STOPPED");
    private static final String ADDITIONAL_DRUG = "ADDITIONAL_DRUG";
    private static final String ANY_TREATMENT_STR = "any study treatment";
    private static final String ANY_AE_STR = "Any AE";
    private static final String ANY_AE_WITH_CAUSALITY_STR = "Any AE causally related to";
    private static final String AE_OF_CTC_GRADE_3_OR_HIGHER_STR = "Any AE of CTC grade 3 or higher";
    private static final String CAUSALLY_RELATED_TO_STR = ", causally related to";
    private static final String AE_LEADING_TO_DISCOUNT_OF_STR = "Any AE leading to discontinuation of";
    private static final String AE_WITH_DEATH_OUTCOME_STR = "Any AE with outcome = Death";
    private static final String DEATH_OUTCOME_SAE_STR = "Any SAE (including events with outcome = Death)";
    private static final String SAE_LEADING_TO_DISC_STR = "Any SAE leading to discontinuation of";
    private static final String AE_WITH_DEATH_OUTCOME_WITH_CAUSALITY_STR = "Any AE with outcome = Death, causally related to";

    public enum RowDescription {

        ANY_AE(ANY_AE_STR + " %s", (ae, drug) -> true),

        AE_CAUSALLY_RELATED_TO(ANY_AE_WITH_CAUSALITY_STR + " %s",
                (ae, drug) -> isDrugEmpty(drug)
                        || isPropertyValueBetweenRequired(drug, ae.getEvent().getDrugsCausality().entrySet(), CAUSALITY_VALUES)),

        AE_OF_CTC_GRADE_3_OR_HIGHER(AE_OF_CTC_GRADE_3_OR_HIGHER_STR + " %s", (ae, drug) -> isSeverityHigherOrEqual(ae, 3)),

        AE_OF_CTC_GRADE_3_OR_HIGHER_RELATED_TO(AE_OF_CTC_GRADE_3_OR_HIGHER_STR + CAUSALLY_RELATED_TO_STR + " %s",
                (ae, drug) -> AE_OF_CTC_GRADE_3_OR_HIGHER.filter.test(ae, drug) && AE_CAUSALLY_RELATED_TO.filter.test(ae, drug)),

        AE_LEADING_TO_DISC(AE_LEADING_TO_DISCOUNT_OF_STR + " %s",
                (ae, drug) -> isDrugEmpty(drug)
                        || isPropertyValueBetweenRequired(drug, ae.getEvent().getDrugsActionTaken().entries(), ACTION_TAKEN_VALUES)),

        AE_LEADING_TO_DISC_CAUSALLY_RELATED_TO(AE_LEADING_TO_DISCOUNT_OF_STR + " %s" + CAUSALLY_RELATED_TO_STR + " %s",
                (ae, drug) -> isDrugEmpty(drug)
                        || (isPropertyValueBetweenRequired(drug, ae.getEvent().getDrugsActionTaken().entries(), ACTION_TAKEN_VALUES)
                        && isPropertyValueBetweenRequired(drug, ae.getEvent().getDrugsCausality().entrySet(), CAUSALITY_VALUES))),

        AE_WITH_DEATH_OUTCOME(AE_WITH_DEATH_OUTCOME_STR + " %s",
                (ae, drug) -> isSeverityHigherOrEqual(ae, 5)),

        AE_WITH_DEATH_OUTCOME_CAUSALLY_RELATED(AE_WITH_DEATH_OUTCOME_WITH_CAUSALITY_STR + " %s",
                (ae, drug) -> AE_WITH_DEATH_OUTCOME.filter.test(ae, drug)
                        && AE_CAUSALLY_RELATED_TO.filter.test(ae, drug)),

        SAE_WITH_DEATH_OUTCOME(DEATH_OUTCOME_SAE_STR + " %s", (ae, drug) -> isSerious(ae)),

        SAE_WITH_DEATH_OUTCOME_AND_CAUSALLY_RELATED(DEATH_OUTCOME_SAE_STR + CAUSALLY_RELATED_TO_STR + " %s",
                (ae, drug) -> SAE_WITH_DEATH_OUTCOME.filter.test(ae, drug)
                        && AE_CAUSALLY_RELATED_TO.filter.test(ae, drug)),

        SAE_LEADING_TO_DISC_CAUSALLY_RELATED(SAE_LEADING_TO_DISC_STR + " %s" + CAUSALLY_RELATED_TO_STR + " %s",
                (ae, drug) -> SAE_WITH_DEATH_OUTCOME.filter.test(ae, drug)
                        && AE_LEADING_TO_DISC_CAUSALLY_RELATED_TO.filter.test(ae, drug)),

        SAE_LEADING_TO_DISC(SAE_LEADING_TO_DISC_STR + " %s",
                (ae, drug) -> SAE_WITH_DEATH_OUTCOME.filter.test(ae, drug)
                        && AE_LEADING_TO_DISC.filter.test(ae, drug));

        private final String description;
        private final BiPredicate<Ae, String> filter;

        RowDescription(String rowDescription, BiPredicate<Ae, String> filter) {
            this.description = rowDescription;
            this.filter = filter;
        }

        private static boolean isPropertyValueBetweenRequired(String drug, Collection<Map.Entry<String, String>> entries, Collection<String> requiredValues) {
            return entries.stream()
                    .filter(e -> (ANY_TREATMENT_STR.equals(drug) && !ADDITIONAL_DRUG.equalsIgnoreCase(e.getKey())) || e.getKey().equals(drug))
                    .map(Map.Entry::getValue)
                    .filter(Objects::nonNull)
                    .anyMatch(value -> requiredValues.contains(value.toUpperCase()));
        }

        private static boolean isSerious(Ae ae) {
            return ae.getEvent().getSerious() != null && SERIOUS_VALUES.contains(ae.getEvent().getSerious().toUpperCase());
        }

        private static boolean isSeverityHigherOrEqual(Ae ae, int border) {
            return ae.getEvent().getMaxAeSeverityNum() != null && ae.getEvent().getMaxAeSeverityNum() >= border;
        }

        private static boolean isDrugEmpty(String drug) {
            return drug == null || "".equals(drug);
        }

        public AeSummariesRow createRow(List<Ae> aes, String drug, Map<AeSummariesCohortCount, List<Subject>> cohortSubjects) {
            List<Subject> filteredSubjects =
                    aes.stream().filter(ae -> filter.test(ae, drug)).map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

            return AeSummariesRow.builder()
                    .rowDescription(description.replace("%s", drug))
                    .cells(getCells(filteredSubjects, cohortSubjects))
                    .build();
        }

        private List<AeSummariesCell> getCells(List<Subject> filteredSubjects,
                                               Map<AeSummariesCohortCount, List<Subject>> cohortSubjects) {

            Map<AeSummariesCohortCount, Long> cohortFilteredSubjectCount = cohortSubjects.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().filter(filteredSubjects::contains).count()));

            return cohortFilteredSubjectCount.entrySet().stream().map(entry -> new AeSummariesCell(
                    entry.getKey().getCohort(),
                    entry.getKey().getGrouping(),
                    entry.getValue().intValue(),
                    ((double) entry.getValue()) * 100 / cohortSubjects.get(entry.getKey()).size(),
                    entry.getKey().getGroupingType(),
                    entry.getKey().getStudyPart() == null ? "(Empty)" : entry.getKey().getStudyPart())
            ).collect(toList());
        }
    }

    @Override
    public List<AeSummariesTable> getAesSummariesTable(Datasets datasets) {
        FilterResult<Ae> filtered = getFilteredData(datasets, AeFilters.empty(), PopulationFilters.empty(), null);
        Collection<Subject> subjects = filtered.getPopulationFilterResult().getFilteredEvents();
        Collection<Ae> aes = getAcceptableAes(filtered.getFilteredEvents());

        return getAeSummariesGroupings(subjects).stream()
                .map(g -> {
                    Map<AeSummariesCohortCount, List<Subject>> cohortSubjects = g.getCohortCountSubjectsMap();

                    List<Ae> aesFromCurrentStudy = aes.stream().filter(ae -> g.getStudyName().equals(ae.getClinicalStudyCode())).collect(toList());

                    List<String> drugs = cohortSubjects.entrySet().stream()
                            .flatMap(e1 -> e1.getValue().stream()).flatMap(subject -> subject.getDrugsDosed().keySet().stream())
                            .distinct().filter(drug -> !ADDITIONAL_DRUG.equalsIgnoreCase(drug)).collect(toList());

                    return AeSummariesTable.builder()
                            .datasetName(g.getStudyName())
                            .cohortCounts(cohortSubjects.keySet())
                            .countDosedSubject(cohortSubjects
                                    .values().stream()
                                    .flatMap(Collection::stream)
                                    .distinct()
                                    .count())
                            .rows(getRowMappers(drugs)
                                    .stream()
                                    .map(a -> a.getKey().createRow(aesFromCurrentStudy, a.getValue(), cohortSubjects))
                                    .collect(toList()))
                            .build();
                })
                .collect(toList());
    }

    private List<Pair<RowDescription, String>> getRowMappers(List<String> drugs) {
        List<Pair<RowDescription, String>> rowMappers = new ArrayList<>(Arrays.asList(
                Pair.of(ANY_AE, ""),
                Pair.of(AE_CAUSALLY_RELATED_TO, ANY_TREATMENT_STR),
                Pair.of(AE_OF_CTC_GRADE_3_OR_HIGHER, ""),
                Pair.of(AE_OF_CTC_GRADE_3_OR_HIGHER_RELATED_TO, ANY_TREATMENT_STR),
                Pair.of(AE_LEADING_TO_DISC, ANY_TREATMENT_STR),
                Pair.of(AE_LEADING_TO_DISC_CAUSALLY_RELATED_TO, ANY_TREATMENT_STR),
                Pair.of(AE_WITH_DEATH_OUTCOME, ""),
                Pair.of(AE_WITH_DEATH_OUTCOME_CAUSALLY_RELATED, ANY_TREATMENT_STR),
                Pair.of(SAE_WITH_DEATH_OUTCOME, ""),
                Pair.of(SAE_WITH_DEATH_OUTCOME_AND_CAUSALLY_RELATED, ANY_TREATMENT_STR),
                Pair.of(SAE_LEADING_TO_DISC, ANY_TREATMENT_STR),
                Pair.of(SAE_LEADING_TO_DISC_CAUSALLY_RELATED, ANY_TREATMENT_STR),
                Pair.of(SAE_LEADING_TO_DISC, ANY_TREATMENT_STR),
                Pair.of(SAE_LEADING_TO_DISC_CAUSALLY_RELATED, ANY_TREATMENT_STR)));
        for (String drug : drugs) {
            rowMappers.add(Pair.of(AE_CAUSALLY_RELATED_TO, drug));
            rowMappers.add(Pair.of(AE_OF_CTC_GRADE_3_OR_HIGHER_RELATED_TO, drug));
            rowMappers.add(Pair.of(AE_LEADING_TO_DISC, drug));
            rowMappers.add(Pair.of(AE_LEADING_TO_DISC_CAUSALLY_RELATED_TO, drug));
            rowMappers.add(Pair.of(AE_WITH_DEATH_OUTCOME_CAUSALLY_RELATED, drug));
            rowMappers.add(Pair.of(SAE_WITH_DEATH_OUTCOME_AND_CAUSALLY_RELATED, drug));
            rowMappers.add(Pair.of(SAE_LEADING_TO_DISC, drug));
            rowMappers.add(Pair.of(SAE_LEADING_TO_DISC_CAUSALLY_RELATED, drug));
        }
        return rowMappers;
    }
}
