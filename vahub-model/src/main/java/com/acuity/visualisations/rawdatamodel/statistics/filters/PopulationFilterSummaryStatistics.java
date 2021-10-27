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

package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.Subject;

public class PopulationFilterSummaryStatistics implements FilterSummaryStatistics<Subject> {

    private PopulationFilters populationFilters = new PopulationFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Subject subject = (Subject) value;

        populationFilters.getSubjectId().completeWithValue(subject.getSubjectCode());
        populationFilters.getStudyIdentifier().completeWithValue(subject.getClinicalStudyCode());
        populationFilters.getStudyPart().completeWithValue(subject.getStudyPart());
        populationFilters.getSex().completeWithValue(subject.getSex());
        populationFilters.getRace().completeWithValue(subject.getRace());
        populationFilters.getCountry().completeWithValue(subject.getCountry());
        populationFilters.getDeath().completeWithValue(subject.getDeathFlag());
        populationFilters.getDeathDate().completeWithValue(subject.getDateOfDeath());
        populationFilters.getPlannedTreatmentArm().completeWithValue(subject.getPlannedArm());
        populationFilters.getActualTreatmentArm().completeWithValue(subject.getActualArm());
        populationFilters.getCentreNumbers().completeWithValue(subject.getCenterNumber());
        populationFilters.getSiteIDs().completeWithValue(subject.getSiteId());
        populationFilters.getRegions().completeWithValue(subject.getRegion());
        populationFilters.getAge().completeWithValue(subject.getAge());
        populationFilters.getPhase().completeWithValue(subject.getPhase());
        populationFilters.getSpecifiedEthnicGroup().completeWithValue(subject.getSpecifiedEthnicGroup());
        populationFilters.getFirstTreatmentDate().completeWithValue(subject.getFirstTreatmentDate());
        populationFilters.getLastTreatmentDate().completeWithValue(subject.getLastTreatmentDate());
        populationFilters.getEthnicGroup().completeWithValue(subject.getEthnicGroup());
        populationFilters.getDurationOnStudy().completeWithValue(subject.getDurationOnStudy());
        populationFilters.getWithdrawalCompletion().completeWithValue(subject.getWithdrawal());
        populationFilters.getWithdrawalCompletionReason().completeWithValue(subject.getReasonForWithdrawal());
        populationFilters.getWithdrawalCompletionDate().completeWithValue(subject.getDateOfWithdrawal());
        populationFilters.getRandomised().completeWithValue(subject.getRandomised());
        populationFilters.getRandomisationDate().completeWithValue(subject.getDateOfRandomisation());
        populationFilters.getDoseCohort().completeWithValue(subject.getDoseCohort());
        populationFilters.getOtherCohort().completeWithValue(subject.getOtherCohort());
        populationFilters.getAttendedVisits().completeWithValues(subject.getAttendedVisitNumbers());
        populationFilters.getDrugsDosed().completeWithValue(subject.getDrugsDosed());
        populationFilters.getDrugsDiscontinued().completeWithValue(subject.getDrugsDiscontinued());
        populationFilters.getDrugsMaxDoses().completeWithValue(subject.getDrugsMaxDoses());
        populationFilters.getDrugsMaxFrequencies().completeWithValue(subject.getDrugsMaxFrequencies());
        populationFilters.getDrugsDiscontinuationReason().completeWithValue(subject.getDrugDiscontinuationMainReason());
        populationFilters.getDrugsDiscontinuationDate().completeWithValue(subject.getDrugDiscontinuationDate());
        populationFilters.getDrugsTotalDurationExclBreaks().completeWithValue(subject.getDrugTotalDurationExclBreaks());
        populationFilters.getDrugsTotalDurationInclBreaks().completeWithValue(subject.getDrugTotalDurationInclBreaks());
        populationFilters.getMedicalHistory().completeWithValues(subject.getMedicalHistories());
        populationFilters.getStudySpecificFilters().completeWithValues(subject.getStudySpecificFilters());
        populationFilters.getSafetyPopulation().completeWithValue(subject.getSafetyPopulation());

