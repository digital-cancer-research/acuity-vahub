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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CIEventFilters extends Filters<CIEvent> {

    @JsonIgnore
    public static CIEventFilters empty() {
        return new CIEventFilters();
    }

    private SetFilter<String> finalDiagnosis = new SetFilter<>();
    private SetFilter<String> otherDiagnosis = new SetFilter<>();
    private DateRangeFilter eventStartDate = new DateRangeFilter();
    private SetFilter<String> eventTerm = new SetFilter<>();
    private SetFilter<String> aeNumber = new SetFilter<>();
    private SetFilter<String> ischemicSymptoms = new SetFilter<>();
    private SetFilter<String> cieSymptomsDuration = new SetFilter<>();
    private SetFilter<String> sympPromtUnsHosp = new SetFilter<>();
    private SetFilter<String> eventSuspToBeDueToStentTromb = new SetFilter<>();
    private SetFilter<String> previousEcgAvailable = new SetFilter<>();
    private DateRangeFilter previousEcgDate = new DateRangeFilter();
    private SetFilter<String> noEcgAtTheEventTime = new SetFilter<>();
    private SetFilter<String> ecgAtTheEventTime = new SetFilter<>();
    private SetFilter<String> localCardiacBiomarkersDrawn = new SetFilter<>();
    private SetFilter<String> coronaryAngiography = new SetFilter<>();
    private DateRangeFilter angiographyDate = new DateRangeFilter();
    private SetFilter<String> description1 = new SetFilter<>();
    private SetFilter<String> description2 = new SetFilter<>();
    private SetFilter<String> description3 = new SetFilter<>();
    private SetFilter<String> description4 = new SetFilter<>();
    private SetFilter<String> description5 = new SetFilter<>();

    @Override
    public Query<CIEvent> getQuery() {
        return getQuery(newArrayList());
    }

    @Override
    public Query<CIEvent> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<CIEvent> cqb = new CombinedQueryBuilder<>(CIEvent.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(CIEvent.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(CIEvent.Attributes.FINAL_DIAGNOSIS, finalDiagnosis)).
                add(getFilterQuery(CIEvent.Attributes.OTHER_DIAGNOSIS, otherDiagnosis)).
                add(getFilterQuery(CIEvent.Attributes.AE_NUMBER, aeNumber)).
                add(getFilterQuery(CIEvent.Attributes.TERM, eventTerm)).
                add(getFilterQuery(CIEvent.Attributes.START_DATE, eventStartDate)).
                add(getFilterQuery(CIEvent.Attributes.ISHEMIC_SYMTOMS, ischemicSymptoms)).
                add(getFilterQuery(CIEvent.Attributes.CI_SYMPTOMS_DURATION, cieSymptomsDuration)).
                add(getFilterQuery(CIEvent.Attributes.DID_SYMPTOMS_PROMPT_UNS_HOSP, sympPromtUnsHosp)).
                add(getFilterQuery(CIEvent.Attributes.EVENT_SUSP_DUE_TO_STENT_THROMB, eventSuspToBeDueToStentTromb)).
                add(getFilterQuery(CIEvent.Attributes.PREVIOUS_ECG_DATE, previousEcgDate)).
                add(getFilterQuery(CIEvent.Attributes.WAS_THERE_NO_ECG_AT_THE_EVENT_TIME, noEcgAtTheEventTime)).
                add(getFilterQuery(CIEvent.Attributes.PREVIOUS_ECG_AVAILABLE, previousEcgAvailable)).
                add(getFilterQuery(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME, ecgAtTheEventTime)).
                add(getFilterQuery(CIEvent.Attributes.WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN, localCardiacBiomarkersDrawn)).
                add(getFilterQuery(CIEvent.Attributes.CORONARY_ANGIOGRAPHY, coronaryAngiography)).
                add(getFilterQuery(CIEvent.Attributes.ANGIOGRAPHY_DATE, angiographyDate)).
                add(getFilterQuery(CIEvent.Attributes.DESCRIPTION_1, description1)).
                add(getFilterQuery(CIEvent.Attributes.DESCRIPTION_2, description2)).
                add(getFilterQuery(CIEvent.Attributes.DESCRIPTION_3, description3)).
                add(getFilterQuery(CIEvent.Attributes.DESCRIPTION_4, description4)).
                add(getFilterQuery(CIEvent.Attributes.DESCRIPTION_5, description5)).
                build();
    }
}
