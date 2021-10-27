package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class TrellisUtilTest {

    @Test
    public void testGetTrellisOptions() {

        Subject subject1 = Subject.builder().clinicalStudyCode("1").sex("female").actualArm("Superdex 20mg").build();
        Subject subject2 = Subject.builder().clinicalStudyCode("2").sex("female").actualArm("Superdex 100mg").build();
        Subject subject3 = Subject.builder().clinicalStudyCode("1").sex("male").actualArm("Placebo").build();
        List<Subject> population = newArrayList(subject1, subject2, subject3);

        List<TrellisOptions<PopulationGroupByOptions>> trellisOptions = TrellisUtil.getTrellisOptions(population,
                PopulationGroupByOptions.SEX, PopulationGroupByOptions.STUDY_CODE,
                PopulationGroupByOptions.ACTUAL_TREATMENT_ARM, PopulationGroupByOptions.AGE);

        assertThat(trellisOptions).hasSize(3);
        assertThat(trellisOptions).extracting(TrellisOptions::getTrellisedBy)
                .containsExactlyInAnyOrder(PopulationGroupByOptions.STUDY_CODE, PopulationGroupByOptions.SEX, PopulationGroupByOptions.ACTUAL_TREATMENT_ARM);
        assertThat(trellisOptions).contains(new TrellisOptions<>(PopulationGroupByOptions.STUDY_CODE, Arrays.asList("1", "2")));
        assertThat(trellisOptions).contains(new TrellisOptions<>(PopulationGroupByOptions.SEX, Arrays.asList("female", "male")));
        assertThat(trellisOptions).contains(new TrellisOptions<>(PopulationGroupByOptions.ACTUAL_TREATMENT_ARM,
                Arrays.asList("Placebo", "Superdex 20mg", "Superdex 100mg")));
    }
}
