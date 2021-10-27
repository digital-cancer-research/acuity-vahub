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

package com.acuity.visualisations.rawdatamodel.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CBioProfile {
    MUTATIONS(0, "_mutations", "MUTATION_EXTENDED"),
    CNA_LENEAR(1, "_linear_CNA", "COPY_NUMBER_ALTERATION"),
    CNA_DISCRETE(2, "_cna", "COPY_NUMBER_ALTERATION"),
    CNA_GISTIC(3, "_gistic", "COPY_NUMBER_ALTERATION"),
    MRNA_U133(4, "_mrna_U133", "MRNA_EXPRESSION"),
    MRNA_U133_ZSCORES(5, "_mrna_U133_Zscores", "MRNA_EXPRESSION"),
    MRNA_ZSCORES(6, "_mrna_median_Zscores", "MRNA_EXPRESSION"),
    MRNA_SEQ(7, "_rna_seq_v2_mrna", "MRNA_EXPRESSION"),
    MRNA_SEQ_ZSCORES(8, "_rna_seq_v2_mrna_median_Zscores", "MRNA_EXPRESSION"),
    METHYLATION_HM27(9, "_methylation_hm27", "METHYLATION"),
    METHYLATION_HM450(10, "_methylation_hm450", "METHYLATION"),
    RPPA(11, "_rppa", "PROTEIN_LEVEL"),
    RPPA_ZSCORES(12, "_rppa_Zscores", "PROTEIN_LEVEL"),
    RPPA_CPTAC_ZSCORES(13, "_protein_quantification_zscores", "PROTEIN_LEVEL");

    private static final String PREFIX = "genetic_profile_ids_PROFILE_";

    private int bit;
    private String postfix;
    private String group;

    CBioProfile(int bit, String postfix, String group) {
        this.bit = bit;
        this.postfix = postfix;
        this.group = group;
    }

    public String getStudyProfileId(String studyId) {
        return studyId + postfix;
    }

    public String getProfileGroupName() {
        return PREFIX + group;
    }

    public static List<CBioProfile> getProfilesFromBitmask(int bitmask) {
        return Arrays.stream(CBioProfile.values())
                .filter(p -> (bitmask & (1L << p.bit)) != 0)
                .collect(Collectors.toList());
    }
}
