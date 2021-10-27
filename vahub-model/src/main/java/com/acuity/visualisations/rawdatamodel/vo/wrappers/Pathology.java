package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Pathology extends SubjectAwareWrapper<PathologyRaw> {

    public Pathology(PathologyRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<Pathology> {

        ID(EntityAttribute.attribute("id", (Pathology e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Pathology e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Pathology e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Pathology e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Pathology e) -> e.getSubject().getSubjectCode())),
        DETERM_METHOD(EntityAttribute.attribute("determMethod", (Pathology e) -> e.getEvent().getDetermMethod())),
        HIST_TYPE(EntityAttribute.attribute("histType", (Pathology e) -> e.getEvent().getHisType())),
        TUMOUR_GRADE(EntityAttribute.attribute("tumourGrade", (Pathology e) -> e.getEvent().getTumourGrade())),
        PRIM_TUMOUR(EntityAttribute.attribute("primTumour", (Pathology e) -> e.getEvent().getPrimTumour())),
        NODES_STATUS(EntityAttribute.attribute("nodesStatus", (Pathology e) -> e.getEvent().getNodesStatus())),
        METASTASES_STATUS(EntityAttribute.attribute("metastasesStatus", (Pathology e) -> e.getEvent().getMetastasesStatus())),
        DIAGNOSIS_DATE(EntityAttribute.attribute("diagnosisDate", (Pathology e) -> e.getEvent().getDate())),
        DAYS_FROM_ORIGINAL_DIAGNOSIS(EntityAttribute.attribute("daysFromOriginalDiagnosis", Pathology::getDaysFromOriginalDiagnosis));

        @Getter
        private final EntityAttribute<Pathology> attribute;

        Attributes(EntityAttribute<Pathology> attribute) {
            this.attribute = attribute;
        }
    }

    public Integer getDaysFromOriginalDiagnosis() {
        final OptionalInt res = DaysUtil.daysBetween(getEvent().getDate(), getSubject().getFirstTreatmentDate());
        return res.isPresent() ? res.getAsInt() : null;
    }
}