        populationFilters.setMatchedItemsCount(count);

    }

    @Override
    public void combine(FilterSummaryStatistics<Subject> other) {

        PopulationFilterSummaryStatistics otherSubject = (PopulationFilterSummaryStatistics) other;
        count += otherSubject.count;

        populationFilters.getSubjectId().complete(otherSubject.populationFilters.getSubjectId());
        populationFilters.getStudyIdentifier().complete(otherSubject.populationFilters.getStudyIdentifier());
        populationFilters.getStudyPart().complete(otherSubject.populationFilters.getStudyPart());
        populationFilters.getSex().complete(otherSubject.populationFilters.getSex());
        populationFilters.getRace().complete(otherSubject.populationFilters.getRace());
        populationFilters.getCountry().complete(otherSubject.populationFilters.getCountry());
        populationFilters.getDeath().complete(otherSubject.populationFilters.getDeath());
        populationFilters.getDeathDate().complete(otherSubject.populationFilters.getDeathDate());
        populationFilters.getPlannedTreatmentArm().complete(otherSubject.populationFilters.getPlannedTreatmentArm());
        populationFilters.getActualTreatmentArm().complete(otherSubject.populationFilters.getActualTreatmentArm());
        populationFilters.getCentreNumbers().complete(otherSubject.populationFilters.getCentreNumbers());
        populationFilters.getSiteIDs().complete(otherSubject.populationFilters.getSiteIDs());
        populationFilters.getRegions().complete(otherSubject.populationFilters.getRegions());
        populationFilters.getAge().complete(otherSubject.populationFilters.getAge());
        populationFilters.getPhase().complete(otherSubject.populationFilters.getPhase());
        populationFilters.getSpecifiedEthnicGroup().complete(otherSubject.populationFilters.getSpecifiedEthnicGroup());
        populationFilters.getFirstTreatmentDate().complete(otherSubject.populationFilters.getFirstTreatmentDate());
        populationFilters.getLastTreatmentDate().complete(otherSubject.populationFilters.getLastTreatmentDate());
        populationFilters.getEthnicGroup().complete(otherSubject.populationFilters.getEthnicGroup());
        populationFilters.getDurationOnStudy().complete(otherSubject.populationFilters.getDurationOnStudy());
        populationFilters.getWithdrawalCompletion().complete(otherSubject.populationFilters.getWithdrawalCompletion());
        populationFilters.getWithdrawalCompletionReason().complete(otherSubject.populationFilters.getWithdrawalCompletionReason());
        populationFilters.getWithdrawalCompletionDate().complete(otherSubject.populationFilters.getWithdrawalCompletionDate());
        populationFilters.getRandomised().complete(otherSubject.populationFilters.getRandomised());
        populationFilters.getRandomisationDate().complete(otherSubject.populationFilters.getRandomisationDate());
        populationFilters.getDoseCohort().complete(otherSubject.populationFilters.getDoseCohort());
        populationFilters.getOtherCohort().complete(otherSubject.populationFilters.getOtherCohort());
        populationFilters.getAttendedVisits().complete(otherSubject.populationFilters.getAttendedVisits());
        populationFilters.getBiomarkerGroups().complete(otherSubject.populationFilters.getBiomarkerGroups());
        populationFilters.getDrugsDosed().complete(otherSubject.populationFilters.getDrugsDosed());
        populationFilters.getDrugsDiscontinued().complete(otherSubject.populationFilters.getDrugsDiscontinued());
        populationFilters.getDrugsMaxDoses().complete(otherSubject.populationFilters.getDrugsMaxDoses());
        populationFilters.getDrugsMaxFrequencies().complete(otherSubject.populationFilters.getDrugsMaxFrequencies());
        populationFilters.getDrugsDiscontinuationReason().complete(otherSubject.populationFilters.getDrugsDiscontinuationReason());
        populationFilters.getDrugsDiscontinuationDate().complete(otherSubject.populationFilters.getDrugsDiscontinuationDate());
        populationFilters.getDrugsTotalDurationExclBreaks().complete(otherSubject.populationFilters.getDrugsTotalDurationExclBreaks());
        populationFilters.getDrugsTotalDurationInclBreaks().complete(otherSubject.populationFilters.getDrugsTotalDurationInclBreaks());
        populationFilters.getMedicalHistory().complete(otherSubject.populationFilters.getMedicalHistory());
        populationFilters.getStudySpecificFilters().complete(otherSubject.populationFilters.getStudySpecificFilters());
        populationFilters.getSafetyPopulation().complete(otherSubject.populationFilters.getSafetyPopulation());
        
        populationFilters.setMatchedItemsCount(count);
    }

    @Override
    public final PopulationFilters getFilters() {
        return populationFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PopulationFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Subject> newStatistics() {
        return new PopulationFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{populationFilters=%s}",
                this.getClass().getSimpleName(),
                this.populationFilters.toString());
    }
}
