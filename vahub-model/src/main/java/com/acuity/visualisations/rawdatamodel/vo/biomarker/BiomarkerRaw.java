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

package com.acuity.visualisations.rawdatamodel.vo.biomarker;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.AcuityEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 11)
public class BiomarkerRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(columnName = "gene", order = 3, displayName = "Gene", type = Column.Type.CBIO)
    @Column(columnName = "gene", order = 3, displayName = "Gene")
    private String gene;
    private String mutation;
    @Column(columnName = "sampleType", order = 0, displayName = "Sample type")
    private String sampleType;
    @Column(columnName = "sampleId", order = 1, displayName = "Sample id", type = Column.Type.CBIO)
    @Column(columnName = "sampleId", order = 1, displayName = "Sample id")
    private String sampleId;
    @Column(columnName = "variantCount", order = 2, displayName = "Variant count")
    private Integer variantCount;
    @Column(columnName = "cdnaChange", order = 4, displayName = "cDNA change")
    private String cDnaChange;
    @Column(columnName = "somaticStatus", order = 5, displayName = "Somatic status")
    private String somaticStatus;
    @Column(columnName = "aminoAcidChange", order = 6, displayName = "Amino acid change")
    private String aminoAcidChange;
    @Column(columnName = "externalVariantId", order = 7, displayName = "External variant id")
    private String externalVariantId;
    @Column(columnName = "chromosome", order = 8, displayName = "Chromosome number")
    private String chromosome;
    @Column(columnName = "chromosomeLocationStart", order = 9, displayName = "Chromosome location starts")
    private Integer chromosomeLocationStart;
    @Column(columnName = "chromosomeLocationEnd", order = 10, displayName = "Chromosome location ends")
    private Integer chromosomeLocationEnd;
    @Column(columnName = "variantType", order = 11, displayName = "Variant type")
    private String variantType;
    @Column(columnName = "mutationType", order = 12, displayName = "Mutation type")
    private String mutationType;
    @Column(columnName = "copyNumber", order = 13, displayName = "Copy number alteration type")
    private String copyNumber;
    @Column(columnName = "rearrangementGene1", order = 14, displayName = "Rearrangement gene 1")
    private String rearrangementGene1;
    @Column(columnName = "rearrangementDescription", order = 15, displayName = "Rearrangement description")
    private String rearrangementDescription;
    @Column(columnName = "totalReads", order = 16, displayName = "Total reads")
    private Integer totalReads;
    @Column(columnName = "germlineFrequency", order = 17, displayName = "Germline frequency")
    private Integer germlineFrequency;
    @Column(columnName = "mutantAlleleFrequency", order = 18, displayName = "Variant allele frequency")
    private Integer mutantAlleleFrequency;
    @Column(columnName = "copyNumberAlterationCopyNumber", order = 19, displayName = "Copy number alteration copy number")
    private Integer copyNumberAlterationCopyNumber;
    @Column(columnName = "chromosomeInstabilityNumber", order = 20, displayName = "Chromosome instability number")
    private Integer chromosomeInstabilityNumber;
    @Column(columnName = "tumourMutationalBurden", order = 21, displayName = "Tumour mutational burden")
    private Integer tumourMutationalBurden;
    private Integer profilesMask;
}
