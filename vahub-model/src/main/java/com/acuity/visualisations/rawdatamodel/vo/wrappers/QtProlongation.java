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
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class QtProlongation extends SubjectAwareWrapper<QtProlongationRaw> implements Serializable {
    public QtProlongation(QtProlongationRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<QtProlongation> {
        ID(EntityAttribute.attribute("id", QtProlongation::getId)),
        SUBJECT_ID(EntityAttribute.attribute("id", QtProlongation::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", QtProlongation::getSubjectCode)),
        ALERT_LEVEL(EntityAttribute.attribute("alertLevel", q -> q.getEvent().getAlertLevel()));

        @Getter
        private final EntityAttribute<QtProlongation> attribute;

        Attributes(EntityAttribute<QtProlongation> attribute) {
            this.attribute = attribute;
        }
    }
}
