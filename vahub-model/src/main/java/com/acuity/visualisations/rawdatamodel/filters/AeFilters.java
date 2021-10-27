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
import com.acuity.visualisations.rawdatamodel.vo.AeDetailLevel;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.ACTION_TAKEN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.AE_NUMBER;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.AE_OF_SPECIAL_INTEREST;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.CAUSALITY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.CAUSED_SUBJECT_WITHDRAWAL;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.COMMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DAYS_FROM_PREV_DOSE_TO_START;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DAYS_SINCE_FIRST_DOSE_AT_END;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DAYS_SINCE_FIRST_DOSE_AT_START;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DOSE_LIMITING_TOXICITY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DRUGS_ACTION_TAKEN_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DRUGS_CAUSALITY_MAP;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.DURATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.END_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.END_PRIOR_TO_RAND;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.HLT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.IMMUNE_MEDIATED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.INFUSION_REACTION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.OUTCOME;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.PT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.REQUIRED_TREATMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.REQUIRES_HOSPITALISATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SERIOUS;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SEVERITY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SOC;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SPECIAL_INTEREST_GROUP;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.START_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.START_PRIOR_TO_RAND;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.STUDY_PERIOD;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SUSPECTED_ENDPOINT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.SUSPECTED_ENDPOINT_CAT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.TEXT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.TIMEPOINT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.TREATMENT_EMERGENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.USED_IN_TFL;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.Attributes.WEPAPP_MAX_CTC;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class AeFilters extends UsedInTflFilters<Ae> {

    protected AeDetailLevel aeDetailLevel = AeDetailLevel.PER_INCIDENCE;

    protected SetFilter<String> pt = new SetFilter<>();
    protected SetFilter<String> hlt = new SetFilter<>();
    protected SetFilter<String> soc = new SetFilter<>();
    protected SetFilter<String> severity = new SetFilter<>(); //
    protected SetFilter<String> serious = new SetFilter<>();
    protected MultiValueSetFilter<String> specialInterestGroup = new MultiValueSetFilter<>();
    protected DateRangeFilter startDate = new DateRangeFilter();
    protected DateRangeFilter endDate = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudyAtStart = new RangeFilter<>();
    protected RangeFilter<Integer> daysOnStudyAtEnd = new RangeFilter<>();
    protected RangeFilter<Integer> duration = new RangeFilter<>();
    protected RangeFilter<Integer> daysFromPrevDoseToStart = new RangeFilter<>();
    protected SetFilter<String> description = new SetFilter<>();
    protected SetFilter<String> studyPeriods = new SetFilter<>();
    protected SetFilter<String> comment = new SetFilter<>();

    // detect
    protected SetFilter<String> actionTaken = new SetFilter<>();
    protected SetFilter<String> causality = new SetFilter<>();
    // acuity maps
    protected MapFilter<String, MultiValueSetFilter<String>> drugsActionTaken = new MapFilter<String, MultiValueSetFilter<String>>(MultiValueSetFilter.class);
    protected MapFilter<String, SetFilter<String>> drugsCausality = new MapFilter<String, SetFilter<String>>(SetFilter.class);
    protected SetFilter<String> outcome = new SetFilter<>();
    protected SetFilter<String> requiredTreatment = new SetFilter<>();
    protected SetFilter<String> treatmentEmergent = new SetFilter<>();
    protected SetFilter<String> requiresHospitalisation = new SetFilter<>();
    protected SetFilter<String> causedSubjectWithdrawal = new SetFilter<>();
    protected SetFilter<String> doseLimitingToxicity = new SetFilter<>();
    protected SetFilter<String> timePointDoseLimitingToxicity = new SetFilter<>();
    protected SetFilter<String> immuneMediated = new SetFilter<>();
    protected SetFilter<String> infusionReaction = new SetFilter<>();

    protected SetFilter<String> suspectedEndpoint = new SetFilter<>();
    protected SetFilter<String> suspectedEndpointCategory = new SetFilter<>();
    protected SetFilter<String> aeOfSpecialInterest = new SetFilter<>();
    protected SetFilter<String> aeNumber = new SetFilter<>();
    protected SetFilter<String> aeStartPriorToRandomisation = new SetFilter<>();
    protected SetFilter<String> aeEndPriorToRandomisation = new SetFilter<>();

    @JsonIgnore
    public static AeFilters empty() {
        return new AeFilters();
    }

    @JsonIgnore
    public boolean isAePerSeverityChange() {
        return aeDetailLevel == AeDetailLevel.PER_SEVERITY_CHANGE;
    }

    @JsonIgnore
    public boolean isAePerIncidence() {
        //this is by default if null
        return (aeDetailLevel == null) || (aeDetailLevel == AeDetailLevel.PER_INCIDENCE);
    }

    @Override
    public Query<Ae> getQuery() {
        return getQuery(Collections.emptyList());
    }

    @Override
    public Query<Ae> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Ae> cqb = new CombinedQueryBuilder<>(Ae.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            final SetFilter<String> stringSetFilter = new SetFilter<>(subjectIds);
            cqb.add(getFilterQuery(Ae.Attributes.SUBJECT_ID, stringSetFilter));
        }
        return cqb.add(getFilterQuery(PT, this.pt)).
                add(getFilterQuery(HLT, hlt)).
                add(getFilterQuery(SOC, soc)).
                add(getFilterQuery(SERIOUS, serious)).
                add(getFilterQuery(SEVERITY, severity)).
                add(getFilterQuery(WEPAPP_MAX_CTC, severity)).
                add(getFilterQuery(STUDY_PERIOD, studyPeriods)).

                add(getFilterQuery(START_DATE, startDate)).
                add(getFilterQuery(END_DATE, endDate)).
                add(getFilterQuery(DAYS_SINCE_FIRST_DOSE_AT_START, daysOnStudyAtStart)).
                add(getFilterQuery(DAYS_SINCE_FIRST_DOSE_AT_END, daysOnStudyAtEnd)).
                add(getFilterQuery(DURATION, duration)).
                add(getFilterQuery(DAYS_FROM_PREV_DOSE_TO_START, daysFromPrevDoseToStart)).
                add(getFilterQuery(SPECIAL_INTEREST_GROUP, specialInterestGroup)).

                add(getFilterQuery(TEXT, description)).
                add(getFilterQuery(COMMENT, comment)).
                add(getFilterQuery(ACTION_TAKEN, actionTaken)).
                add(getFilterQuery(CAUSALITY, causality)).
                add(getFilterQueryForMapFilter(DRUGS_CAUSALITY_MAP, drugsCausality)).
                add(getFilterQueryForMultimapFilter(String.class, DRUGS_ACTION_TAKEN_MAP, drugsActionTaken)).
                add(getFilterQuery(OUTCOME, outcome)).
                add(getFilterQuery(REQUIRED_TREATMENT, requiredTreatment)).
                add(getFilterQuery(REQUIRES_HOSPITALISATION, requiresHospitalisation)).
                add(getFilterQuery(TREATMENT_EMERGENT, treatmentEmergent)).
                add(getFilterQuery(CAUSED_SUBJECT_WITHDRAWAL, causedSubjectWithdrawal)).
                add(getFilterQuery(DOSE_LIMITING_TOXICITY, doseLimitingToxicity)).
                add(getFilterQuery(TIMEPOINT, timePointDoseLimitingToxicity)).
                add(getFilterQuery(IMMUNE_MEDIATED, immuneMediated)).
                add(getFilterQuery(INFUSION_REACTION, infusionReaction)).
                add(getFilterQuery(SUSPECTED_ENDPOINT, suspectedEndpoint)).
                add(getFilterQuery(SUSPECTED_ENDPOINT_CAT, suspectedEndpointCategory)).
                add(getFilterQuery(AE_OF_SPECIAL_INTEREST, aeOfSpecialInterest)).
                add(getFilterQuery(AE_NUMBER, aeNumber)).
                add(getFilterQuery(START_PRIOR_TO_RAND, aeStartPriorToRandomisation)).
                add(getFilterQuery(END_PRIOR_TO_RAND, aeEndPriorToRandomisation)).
                add(getFilterQuery(USED_IN_TFL, usedInTfl)).
                build();
    }

    @Override
    public List<String> getEmptyFilterNames() {
        List<String> emptyFieldNames = super.getEmptyFilterNames();

        Set<String> specialInterestGroupValues = this.specialInterestGroup.getValues();

        if (specialInterestGroupValues != null && specialInterestGroupValues.size() == 1 && specialInterestGroupValues.contains("Default group")
                && !emptyFieldNames.contains("specialInterestGroup")) {
            emptyFieldNames.add("specialInterestGroup");
        }

        return emptyFieldNames;
    }
}
