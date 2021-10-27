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
import com.acuity.visualisations.rawdatamodel.util.DodUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasAssociatedAe;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType.ACUITY;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.anyNull;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Ae extends SubjectAwareWrapper<AeRaw> implements HasStartEndDate, Serializable, HasAssociatedAe {

    public Ae(AeRaw event, Subject subject) {
        super(event, subject);
    }

    //@Column(columnName = "aeNumber", order = -100, displayName = "Associated AE No.")
    @Override
    public String getAeNumber() {
        if (getEvent().getAeNumber() == null) {
            return null;
        }
        return getSubjectCode() + "-" + getEvent().getAeNumber();
    }

    public Collection<AeSeverity> getAeSeverities() {
        return getEvent().getAeSeverities().stream().
                map(AeSeverityRaw::getSeverity).
                filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    @Column(columnName = "startDate", order = 6, displayName = "Start date", defaultSortBy = true)
    @Column(columnName = "startDate", order = 6, displayName = "Start date", defaultSortBy = true, type = Column.Type.AML)
    @Override
    public Date getStartDate() {
        return getEvent().getMinStartDate();
    }

    @Override
    public Integer getDurationRaw() {
        return getEvent().getDuration();
    }

    @Override
    public Boolean isCalcDurationIfNull() {
        return getEvent().getCalcDurationIfNull();
    }

    @Column(columnName = "duration", order = 10, displayName = "Duration")
    @Column(columnName = "duration", order = 10, displayName = "Duration", type = Column.Type.AML)
    public Integer getDurationForDod() {
        return getDuration();
    }

    @Column(columnName = "actionTaken", order = 13, displayName = "Action taken", datasetType = ACUITY)
    @Column(columnName = "actionTaken", order = 13, displayName = "Action taken", datasetType = ACUITY, type = Column.Type.AML)
    public String getActionTakenAsString() {
        return DodUtil.toString(getEvent().getDrugsActionTaken(), ": ", ", ");
    }

    @Column(columnName = "causality", order = 14, displayName = "Causality", datasetType = ACUITY)
    @Column(columnName = "causality", order = 14, displayName = "Causality", datasetType = ACUITY, type = Column.Type.AML)
    public String getCausalityAsString() {
        return DodUtil.toString(getEvent().getDrugsCausality(), ": ", ", ");
    }

    @Column(columnName = "endDate", order = 7, displayName = "End date")
    @Column(columnName = "endDate", order = 7, displayName = "End date", type = Column.Type.AML)
    @Override
    public Date getEndDate() {
        return getEvent().getMaxEndDate();
    }

    public String getEndDatePriorToRandomisation() {
        if (anyNull(getEndDate(), getSubject().getDateOfRandomisation())) {
            return null;
        }
        if (getEndDate().before(getSubject().getDateOfRandomisation())) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public String getStartDatePriorToRandomisation() {
        if (anyNull(getStartDate(), getSubject().getDateOfRandomisation())) {
            return null;
        }
        if (getStartDate().before(getSubject().getDateOfRandomisation())) {
            return "Yes";
        } else {
            return "No";
        }
    }

    private Integer calculateDaysSince(Date sinceDate, Date aeDate) {
        OptionalInt res = DaysUtil.daysBetween(sinceDate, aeDate);
        return res.isPresent() ? res.getAsInt() : null;
    }

    @Column(columnName = "daysOnStudyAtAEStart", order = 8, displayName = "Days on study at ae start")
    @Column(columnName = "daysOnStudyAtAEStart", order = 8, displayName = "Days on study at ae start", type = Column.Type.AML)
    public Integer getDaysOnStudyAtStart() {
        if (anyNull(getStartDate(), getSubject().getFirstTreatmentDate())) {
            return null;
        }
        return calculateDaysSince(getSubject().getFirstTreatmentDate(), getStartDate());
    }

    @Column(columnName = "daysOnStudyAtAEEnd", order = 9, displayName = "Days on study at ae end")
    @Column(columnName = "daysOnStudyAtAEEnd", order = 9, displayName = "Days on study at ae end", type = Column.Type.AML)
    public Integer getDaysOnStudyAtEnd() {
        if (anyNull(getEndDate(), getSubject().getFirstTreatmentDate())) {
            return null;
        }
        return calculateDaysSince(getSubject().getFirstTreatmentDate(), getEndDate());
    }

    public enum Attributes implements GroupByOption<Ae> {
        ID(EntityAttribute.attribute("ID", Ae::getId)),
        STUDY_ID(EntityAttribute.attribute("STUDY_ID", Ae::getStudyId)),
        SUBJECT(EntityAttribute.attribute("SUBJECT", Ae::getSubjectCode)),
        SUBJECT_ID(EntityAttribute.attribute("SUBJECT_ID", Ae::getSubjectId)),
        START_DATE(EntityAttribute.attribute("START_DATE", Ae::getStartDate)),
        END_DATE(EntityAttribute.attribute("END_DATE", Ae::getEndDate)),
        DAYS_SINCE_FIRST_DOSE_AT_START(EntityAttribute.attribute("DAYS_SINCE_FIRST_DOSE_AT_START", (Ae e) -> {
            final OptionalInt res = DaysUtil.daysBetween(e.getSubject().getFirstTreatmentDate(), e.getEvent().getMinStartDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        WEEKS_SINCE_FIRST_DOSE_AT_START(EntityAttribute.attribute("WEEKS_SINCE_FIRST_DOSE_AT_START", (Ae e) -> {
            final OptionalInt res = DaysUtil.weeksBetween(e.getSubject().getFirstTreatmentDate(), e.getEvent().getMinStartDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        DAYS_SINCE_FIRST_DOSE_AT_END(EntityAttribute.attribute("DAYS_SINCE_FIRST_DOSE_AT_END", (Ae e) -> {
            final OptionalInt res = DaysUtil.daysBetween(e.getSubject().getFirstTreatmentDate(), e.getEvent().getMaxEndDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        WEEKS_SINCE_FIRST_DOSE_AT_END(EntityAttribute.attribute("WEEKS_SINCE_FIRST_DOSE_AT_END", (Ae e) -> {
            final OptionalInt res = DaysUtil.weeksBetween(e.getSubject().getFirstTreatmentDate(), e.getEvent().getMaxEndDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        DAYS_SINCE_RANDOMISATION_AT_START(EntityAttribute.attribute("DAYS_SINCE_RANDOMISATION_AT_START", (Ae e) -> {
            final OptionalInt res = DaysUtil.daysBetween(e.getSubject().getDateOfRandomisation(), e.getEvent().getMinStartDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        WEEKS_SINCE_RANDOMISATION_AT_START(EntityAttribute.attribute("WEEKS_SINCE_RANDOMISATION_AT_START", (Ae e) -> {
            final OptionalInt res = DaysUtil.weeksBetween(e.getSubject().getDateOfRandomisation(), e.getEvent().getMinStartDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        DAYS_SINCE_RANDOMISATION_AT_END(EntityAttribute.attribute("DAYS_SINCE_RANDOMISATION_AT_END", (Ae e) -> {
            final OptionalInt res = DaysUtil.daysBetween(e.getSubject().getDateOfRandomisation(), e.getEvent().getMaxEndDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        WEEKS_SINCE_RANDOMISATION_AT_END(EntityAttribute.attribute("WEEKS_SINCE_RANDOMISATION_AT_END", (Ae e) -> {
            final OptionalInt res = DaysUtil.weeksBetween(e.getSubject().getDateOfRandomisation(), e.getEvent().getMaxEndDate());
            return res.isPresent() ? res.getAsInt() : null;
        })),
        DURATION(EntityAttribute.attribute("DURATION", Ae::getDuration)),
        EVENT_ID(EntityAttribute.attribute("EVENT_ID", (Ae e) -> e.getEvent().getEventId())),
        PT(EntityAttribute.attribute("PT", (Ae e) -> e.getEvent().getPt())),
        HLT(EntityAttribute.attribute("HLT", (Ae e) -> e.getEvent().getHlt())),
        SOC(EntityAttribute.attribute("SOC", (Ae e) -> e.getEvent().getSoc())),
        AE_NUMBER(EntityAttribute.attribute("AE_NUMBER", Ae::getAeNumber)),
        TEXT(EntityAttribute.attribute("TEXT", (Ae e) -> e.getEvent().getText())),
        COMMENT(EntityAttribute.attribute("COMMENT", (Ae e) -> e.getEvent().getComment())),
        SERIOUS(EntityAttribute.attribute("SERIOUS", (Ae e) -> e.getEvent().getSerious())),
        SEVERITY(EntityAttribute.attribute("SEVERITY", (Ae e) -> e.getEvent().getMaxAeSeverity())),
        ACTION_TAKEN(EntityAttribute.attribute("ACTION_TAKEN", (Ae e) -> e.getEvent().getActionTaken())),
        CAUSALITY(EntityAttribute.attribute("CAUSALITY", (Ae e) -> e.getEvent().getCausality())),
        //NUM_MAX_CTC(EntityAttribute.attribute("NUM_MAX_CTC", (Ae e) -> e.getEvent().getMaxAeSeverity().getSeverityNum())),
        WEPAPP_MAX_CTC(EntityAttribute.attribute("WEPAPP_MAX_CTC", (Ae e) -> e.getEvent().getMaxAeSeverity())),
        OUTCOME(EntityAttribute.attribute("OUTCOME", (Ae e) -> e.getEvent().getOutcome())),
        DOSE_LIMITING_TOXICITY(EntityAttribute.attribute("DOSE_LIMITING_TOXICITY", (Ae e) -> e.getEvent().getDoseLimitingToxicity())),
        TIMEPOINT(EntityAttribute.attribute("TIMEPOINT", (Ae e) -> e.getEvent().getTimepoint())),
        IMMUNE_MEDIATED(EntityAttribute.attribute("IMMUNE_MEDIATED", (Ae e) -> e.getEvent().getImmuneMediated())),
        INFUSION_REACTION(EntityAttribute.attribute("INFUSION_REACTION", (Ae e) -> e.getEvent().getInfusionReaction())),
        REQUIRED_TREATMENT(EntityAttribute.attribute("REQUIRED_TREATMENT", (Ae e) -> e.getEvent().getRequiredTreatment())),
        TREATMENT_EMERGENT(EntityAttribute.attribute("TREATMENT_EMERGENT", (Ae e) -> e.getEvent().getTreatmentEmergent())),
        REQUIRES_HOSPITALISATION(EntityAttribute.attribute("REQUIRES_HOSPITALISATION", (Ae e) -> e.getEvent().getRequiresHospitalisation())),
        CAUSED_SUBJECT_WITHDRAWAL(EntityAttribute.attribute("CAUSED_SUBJECT_WITHDRAWAL", (Ae e) -> e.getEvent().getCausedSubjectWithdrawal())),
        SUSPECTED_ENDPOINT(EntityAttribute.attribute("SUSPECTED_ENDPOINT", (Ae e) -> e.getEvent().getSuspectedEndpoint())),
        SUSPECTED_ENDPOINT_CAT(EntityAttribute.attribute("SUSPECTED_ENDPOINT_CAT", (Ae e) -> e.getEvent().getSuspectedEndpointCategory())),
        AE_OF_SPECIAL_INTEREST(EntityAttribute.attribute("AE_OF_SPECIAL_INTEREST", (Ae e) -> e.getEvent().getAeOfSpecialInterest())),
        DRUGS_ACTION_TAKEN_MAP(EntityAttribute.attribute("DRUGS_ACTION_TAKEN_MAP", (Ae e) -> e.getEvent().getDrugsActionTaken())),
        DRUGS_CAUSALITY_MAP(EntityAttribute.attribute("DRUGS_CAUSALITY_MAP", (Ae e) -> e.getEvent().getDrugsCausality())),
        START_PRIOR_TO_RAND(EntityAttribute.attribute("START_PRIOR_TO_RAND", Ae::getStartDatePriorToRandomisation)),
        END_PRIOR_TO_RAND(EntityAttribute.attribute("END_PRIOR_TO_RAND", Ae::getEndDatePriorToRandomisation)),
        DAYS_FROM_PREV_DOSE_TO_START(EntityAttribute.attribute("DAYS_FROM_PREV_DOSE_TO_START", (Ae e) -> e.getEvent().getDaysFromPrevDoseToStart())),
        STUDY_PERIOD(EntityAttribute.attribute("STUDY_PERIOD", (Ae e) -> e.getEvent().getStudyPeriod())),
        USED_IN_TFL(EntityAttribute.attribute("USED_IN_TFL", (Ae e) -> e.getEvent().getUsedInTfl())),
        SPECIAL_INTEREST_GROUP(EntityAttribute.attribute("SPECIAL_INTEREST_GROUP", (Ae e) -> e.getEvent().getSpecialInterestGroups(), String.class)),
        TREATMENT_ARM(EntityAttribute.attribute("TREATMENT_ARM", (Ae e) -> e.getSubject().getActualArm())),
        SEX(EntityAttribute.attribute("sex", (Ae e) -> e.getSubject().getSex()));

        @Getter
        private final EntityAttribute<Ae> attribute;

        Attributes(EntityAttribute<Ae> attribute) {
            this.attribute = attribute;
        }


    }
    public String getPt() {
        return getEvent().getPt();
    }

    public List<String> getSpecialInterestGroups() {
        return getEvent().getSpecialInterestGroups();
    }
    public enum TermLevel {
        PT,
        HLT,
        SOC
    }
}
