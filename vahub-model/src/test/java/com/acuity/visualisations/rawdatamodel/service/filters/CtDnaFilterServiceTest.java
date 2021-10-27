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

package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.ONLY_TRACKED_MUTATIONS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class CtDnaFilterServiceTest {
    private static final List<CtDna> CT_DNAS = generateCtDnaList();
    private static final List<Subject> POPULATION = newArrayList(
            Subject.builder().subjectId("subject1").build(),
            Subject.builder().subjectId("subject2").build(),
            Subject.builder().subjectId("subject3").build(),
            Subject.builder().subjectId("subject4").build()
    );

    @Autowired
    private CtDnaFilterService ctDnaFilterService;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetFiltersWithFilteredGene() {
        CtDnaFilters result = givenFilterSetup(filters ->
                filters.setGene(new SetFilter<>(newHashSet("gene1", "gene3"))));
        softly.assertThat(result.getGene().getValues()).containsOnly("gene1", "gene3");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(5);
    }

    @Test
    public void testGetFiltersWithFilteredGeneIncludingEmpty() {
        CtDnaFilters result = givenFilterSetup(filters ->
                filters.setGene(new SetFilter<>(newHashSet("gene1", null))));
        softly.assertThat(result.getGene().getValues()).containsOnly("gene1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(5);
    }

    @Test
    public void testGetFiltersWithFilteredMutation() {
        CtDnaFilters result = givenFilterSetup(filters ->
                filters.setMutation(new SetFilter<>(newHashSet("mutation1", "mutation5"))));
        softly.assertThat(result.getMutation().getValues()).containsOnly("mutation1", "mutation5");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void testGetFiltersWithFilteredMutationIncludingEmpty() {
        CtDnaFilters result = givenFilterSetup(filters ->
                filters.setMutation(new SetFilter<>(newHashSet("mutation1", null))));
        softly.assertThat(result.getMutation().getValues()).containsOnly("mutation1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void testGetAvailableFilters() {
        CtDnaFilters result = givenFilterSetup(filters -> CtDnaFilters.empty());
        softly.assertThat(result.getMutation().getValues()).size().isEqualTo(5);
        softly.assertThat(result.getGene().getValues()).size().isEqualTo(5);
        softly.assertThat(result.getMutation().getValues())
              .containsExactlyInAnyOrder("mutation1", "mutation2", "mutation4", "mutation5", null);
        softly.assertThat(result.getGene().getValues())
              .containsExactlyInAnyOrder("gene1", "gene2", "gene3", "gene4", null);
        softly.assertThat(result.getTrackedMutation().getValues())
              .containsExactly(ONLY_TRACKED_MUTATIONS);
    }

    @Test
    public void testQueryEmptyFilters() {
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, CtDnaFilters.empty(),
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(CT_DNAS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(CT_DNAS.size());
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() {
        List<Subject> filteredPopulations = POPULATION.stream()
                                                      .filter(subj -> "subject1".equals(subj.getSubjectId()))
                                                      .collect(Collectors.toList());
        List<CtDna> filteredEvents = CT_DNAS.stream()
                                            .filter(ctDna -> "subject1".equals(ctDna.getSubjectId()))
                                            .collect(toList());
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, CtDnaFilters.empty(),
                filteredPopulations, PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(CT_DNAS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(filteredEvents.size());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() {
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, CtDnaFilters.empty(),
                newArrayList(), PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(CT_DNAS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(0);
    }

    @Test
    public void testQueryFiltersWithFilteredGene() {
        CtDnaFilters filters = new CtDnaFilters();
        filters.setGene(new SetFilter<>(newHashSet("gene1", "gene3")));
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, filters,
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(CT_DNAS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(5);
        List<CtDna> filteredEvents = CT_DNAS.stream()
                                            .filter(ctDna -> "gene1".equals(ctDna.getEvent().getGene())
                                                    || "gene3".equals(ctDna.getEvent().getGene()))
                                            .collect(toList());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    @Test
    public void testQueryFiltersWithFilteredMutation() {
        CtDnaFilters filters = new CtDnaFilters();
        filters.setMutation(new SetFilter<>(newHashSet("mutation1", "mutation5", null)));
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, filters,
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(CT_DNAS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(5);
        List<CtDna> filteredEvents = CT_DNAS.stream()
                                            .filter(ctDna -> "mutation1".equals(ctDna.getEvent().getMutation())
                                                    || "mutation5".equals(ctDna.getEvent().getMutation())
                                                    || ctDna.getEvent().getMutation() == null)
                                            .collect(toList());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    @Test
    public void testQueryFiltersWithTrackedMutationOnly() {
        CtDnaFilters filters = new CtDnaFilters();
        filters.setTrackedMutation(new SetFilter<>(newHashSet(ONLY_TRACKED_MUTATIONS)));
        FilterQuery<CtDna> filterQuery = new FilterQuery<>(
                CT_DNAS, filters,
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<CtDna> filterResult = ctDnaFilterService.query(filterQuery);
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(2);
        List<CtDna> filteredEvents = CT_DNAS.stream()
                                            .filter(ctDna -> YES.equals(ctDna.getEvent().getTrackedMutation()))
                                            .collect(toList());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    private CtDnaFilters givenFilterSetup(final Consumer<CtDnaFilters> filterSetter) {
        List<Subject> subjects = CT_DNAS.stream().map(CtDna::getSubject).collect(toList());
        CtDnaFilters ctDnaFilters = new CtDnaFilters();
        filterSetter.accept(ctDnaFilters);
        return (CtDnaFilters) ctDnaFilterService.getAvailableFilters(CT_DNAS, ctDnaFilters,
                subjects, PopulationFilters.empty());
    }

    private static List<CtDna> generateCtDnaList() {
        Subject subject1 = Subject.builder().subjectId("subject1").build();
        String subjId1 = subject1.getSubjectId();
        Subject subject2 = Subject.builder().subjectId("subject2").build();
        String subjId2 = subject1.getSubjectId();
        Subject subject3 = Subject.builder().subjectId("subject3").build();
        String subjId3 = subject1.getSubjectId();
        Subject subject4 = Subject.builder().subjectId("subject4").build();
        String subjId4 = subject1.getSubjectId();
        CtDna ctDna1 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna1")
                                         .subjectId(subjId1)
                                         .gene("gene1")
                                         .mutation("mutation1")
                                         .trackedMutation(YES)
                                         .build(), subject1);
        CtDna ctDna2 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna2")
                                         .subjectId(subjId1)
                                         .gene("gene1")
                                         .mutation("mutation2")
                                         .trackedMutation(YES)
                                         .build(), subject1);
        CtDna ctDna3 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna3")
                                         .subjectId(subjId2)
                                         .gene("gene2")
                                         .mutation("mutation1")
                                         .trackedMutation(NO)
                                         .build(), subject2);
        CtDna ctDna4 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna4")
                                         .subjectId(subjId1)
                                         .gene("gene2")
                                         .mutation("mutation2")
                                         .build(), subject1);
        CtDna ctDna5 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna5")
                                         .subjectId(subjId1)
                                         .gene("gene3")
                                         .mutation("mutation4")
                                         .build(), subject1);
        CtDna ctDna6 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna6")
                                         .subjectId(subjId2)
                                         .gene("gene4")
                                         .mutation("mutation5")
                                         .build(), subject2);
        CtDna ctDna7 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna7")
                                         .subjectId(subjId2)
                                         .gene("gene4")
                                         .mutation("mutation2")
                                         .build(), subject2);
        CtDna ctDna8 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna7")
                                         .subjectId(subjId3)
                                         .gene("gene1")
                                         .mutation("mutation1")
                                         .build(), subject3);
        CtDna ctDna9 = new CtDna(CtDnaRaw.builder()
                                         .id("ctdna7")
                                         .subjectId(subjId4)
                                         .gene("gene1")
                                         .mutation("mutation2")
                                         .build(), subject4);
        CtDna ctDna10 = new CtDna(CtDnaRaw.builder()
                                          .id("ctdna6")
                                          .subjectId(subjId2)
                                          .gene("gene4")
                                          .mutation(null)
                                          .build(), subject2);
        CtDna ctDna11 = new CtDna(CtDnaRaw.builder()
                                          .id("ctdna5")
                                          .subjectId(subjId1)
                                          .gene(null)
                                          .mutation("mutation4")
                                          .build(), subject1);
        return newArrayList(ctDna1, ctDna2, ctDna3, ctDna4, ctDna5, ctDna6, ctDna7, ctDna8, ctDna9, ctDna10, ctDna11);
    }
}
