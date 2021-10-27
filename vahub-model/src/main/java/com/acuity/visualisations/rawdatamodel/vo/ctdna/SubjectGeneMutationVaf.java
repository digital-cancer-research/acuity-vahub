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

package com.acuity.visualisations.rawdatamodel.vo.ctdna;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;


import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_THREE_DECIMAL_PLACES;
import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_TWO_DECIMAL_PLACES;
import static com.acuity.visualisations.rawdatamodel.util.Constants.PERCENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class SubjectGeneMutationVaf extends SubjectGeneMutation implements Serializable {
    private String vaf;
    private String vafPercent;

    @Builder
    private SubjectGeneMutationVaf(String subjectCode, String gene, String mutation, Double vaf, Double vafPercent) {
        super(subjectCode, gene, mutation);
        this.vaf = vaf == null
                ? NO_MUTATIONS_DETECTED
                : String.format(FORMATTING_THREE_DECIMAL_PLACES, vaf);
        this.vafPercent = vafPercent == null
                ? NO_MUTATIONS_DETECTED
                : String.format(FORMATTING_TWO_DECIMAL_PLACES, vafPercent) + PERCENT;
    }

    @Getter(lazy = true)
    private final String asString = String.format("%s, %s, %s, VAF: %s, VAF in percent: %s",
            getSubjectCode(), getGene(), getMutation(), vaf, vafPercent);

    @Override
    public String toString() {
        return getAsString();
    }
}
