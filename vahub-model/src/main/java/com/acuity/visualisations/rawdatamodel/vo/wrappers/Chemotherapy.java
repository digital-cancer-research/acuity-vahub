package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Chemotherapy extends SubjectAwareWrapper<ChemotherapyRaw> implements HasStartEndDate, Serializable {

    public static final String CHEMOTHERAPY = "Chemotherapy";

    public Chemotherapy(ChemotherapyRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEndDate();
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<Chemotherapy> {

        ID(EntityAttribute.attribute("id", Chemotherapy::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Chemotherapy::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Chemotherapy::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Chemotherapy::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Chemotherapy::getSubjectCode)),
        START_DATE(EntityAttribute.attribute("startDate", Chemotherapy::getStartDate)),
        END_DATE(EntityAttribute.attribute("endDate", Chemotherapy::getEndDate)),
        PREFERRED_MED(EntityAttribute.attribute("preferredMed", (Chemotherapy c) -> c.getEvent().getPreferredMed())),
        THERAPY_CLASS(EntityAttribute.attribute("therapyClass", (Chemotherapy c) -> c.getEvent().getTherapyClass())),
        THERAPY_STATUS(EntityAttribute.attribute("therapyStatus", (Chemotherapy c) -> c.getEvent().getTreatmentStatus())),
        REASON_FOR_CHEMOTHERAPY_FAILURE(EntityAttribute.attribute("reasonForChemotherapyFailure", (Chemotherapy c) -> c.getEvent().getFailureReason())),
        CHEMOTHERAPY_BEST_RESPONSE(EntityAttribute.attribute("chemotherapyBestResponse", (Chemotherapy c) -> c.getEvent().getBestResponse())),
        NUMBER_OF_CHEMOTHERAPY_CYCLES(EntityAttribute.attribute("numberOfChemotherapyCycles", (Chemotherapy c) -> c.getEvent().getNumOfCycles()));


        @Getter
        private final EntityAttribute<Chemotherapy> attribute;

        Attributes(EntityAttribute<Chemotherapy> attribute) {
            this.attribute = attribute;
        }
    }

}
