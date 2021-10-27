package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.AssessedNonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;

import java.io.Serializable;

public class AssessedNonTargetLesion extends SubjectAwareWrapper<AssessedNonTargetLesionRaw> implements Serializable {

    public AssessedNonTargetLesion(AssessedNonTargetLesionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<AssessedNonTargetLesion> {
        ID(EntityAttribute.attribute("id", AssessedNonTargetLesion::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", AssessedNonTargetLesion::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", AssessedNonTargetLesion::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", AssessedNonTargetLesion::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", AssessedNonTargetLesion::getSubjectCode)),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (AssessedNonTargetLesion e) -> e.getEvent().getVisitNumber())),
        LESION_DATE(EntityAttribute.attribute("lesionDate", (AssessedNonTargetLesion e) -> e.getEvent().getLesionDate())),
        ASSESSMENT_METHOD(EntityAttribute.attribute("assessmentMethod", (AssessedNonTargetLesion e) -> e.getEvent().getAssessmentMethod())),
        RESPONSE(EntityAttribute.attribute("response", (AssessedNonTargetLesion e) -> e.getEvent().getResponse()));

        @Getter
        private final EntityAttribute<AssessedNonTargetLesion> attribute;

        Attributes(EntityAttribute<AssessedNonTargetLesion> attribute) {
            this.attribute = attribute;
        }
    }

}
