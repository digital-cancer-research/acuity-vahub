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
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasAssociatedAe;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class CIEvent extends SubjectAwareWrapper<CIEventRaw> implements HasStartDate, Serializable, HasAssociatedAe {

    public CIEvent(CIEventRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "startTime", order = 4, displayName = "Start time")
    public String getStartTime() {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(getEvent().getStartDate());
    }

    @Column(columnName = "aeNumber", order = 6, displayName = "Ae number")
    @Override
    public String getAeNumber() {
        return getSubjectCode() + "-" + getEvent().getAeNumber();
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<CIEvent> {
        ID(EntityAttribute.attribute("ID", CIEvent::getId)),
        STUDY_ID(EntityAttribute.attribute("STUDY_ID", CIEvent::getStudyId)),
        SUBJECT(EntityAttribute.attribute("SUBJECT", CIEvent::getSubject)),
        SUBJECT_ID(EntityAttribute.attribute("SUBJECT_ID", CIEvent::getSubjectId)),
        START_DATE(EntityAttribute.attribute("START_DATE", CIEvent::getStartDate)),
        TERM(EntityAttribute.attribute("TERM", (CIEvent e) -> e.getEvent().getTerm())),
        AE_NUMBER(EntityAttribute.attribute("AE_NUMBER", CIEvent::getAeNumber)),
        ISHEMIC_SYMTOMS(EntityAttribute.attribute("ISHEMIC_SYMTOMS", (CIEvent e) -> e.getEvent().getIschemicSymptoms())),
        CI_SYMPTOMS_DURATION(EntityAttribute.attribute("CI_SYMPTOMS_DURATION", (CIEvent e) -> e.getEvent().getCieSymptomsDuration())),
        DID_SYMPTOMS_PROMPT_UNS_HOSP(EntityAttribute.attribute("DID_SYMPTOMS_PROMPT_UNS_HOSP",
                (CIEvent e) -> e.getEvent().getSymptPromptUnschedHospit())),
        EVENT_SUSP_DUE_TO_STENT_THROMB(EntityAttribute.attribute("EVENT_SUSP_DUE_TO_STENT_THROMB",
                (CIEvent e) -> e.getEvent().getEventSuspDueToStentThromb())),
        PREVIOUS_ECG_AVAILABLE(EntityAttribute.attribute("PREVIOUS_ECG_AVAILABLE", (CIEvent e) -> e.getEvent().getPreviousEcgAvailable())),
        PREVIOUS_ECG_DATE(EntityAttribute.attribute("PREVIOUS_ECG_DATE", (CIEvent e) -> e.getEvent().getPreviousEcgDate())),
        ECG_AT_THE_EVENT_TIME(EntityAttribute.attribute("ECG_AT_THE_EVENT_TIME", (CIEvent e) -> e.getEvent().getEcgAtTheEventTime())),
        WAS_THERE_NO_ECG_AT_THE_EVENT_TIME(EntityAttribute.attribute("WAS_THERE_NO_ECG_AT_THE_EVENT_TIME",
                (CIEvent e) -> e.getEvent().getNoEcgAtTheEventTime())),
        WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN(
                EntityAttribute.attribute("WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN", (CIEvent e) -> e.getEvent().getLocalCardiacBiomarkersDrawn())),
        CORONARY_ANGIOGRAPHY(EntityAttribute.attribute("CORONARY_ANGIOGRAPHY", (CIEvent e) -> e.getEvent().getCoronaryAngiography())),
        ANGIOGRAPHY_DATE(EntityAttribute.attribute("ANGIOGRAPHY_DATE", (CIEvent e) -> e.getEvent().getAngiographyDate())),
        FINAL_DIAGNOSIS(EntityAttribute.attribute("FINAL_DIAGNOSIS", (CIEvent e) -> e.getEvent().getFinalDiagnosis())),
        OTHER_DIAGNOSIS(EntityAttribute.attribute("OTHER_DIAGNOSIS", (CIEvent e) -> e.getEvent().getOtherDiagnosis())),
        DESCRIPTION_1(EntityAttribute.attribute("DESCRIPTION_1", (CIEvent e) -> e.getEvent().getDescription1())),
        DESCRIPTION_2(EntityAttribute.attribute("DESCRIPTION_2", (CIEvent e) -> e.getEvent().getDescription2())),
        DESCRIPTION_3(EntityAttribute.attribute("DESCRIPTION_3", (CIEvent e) -> e.getEvent().getDescription3())),
        DESCRIPTION_4(EntityAttribute.attribute("DESCRIPTION_4", (CIEvent e) -> e.getEvent().getDescription4())),
        DESCRIPTION_5(EntityAttribute.attribute("DESCRIPTION_5", (CIEvent e) -> e.getEvent().getDescription5()));

        @Getter
        private final EntityAttribute<CIEvent> attribute;

        Attributes(EntityAttribute<CIEvent> attribute) {
            this.attribute = attribute;
        }
    }

}

