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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SeriousAeFilters extends Filters<SeriousAe> {

    public static SeriousAeFilters empty() {
        return new SeriousAeFilters();
    }

    protected RangeFilter<Integer> aeNumber = new RangeFilter<>();
    protected SetFilter<String> ae = new SetFilter<>();
    protected SetFilter<String> pt = new SetFilter<>();
    protected DateRangeFilter startDate = new DateRangeFilter();
    protected DateRangeFilter endDate = new DateRangeFilter();
    protected RangeFilter<Integer> daysFromFirstDoseToCriteria = new RangeFilter<>();
    protected SetFilter<String> primaryDeathCause = new SetFilter<>();
    protected SetFilter<String> secondaryDeathCause = new SetFilter<>();
    protected SetFilter<String> otherMedication = new SetFilter<>();
    protected SetFilter<String> causedByOtherMedication = new SetFilter<>();
    protected SetFilter<String> studyProcedure = new SetFilter<>();
    protected SetFilter<String> causedByStudy = new SetFilter<>();
    protected SetFilter<String> description = new SetFilter<>();
    protected SetFilter<String> resultInDeath = new SetFilter<>();
    protected SetFilter<String> hospitalizationRequired = new SetFilter<>();
    protected SetFilter<String> congenitalAnomaly = new SetFilter<>();
    protected SetFilter<String> lifeThreatening = new SetFilter<>();
    protected SetFilter<String> disability = new SetFilter<>();
    protected SetFilter<String> otherSeriousEvent = new SetFilter<>();
    protected SetFilter<String> ad = new SetFilter<>();
    protected SetFilter<String> causedByAD = new SetFilter<>();
    protected SetFilter<String> ad1 = new SetFilter<>();
    protected SetFilter<String> causedByAD1 = new SetFilter<>();
    protected SetFilter<String> ad2 = new SetFilter<>();
    protected SetFilter<String> causedByAD2 = new SetFilter<>();
    protected DateRangeFilter becomeSeriousDate = new DateRangeFilter();
    protected DateRangeFilter findOutDate = new DateRangeFilter();
    protected DateRangeFilter hospitalizationDate = new DateRangeFilter();
    protected DateRangeFilter dischargeDate = new DateRangeFilter();

    @Override
    public Query<SeriousAe> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SeriousAe> cqb = new CombinedQueryBuilder<>(SeriousAe.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SeriousAe.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
            .add(getFilterQuery(SeriousAe.Attributes.AE_NUMBER, aeNumber))
            .add(getFilterQuery(SeriousAe.Attributes.AE, ae))
            .add(getFilterQuery(SeriousAe.Attributes.PT, pt))
            .add(getFilterQuery(SeriousAe.Attributes.START_DATE, startDate))
            .add(getFilterQuery(SeriousAe.Attributes.END_DATE, endDate))
            .add(getFilterQuery(SeriousAe.Attributes.DAYS_FROM_FIRST_DOSE_TO_CRITERIA, daysFromFirstDoseToCriteria))
            .add(getFilterQuery(SeriousAe.Attributes.PRIMARY_DEATH_CAUSE, primaryDeathCause))
            .add(getFilterQuery(SeriousAe.Attributes.SECONDARY_DEATH_CAUSE, secondaryDeathCause))
            .add(getFilterQuery(SeriousAe.Attributes.OTHER_MEDICATION, otherMedication))
            .add(getFilterQuery(SeriousAe.Attributes.CAUSED_BY_OTHER_MEDICATION, causedByOtherMedication))
            .add(getFilterQuery(SeriousAe.Attributes.STUDY_PROCEDURE, studyProcedure))
            .add(getFilterQuery(SeriousAe.Attributes.CAUSED_BY_STUDY, causedByStudy))
            .add(getFilterQuery(SeriousAe.Attributes.DESCRIPTION, description))
            .add(getFilterQuery(SeriousAe.Attributes.RESULT_IN_DEATH, resultInDeath))
            .add(getFilterQuery(SeriousAe.Attributes.HOSPITALIZATION_REQUIRED, hospitalizationRequired))
            .add(getFilterQuery(SeriousAe.Attributes.CONGENITAL_ANOMALY, congenitalAnomaly))
            .add(getFilterQuery(SeriousAe.Attributes.LIFE_THREATENING, lifeThreatening))
            .add(getFilterQuery(SeriousAe.Attributes.DISABILITY, disability))
            .add(getFilterQuery(SeriousAe.Attributes.OTHER_SERIOUS_EVENT, otherSeriousEvent))
            .add(getFilterQuery(SeriousAe.Attributes.AD, ad))
            .add(getFilterQuery(SeriousAe.Attributes.CAUSED_BY_AD, causedByAD))
            .add(getFilterQuery(SeriousAe.Attributes.AD1, ad1))
            .add(getFilterQuery(SeriousAe.Attributes.CAUSED_BY_AD1, causedByAD1))
            .add(getFilterQuery(SeriousAe.Attributes.AD2, ad2))
            .add(getFilterQuery(SeriousAe.Attributes.CAUSED_BY_AD2, causedByAD2))
            .add(getFilterQuery(SeriousAe.Attributes.BECOME_SERIOUS_DATE, becomeSeriousDate))
            .add(getFilterQuery(SeriousAe.Attributes.FIND_OUT_DATE, findOutDate))
            .add(getFilterQuery(SeriousAe.Attributes.HOSPITALIZATION_DATE, hospitalizationDate))
            .add(getFilterQuery(SeriousAe.Attributes.DISCHARGE_DATE, dischargeDate))
            .build();
    }
}
