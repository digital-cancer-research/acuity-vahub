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
