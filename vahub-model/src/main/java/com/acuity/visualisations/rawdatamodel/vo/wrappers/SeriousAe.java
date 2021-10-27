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

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.OptionalInt;

@EqualsAndHashCode(callSuper = true)
public class SeriousAe extends SubjectAwareWrapper<SeriousAeRaw> {

    public SeriousAe(SeriousAeRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "daysFromFirstDoseToCriteria", order = 15, displayName = "Days from first dose to AE met criteria",
        type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    public Integer getDaysFromFirstDoseToCriteria() {
        final OptionalInt res = DaysUtil.daysBetween(getSubject().getDateOfFirstDose(), getEvent().getBecomeSeriousDate());
        return res.isPresent() ? res.getAsInt() : null;
    }

    public enum Attributes implements GroupByOption<SeriousAe> {
        ID(EntityAttribute.attribute("id", (SeriousAe e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SeriousAe e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SeriousAe e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SeriousAe e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SeriousAe e) -> e.getSubject().getSubjectCode())),
        EVENT(EntityAttribute.attribute("aeEvent", (SeriousAe e) -> e.getEvent().getAe())),
        START_DATE(EntityAttribute.attribute("startDate", (SeriousAe e) -> e.getEvent().getStartDate())),
        END_DATE(EntityAttribute.attribute("endDate", (SeriousAe e) -> e.getEvent().getEndDate())),
        PT(EntityAttribute.attribute("pt", (SeriousAe e) -> e.getEvent().getPt())),
        AE_NUMBER(EntityAttribute.attribute("aeNumber", (SeriousAe e) -> e.getEvent().getNum())),
        AE(EntityAttribute.attribute("ae", (SeriousAe e) -> e.getEvent().getAe())),
        DAYS_FROM_FIRST_DOSE_TO_CRITERIA(EntityAttribute.attribute("daysFromFirstDoseToCriteria", SeriousAe::getDaysFromFirstDoseToCriteria)),
        PRIMARY_DEATH_CAUSE(EntityAttribute.attribute("primaryDeathCause", (SeriousAe e) -> e.getEvent().getPrimaryDeathCause())),
        SECONDARY_DEATH_CAUSE(EntityAttribute.attribute("secondaryDeathCause", (SeriousAe e) -> e.getEvent().getSecondaryDeathCause())),
        OTHER_MEDICATION(EntityAttribute.attribute("otherMedication", (SeriousAe e) -> e.getEvent().getOtherMedication())),
        CAUSED_BY_OTHER_MEDICATION(EntityAttribute.attribute("causedByOtherMedication", (SeriousAe e) -> e.getEvent().getCausedByOtherMedication())),
        STUDY_PROCEDURE(EntityAttribute.attribute("studyProcedure", (SeriousAe e) -> e.getEvent().getStudyProcedure())),
        CAUSED_BY_STUDY(EntityAttribute.attribute("causedByStudy", (SeriousAe e) -> e.getEvent().getCausedByStudy())),
        DESCRIPTION(EntityAttribute.attribute("description", (SeriousAe e) -> e.getEvent().getDescription())),
        RESULT_IN_DEATH(EntityAttribute.attribute("resultInDeath", (SeriousAe e) -> e.getEvent().getResultInDeath())),
        HOSPITALIZATION_REQUIRED(EntityAttribute.attribute("hospitalizationRequired", (SeriousAe e) -> e.getEvent().getHospitalizationRequired())),
        CONGENITAL_ANOMALY(EntityAttribute.attribute("congenitalAnomaly", (SeriousAe e) -> e.getEvent().getCongenitalAnomaly())),
        LIFE_THREATENING(EntityAttribute.attribute("lifeThreatening", (SeriousAe e) -> e.getEvent().getLifeThreatening())),
        DISABILITY(EntityAttribute.attribute("disability", (SeriousAe e) -> e.getEvent().getDisability())),
        OTHER_SERIOUS_EVENT(EntityAttribute.attribute("otherSeriousEvent", (SeriousAe e) -> e.getEvent().getOtherSeriousEvent())),
        AD(EntityAttribute.attribute("ad", (SeriousAe e) -> e.getEvent().getAd())),
        CAUSED_BY_AD(EntityAttribute.attribute("causedByAD", (SeriousAe e) -> e.getEvent().getCausedByAD())),
        AD1(EntityAttribute.attribute("ad1", (SeriousAe e) -> e.getEvent().getAd1())),
        CAUSED_BY_AD1(EntityAttribute.attribute("causedByAD1", (SeriousAe e) -> e.getEvent().getCausedByAD1())),
        AD2(EntityAttribute.attribute("ad2", (SeriousAe e) -> e.getEvent().getAd2())),
        CAUSED_BY_AD2(EntityAttribute.attribute("causedByAD2", (SeriousAe e) -> e.getEvent().getCausedByAD2())),
        BECOME_SERIOUS_DATE(EntityAttribute.attribute("becomeSeriousDate", (SeriousAe e) -> e.getEvent().getBecomeSeriousDate())),
        FIND_OUT_DATE(EntityAttribute.attribute("findOutDate", (SeriousAe e) -> e.getEvent().getFindOutDate())),
        HOSPITALIZATION_DATE(EntityAttribute.attribute("hospitalizationDate", (SeriousAe e) -> e.getEvent().getHospitalizationDate())),
        DISCHARGE_DATE(EntityAttribute.attribute("dischargeDate", (SeriousAe e) -> e.getEvent().getDischargeDate()));

        @Getter
        private final EntityAttribute<SeriousAe> attribute;

        Attributes(EntityAttribute<SeriousAe> attribute) {
            this.attribute = attribute;
        }
    }

}
