package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerMutation;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerParameters;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData.SOMATIC_STATUS_KNOWN;
import static com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData.SOMATIC_STATUS_LIKELY;
import static org.assertj.core.api.Assertions.assertThat;

public class BiomarkerDataTest {

    @Test
    public void shouldGetAlphabeticallyFirstBiomarkerMutationFromBiomarkerMutationsOfEqualPrioritySomaticKnown() {
        List<BiomarkerParameters> params = Arrays.asList(
                BiomarkerParameters.builder()
                        .mutation(BiomarkerMutation.AMPLIFICATION_MUTATION.getName())
                        .somaticStatus(SOMATIC_STATUS_KNOWN)
                        .build(),
                BiomarkerParameters.builder()
                        .mutation(BiomarkerMutation.DELETION_MUTATION.getName())
                        .somaticStatus(SOMATIC_STATUS_KNOWN)
                        .build());
        String mutation = BiomarkerData.builder()
                .biomarkerParameters(params)
                .build()
                .getPriorityMutation();

        assertThat(mutation).isEqualTo(BiomarkerMutation.AMPLIFICATION_MUTATION.getName());
    }

    @Test
    public void shouldGetAlphabeticallyFirstBiomarkerMutationFromBiomarkerMutationsOfEqualPrioritySomaticLikely() {
        List<BiomarkerParameters> params = Arrays.asList(
                BiomarkerParameters.builder()
                        .mutation(BiomarkerMutation.AMPLIFICATION_MUTATION.getName())
                        .somaticStatus(SOMATIC_STATUS_LIKELY)
                        .build(),
                BiomarkerParameters.builder()
                        .mutation(BiomarkerMutation.DELETION_MUTATION.getName())
                        .somaticStatus(SOMATIC_STATUS_LIKELY)
                        .build());
        String mutation = BiomarkerData.builder()
                .biomarkerParameters(params)
                .build()
                .getPriorityMutation();

        assertThat(mutation).isEqualTo(BiomarkerMutation.DELETION_MUTATION.getName());
    }

    @Test
    public void shouldGetEmptyStringWhenNoBiomarkerParameters() {
        String mutation = BiomarkerData.builder()
                .biomarkerParameters(Collections.emptyList())
                .build()
                .getPriorityMutation();

        assertThat(mutation).isEqualTo("");
    }


}
