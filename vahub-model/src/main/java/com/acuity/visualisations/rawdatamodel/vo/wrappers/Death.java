package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Death extends SubjectAwareWrapper<DeathRaw> implements Serializable {

    public Death(DeathRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "daysFromFirstDoseToDeath", order = 3, displayName = "Days from first dose to death",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    public Integer getDaysFromFirstDoseToDeath() {
        final OptionalInt res = DaysUtil.daysBetween(getSubject().getDateOfFirstDose(), getEvent().getDateOfDeath());
        return res.isPresent() ? res.getAsInt() : null;
    }

    public enum Attributes implements GroupByOption<Death> {

        ID(EntityAttribute.attribute("id", (Death e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Death e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Death e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Death e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Death e) -> e.getSubject().getSubjectCode())),
        DEATH_CAUSE(EntityAttribute.attribute("deathCause", (Death e) -> e.getEvent().getDeathCause())),
        DATE_OF_DEATH(EntityAttribute.attribute("dateOfDeath", (Death e) -> e.getEvent().getDateOfDeath())),
        AUTOPSY_PERFORMED(EntityAttribute.attribute("autopsyPerformed", (Death e) -> e.getEvent().getAutopsyPerformed())),
        DESIGNATION(EntityAttribute.attribute("designation", (Death e) -> e.getEvent().getDesignation())),
        DEATH_RELATED_TO_DISEASE(EntityAttribute.attribute("deathRelatedToDisease", (Death e) -> e.getEvent().getDiseaseUnderInvestigationDeath())),
        HLT(EntityAttribute.attribute("hlt", (Death e) -> e.getEvent().getHlt())),
        LLT(EntityAttribute.attribute("llt", (Death e) -> e.getEvent().getLlt())),
        PT(EntityAttribute.attribute("pt", (Death e) -> e.getEvent().getPreferredTerm())),
        SOC(EntityAttribute.attribute("soc", (Death e) -> e.getEvent().getSoc())),
        DAYS_FROM_FIRST_DOSE_TO_DEATH(EntityAttribute.attribute("daysFromFirstDoseToDeath", Death::getDaysFromFirstDoseToDeath));

        @Getter
        private final EntityAttribute<Death> attribute;

        Attributes(EntityAttribute<Death> attribute) {
            this.attribute = attribute;
        }
    }
}
