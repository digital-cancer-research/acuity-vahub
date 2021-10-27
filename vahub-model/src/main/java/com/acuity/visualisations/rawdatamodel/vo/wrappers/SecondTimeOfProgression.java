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
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecondTimeOfProgression extends SubjectAwareWrapper<SecondTimeOfProgressionRaw> implements Serializable {
    public SecondTimeOfProgression(SecondTimeOfProgressionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<SecondTimeOfProgression> {

        ID(EntityAttribute.attribute("id", (SecondTimeOfProgression e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SecondTimeOfProgression e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SecondTimeOfProgression e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SecondTimeOfProgression e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SecondTimeOfProgression e) -> e.getSubject().getSubjectCode())),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (SecondTimeOfProgression e) -> e.getEvent().getVisitDate())),
        SCAN_DATE(EntityAttribute.attribute("scanDate", (SecondTimeOfProgression e) -> e.getEvent().getScanDate())),
        ASSESSMENT_PERFORMED(EntityAttribute.attribute("assessmentPerformed", (SecondTimeOfProgression e) -> e.getEvent().getAssessmentPerformed()));

        @Getter
        private final EntityAttribute<SecondTimeOfProgression> attribute;

        Attributes(EntityAttribute<SecondTimeOfProgression> attribute) {
            this.attribute = attribute;
        }
    }
}
