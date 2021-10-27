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
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.ACTUAL_ARM;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.AGE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.ATTENDED_VISIT_NUMBERS;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.CENTER_NUMBER;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.COUNTRY;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DATE_OF_DEATH;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DATE_OF_RANDOMISATION;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DATE_OF_WITHDRAWAL;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DEATH_FLAG;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DISC_DATES_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DISC_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DISC_REASONS_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DOSE_COHORT;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DRUG_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DURATION_ON_STUDY;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DUR_EXCL_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.DUR_INCL_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.ETHNIC_GROUP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.FIRST_TREATMENT_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.LAST_TREATMENT_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.MAX_DOSE_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.MAX_FREQUENCY_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.MEDICAL_HISTORY;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.OTHER_COHORT;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.PHASE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.PLANNED_ARM;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.RACE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.RANDOMISED;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.REASON_FOR_WITHDRAWAL;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.REGION;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.SAFETYPOPULATION;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.SEX;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.SITE_ID;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.SPEC_ETHNIC_GROUP;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.STUDY_CODE;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.STUDY_PART;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.STUDY_SPECIFIC_FILTERS;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.SUBJECT;
import static com.acuity.visualisations.rawdatamodel.vo.Subject.Attributes.WITHDRAWAL;
import static com.google.common.collect.Lists.newArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PopulationFilters extends Filters<Subject> {

    @JsonIgnore
    public static PopulationFilters empty() {
        return new PopulationFilters();
    }

    @JsonIgnore
    public static PopulationFilters detectSafety() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSafetyPopulation(new SetFilter<>(newArrayList("Y")));

        return populationFilters;
    }

    // subject id is Ecode/subject/pdm_pat_subject ie E5707029
    // this came from SQL version of PopFilters to is called subjectId
    // but its really subject
    private SetFilter<String> subjectId = new SetFilter<>();
    private SetFilter<String> studyIdentifier = new SetFilter<>();
    private SetFilter<String> studyPart = new SetFilter<>();
    private SetFilter<String> sex = new SetFilter<>();
    private SetFilter<String> race = new SetFilter<>();
    private SetFilter<String> country = new SetFilter<>();
    private SetFilter<String> death = new SetFilter<>();
    private SetFilter<String> plannedTreatmentArm = new SetFilter<>();
    private SetFilter<String> actualTreatmentArm = new SetFilter<>();
    private SetFilter<String> centreNumbers = new SetFilter<>();
    protected SetFilter<String> siteIDs = new SetFilter<>(); // detect only
    private SetFilter<String> regions = new SetFilter<>();
    private RangeFilter<Integer> age = new RangeFilter<>();
    private DateRangeFilter firstTreatmentDate = new DateRangeFilter();
    private DateRangeFilter lastTreatmentDate = new DateRangeFilter();
    private SetFilter<String> phase = new SetFilter<>();
    private MultiValueSetFilter<String> medicalHistory = new MultiValueSetFilter<>();
    private InverseMultiValueSetFilter<String> studySpecificFilters = new InverseMultiValueSetFilter<>();
    private SetFilter<String> safetyPopulation = new SetFilter<>();
    private SetFilter<String> specifiedEthnicGroup = new SetFilter<>();

    private RangeFilter<Integer> totalStudyDuration = new RangeFilter<>();
    private SetFilter<String> withdrawalCompletion = new SetFilter<>();
    private SetFilter<String> withdrawalCompletionReason = new SetFilter<>();
    private DateRangeFilter withdrawalCompletionDate = new DateRangeFilter();
    private SetFilter<String> randomised = new SetFilter<>();
    private DateRangeFilter randomisationDate = new DateRangeFilter();
    private SetFilter<String> doseCohort = new SetFilter<>();
    private SetFilter<String> otherCohort = new SetFilter<>();
    private DateRangeFilter deathDate = new DateRangeFilter();
    private MultiValueSetFilter<String> attendedVisits = new MultiValueSetFilter<>();
    private RangeFilter<Integer> durationOnStudy = new RangeFilter<>();
    private SetFilter<String> ethnicGroup = new SetFilter<>();
    private RangeFilter<Integer> exposureInDays = new RangeFilter<>();
    private RangeFilter<Integer> actualExposureInDays = new RangeFilter<>();

    private MapFilter<String, SetFilter<String>> biomarkerGroups = new MapFilter<>(SetFilter.class);
    private MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>(SetFilter.class);
    private MapFilter<String, SetFilter<String>> drugsDiscontinued = new MapFilter<>(SetFilter.class);
    private MapFilter<String, SetFilter<String>> drugsMaxDoses = new MapFilter<>(SetFilter.class);
    private MapFilter<String, SetFilter<String>> drugsMaxFrequencies = new MapFilter<>(SetFilter.class);
    private MapFilter<String, SetFilter<String>> drugsDiscontinuationReason = new MapFilter<>(SetFilter.class);
    private MapFilter<Date, DateRangeFilter> drugsDiscontinuationDate = new MapFilter<>(DateRangeFilter.class);
    private MapFilter<Integer, RangeFilter<Integer>> drugsTotalDurationInclBreaks = new MapFilter<>(RangeFilter.class);
    private MapFilter<Integer, RangeFilter<Integer>> drugsTotalDurationExclBreaks = new MapFilter<>(RangeFilter.class);


    /* CombinedQueryBuilder
     * Never apply subjectIds to query for population
     */
    public Query<Subject> getQuery(Collection<String> subjectIds) {

        return new CombinedQueryBuilder<Subject>(Subject.class).
                // subject id is Ecode ie E5707029
                add(getFilterQuery(SUBJECT, subjectId)).
                add(getFilterQuery(STUDY_CODE, studyIdentifier)).
                add(getFilterQuery(STUDY_PART, studyPart)).
                add(getFilterQuery(SEX, sex)).
                add(getFilterQuery(RACE, race)).
                add(getFilterQuery(DEATH_FLAG, death)).
                add(getFilterQuery(PLANNED_ARM, plannedTreatmentArm)).
                add(getFilterQuery(ACTUAL_ARM, actualTreatmentArm)).
                add(getFilterQuery(REGION, regions)).
                add(getFilterQuery(AGE, age)).
                add(getFilterQuery(FIRST_TREATMENT_DATE, firstTreatmentDate)).
                add(getFilterQuery(LAST_TREATMENT_DATE, lastTreatmentDate)).
                add(getFilterQuery(STUDY_SPECIFIC_FILTERS, studySpecificFilters)).// detect specific,
                add(getFilterQuery(SAFETYPOPULATION, safetyPopulation)).// detect specific,
                add(getFilterQuery(DURATION_ON_STUDY, totalStudyDuration)).
                add(getFilterQuery(WITHDRAWAL, withdrawalCompletion)).
                add(getFilterQuery(REASON_FOR_WITHDRAWAL, withdrawalCompletionReason)).
                add(getFilterQuery(DATE_OF_WITHDRAWAL, withdrawalCompletionDate)).
                add(getFilterQuery(RANDOMISED, randomised)).
                add(getFilterQuery(DATE_OF_RANDOMISATION, randomisationDate)).
                add(getFilterQuery(DOSE_COHORT, doseCohort)).
                add(getFilterQuery(OTHER_COHORT, otherCohort)).
                add(getFilterQuery(DATE_OF_DEATH, deathDate)).
                add(getFilterQuery(DURATION_ON_STUDY, durationOnStudy)).
                add(getFilterQuery(SPEC_ETHNIC_GROUP, specifiedEthnicGroup)).
                add(getFilterQuery(ETHNIC_GROUP, ethnicGroup)).
                add(getFilterQuery(PHASE, phase)).
                add(getFilterQuery(MEDICAL_HISTORY, medicalHistory)).
                add(getFilterQuery(COUNTRY, country)).
                add(getFilterQuery(SITE_ID, siteIDs)).
                add(getFilterQuery(CENTER_NUMBER, centreNumbers)).
                add(getFilterQuery(ATTENDED_VISIT_NUMBERS, attendedVisits)).
                add(getFilterQueryForMapFilter(DRUG_MAP, drugsDosed)).
                add(getFilterQueryForMapFilter(DISC_MAP, drugsDiscontinued)).
                add(getFilterQueryForMapFilter(MAX_DOSE_MAP, drugsMaxDoses)).
                add(getFilterQueryForMapFilter(MAX_FREQUENCY_MAP, drugsMaxFrequencies)).
                add(getFilterQueryForMapFilter(DISC_REASONS_MAP, drugsDiscontinuationReason)).
                add(getFilterQueryForMapFilter(DISC_DATES_MAP, drugsDiscontinuationDate)).
                add(getFilterQueryForMapFilter(DUR_INCL_MAP, drugsTotalDurationInclBreaks)).
                add(getFilterQueryForMapFilter(DUR_EXCL_MAP, drugsTotalDurationExclBreaks)).
                build();
    }

    @Override
    public List<String> getEmptyFilterNames() {
        List<String> emptyFieldNames = super.getEmptyFilterNames();

        Map<String, SetFilter<String>> drugsDosedMap = this.drugsDosed.getMap();

        if (drugsDosedMap != null && allContainNo(drugsDosedMap) && !emptyFieldNames.contains("drugsDosed")) {
            emptyFieldNames.add("drugsDosed");
        }

        Map<String, SetFilter<String>> drugsDiscontinuedMap = this.drugsDiscontinued.getMap();

        if (drugsDiscontinuedMap != null && allContainNo(drugsDiscontinuedMap) && !emptyFieldNames.contains("drugsDiscontinued")) {
            emptyFieldNames.add("drugsDiscontinued");
        }

        Map<String, Set<String>> cohortFilterMap = new HashMap<>();
        cohortFilterMap.put("doseCohort", this.doseCohort.getValues());
        cohortFilterMap.put("otherCohort", this.otherCohort.getValues());

        cohortFilterMap.forEach((filterKey, filterValues) -> {
            if (filterValues != null && filterValues.size() == 1 && filterValues.contains("Default group") && !emptyFieldNames.contains(filterKey)) {
                emptyFieldNames.add(filterKey);
            }
        });

        return emptyFieldNames;
    }

    @Override
    public List<String> getEmptyFilterNames(Datasets datasets) {
        List<String> emptyFieldNames = super.getEmptyFilterNames(datasets);

        if (datasets.isAcuityType()) {
            emptyFieldNames.add("lastTreatmentDate");
        }

        return emptyFieldNames;
    }

    private boolean allContainNo(Map<String, SetFilter<String>> map) {
        return map.values().stream().allMatch(v -> v.getValues().size() == 1 && (v.getValues().contains("No") || v.getValues().contains("N")));
    }
}
