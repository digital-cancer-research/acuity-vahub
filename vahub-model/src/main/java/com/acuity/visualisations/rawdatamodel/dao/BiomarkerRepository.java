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

package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.common.lookup.AcuityRepository;
import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@AcuityRepository
public interface BiomarkerRepository extends RawDataRepository<BiomarkerRaw> {
    @Select("WITH bio AS "
            + "  (SELECT BMR_ID, "
            + "    BMR_PAT_ID, "
            + "    BMR_GENE, "
            + "    CASE "
            + "      WHEN BMR_VARIANT_TYPE = 'short variant' "
            + "      THEN BMR_MUTATION_TYPE "
            + "      ELSE "
            + "        CASE "
            + "          WHEN BMR_VARIANT_TYPE = 'copy number alteration' "
            + "          THEN BMR_CNA_TYPE "
            + "          ELSE "
            + "            CASE "
            + "              WHEN BMR_VARIANT_TYPE = 'rearrangement' "
            + "              THEN BMR_REARR_DESCRIPTION "
            + "              ELSE NULL "
            + "            END "
            + "        END "
            + "    END AS BMR_ALTERATION_TYPE, "
            + "    BMR_CNA_TYPE, "
            + "    BMR_MUTATION_TYPE, "
            + "    BMR_REARR_DESCRIPTION, "
            + "    BMR_SOMATIC_STATUS, "
            + "    BMR_AMINO_ACID_CHANGE, "
            + "    BMR_COPY_NUMBER, "
            + "    BMR_MUTANT_ALLELE_FREQ "
            + "  FROM RESULT_BIOMARKERS "
            + "  ) "
            + "SELECT DISTINCT s.BMR_ID, "
            + "  s.BMR_PAT_ID, "
            + "  UPPER(s.BMR_GENE) AS GENE, "
            + "  CASE "
            + "    WHEN mtd1.MTD_DEFINITION IS NOT NULL "
            + "    THEN mtd1.MTD_DEFINITION "
            + "    ELSE 'Not recognised' "
            + "  END AS MUTATION, "
            + "  CASE "
            + "    WHEN b.BMR_CNA_TYPE IS NULL OR b.BMR_CNA_TYPE = '' OR b.BMR_CNA_TYPE = '-' "
            + "    THEN b.BMR_CNA_TYPE "
            + "    ELSE "
            + "      CASE "
            + "        WHEN mtd2.MTD_DEFINITION IS NOT NULL "
            + "        THEN mtd2.MTD_DEFINITION "
            + "        ELSE 'Not recognised' "
            + "      END "
            + "  END               AS CNA_TYPE, "
            + "  b.BMR_SPECIMEN_ID AS SAMPLE_TYPE, "
            + "  b.BMR_SAMPLE_ID, "
            + "  b.BMR_VARIANT_COUNT, "
            + "  b.BMR_CDNA_CHANGE, "
            + "  b.BMR_SOMATIC_STATUS, "
            + "  b.BMR_AMINO_ACID_CHANGE, "
            + "  b.BMR_EXTERNAL_VARIANT_ID, "
            + "  b.BMR_CHROMOSOME, "
            + "  b.BMR_CHROMOSOME_LOCATION_START, "
            + "  b.BMR_CHROMOSOME_LOCATION_END, "
            + "  b.BMR_VARIANT_TYPE, "
            + "  CASE "
            + "    WHEN b.BMR_MUTATION_TYPE IS NULL OR b.BMR_MUTATION_TYPE = '' OR b.BMR_MUTATION_TYPE = '-' "
            + "    THEN b.BMR_MUTATION_TYPE "
            + "    ELSE "
            + "      CASE "
            + "        WHEN mtd3.MTD_DEFINITION IS NOT NULL "
            + "        THEN mtd3.MTD_DEFINITION "
            + "        ELSE 'Not recognised' "
            + "      END "
            + "  END AS MUTATION_TYPE, "
            + "  b.BMR_COPY_NUMBER, "
            + "  b.BMR_REARR_GENE_1, "
            + "  CASE "
            + "    WHEN b.BMR_REARR_DESCRIPTION IS NULL OR b.BMR_REARR_DESCRIPTION = '' OR b.BMR_REARR_DESCRIPTION = '-' "
            + "    THEN b.BMR_REARR_DESCRIPTION "
            + "    ELSE "
            + "      CASE "
            + "        WHEN mtd4.MTD_DEFINITION IS NOT NULL "
            + "        THEN mtd4.MTD_DEFINITION "
            + "        ELSE 'Not recognised' "
            + "      END "
            + "  END AS REARR_DESCRIPTION, "
            + "  b.BMR_TOTAL_READS, "
            + "  b.BMR_GERMLINE_FREQUENCY, "
            + "  b.BMR_MUTANT_ALLELE_FREQ, "
            + "  b.BMR_CIN_RANK AS CHROMOSOME_INSTABILITY_NUMBER, "
            + "  b.BMR_TUMOUR_MUTATIONAL_BURDEN, "
            + "  m.MSR_CBIO_PROFILES_MASK "
            + "FROM bio s "
            + "JOIN result_patient "
            + "ON s.bmr_pat_id = pat_id "
            + "JOIN result_study st "
            + "ON pat_std_id = std_id "
            + "JOIN map_study_rule m "
            + "ON m.msr_study_code = std_name "
            + "LEFT JOIN UTIL_MUTATION_TYPE_SYNONYM mts1 "
            + "ON UPPER(BMR_ALTERATION_TYPE) = UPPER(mts1.MTS_SYNONYM) "
            + "LEFT JOIN UTIL_MUTATION_TYPE_DICTIONARY mtd1 "
            + "ON mtd1.MTD_ID::varchar = mts1.MTS_MTD_ID::varchar "
            + "LEFT JOIN UTIL_MUTATION_TYPE_SYNONYM mts2 "
            + "ON UPPER(BMR_CNA_TYPE) = UPPER(mts2.MTS_SYNONYM) "
            + "LEFT JOIN UTIL_MUTATION_TYPE_DICTIONARY mtd2 "
            + "ON mtd2.MTD_ID::varchar = mts2.MTS_MTD_ID::varchar "
            + "LEFT JOIN UTIL_MUTATION_TYPE_SYNONYM mts3 "
            + "ON UPPER(BMR_MUTATION_TYPE) = UPPER(mts3.MTS_SYNONYM) "
            + "LEFT JOIN UTIL_MUTATION_TYPE_DICTIONARY mtd3 "
            + "ON mtd3.MTD_ID::varchar = mts3.MTS_MTD_ID::varchar "
            + "LEFT JOIN UTIL_MUTATION_TYPE_SYNONYM mts4 "
            + "ON UPPER(BMR_REARR_DESCRIPTION) = UPPER(mts4.MTS_SYNONYM) "
            + "LEFT JOIN UTIL_MUTATION_TYPE_DICTIONARY mtd4 "
            + "ON mtd4.MTD_ID::varchar = mts4.MTS_MTD_ID::varchar "
            + "JOIN RESULT_BIOMARKERS b "
            + "ON b.BMR_ID                 = s.BMR_ID "
            + "WHERE (b.bmr_somatic_status = 'known' "
            + "OR b.bmr_somatic_status     = 'likely') "
            + "AND MSR_ID = #{datasetId}")
    @Results({
            @Result(property = "id", column = "bmr_id"),
            @Result(property = "subjectId", column = "bmr_pat_id"),
            @Result(property = "gene", column = "gene"),
            @Result(property = "mutation", column = "mutation"),
            @Result(property = "sampleType", column = "sample_type"),
            @Result(property = "sampleId", column = "bmr_sample_id"),
            @Result(property = "variantCount", column = "bmr_variant_count"),
            @Result(property = "cDnaChange", column = "bmr_cdna_change"),
            @Result(property = "somaticStatus", column = "bmr_somatic_status"),
            @Result(property = "aminoAcidChange", column = "bmr_amino_acid_change"),
            @Result(property = "externalVariantId", column = "bmr_external_variant_id"),
            @Result(property = "chromosome", column = "bmr_chromosome"),
            @Result(property = "chromosomeLocationStart", column = "bmr_chromosome_location_start"),
            @Result(property = "chromosomeLocationEnd", column = "bmr_chromosome_location_end"),
            @Result(property = "variantType", column = "bmr_variant_type"),
            @Result(property = "mutationType", column = "mutation_type"),
            @Result(property = "copyNumber", column = "cna_type"),
            @Result(property = "rearrangementGene1", column = "bmr_rearr_gene_1"),
            @Result(property = "rearrangementDescription", column = "rearr_description"),
            @Result(property = "totalReads", column = "bmr_total_reads"),
            @Result(property = "germlineFrequency", column = "bmr_germline_frequency"),
            @Result(property = "mutantAlleleFrequency", column = "bmr_mutant_allele_freq"),
            @Result(property = "copyNumberAlterationCopyNumber", column = "bmr_copy_number"),
            @Result(property = "chromosomeInstabilityNumber", column = "chromosome_instability_number"),
            @Result(property = "tumourMutationalBurden", column = "bmr_tumour_mutational_burden"),
            @Result(property = "profilesMask", column = "MSR_CBIO_PROFILES_MASK")
    })
    @Options(fetchSize = 5000)
    List<BiomarkerRaw> getRawData(@Param("datasetId") long datasetId);
}
