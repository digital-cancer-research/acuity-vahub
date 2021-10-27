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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PatientOutcomeSummaryService implements SsvSummaryTableService {

    @Autowired
    private DeathService deathService;
    @Autowired
    private DoseDiscService doseDiscService;
    @Autowired
    private PopulationService populationService;
    @Autowired
    private AssessedTargetLesionService tumourService;
    @Autowired
    private SeriousAdverseEventService seriousAdverseEventService;

    private static final String[] COLUMN_NAMES = {"date", "event", "outcome", "reason"};
    private static final String[] COLUMN_DESCRIPTIONS = {"Date", "Event", "Outcome", "Reason"};

    // event types
    private static final String DEATH = "DEATH";
    private static final String BEST_RECIST_RESPONSE = "BEST RECIST RESPONSE";
    private static final String SERIOUS_AE = "SERIOUS AE";
    private static final String DISCONTINUATION = "DISCONTINUATION";

    private static final String PRIMARY = "Primary";
    private static final String SECONDARY = "Secondary";
    private static final String NOT_SPECIFIED = "not specified";

    private static final String DESIGNATION_FIELD_NAME = "designation";
    private static final String DEATH_CAUSE_FIELD_NAME = "deathCause";
    private static final String DEATH_DATE_FIELD_NAME = "dateOfDeath";

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        throw new IllegalStateException("Only call to getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) is allowed");
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        List<Map<String, String>> output = new ArrayList<>();
        output.addAll(getDeathSummary(datasets, subjectId));
        if (hasTumourAccess) {
            output.addAll(getBestResponseSummary(datasets, subjectId));
        }
        output.addAll(getSeriousAeSummary(datasets, subjectId));
        output.addAll(getDiscontinuationSummary(datasets, subjectId));
        return output;
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return IntStream.range(0, COLUMN_NAMES.length).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> COLUMN_DESCRIPTIONS[i],
                (i1, i2) -> i1, LinkedHashMap::new));
    }

    @Override
    public String getSsvTableName() {
        return "outcomeSummary";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "PATIENT OUTCOME SUMMARY";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT OUTCOME SUMMARY";
    }

    @Override
    public double getOrder() {
        return 2;
    }

    private List<Map<String, String>> getDeathSummary(Datasets datasets, String subjectId) {
        List<Map<String, String>> deathSummary = new ArrayList<>();

        List<Map<String, String>> deathData = deathService.getSingleSubjectData(datasets, subjectId, DeathFilters.empty());

        if (deathData.isEmpty()) {
            deathSummary.add(getSummaryRow("", DEATH, "NO", ""));
        } else {
            String deathDate = deathData.stream().filter(entry -> entry.get(DEATH_DATE_FIELD_NAME) != null).findAny()
                    .map(d -> d.get(DEATH_DATE_FIELD_NAME)).orElse("");

            String primaryCause = deathData.stream().filter(entry -> PRIMARY.equalsIgnoreCase(entry.get(DESIGNATION_FIELD_NAME))).findAny()
                    .map(entry -> entry.get(DEATH_CAUSE_FIELD_NAME)).orElse(NOT_SPECIFIED);

            Map<String, String> deathPrimary = getSummaryRow(deathDate, DEATH, "YES", "Primary cause: " + primaryCause);
            deathSummary.add(deathPrimary);

            String secondaryCause = deathData.stream().filter(entry -> SECONDARY.equalsIgnoreCase(entry.get(DESIGNATION_FIELD_NAME))).findAny()
                    .map(entry -> entry.get(DEATH_CAUSE_FIELD_NAME)).orElse(NOT_SPECIFIED);
            Map<String, String> deathSecondary = getSummaryRow("", "", "", "Secondary cause: " + secondaryCause);
            deathSummary.add(deathSecondary);
        }
        return deathSummary;
    }

    private List<Map<String, String>> getBestResponseSummary(Datasets datasets, String subjectId) {
        Map<String, String> bestResponseSummary;

        Predicate<AssessedTargetLesion> predicate = t -> (subjectId.equals(t.getSubjectId()) && t.getEvent().isBestPercentageChange());

        FilterResult<AssessedTargetLesion> filtered
                = tumourService.getFilteredData(datasets, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(), null, predicate);

        Optional<AssessedTargetLesion> bestResponse = filtered.stream().findAny();
        if (bestResponse.isPresent()) {
            AssessedTargetLesion response = bestResponse.get();
            bestResponseSummary = getSummaryRow(DaysUtil.toDisplayString(response.getEvent().getTargetLesionRaw().getVisitDate()),
                    BEST_RECIST_RESPONSE, response.getEvent().getBestResponse(), "");
        } else {
            bestResponseSummary = getSummaryRow("", BEST_RECIST_RESPONSE, "", "");
        }

        return Collections.singletonList(bestResponseSummary);
    }

    private List<Map<String, String>> getSeriousAeSummary(Datasets datasets, String subjectId) {

        Predicate<SeriousAe> predicate = e -> subjectId.equals(e.getSubjectId());

        FilterResult<SeriousAe> filtered
                = seriousAdverseEventService.getFilteredData(datasets, SeriousAeFilters.empty(), PopulationFilters.empty(), null, predicate);
        List<Map<String, String>> saeSummary = filtered.stream()
                .sorted(Comparator.comparing(s -> s.getEvent().getBecomeSeriousDate(), Comparator.nullsLast(Comparator.naturalOrder())))
                .map(s -> getSummaryRow(DaysUtil.toDisplayString(s.getEvent().getBecomeSeriousDate()),
                        SERIOUS_AE, s.getEvent().getAe(), "")).collect(Collectors.toList());

        return saeSummary;
    }

    private List<Map<String, String>> getDiscontinuationSummary(Datasets datasets, String subjectId) {
        Predicate<DoseDisc> predicate = e -> subjectId.equals(e.getSubjectId());

        FilterResult<DoseDisc> filtered
                = doseDiscService.getFilteredData(datasets, DoseDiscFilters.empty(), PopulationFilters.empty(), null, predicate);

        return filtered.stream()
                .sorted(Comparator.comparing(s -> s.getEvent().getDiscDate(), Comparator.nullsLast(Comparator.naturalOrder())))
                .map(s -> getSummaryRow(DaysUtil.toDisplayString(s.getEvent().getDiscDate()),
                        String.format("%s %s", s.getEvent().getStudyDrug(), DISCONTINUATION),
                        "",
                        s.getEvent().getDiscReason())).collect(Collectors.toList());
    }

    private Map<String, String> getSummaryRow(String... values) {
        return IntStream.range(0, Integer.min(values.length, COLUMN_NAMES.length)).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> values[i] == null ? "" : values[i]));
    }
}
