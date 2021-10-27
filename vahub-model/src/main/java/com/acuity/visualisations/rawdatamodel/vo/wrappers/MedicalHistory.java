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

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
public final class MedicalHistory extends SubjectAwareWrapper<MedicalHistoryRaw> implements HasStartEndDate {

    public MedicalHistory(MedicalHistoryRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStart();
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEnd();
    }

    public enum Attributes implements GroupByOption<MedicalHistory> {

        ID(EntityAttribute.attribute("id", (MedicalHistory e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (MedicalHistory e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (MedicalHistory e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (MedicalHistory e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (MedicalHistory e) -> e.getSubject().getSubjectCode())),
        PREFERRED_TERM(EntityAttribute.attribute("preferredTerm", (MedicalHistory e) -> e.getEvent().getPreferredTerm())),
        CONDITION_STATUS(EntityAttribute.attribute("conditionStatus", (MedicalHistory e) -> e.getEvent().getConditionStatus())),
        CURRENT_MEDICATION(EntityAttribute.attribute("currentMedication", (MedicalHistory e) -> e.getEvent().getCurrentMedication())),
        CATEGORY(EntityAttribute.attribute("category", (MedicalHistory e) -> e.getEvent().getCategory())),
        START_DATE(EntityAttribute.attribute("startDate", MedicalHistory::getStartDate)),
        END_DATE(EntityAttribute.attribute("endDate", MedicalHistory::getEndDate)),
        SOC(EntityAttribute.attribute("soc", (MedicalHistory e) -> e.getEvent().getSoc())),
        HLT(EntityAttribute.attribute("hlt", (MedicalHistory e) -> e.getEvent().getHlt())),
        TERM(EntityAttribute.attribute("term", (MedicalHistory e) -> e.getEvent().getTerm()));

        @Getter
        private final EntityAttribute<MedicalHistory> attribute;

        Attributes(EntityAttribute<MedicalHistory> attribute) {
            this.attribute = attribute;
        }
    }

    public boolean endsBeforeFirstTreatmentDate() {
        return getEvent().getEnd() != null
                && getDateOfFirstDose() != null
                && getEvent().getEnd().before(getDateOfFirstDose());
    }
}
