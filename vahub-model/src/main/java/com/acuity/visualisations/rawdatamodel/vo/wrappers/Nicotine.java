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
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
public class Nicotine extends SubjectAwareWrapper<NicotineRaw> implements HasSubjectId, HasStartEndDate {
    public Nicotine(NicotineRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEndDate();
    }

    public enum Attributes implements GroupByOption<Nicotine> {

        ID(EntityAttribute.attribute("id", (Nicotine e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Nicotine e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Nicotine e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Nicotine e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Nicotine e) -> e.getSubject().getSubjectCode())),
        CATEGORY(EntityAttribute.attribute("category", (Nicotine e) -> e.getEvent().getCategory())),
        CURRENT_USE_SPEC(EntityAttribute.attribute("currentUseSpec",
                (Nicotine e) -> e.getEvent().getCurrentUseSpec())),
        FREQUENCY_INTERVAL(EntityAttribute.attribute("frequencyInterval",
                (Nicotine e) -> e.getEvent().getFrequencyInterval())),
        START_DATE(EntityAttribute.attribute("startDate", Nicotine::getStartDate)),
        END_DATE(EntityAttribute.attribute("endDate", Nicotine::getEndDate)),
        NUMBER_PACK_YEARS(EntityAttribute.attribute("numberPackYears",
                (Nicotine e) -> e.getEvent().getNumberPackYears())),
        OTHER_TYPE_SPEC(EntityAttribute.attribute("otherTypeSpec",
                (Nicotine e) -> e.getEvent().getOtherTypeSpec())),
        TYPE(EntityAttribute.attribute("type", (Nicotine e) -> e.getEvent().getType())),
        SUB_TYPE_USE_OCCURRENCE(EntityAttribute.attribute("subTypeUseOccurrence",
                (Nicotine e) -> e.getEvent().getSubTypeUseOccurrence())),
        USE_OCCURRENCE(EntityAttribute.attribute("useOccurrence",
                (Nicotine e) -> e.getEvent().getUseOccurrence())),
        CONSUMPTION(EntityAttribute.attribute("consumption", (Nicotine e) -> e.getEvent().getConsumption()));

        @Getter
        private final EntityAttribute<Nicotine> attribute;

        Attributes(EntityAttribute<Nicotine> attribute) {
            this.attribute = attribute;
        }
    }
}
