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
import com.acuity.visualisations.rawdatamodel.vo.SubjectExtRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectExt extends SubjectAwareWrapper<SubjectExtRaw> implements Serializable {

    public SubjectExt(SubjectExtRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<SubjectExt> {

        SUBJECT_ID(EntityAttribute.attribute("subjectId", SubjectAwareWrapper::getSubjectId)),
        DIAGNOSIS_DATE(EntityAttribute.attribute("diagnosisDate", s -> s.getEvent().getDiagnosisDate())),
        DAYS_FROM_DIAGNOSIS_DATE(EntityAttribute.attribute("daysFromDiagnosisDate", s -> s.getEvent().getDaysFromDiagnosisDate())),
        RECENT_PROGRESSION_DATE(EntityAttribute.attribute("recentProgressionDate", s -> s.getEvent().getRecentProgressionDate()));

        @Getter
        private final EntityAttribute<SubjectExt> attribute;

        Attributes(EntityAttribute<SubjectExt> attribute) {
            this.attribute = attribute;
        }
    }
}


