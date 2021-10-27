package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class QtProlongation extends SubjectAwareWrapper<QtProlongationRaw> implements Serializable {
    public QtProlongation(QtProlongationRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<QtProlongation> {
        ID(EntityAttribute.attribute("id", QtProlongation::getId)),
        SUBJECT_ID(EntityAttribute.attribute("id", QtProlongation::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", QtProlongation::getSubjectCode)),
        ALERT_LEVEL(EntityAttribute.attribute("alertLevel", q -> q.getEvent().getAlertLevel()));

        @Getter
        private final EntityAttribute<QtProlongation> attribute;

        Attributes(EntityAttribute<QtProlongation> attribute) {
            this.attribute = attribute;
        }
    }
}
