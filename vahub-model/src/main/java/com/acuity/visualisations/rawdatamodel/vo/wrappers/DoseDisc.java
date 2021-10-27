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
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DoseDisc extends SubjectAwareWrapper<DoseDiscRaw> implements Serializable {

    public DoseDisc(DoseDiscRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<DoseDisc> {

        ID(EntityAttribute.attribute("id", (DoseDisc e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (DoseDisc death) -> death.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (DoseDisc death) -> death.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (DoseDisc death) -> death.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (DoseDisc death) -> death.getSubject().getSubjectCode())),
        STUDY_DRUG(EntityAttribute.attribute("studyDrug", (DoseDisc e) -> e.getEvent().getStudyDrug())),
        DISC_DATE(EntityAttribute.attribute("discDate", (DoseDisc e) -> e.getEvent().getDiscDate())),
        DISC_REASON(EntityAttribute.attribute("discReason", (DoseDisc e) -> e.getEvent().getDiscReason())),
        DISC_SPEC(EntityAttribute.attribute("discSpec", (DoseDisc e) -> e.getEvent().getIpDiscSpec())),
        SUBJECT_DECISION_SPEC(EntityAttribute.attribute("subjectDecisionSpec",
                (DoseDisc e) -> e.getEvent().getSubjectDecisionSpec())),
        SUBJECT_DECISION_SPEC_OTHER(EntityAttribute.attribute("subjectDecisionSpecOther",
                (DoseDisc e) -> e.getEvent().getSubjectDecisionSpecOther())),
        STUDY_DAY_AT_DISC(EntityAttribute.attribute("studyDayAtDisc", DoseDisc::getStudyDayAtIpDiscontinuation));

        @Getter
        private final EntityAttribute<DoseDisc> attribute;

        Attributes(EntityAttribute<DoseDisc> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 3, displayName = "Study day at IP discontinuation", columnName = "studyDayAtIpDiscontinuation",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    public Integer getStudyDayAtIpDiscontinuation() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getDiscDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }

    @Column(order = 4, columnName = "durationOnTherapy", displayName = "Duration on therapy", type = Column.Type.SSV)
    public Integer getDurationOnTherapy() {
        Integer studyDayAtIpDiscontinuation = getStudyDayAtIpDiscontinuation();
        return studyDayAtIpDiscontinuation == null ? null : studyDayAtIpDiscontinuation + 1;
    }

    public Date getDiscDate() {
        return getEvent().getDiscDate();
    }

    public String getStudyDrug() {
        return getEvent().getStudyDrug();
    }
}
