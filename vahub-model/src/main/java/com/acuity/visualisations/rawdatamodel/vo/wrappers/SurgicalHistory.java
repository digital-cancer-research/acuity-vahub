package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class SurgicalHistory extends SubjectAwareWrapper<SurgicalHistoryRaw> implements HasStartDate {

    public SurgicalHistory(SurgicalHistoryRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStart();
    }

    public enum Attributes implements GroupByOption<SurgicalHistory> {

        ID(EntityAttribute.attribute("id", (SurgicalHistory e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SurgicalHistory e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SurgicalHistory e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SurgicalHistory e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SurgicalHistory e) -> e.getSubject().getSubjectCode())),
        PREFERRED_TERM(EntityAttribute.attribute("preferredTerm", (SurgicalHistory e) -> e.getEvent().getPreferredTerm())),
        CURRENT_MEDICATION(EntityAttribute.attribute("currentMedication", (SurgicalHistory e) -> e.getEvent().getCurrentMedication())),
        SURGICAL_PROCEDURE(EntityAttribute.attribute("surgicalProcedure", (SurgicalHistory e) -> e.getEvent().getSurgicalProcedure())),
        START_DATE(EntityAttribute.attribute("startDate", SurgicalHistory::getStartDate)),
        HLT(EntityAttribute.attribute("hlt", (SurgicalHistory e) -> e.getEvent().getHlt())),
        SOC(EntityAttribute.attribute("soc", (SurgicalHistory e) -> e.getEvent().getSoc()));

        @Getter
        private final EntityAttribute<SurgicalHistory> attribute;

        Attributes(EntityAttribute<SurgicalHistory> attribute) {
            this.attribute = attribute;
        }
    }
}
