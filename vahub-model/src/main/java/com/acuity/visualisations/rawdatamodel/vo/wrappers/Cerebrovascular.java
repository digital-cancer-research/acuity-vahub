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
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasAssociatedAe;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Cerebrovascular extends SubjectAwareWrapper<CerebrovascularRaw> implements HasStartDate, Serializable, HasAssociatedAe {

    public Cerebrovascular(CerebrovascularRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "aeNumber", order = 2, displayName = "Ae number")
    @Override
    public String getAeNumber() {
        return getSubjectCode() + "-" + getEvent().getAeNumber();
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<Cerebrovascular> {
        STUDY_ID(EntityAttribute.attribute("STUDY_ID", Cerebrovascular::getStudyId)),
        PART(EntityAttribute.attribute("PART", Cerebrovascular::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("SUBJECT_ID", Cerebrovascular::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("SUBJECT_ID", Cerebrovascular::getSubjectCode)),
        EVENT_TYPE(EntityAttribute.attribute("EVENT_TYPE", (Cerebrovascular e) -> e.getEvent().getEventType())),
        AE_NUMBER(EntityAttribute.attribute("AE_NUMBER", Cerebrovascular::getAeNumber)),
        EVENT_START_DATE(EntityAttribute.attribute("EVENT_START_DATE", (Cerebrovascular e) -> e.getEvent().getStartDate())),
        EVENT_TERM(EntityAttribute.attribute("EVENT_TERM", (Cerebrovascular e) -> e.getEvent().getTerm())),
        PRIMARY_ISCHEMIC_STROKE(EntityAttribute.attribute("PRIMARY_ISCHEMIC_STROKE", (Cerebrovascular e) -> e.getEvent().getPrimaryIschemicStroke())),
        TRAUMATIC(EntityAttribute.attribute("TRAUMATIC", (Cerebrovascular e) -> e.getEvent().getTraumatic())),
        INTRA_HEMORRHAGE_LOC(EntityAttribute.attribute("INTRA_HEMORRHAGE_LOC", (Cerebrovascular e) -> e.getEvent().getIntraHemorrhageLoc())),
        INTRA_HEMORRHAGE_OTHER_LOC(EntityAttribute.attribute("INTRA_HEMORRHAGE_OTHER_LOC", (Cerebrovascular e) -> e.getEvent().getIntraHemorrhageOtherLoc())),
        SYMPTOMS_DURATION(EntityAttribute.attribute("SYMPTOMS_DURATION", (Cerebrovascular e) -> e.getEvent().getSymptomsDuration())),
        MRS_PRIOR_STROKE(EntityAttribute.attribute("MRS_PRIOR_STROKE", (Cerebrovascular e) -> e.getEvent().getMrsPriorToStroke())),
        MRS_DURING_STROKE_HOSP(EntityAttribute.attribute("MRS_DURING_STROKE_HOSP", (Cerebrovascular e) -> e.getEvent().getMrsDuringStrokeHosp())),
        MRS_CURR_VISIT_OR_90D_AFTER(EntityAttribute.attribute("MRS_CURR_VISIT_OR_90D_AFTER", (Cerebrovascular e) -> e.getEvent().getMrsCurrVisitOr90dAfter())),
        COMMENT(EntityAttribute.attribute("COMMENT", (Cerebrovascular e) -> e.getEvent().getComment()));

        @Getter
        private final EntityAttribute<Cerebrovascular> attribute;

        Attributes(EntityAttribute<Cerebrovascular> attribute) {
            this.attribute = attribute;
        }
    }

}
