package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Radiotherapy extends SubjectAwareWrapper<RadiotherapyRaw> implements HasStartEndDate, Serializable {

    public static final String RADIOTHERAPY_LABEL = "Radiotherapy";

    public Radiotherapy(RadiotherapyRaw event, Subject subject) {
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

    public enum Attributes implements GroupByOption<Radiotherapy> {

        ID(EntityAttribute.attribute("id", (Radiotherapy e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (Radiotherapy radiotherapy) -> radiotherapy.getSubject().getSubjectCode())),
        START_DATE(EntityAttribute.attribute("startDate", (Radiotherapy e) -> e.getEvent().getStartDate())),
        END_DATE(EntityAttribute.attribute("endDate", (Radiotherapy e) -> e.getEvent().getEndDate())),
        RADIATION_DOSE(EntityAttribute.attribute("radiationDose", Radiotherapy::getRadiationDose)),
        THERAPY_STATUS(EntityAttribute.attribute("therapyStatus", (Radiotherapy r) -> r.getEvent().getTreatmentStatus()));

        @Getter
        private final EntityAttribute<Radiotherapy> attribute;

        Attributes(EntityAttribute<Radiotherapy> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 6, displayName = "Total grays", type = Column.Type.SSV)
    public String getTotalGrays() {
        return String.valueOf(ObjectUtils.defaultIfNull(getRadiationDose(), ""));
    }

    public Double getRadiationDose() {
        return getEvent().getDose() == null || getEvent().getNumOfDoses() == null ? null
                : getEvent().getDose() * getEvent().getNumOfDoses();
    }
}
