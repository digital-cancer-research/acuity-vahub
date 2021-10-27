package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class PatientData extends SubjectAwareWrapper<PatientDataRaw> implements Serializable {

    public PatientData(PatientDataRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<PatientData> {

        ID(EntityAttribute.attribute("id", PatientData::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", PatientData::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", PatientData::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", PatientData::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", PatientData::getSubjectCode)),
        MEASUREMENT_NAME(EntityAttribute.attribute("measurementName", d -> d.getEvent().getMeasurementName())),
        VALUE(EntityAttribute.attribute("value", d -> d.getEvent().getValue())),
        UNIT(EntityAttribute.attribute("unit", d -> d.getEvent().getUnit())),
        MEASUREMENT_DATE(EntityAttribute.attribute("measurementDate", d -> d.getEvent().getMeasurementDate())),
        REPORT_DATE(EntityAttribute.attribute("reportDate", d -> d.getEvent().getReportDate())),
        SOURCE_TYPE(EntityAttribute.attribute("sourceType", d -> d.getEvent().getSourceType()));

        @Getter
        private final EntityAttribute<PatientData> attribute;

        Attributes(EntityAttribute<PatientData> attribute) {
            this.attribute = attribute;
        }
    }

}

