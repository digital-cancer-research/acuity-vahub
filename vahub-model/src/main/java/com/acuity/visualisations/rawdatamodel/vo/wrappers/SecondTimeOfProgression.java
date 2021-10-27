package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecondTimeOfProgression extends SubjectAwareWrapper<SecondTimeOfProgressionRaw> implements Serializable {
    public SecondTimeOfProgression(SecondTimeOfProgressionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<SecondTimeOfProgression> {

        ID(EntityAttribute.attribute("id", (SecondTimeOfProgression e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (SecondTimeOfProgression e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (SecondTimeOfProgression e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (SecondTimeOfProgression e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (SecondTimeOfProgression e) -> e.getSubject().getSubjectCode())),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (SecondTimeOfProgression e) -> e.getEvent().getVisitDate())),
        SCAN_DATE(EntityAttribute.attribute("scanDate", (SecondTimeOfProgression e) -> e.getEvent().getScanDate())),
        ASSESSMENT_PERFORMED(EntityAttribute.attribute("assessmentPerformed", (SecondTimeOfProgression e) -> e.getEvent().getAssessmentPerformed()));

        @Getter
        private final EntityAttribute<SecondTimeOfProgression> attribute;

        Attributes(EntityAttribute<SecondTimeOfProgression> attribute) {
            this.attribute = attribute;
        }
    }
}
