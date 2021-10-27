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
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SurgicalHistory extends SubjectAwareWrapper<SurgicalHistoryRaw> implements HasStartDate {

    public SurgicalHistory(SurgicalHistoryRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStart();
    }

    public enum Attributes implements GroupByOption<SurgicalHistory> {

        ID(EntityAttribute.attribute("id", (SurgicalHistory e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SurgicalHistory e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SurgicalHistory e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SurgicalHistory e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SurgicalHistory e) -> e.getSubject().getSubjectCode())),
        PREFERRED_TERM(EntityAttribute.attribute("preferredTerm", (SurgicalHistory e) -> e.getEvent().getPreferredTerm())),
        CURRENT_MEDICATION(EntityAttribute.attribute("currentMedication", (SurgicalHistory e) -> e.getEvent().getCurrentMedication())),
        SURGICAL_PROCEDURE(EntityAttribute.attribute("surgicalProcedure", (SurgicalHistory e) -> e.getEvent().getSurgicalProcedure())),
        START_DATE(EntityAttribute.attribute("startDate", SurgicalHistory::getStartDate)),
        HLT(EntityAttribute.attribute("hlt", (SurgicalHistory e) -> e.getEvent().getHlt())),
        SOC(EntityAttribute.attribute("soc", (SurgicalHistory e) -> e.getEvent().getSoc()));

        @Getter
        private final EntityAttribute<SurgicalHistory> attribute;

        Attributes(EntityAttribute<SurgicalHistory> attribute) {
            this.attribute = attribute;
        }
    }
}
