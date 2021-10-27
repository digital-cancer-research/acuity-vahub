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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.BiomarkerDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.CBioProfile;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.filters.BiomarkerFilterServiceTest.BIOMARKERS;
import static com.acuity.visualisations.rawdatamodel.service.filters.BiomarkerFilterServiceTest.BIOMARKER_LIST;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class BiomarkerServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private BiomarkerService biomarkerService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private BiomarkerDatasetsDataProvider biomarkerDatasetsDataProvider;
    @MockBean
    private StudyInfoRepository studyInfoRepository;

    @Test
    public void testGetFilteredDataGenePercentageFilterWithEmptyPopFilters() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        BiomarkerFilters filters = new BiomarkerFilters();
        filters.setGenePercentage(new RangeFilter<>(30, 80));
        filters.getGene().completeWithValue("gene3");

        FilterResult<Biomarker> filtered = biomarkerService.getFilteredData(DATASETS, filters, PopulationFilters.empty());
        softly.assertThat(filtered.getFilteredResult()).containsExactly(events.get(2), events.get(7));
    }

    @Test
    public void testGetFilteredDataGenePercentageFilterWithFilteredPopulation() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        BiomarkerFilters filters = new BiomarkerFilters();
        filters.setGenePercentage(new RangeFilter<>(30, 80));

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.getSubjectId().completeWithValue("subject1");
        populationFilters.getSubjectId().completeWithValue("subject2");

        FilterResult<Biomarker> filtered = biomarkerService.getFilteredData(DATASETS, filters, populationFilters);

        softly.assertThat(filtered.getFilteredResult()).containsExactly(events.get(1), events.get(2), events.get(3), events.get(4));
    }

    @Test
    public void testGetFilteredDataWithEmptyFilters() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        FilterResult<Biomarker> filtered = biomarkerService.getFilteredData(DATASETS, BiomarkerFilters.empty(), PopulationFilters.empty());
        softly.assertThat(filtered.getFilteredResult()).containsExactlyElementsOf(events);
    }

    @Test
    public void testGetAvailableFiltersWithEmptyFilters() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        BiomarkerFilters filters = (BiomarkerFilters) biomarkerService.getAvailableFilters(DATASETS, BiomarkerFilters.empty(), PopulationFilters.empty());

        softly.assertThat(filters.getGenePercentage().getFrom()).isEqualTo(25);
        softly.assertThat(filters.getGenePercentage().getTo()).isEqualTo(75);
    }

    @Test
    public void testGetAvailableFiltersWithNonEmptyFilters() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        BiomarkerFilters biomarkerFilters = new BiomarkerFilters();
        biomarkerFilters.setGenePercentage(new RangeFilter<>(35, 80));
        biomarkerFilters.getGene().completeWithValue("gene1");
        biomarkerFilters.getGene().completeWithValue("gene2");
        biomarkerFilters.getGene().completeWithValue("gene4");

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.getSubjectId().completeWithValue("subject1");
        populationFilters.getSubjectId().completeWithValue("subject2");
        populationFilters.getSubjectId().completeWithValue("subject4");

        BiomarkerFilters availableFilters = (BiomarkerFilters) biomarkerService.getAvailableFilters(DATASETS, biomarkerFilters, populationFilters);

        softly.assertThat(availableFilters.getGenePercentage().getFrom()).isEqualTo(67);
        softly.assertThat(availableFilters.getGenePercentage().getTo()).isEqualTo(67);
        softly.assertThat(availableFilters.getGene().getValues()).containsExactly("gene1");
    }

    @Test
    public void testGetAvailableFiltersWithFilteredPopulation() {

        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.getSubjectId().completeWithValue("subject1");
        populationFilters.getSubjectId().completeWithValue("subject2");

        BiomarkerFilters filters = (BiomarkerFilters) biomarkerService.getAvailableFilters(DATASETS, BiomarkerFilters.empty(), populationFilters);

        softly.assertThat(filters.getGenePercentage().getFrom()).isEqualTo(50);
        softly.assertThat(filters.getGenePercentage().getTo()).isEqualTo(100);
    }

    @Test
    public void testGetCBioDataOrderedByPercetageAndGeneNotSelectedEvents() {
        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        List<Map<String, String>> data = biomarkerService
                .getCBioData(DATASETS, Collections.EMPTY_SET, BiomarkerFilters.empty(), PopulationFilters.empty()).getData();
        softly.assertThat(data.stream().map(e -> e.get("gene")).collect(Collectors.toList()))
                .containsExactly("gene1", "gene1", "gene1", "gene3", "gene3", "gene2", "gene4", "gene5");
    }

    @Test
    public void testGetCBioDataOrderedByPercetageAndGeneWithSelectedEvents() {
        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        List<Map<String, String>> data = biomarkerService.getCBioData(DATASETS, Stream.of("bm1", "bm2", "bm8").collect(Collectors.toSet()),
                BiomarkerFilters.empty(), PopulationFilters.empty()).getData();
        softly.assertThat(data.stream().map(e -> e.get("gene")).collect(Collectors.toList()))
                .containsExactly("gene1", "gene3", "gene2");
    }

    @Test
    public void testGetCBioDataReturnsProfilesGroupNames() {
        final List<Biomarker> events = BIOMARKER_LIST;
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        Map<String, List<String>> profiles = biomarkerService
                .getCBioData(DATASETS, Collections.EMPTY_SET, BiomarkerFilters.empty(), PopulationFilters.empty())
                .getProfiles();

        softly.assertThat(profiles.keySet()).containsOnly(
                CBioProfile.CNA_DISCRETE.getProfileGroupName(),
                CBioProfile.MRNA_U133.getProfileGroupName(),
                CBioProfile.RPPA.getProfileGroupName(),
                CBioProfile.METHYLATION_HM450.getProfileGroupName(),
                CBioProfile.MUTATIONS.getProfileGroupName()
        );
    }

    @Test
    public void testGetCBioDataReturnsStudyProfilesIds() {
        final List<Biomarker> events = BIOMARKER_LIST;
        final String studyCode = events.iterator().next().getClinicalStudyCode();
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(events.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        Map<String, List<String>> profiles = biomarkerService
                .getCBioData(DATASETS, Collections.EMPTY_SET, BiomarkerFilters.empty(), PopulationFilters.empty())
                .getProfiles();

        softly.assertThat(profiles.values().stream().flatMap(Collection::stream).collect(Collectors.toList())).containsOnly(
                CBioProfile.CNA_DISCRETE.getStudyProfileId(studyCode),
                CBioProfile.MRNA_U133.getStudyProfileId(studyCode),
                CBioProfile.RPPA.getStudyProfileId(studyCode),
                CBioProfile.METHYLATION_HM450.getStudyProfileId(studyCode),
                CBioProfile.MUTATIONS.getStudyProfileId(studyCode)
        );

    }

    @Test
    public void testGetBiomarkersFromDatasetsBySubjectIds() {
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKER_LIST);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKER_LIST.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));
        Set<String> subjectIds = new HashSet<>();
        subjectIds.add("subjectId1");
        subjectIds.add("subjectId2");
        final SelectionDetail selectionDetail = biomarkerService.getSelectionBySubjectIds(DATASETS, subjectIds);
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(8);
        softly.assertThat(selectionDetail.getEventIds().size()).isEqualTo(6);
        softly.assertThat(selectionDetail.getEventIds()).containsOnly("bm1", "bm2", "bm3", "bm4", "bm5", "bm6");
        softly.assertThat(selectionDetail.getSubjectIds().size()).isEqualTo(2);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(4);

    }

    @Test
    public void testGetBiomarkersFromDatasetsBySubjectIdsEmpty() {
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKER_LIST);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKER_LIST.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));
        Set<String> subjectIds = new HashSet<>();
        final SelectionDetail selectionDetail = biomarkerService.getSelectionBySubjectIds(DATASETS, subjectIds);
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(8);
        softly.assertThat(selectionDetail.getEventIds().size()).isEqualTo(0);
        softly.assertThat(selectionDetail.getSubjectIds().size()).isEqualTo(0);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(4);
    }

    @Test
    public void testGetBoxPlotColorBy() {
        when(biomarkerDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKERS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(BIOMARKERS.stream().map(Biomarker::getSubject).collect(Collectors.toSet()));

        List<TrellisOptions<BiomarkerGroupByOptions>> colorBy = biomarkerService.getHeatmapColorByOptions(DATASETS,
                BiomarkerFilters.empty(), PopulationFilters.empty());

        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(BiomarkerGroupByOptions.ALTERATION_TYPE);
    }

}
