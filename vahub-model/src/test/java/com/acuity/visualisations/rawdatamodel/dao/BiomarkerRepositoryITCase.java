package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class BiomarkerRepositoryITCase {

    @Autowired
    private BiomarkerRepository biomarkerRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetBiomarkerByDatasetId() {
        List<BiomarkerRaw> events = biomarkerRepository.getRawData(DUMMY_2_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(81);
    }

    @Test
    public void shouldGetBiomarkerByDatasetIdWithCorrectValue() {
        BiomarkerRaw biomarker = biomarkerRepository.getRawData(DUMMY_2_ACUITY_VA_ID)
                .stream().filter(t -> t.getId().equals("f61a0596eb134689ba7cbf4759252c38")).findFirst().get();
        softly.assertThat(biomarker.getId()).isEqualTo("f61a0596eb134689ba7cbf4759252c38");
        softly.assertThat(biomarker.getSubjectId()).isEqualTo("810a7570a5ec4aad92c5f3216a6dd01b");
        softly.assertThat(biomarker.getGene()).isEqualTo("TERT");
        softly.assertThat(biomarker.getMutation()).isEqualTo("Other");
    }

    @Test
    public void shouldGetBiomarkerExtendedFieldsWithCorrectId() {
        BiomarkerRaw biomarker = biomarkerRepository.getRawData(DUMMY_2_ACUITY_VA_ID)
                .stream().filter(t -> t.getId().equals("f61a0596eb134689ba7cbf4759252c38")).findFirst().get();
        softly.assertThat(biomarker.getId()).isEqualTo("f61a0596eb134689ba7cbf4759252c38");
        // skip fields, checked during test above
        softly.assertThat(biomarker.getSampleId()).isEqualTo("6203261250");
        softly.assertThat(biomarker.getSomaticStatus()).isEqualTo("known");
        softly.assertThat(biomarker.getAminoAcidChange()).isEqualTo("promoter -124C>T");
        softly.assertThat(biomarker.getCDnaChange()).isEqualTo("-124C>T");
        softly.assertThat(biomarker.getChromosome()).isEqualTo("chr5");
        softly.assertThat(biomarker.getChromosomeLocationStart()).isEqualTo(1295228);
        softly.assertThat(biomarker.getChromosomeLocationEnd()).isEqualTo(1295228);
        softly.assertThat(biomarker.getVariantType()).isEqualTo("short variant");
        softly.assertThat(biomarker.getMutationType()).isEqualTo("Other");
        softly.assertThat(biomarker.getTotalReads()).isEqualTo(79);
        softly.assertThat(biomarker.getMutantAlleleFrequency()).isEqualTo(19);
    }

}
