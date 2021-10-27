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
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.exposure.SubjectCycle;
import lombok.Getter;

import java.io.Serializable;

public final class Exposure extends SubjectAwareWrapper<ExposureRaw> implements Serializable {

    public Exposure(ExposureRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<Exposure> {

        ID(EntityAttribute.attribute("id", (Exposure e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Exposure e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Exposure e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Exposure e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Exposure e) -> e.getSubject().getSubjectCode())),
        ANALYTE(EntityAttribute.attribute("analyte", (Exposure e) -> e.getEvent().getCycle().getAnalyte())),
        ANALYTE_CONCENTRATION(EntityAttribute.attribute("analyteConcentration", (Exposure e) -> e.getEvent().getAnalyteConcentration())),
        ANALYTE_UNIT(EntityAttribute.attribute("analyteUnit", (Exposure e) -> e.getEvent().getAnalyteUnit())),
        TIME_FROM_ADMINISTRATION(EntityAttribute.attribute("timeFromAdministration", (Exposure e) -> e.getEvent().getTimeFromAdministration())),
        TREATMENT_CYCLE(EntityAttribute.attribute("treatmentCycle", (Exposure e) -> e.getEvent().getCycle().getTreatmentCycle())),
        SUBJECT_CYCLE(EntityAttribute.attribute("subjectCycle", (Exposure e) -> new SubjectCycle(e.getSubject().getSubjectCode(), e.getEvent().getCycle()))),
        VISIT(EntityAttribute.attribute("visit", (Exposure e) -> e.getEvent().getCycle().getVisit())),
        DRUG_ADMINISTRATION_DATE(EntityAttribute.attribute("drugAdministrationDate", (Exposure e) -> e.getEvent().getCycle().getDrugAdministrationDate())),
        DAY(EntityAttribute.attribute("protocolScheduleDay", (Exposure e) -> e.getEvent().getProtocolScheduleDay())),
        TREATMENT(EntityAttribute.attribute("treatment", (Exposure e) -> e.getEvent().getTreatment()));

        @Getter
        private final EntityAttribute<Exposure> attribute;

        Attributes(EntityAttribute<Exposure> attribute) {
            this.attribute = attribute;
        }
    }
}
