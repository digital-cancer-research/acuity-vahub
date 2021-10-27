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
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;

import java.io.Serializable;
import java.util.OptionalInt;


public class Assessment extends SubjectAwareWrapper<AssessmentRaw> implements Serializable {

    public Assessment(AssessmentRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<Assessment> {
        ID(EntityAttribute.attribute("id", Assessment::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Assessment::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Assessment::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Assessment::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Assessment::getSubjectCode)),
        NEW_LESION_SINCE_BASELINE(EntityAttribute.attribute("newLesionSinceBaseline", (Assessment e) -> e.getEvent().getNewLesionSinceBaseline())),
        ASSESSMENT_DATE(EntityAttribute.attribute("assessmentDate", (Assessment e) -> e.getEvent().getAssessmentDate())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (Assessment e) -> e.getEvent().getVisitNumber())),
        RESPONSE(EntityAttribute.attribute("response", (Assessment e) -> e.getEvent().getResponse())),
        LESION_SITE(EntityAttribute.attribute("lesionSite", (Assessment e) -> e.getEvent().getLesionSite()));

        @Getter
        private final EntityAttribute<Assessment> attribute;

        Attributes(EntityAttribute<Assessment> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 2, columnName = "studyDay", displayName = "Study day", type = Column.Type.SSV)
    public Integer getStudyDay() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getVisitDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }
}
