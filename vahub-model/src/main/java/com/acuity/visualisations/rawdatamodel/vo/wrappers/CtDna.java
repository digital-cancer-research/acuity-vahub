package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class CtDna extends SubjectAwareWrapper<CtDnaRaw> implements Serializable {

    public static final String ONLY_TRACKED_MUTATIONS = "Show only tracked mutations";
    public static final String NO_MUTATIONS_DETECTED = "No mutations detected";
    public static final Double NO_MUTATIONS_DETECTED_VALUE = 0.002;
    public static final Double NO_MUTATIONS_DETECTED_VALUE_IN_PERCENT = 0.2;

    public CtDna(CtDnaRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<CtDna> {

        ID(EntityAttribute.attribute("id", CtDna::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", CtDna::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", CtDna::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", CtDna::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", CtDna::getSubjectCode)),
        GENE(EntityAttribute.attribute("gene", (CtDna e) -> e.getEvent().getGene())),
        MUTATION(EntityAttribute.attribute("mutation", (CtDna e) -> e.getEvent().getMutation())),
        TRACKED_MUTATION(EntityAttribute.attribute("trackedMutation", (CtDna e) -> e.getEvent().getTrackedMutation())),
        SAMPLE_DATE(EntityAttribute.attribute("sampleDate", (CtDna e) -> e.getEvent().getSampleDate())),
        VARIANT_ALLELE_FREQUENCY(EntityAttribute.attribute("vaf", (CtDna e) -> e.getEvent().getReportedVafCalculated())),
        VARIANT_ALLELE_FREQUENCY_PERCENT(EntityAttribute.attribute("vafInPercent",
                (CtDna e) -> e.getEvent().getReportedVafCalculatedPercent())),
        VISIT_DATE(EntityAttribute.attribute("sampleDate", (CtDna e) -> DaysUtil.toString(e.getEvent().getSampleDate()))),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (CtDna e) -> e.getEvent().getVisitNumber()));

        @Getter
        private final EntityAttribute<CtDna> attribute;

        Attributes(EntityAttribute<CtDna> attribute) {
            this.attribute = attribute;
        }
    }
}

