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
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import lombok.Getter;

import java.io.Serializable;
import java.util.OptionalInt;

public class TargetLesion extends SubjectAwareWrapper<TargetLesionRaw> implements Serializable {

    public TargetLesion(TargetLesionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<TargetLesion> {
        ID(EntityAttribute.attribute("id", TargetLesion::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", TargetLesion::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", TargetLesion::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", TargetLesion::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", TargetLesion::getSubjectCode)),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (TargetLesion e) -> e.getEvent().getVisitNumber())),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (TargetLesion e) -> e.getEvent().getVisitDate())),
        LESION_NUMBER(EntityAttribute.attribute("lesionNumber", (TargetLesion e) -> e.getEvent().getLesionNumber())),
        LESION_DATE(EntityAttribute.attribute("lesionDate", (TargetLesion e) -> e.getEvent().getLesionDate())),
        LESION_DIAMETER(EntityAttribute.attribute("lesionDiameter", (TargetLesion e) -> e.getEvent().getLesionDiameter())),
        LESION_SITE(EntityAttribute.attribute("lesionSite", (TargetLesion e) -> e.getEvent().getLesionSite()));

        @Getter
        private final EntityAttribute<TargetLesion> attribute;

        Attributes(EntityAttribute<TargetLesion> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 2, columnName = "studyDay", displayName = "Study day", type = Column.Type.SSV)
    public Integer getStudyDay() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getLesionDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }

}
