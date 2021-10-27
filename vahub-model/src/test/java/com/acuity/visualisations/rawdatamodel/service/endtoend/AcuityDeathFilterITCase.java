package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.DeathFilterService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.google.common.collect.Lists.newArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityDeathFilterITCase {
    private static final String[] DEATH_CAUSES = {"KNEE PAIN", "BREAST DISCHARGE", "PYROSIS", "ERYTHEMA",
            "ALT INCREASED", "CRACKED LIPS", "NAIL CHANGES", "VIRAL INFECTION NOS", "PALPITATION", "ODYNOPHAGIA",
            "EMOTIONAL LABILITY", "OSTEONECROSIS OF JAW", "FOOT EDEMA", "INCONTINENCE", "BREAST INFLAMMATION",
            "HYPOKALEMIA", "HEADACHE", "PAIN OF LOWER EXTREMITIES", "ITCHY EYES", "IRRITATION OF EYES",
            "NASAL CONGESTION", "DYSURIA", "WOUND", "INFUSION SITE DISCOMFORT", "ARTHRALGIA AGGRAVATED",
            "BRONCHOSPASM", "TONSILLITIS", "EAR NOISES", "NAIL LOSS", "PERIOCULAR RASH", "THRUSH", "UNSTABLE ANGINA",
            "BRITTLE NAILS", null};
    private static final String[] NULL = {null};


    @Autowired
    private DeathFilterService deathFilterService;

    @Autowired
    private DeathDatasetsDataProvider deathDatasetsDataProvider;

    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListAllFromEmptyFilter() {
        Collection<Death> events = deathDatasetsDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);

        DeathFilters filters = (DeathFilters) deathFilterService.getAvailableFilters(events, DeathFilters.empty(),
                subjects, PopulationFilters.empty());

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(46);

        softly.assertThat(filters.getDeathCause().getValues()).containsExactlyInAnyOrder(DEATH_CAUSES);
        softly.assertThat(filters.getAutopsyPerformed().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getDesignation().getValues()).containsExactlyInAnyOrder("Primary", "Secondary");
        softly.assertThat(filters.getDeathRelatedToDisease().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getHlt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getLlt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getSoc().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getPt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getDaysFromFirstDoseToDeath().getFrom()).isEqualTo(27);
        softly.assertThat(filters.getDaysFromFirstDoseToDeath().getTo()).isEqualTo(706);
    }

    @Test
    public void shouldGetCorrectFilteredValues() {
        Collection<Death> events = deathDatasetsDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(newArrayList("E00001009")));

        DeathFilters deathFilters = DeathFilters.empty();
        deathFilters.setDeathCause(new SetFilter<>(newArrayList("NAIL LOSS"), false));

        DeathFilters filters = (DeathFilters) deathFilterService.getAvailableFilters(events, deathFilters,
                subjects, populationFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(1);

        softly.assertThat(filters.getDeathCause().getValues()).containsExactly("NAIL LOSS");
        softly.assertThat(filters.getAutopsyPerformed().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getDesignation().getValues()).containsExactly("Primary");
        softly.assertThat(filters.getDeathRelatedToDisease().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getHlt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getLlt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getSoc().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getPt().getValues()).containsExactly(NULL);
        softly.assertThat(filters.getDaysFromFirstDoseToDeath().getFrom()).isEqualTo(78);
        softly.assertThat(filters.getDaysFromFirstDoseToDeath().getTo()).isEqualTo(78);
    }
}
