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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.ANTIBIOTICS_TREATMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.CLASSIFICATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.DAYS_ON_STUDY_AT_END;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.DAYS_ON_STUDY_AT_START;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.DEPOT_CORTICOSTEROID_TREATMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.DURATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.EMERGENCY_ROOM_VISIT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.END_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.END_PRIOR_TO_RANDOMISATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.HOSPITALISATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.INCREASED_INHALED_CORTICOSTEROID_TREATMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.START_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.START_PRIOR_TO_RANDOMISATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation.Attributes.SYSTEMIC_CORTICOSTEROID_TREATMENT;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ExacerbationFilters extends Filters<Exacerbation> {
    protected SetFilter<String> exacerbationClassification = new SetFilter<>();
    protected DateRangeFilter startDate = new DateRangeFilter();
    protected DateRangeFilter endDate = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudyAtStart = new RangeFilter<>();
    protected RangeFilter<Integer> daysOnStudyAtEnd = new RangeFilter<>();
    protected RangeFilter<Integer> duration = new RangeFilter<>();
    protected SetFilter<String> startPriorToRandomisation = new SetFilter<>();
    protected SetFilter<String> endPriorToRandomisation = new SetFilter<>();
    protected SetFilter<String> hospitalisation = new SetFilter<>();
    protected SetFilter<String> emergencyRoomVisit = new SetFilter<>();
    protected SetFilter<String> antibioticsTreatment = new SetFilter<>();
    protected SetFilter<String> depotCorticosteroidTreatment = new SetFilter<>();
    protected SetFilter<String> systemicCorticosteroidTreatment = new SetFilter<>();
    protected SetFilter<String> increasedInhaledCorticosteroidTreatment = new SetFilter<>();

    public static ExacerbationFilters empty() {
        return new ExacerbationFilters();
    }

    @Override
    public Query<Exacerbation> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Exacerbation> cqb = new CombinedQueryBuilder<>(Exacerbation.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            final SetFilter<String> stringSetFilter = new SetFilter<>(subjectIds);
            cqb.add(getFilterQuery(Exacerbation.Attributes.SUBJECT_ID, stringSetFilter));
        }
        return cqb.add(getFilterQuery(CLASSIFICATION, this.exacerbationClassification)).
                add(getFilterQuery(START_DATE, this.startDate)).
                add(getFilterQuery(END_DATE, this.endDate)).
                add(getFilterQuery(DAYS_ON_STUDY_AT_START, this.daysOnStudyAtStart)).
                add(getFilterQuery(DAYS_ON_STUDY_AT_END, this.daysOnStudyAtEnd)).
                add(getFilterQuery(DURATION, this.duration)).
                add(getFilterQuery(START_PRIOR_TO_RANDOMISATION, this.startPriorToRandomisation)).
                add(getFilterQuery(END_PRIOR_TO_RANDOMISATION, this.endPriorToRandomisation)).
                add(getFilterQuery(HOSPITALISATION, this.hospitalisation)).
                add(getFilterQuery(EMERGENCY_ROOM_VISIT, this.emergencyRoomVisit)).
                add(getFilterQuery(ANTIBIOTICS_TREATMENT, this.antibioticsTreatment)).
                add(getFilterQuery(DEPOT_CORTICOSTEROID_TREATMENT, this.depotCorticosteroidTreatment)).
                add(getFilterQuery(SYSTEMIC_CORTICOSTEROID_TREATMENT, this.systemicCorticosteroidTreatment)).
                add(getFilterQuery(INCREASED_INHALED_CORTICOSTEROID_TREATMENT, this.increasedInhaledCorticosteroidTreatment)).
                build();
    }
}
