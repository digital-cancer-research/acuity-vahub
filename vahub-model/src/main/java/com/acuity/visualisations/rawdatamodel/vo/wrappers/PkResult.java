package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class PkResult extends SubjectAwareWrapper<PkResultRaw> implements Serializable {

    public PkResult(PkResultRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<PkResult> {

        ID(EntityAttribute.attribute("id", PkResult::getId)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", PkResult::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", PkResult::getSubjectCode)),
        PARAMETER(EntityAttribute.attribute("parameter", d -> d.getEvent().getParameter())),
        PARAMETER_VALUE(EntityAttribute.attribute("parameterValue", d -> d.getEvent().getParameterValue())),
        PARAMETER_UNIT(EntityAttribute.attribute("parameterUnit", d -> d.getEvent().getParameterUnit())),
        PARAMETER_WITH_UNIT(EntityAttribute.attribute("parameterWithUnit", (PkResult l) -> l.getEvent().getParameter()
                + (l.getEvent().getParameterUnit() == null ? "" : (" (" + l.getEvent().getParameterUnit() + ")")))),
        ANALYTE(EntityAttribute.attribute("analyte", d -> d.getEvent().getAnalyte())),
        DOSE(EntityAttribute.attribute("dose", d -> d.getEvent().getTreatment())),
        TREATMENT_CYCLE(EntityAttribute.attribute("treatmentCycle", d -> d.getEvent().getTreatmentCycle())),
        DAY(EntityAttribute.attribute("treatmentCycle", d -> d.getEvent().getProtocolScheduleStartDay())),
        VISIT(EntityAttribute.attribute("visit", d -> d.getEvent().getVisit())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", d -> d.getEvent().getVisitNumber())),
        ACTUAL_DOSE(EntityAttribute.attribute("actualDose", d -> d.getEvent().getActualDose())),
        BEST_OVERALL_RESPONSE(EntityAttribute.attribute("bestOverallResponse",
                d -> d.getEvent().getBestOverallResponse()));

        @Getter
        private final EntityAttribute<PkResult> attribute;

        Attributes(EntityAttribute<PkResult> attribute) {
            this.attribute = attribute;
        }
    }

}

