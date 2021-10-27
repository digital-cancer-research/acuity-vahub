/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
