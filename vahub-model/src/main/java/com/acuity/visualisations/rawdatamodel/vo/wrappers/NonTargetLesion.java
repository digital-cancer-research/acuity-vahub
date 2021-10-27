package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class NonTargetLesion extends SubjectAwareWrapper<NonTargetLesionRaw> implements Serializable {

    public NonTargetLesion(NonTargetLesionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<NonTargetLesion> {

        ID(EntityAttribute.attribute("id", (NonTargetLesion e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (NonTargetLesion e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (NonTargetLesion e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (NonTargetLesion e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (NonTargetLesion e) -> e.getSubject().getSubjectCode())),
        LESION_DATE(EntityAttribute.attribute("lesionDate", (NonTargetLesion e) -> e.getEvent().getLesionDate())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (NonTargetLesion e) -> e.getEvent().getVisitNumber())),
        LESION_SITE(EntityAttribute.attribute("lesionSite", (NonTargetLesion e) -> e.getEvent().getLesionSite()));

        @Getter
        private final EntityAttribute<NonTargetLesion> attribute;

        Attributes(EntityAttribute<NonTargetLesion> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 2, columnName = "studyDay", displayName = "Study day", type = Column.Type.SSV)
    public Integer getStudyDay() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getLesionDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }
}
