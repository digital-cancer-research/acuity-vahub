package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.ctdna.SubjectGeneMutation;
import com.acuity.visualisations.rawdatamodel.vo.ctdna.SubjectGeneMutationVaf;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;

public enum CtDnaGroupByOptions implements GroupByOption<CtDna> {

    SUBJECT(CtDna.Attributes.SUBJECT),
    @TimestampOption
    SAMPLE_DATE(CtDna.Attributes.SAMPLE_DATE) {
        @Override
        public EntityAttribute<CtDna> getAttribute(Params params) {
            return params == null ? getAttribute()
                    : Attributes.getDateAttribute("SAMPLE_DATE", params,
                    CtDna.Attributes.SAMPLE_DATE.getAttribute().getFunction());
        }
    },
    VISIT_DATE(CtDna.Attributes.VISIT_DATE),
    VISIT_NUMBER(CtDna.Attributes.VISIT_NUMBER),
    VARIANT_ALLELE_FREQUENCY(CtDna.Attributes.VARIANT_ALLELE_FREQUENCY),
    VARIANT_ALLELE_FREQUENCY_PERCENT(CtDna.Attributes.VARIANT_ALLELE_FREQUENCY_PERCENT),
    GENE(CtDna.Attributes.GENE),
    MUTATION(CtDna.Attributes.MUTATION),
    SUBJECT_GENE_MUT(null) {
        @Override
        public EntityAttribute<CtDna> getAttribute() {
            return EntityAttribute.attribute("subjectGeneMutation",
                    e -> new SubjectGeneMutation(e.getSubjectCode(), e.getEvent().getGene(), e.getEvent().getMutation()));
        }
    },
    SUBJECT_GENE_MUT_VAF(null) {
        @Override
        public EntityAttribute<CtDna> getAttribute() {
            return EntityAttribute.attribute("subjectGeneMutationVaf",
                    e -> SubjectGeneMutationVaf.builder()
                                               .subjectCode(e.getSubjectCode())
                                               .gene(e.getEvent().getGene())
                                               .mutation(e.getEvent().getMutation())
                                               .vaf(e.getEvent().getReportedVaf())
                                               .vafPercent(e.getEvent().getReportedVafPercent())
                                               .build());
        }
    };

    private CtDna.Attributes originAttribute;

    CtDnaGroupByOptions(CtDna.Attributes originAttribute) {
        this.originAttribute = originAttribute;
    }

    @Override
    public EntityAttribute<CtDna> getAttribute() {
        return originAttribute.getAttribute();
    }
}
