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
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurvivalStatusRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SurvivalStatus extends SubjectAwareWrapper<SurvivalStatusRaw> implements Serializable {
    public SurvivalStatus(SurvivalStatusRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<SurvivalStatus> {

        ID(EntityAttribute.attribute("id", (SurvivalStatus e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SurvivalStatus e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SurvivalStatus e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SurvivalStatus e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SurvivalStatus e) -> e.getSubject().getSubjectCode())),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (SurvivalStatus e) -> e.getEvent().getVisitDate())),
        SURVIVAL_STATUS(EntityAttribute.attribute("survivalStatus", (SurvivalStatus e) -> e.getEvent().getSurvivalStatus())),
        LAST_ALIVE_DATE(EntityAttribute.attribute("lastAliveDate", (SurvivalStatus e) -> e.getEvent().getLastAliveDate()));

        @Getter
        private final EntityAttribute<SurvivalStatus> attribute;

        Attributes(EntityAttribute<SurvivalStatus> attribute) {
            this.attribute = attribute;
        }
    }
}
