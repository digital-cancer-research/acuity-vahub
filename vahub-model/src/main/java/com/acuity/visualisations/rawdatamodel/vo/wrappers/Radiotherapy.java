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
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Radiotherapy extends SubjectAwareWrapper<RadiotherapyRaw> implements HasStartEndDate, Serializable {

    public static final String RADIOTHERAPY_LABEL = "Radiotherapy";

    public Radiotherapy(RadiotherapyRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEndDate();
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<Radiotherapy> {

        ID(EntityAttribute.attribute("id", (Radiotherapy e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getSubjectCode())),
        START_DATE(EntityAttribute.attribute("startDate", (Radiotherapy e) -> e.getEvent().getStartDate())),
        END_DATE(EntityAttribute.attribute("endDate", (Radiotherapy e) -> e.getEvent().getEndDate())),
        RADIATION_DOSE(EntityAttribute.attribute("radiationDose", Radiotherapy::getRadiationDose)),
        THERAPY_STATUS(EntityAttribute.attribute("therapyStatus", (Radiotherapy r) -> r.getEvent().getTreatmentStatus()));

        @Getter
        private final EntityAttribute<Radiotherapy> attribute;

        Attributes(EntityAttribute<Radiotherapy> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 6, displayName = "Total grays", type = Column.Type.SSV)
    public String getTotalGrays() {
        return String.valueOf(ObjectUtils.defaultIfNull(getRadiationDose(), ""));
    }

    public Double getRadiationDose() {
        return getEvent().getDose() == null || getEvent().getNumOfDoses() == null ? null
                : getEvent().getDose() * getEvent().getNumOfDoses();
    }
}
