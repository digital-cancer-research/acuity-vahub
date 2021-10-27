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
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class NonTargetLesion extends SubjectAwareWrapper<NonTargetLesionRaw> implements Serializable {

    public NonTargetLesion(NonTargetLesionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<NonTargetLesion> {

        ID(EntityAttribute.attribute("id", (NonTargetLesion e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (NonTargetLesion e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (NonTargetLesion e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (NonTargetLesion e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (NonTargetLesion e) -> e.getSubject().getSubjectCode())),
        LESION_DATE(EntityAttribute.attribute("lesionDate", (NonTargetLesion e) -> e.getEvent().getLesionDate())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (NonTargetLesion e) -> e.getEvent().getVisitNumber())),
        LESION_SITE(EntityAttribute.attribute("lesionSite", (NonTargetLesion e) -> e.getEvent().getLesionSite()));

        @Getter
        private final EntityAttribute<NonTargetLesion> attribute;

        Attributes(EntityAttribute<NonTargetLesion> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 2, columnName = "studyDay", displayName = "Study day", type = Column.Type.SSV)
    public Integer getStudyDay() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getLesionDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }
}
