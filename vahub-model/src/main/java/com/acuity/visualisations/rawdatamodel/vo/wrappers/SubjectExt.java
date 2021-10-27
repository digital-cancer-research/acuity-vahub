package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SubjectExtRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectExt extends SubjectAwareWrapper<SubjectExtRaw> implements Serializable {

    public SubjectExt(SubjectExtRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<SubjectExt> {

        SUBJECT_ID(EntityAttribute.attribute("subjectId", SubjectAwareWrapper::getSubjectId)),
        DIAGNOSIS_DATE(EntityAttribute.attribute("diagnosisDate", s -> s.getEvent().getDiagnosisDate())),
        DAYS_FROM_DIAGNOSIS_DATE(EntityAttribute.attribute("daysFromDiagnosisDate", s -> s.getEvent().getDaysFromDiagnosisDate())),
        RECENT_PROGRESSION_DATE(EntityAttribute.attribute("recentProgressionDate", s -> s.getEvent().getRecentProgressionDate()));

        @Getter
        private final EntityAttribute<SubjectExt> attribute;

        Attributes(EntityAttribute<SubjectExt> attribute) {
            this.attribute = attribute;
        }
    }
}


