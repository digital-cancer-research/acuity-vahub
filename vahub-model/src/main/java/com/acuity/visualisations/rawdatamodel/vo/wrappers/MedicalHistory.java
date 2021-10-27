package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
public final class MedicalHistory extends SubjectAwareWrapper<MedicalHistoryRaw> implements HasStartEndDate {

    public MedicalHistory(MedicalHistoryRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStart();
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEnd();
    }

    public enum Attributes implements GroupByOption<MedicalHistory> {

        ID(EntityAttribute.attribute("id", (MedicalHistory e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (MedicalHistory e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (MedicalHistory e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (MedicalHistory e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (MedicalHistory e) -> e.getSubject().getSubjectCode())),
        PREFERRED_TERM(EntityAttribute.attribute("preferredTerm", (MedicalHistory e) -> e.getEvent().getPreferredTerm())),
        CONDITION_STATUS(EntityAttribute.attribute("conditionStatus", (MedicalHistory e) -> e.getEvent().getConditionStatus())),
        CURRENT_MEDICATION(EntityAttribute.attribute("currentMedication", (MedicalHistory e) -> e.getEvent().getCurrentMedication())),
        CATEGORY(EntityAttribute.attribute("category", (MedicalHistory e) -> e.getEvent().getCategory())),
        START_DATE(EntityAttribute.attribute("startDate", MedicalHistory::getStartDate)),
        END_DATE(EntityAttribute.attribute("endDate", MedicalHistory::getEndDate)),
        SOC(EntityAttribute.attribute("soc", (MedicalHistory e) -> e.getEvent().getSoc())),
        HLT(EntityAttribute.attribute("hlt", (MedicalHistory e) -> e.getEvent().getHlt())),
        TERM(EntityAttribute.attribute("term", (MedicalHistory e) -> e.getEvent().getTerm()));

        @Getter
        private final EntityAttribute<MedicalHistory> attribute;

        Attributes(EntityAttribute<MedicalHistory> attribute) {
            this.attribute = attribute;
        }
    }

    public boolean endsBeforeFirstTreatmentDate() {
        return getEvent().getEnd() != null
                && getDateOfFirstDose() != null
                && getEvent().getEnd().before(getDateOfFirstDose());
    }
}
