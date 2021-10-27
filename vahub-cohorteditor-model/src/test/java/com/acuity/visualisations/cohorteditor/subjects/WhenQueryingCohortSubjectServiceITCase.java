package com.acuity.visualisations.cohorteditor.subjects;

import com.acuity.visualisations.cohorteditor.ApplicationCohortEditorConfigITCase;
import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.service.CohortSubjectService;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author ksnd199
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationModelConfigITCase.class, ApplicationCohortEditorConfigITCase.class})
@TransactionalOracleITTest
@Rollback
public class WhenQueryingCohortSubjectServiceITCase {

    @Autowired
    private CohortSubjectService cohortSubjectService;

    @Test
    public void shouldGetSubjectIdsForEmptyPopAndAesOperatorAnd() {

        //  197 pop, 185 aes as not all subjects have ases
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(AeFilters.empty(), PopulationFilters.empty()), SavedFilter.Operator.AND);

        assertThat(subjects).hasSize(185);
    }

    @Test
    public void shouldGetSubjectIdsForEmptyPopAndAesOperatorOr() {

        //  197 pop, 185 aes as not all subjects have ases
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(AeFilters.empty(), PopulationFilters.empty()), SavedFilter.Operator.OR);

        assertThat(subjects).hasSize(197);
    }

    @Test
    public void shouldGetSubjectIdsForMalesPop() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("M")));

        //  197 pop, 116 aes as males
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(populationFilters), SavedFilter.Operator.AND);

        assertThat(subjects).hasSize(116);
    }

    @Test
    public void shouldGetSubjectIdsForMalesPopAndAesPTOperatorAnd() {

        //  116 pop
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("M")));

        //  3 aes
        AeFilters aesFilters = new AeFilters();
        aesFilters.setPt(new SetFilter(newArrayList("Abdominal discomfort")));

        //  116 pop, 3 aes = AND = 1
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(aesFilters, populationFilters), SavedFilter.Operator.AND);

        assertThat(subjects).hasSize(1);
    }

    @Test
    public void shouldGetSubjectIdsForMalesPopAndAesPTOperatorOr() {

        //  116 pop
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("M")));

        //  3 aes
        AeFilters aesFilters = new AeFilters();
        aesFilters.setPt(new SetFilter(newArrayList("Abdominal discomfort")));

        //  116 pop, 3 aes = OR = 118
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(aesFilters, populationFilters), SavedFilter.Operator.OR);

        assertThat(subjects).hasSize(118);
    }

    @Test
    public void shouldGetSubjectIdsForMalesPopLabsAndAesPTOperatorOr() {

        //  116 pop
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("M")));

        //  3 aes
        AeFilters aesFilters = new AeFilters();
        aesFilters.setPt(new SetFilter(newArrayList("Abdominal discomfort")));

        // ~100 labs
        LabFilters labsFilters = new LabFilters();
        labsFilters.setLabcode(new SetFilter(newArrayList("ALBUMIN")));

        //  116 pop, 3 aes, ~100 labs = AND = 1
        List<String> subjects = cohortSubjectService.getDistinctSubjectIds(
                DUMMY_DETECT_DATASETS, newArrayList(aesFilters, labsFilters, populationFilters), SavedFilter.Operator.AND);

        assertThat(subjects).hasSize(1);
    }
}
