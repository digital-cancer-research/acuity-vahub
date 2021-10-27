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

package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.MMM_YY;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YYYY;

public class Exacerbation extends SubjectAwareWrapper<ExacerbationRaw> implements HasStartEndDate, Serializable {

    public static final ExacerbationRaw NO_INCIDENCE = ExacerbationRaw.builder().build();

    public Exacerbation(ExacerbationRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getEndDate() {
        Date endDate = getEvent().getEndDate();
        return endDate != null ? endDate
                : Stream.of(getSubject()
                .getStudyLeaveDate(), getSubject().getDateOfDeath())
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }


    public enum Attributes implements GroupByOption<Exacerbation> {
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Exacerbation::getSubjectId)),
        CLASSIFICATION(EntityAttribute.attribute("exacerbationClassification", (Exacerbation e) -> e.getEvent().getExacerbationClassification())),
        START_DATE(EntityAttribute.attribute("startDate", (Exacerbation e) -> e.getEvent().getStartDate())),
        END_DATE(EntityAttribute.attribute("endDate", (Exacerbation e) -> e.getEvent().getEndDate())),
        DAYS_ON_STUDY_AT_START(EntityAttribute.attribute("daysOnStudyAtStart", (Exacerbation e) -> e.getEvent().getDaysOnStudyAtStart())),
        DAYS_ON_STUDY_AT_END(EntityAttribute.attribute("daysOnStudyAtEnd", (Exacerbation e) -> e.getEvent().getDaysOnStudyAtEnd())),
        DURATION(EntityAttribute.attribute("duration", (Exacerbation e) -> e.getEvent().getDuration())),
        START_PRIOR_TO_RANDOMISATION(EntityAttribute.attribute("startPriorToRandomisation", (Exacerbation e) -> e.getEvent().getStartPriorToRandomisation())),
        END_PRIOR_TO_RANDOMISATION(EntityAttribute.attribute("endPriorToRandomisation", (Exacerbation e) -> e.getEvent().getEndPriorToRandomisation())),
        HOSPITALISATION(EntityAttribute.attribute("hospitalisation", (Exacerbation e) -> e.getEvent().getHospitalisation())),
        EMERGENCY_ROOM_VISIT(EntityAttribute.attribute("emergencyRoomVisit", (Exacerbation e) -> e.getEvent().getEmergencyRoomVisit())),
        ANTIBIOTICS_TREATMENT(EntityAttribute.attribute("antibioticsTrt", (Exacerbation e) -> e.getEvent().getAntibioticsTreatment())),
        DEPOT_CORTICOSTEROID_TREATMENT(EntityAttribute.attribute("depotCorticosteroidTreatment",
                (Exacerbation e) -> e.getEvent().getDepotCorticosteroidTreatment())),
        SYSTEMIC_CORTICOSTEROID_TREATMENT(EntityAttribute.attribute("sysCortTrt", (Exacerbation e) -> e.getEvent().getSystemicCorticosteroidTreatment())),
        INCREASED_INHALED_CORTICOSTEROID_TREATMENT(EntityAttribute.attribute("incInhaledCortTrt",
                (Exacerbation e) -> e.getEvent().getIncreasedInhaledCorticosteroidTreatment())),
        WITHDRAWAL(EntityAttribute.attribute("withdrawal", (Exacerbation e) -> e.getSubject().getWithdrawal())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Exacerbation e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_NAME(EntityAttribute.attribute("studyName", (Exacerbation e) -> e.getSubject().getClinicalStudyName())),
        STUDY_PART_ID(EntityAttribute.attribute("studyPart", (Exacerbation e) -> e.getSubject().getStudyPart())),
        DURATION_ON_STUDY(EntityAttribute.attribute("durationOnStudy", (Exacerbation e) -> e.getSubject().getDurationOnStudy())),
        RANDOMISATION_DATE(EntityAttribute.attribute("randomisationDate", (Exacerbation e) -> e.getSubject().getDateOfRandomisation())),
        REASON_FOR_WITHDRAWAL(EntityAttribute.attribute("reasonForWithdrawal", (Exacerbation e) -> e.getSubject().getReasonForWithdrawal())),
        CENTRE(EntityAttribute.attribute("centre", (Exacerbation e) -> e.getSubject().getCenterNumber())),
        COUNTRY(EntityAttribute.attribute("country", (Exacerbation e) -> e.getSubject().getCountry())),
        SEX(EntityAttribute.attribute("sex", (Exacerbation e) -> e.getSubject().getSex())),
        RACE(EntityAttribute.attribute("race", (Exacerbation e) -> e.getSubject().getRace())),
        AGE(EntityAttribute.attribute("age", (Exacerbation e) -> e.getSubject().getAge())),
        FIRST_TREATMENT_DATE(EntityAttribute.attribute("firstTreatmentDate", (Exacerbation e) -> e.getSubject().getFirstTreatmentDate())),
        DEATH(EntityAttribute.attribute("death", (Exacerbation e) -> e.getSubject().getDeathFlag())),
        PLANNED_TREATMENT_ARM(EntityAttribute.attribute("plannedTreatmentArm", (Exacerbation e) -> e.getSubject().getPlannedArm())),
        ACTUAL_TREATMENT_ARM(EntityAttribute.attribute("actualTreatmentArm", (Exacerbation e) -> e.getSubject().getActualArm())),
        SITE_ID(EntityAttribute.attribute("siteId", (Exacerbation e) -> e.getSubject().getSiteId())),
        COUNTRY_AND_REGION(EntityAttribute.attribute("countryAndRegion", (Exacerbation e) -> e.getSubject().getCountryAndRegion())),
        WEIGHT(EntityAttribute.attribute("weight", (Exacerbation e) -> e.getSubject().getWeight())),
        HEIGHT(EntityAttribute.attribute("height", (Exacerbation e) -> e.getSubject().getHeight())),
        LAST_TREATMENT_DATE(EntityAttribute.attribute("lastTreatmentDate", (Exacerbation e) -> e.getSubject().getLastTreatmentDate())),
        DATE_OF_DEATH(EntityAttribute.attribute("dateOfDeath", (Exacerbation e) -> e.getSubject().getDateOfDeath())),
        DISC_MAP(EntityAttribute.attribute("drugsDiscontinued", (Exacerbation e) -> e.getSubject().getDrugsDiscontinued())),
        DISC_REASONS_MAP(EntityAttribute.attribute("drugDiscontinuationMainReason", (Exacerbation e) -> e.getSubject().getDrugDiscontinuationMainReason())),
        STUDY_COUNTRY_CENTRE_SEVERITY(EntityAttribute.attribute("studyCountryCentreSeverityString", Exacerbation::getStudyCountryCentreSeverityString)),
        PHASE(EntityAttribute.attribute("phase", (Exacerbation e) -> e.getSubject().getPhase())),
        MONTH(EntityAttribute.attribute("month", Exacerbation::getMonth)),
        YEAR(EntityAttribute.attribute("year", Exacerbation::getYear));

        @Getter
        private final EntityAttribute<Exacerbation> attribute;

        Attributes(EntityAttribute<Exacerbation> attribute) {
            this.attribute = attribute;
        }
    }

    public String getCentre() {
        return getSubject().getCenterNumber();
    }

    private String getStudyCountryCentreSeverityString() {
        return String.join(" - ", defaultNullableValue(getClinicalStudyName()).toString(), defaultNullableValue(getSubject().getCountry()).toString(),
                defaultNullableValue(getSubject().getCenterNumber()).toString(), defaultNullableValue(getEvent().getExacerbationClassification()).toString());
    }

    private String getMonth() {
        if (getStartDate() == null) {
            return null;
        }
        return DaysUtil.toString(getStartDate(), MMM_YY);
    }

    private String getYear() {
        if (getStartDate() == null) {
            return null;
        }
        return DaysUtil.toString(getStartDate(), YYYY);
    }

    public Integer getDaysOnStudyAtStart() {
        return getEvent().getDaysOnStudyAtStart();
    }

    public Integer getDaysOnStudyAtEnd() {
        return getEvent().getDaysOnStudyAtEnd();
    }

    @Override
    public Integer getDuration() {
        return getEvent().getDuration();
    }

    public String getStartPriorToRandomisation() {
        return getEvent().getStartPriorToRandomisation();
    }

    public String getEndPriorToRandomisation() {
        return getEvent().getEndPriorToRandomisation();
    }
}
