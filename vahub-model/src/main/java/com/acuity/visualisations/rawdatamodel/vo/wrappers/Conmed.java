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
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.OptionalInt;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Conmed extends SubjectAwareWrapper<ConmedRaw> implements HasStartEndDate {

    public Conmed(ConmedRaw event, Subject subject) {
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


    public enum Attributes implements GroupByOption<Conmed> {
        // common attributes
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Conmed e) -> e.getSubject().getSubjectId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Conmed e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Conmed e) -> e.getSubject().getStudyPart())),
        ID(EntityAttribute.attribute("id", (Conmed e) -> e.getEvent().getId())),
        // for DoD themselves
        MEDICATION_NAME(EntityAttribute.attribute("medicationName", (Conmed e) -> e.getEvent().getMedicationName())),
        ATC_CODE(EntityAttribute.attribute("atcCode", (Conmed e) -> e.getEvent().getAtcCode())),
        DOSE(EntityAttribute.attribute("dose", (Conmed e) -> e.getEvent().getDose())),
        DOSE_UNITS(EntityAttribute.attribute("doseUnits", (Conmed e) -> e.getEvent().getDoseUnits())),
        DOSE_FREQUENCY(EntityAttribute.attribute("doseFrequency", (Conmed e) -> e.getEvent().getDoseFrequency())),
        START_DATE(EntityAttribute.attribute("startDate", Conmed::getStartDate)),
        END_DATE(EntityAttribute.attribute("endDate", Conmed::getEndDate)),
        DURATION(EntityAttribute.attribute("duration", Conmed::getDuration)),
        ONGOING(EntityAttribute.attribute("ongoing", Conmed::getConmedTreatmentOngoing)),
        STUDY_DAY_AT_START(EntityAttribute.attribute("studyDayAtConmedStart", Conmed::getStudyDayAtConmedStart)),
        STUDY_DAY_AT_END(EntityAttribute.attribute("studyDayAtConmedEnd", Conmed::getStudyDayAtConmedEnd)),
        START_PRIOR_TO_RANDOMISATION(EntityAttribute.attribute("startPriorToRandomisation", Conmed::getConmedStartPriorToRandomisation)),
        END_PRIOR_TO_RANDOMISATION(EntityAttribute.attribute("endPriorToRandomisation", Conmed::getConmedEndPriorToRandomisation)),
        ATC_TEXT(EntityAttribute.attribute("atcText", (Conmed e) -> e.getEvent().getAtcText())),
        TREATMENT_REASON(EntityAttribute.attribute("treatmentReason", (Conmed e) -> e.getEvent().getTreatmentReason())),
        // for grouping
        ARM(EntityAttribute.attribute("arm", (Conmed e) -> e.getSubject().getActualArm())),
        ANATOMICAL_GROUP(EntityAttribute.attribute("anatomicalGroup", Conmed::getAnatomicalGroup)),
        CONMED_STARTED_PRIOR_TO_STUDY(EntityAttribute.attribute("conmedStartedPriorToStudy", Conmed::getConmedStartedPriorToStudy)),
        CONMED_ENDED_PRIOR_TO_STUDY(EntityAttribute.attribute("conmedEndedPriorToStudy", Conmed::getConmedEndedPriorToStudy)),
        AE_PT(EntityAttribute.attribute("aePt", (Conmed e) -> e.getEvent().getAePt())),
        AE_NUMBER(EntityAttribute.attribute("aeNum", (Conmed e) -> e.getEvent().getAeNum()));


        @Getter
        private final EntityAttribute<Conmed> attribute;

        Attributes(EntityAttribute<Conmed> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 3, displayName = "Dose", type = Column.Type.SSV)
    public String getDoseWithUnits() {
        Double dose = getEvent().getDose();
        String doseUnits = getEvent().getDoseUnits();
        if (dose == null) {
            return "";
        }
        if (doseUnits == null) {
            return String.valueOf(dose);
        }
        return dose + " " + doseUnits;
    }

    @Column(order = 11, columnName = "duration", displayName = "Duration", type = Column.Type.DOD)
    @Column(order = 6, columnName = "duration", displayName = "Duration", type = Column.Type.SSV)
    @Override
    public Integer getDuration() {
        OptionalInt duration = DaysUtil.daysBetween(getStartDate(), getEndDate());
        return duration.isPresent() ? duration.getAsInt() + 1 : null;
    }

    @Column(order = 12, columnName = "conmedTreatmentOngoing", displayName = "Conmed Treatment Ongoing", type = Column.Type.DOD)
    @Column(order = 8, columnName = "conmedTreatmentOngoing", displayName = "Conmed treatment ongoing", type = Column.Type.SSV)
    public String getConmedTreatmentOngoing() {
        return getEndDate() == null ? YES : NO;
    }

    @Column(order = 13, columnName = "studyDayAtConmedStart", displayName = "Study Day At Conmed Start", type = Column.Type.DOD)
    @Column(order = 10, columnName = "studyDayAtConmedStart", displayName = "Study day at conmed start", type = Column.Type.SSV)
    public Integer getStudyDayAtConmedStart() {
        OptionalInt days = DaysUtil.daysBetween(getDateOfFirstDose(), getEvent().getStartDate());
        return days.isPresent() ? days.getAsInt() : null;
    }

    @Column(order = 14, columnName = "studyDayAtConmedEnd", displayName = "Study Day At Conmed End", type = Column.Type.DOD)
    @Column(order = 11, displayName = "Study day at conmed end", type = Column.Type.SSV)
    public Integer getStudyDayAtConmedEnd() {
        OptionalInt days = DaysUtil.daysBetween(getDateOfFirstDose(), getEvent().getEndDate());
        return days.isPresent() ? days.getAsInt() : null;
    }

    @Column(order = 15, columnName = "startPriorToRandomisation", displayName = "Conmed Start Prior Randomisation", type = Column.Type.DOD)
    @Column(order = 12, displayName = "Conmed start prior randomisation", type = Column.Type.SSV)
    public String getConmedStartPriorToRandomisation() {
        return getPriorTo(getSubject().getDateOfRandomisation(), getEvent().getStartDate(), null);
    }

    @Column(order = 16, columnName = "endPriorToRandomisation", displayName = "Conmed End Prior Randomisation", type = Column.Type.DOD)
    @Column(order = 13, displayName = "Conmed end prior to randomisation", type = Column.Type.SSV)
    public String getConmedEndPriorToRandomisation() {
        return getPriorTo(getSubject().getDateOfRandomisation(), getEvent().getEndDate(), null);
    }

    //Anatomical group is classification of drug. It is the first letter of the ATC Code.
    public String getAnatomicalGroup() {
        return StringUtils.isEmpty(getEvent().getAtcCode()) ? null : getEvent().getAtcCode().substring(0, 1);
    }

    public String getConmedStartedPriorToStudy() {
        return getPriorTo(getSubject().getFirstTreatmentDate(), getStartDate(), DEFAULT_EMPTY_VALUE);
    }

    public String getConmedEndedPriorToStudy() {
        return getPriorTo(getSubject().getFirstTreatmentDate(), getEndDate(), DEFAULT_EMPTY_VALUE);
    }

    private String getPriorTo(Date toTest, Date priorTo, String emptyValue) {
        OptionalInt days = DaysUtil.daysBetween(toTest, priorTo);

        if (!days.isPresent()) {
            return emptyValue;
        }

        return days.getAsInt() < 0 ? YES : NO;
    }
}
