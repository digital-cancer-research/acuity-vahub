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

package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChordGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputChordDiagramData;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordContributor;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordDiagramSelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT1;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT1_2;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT1_3;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT2;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT2_1;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT3;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT4;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT4_NULL_HLT_AND_SOC;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT5_NULL_TERM;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.RAW_EVENT6_NULL_START_DATE;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.SETTINGS;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.SETTINGS_WITH_MAX_TIME_FRAME;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.SUBJECT1_ARM1;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.SUBJECT2_ARM2;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.END;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.START;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singletonList;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AeChordDiagramServiceTest {

    private static final Ae EVENT1_SUBJECT1 = getAeWithIdsSet(RAW_EVENT1.toBuilder().build(), SUBJECT1_ARM1);
    private static final Ae EVENT2_SUBJECT1 = getAeWithIdsSet(RAW_EVENT2, SUBJECT1_ARM1);
    private static final Ae EVENT3_SUBJECT1 = getAeWithIdsSet(RAW_EVENT3, SUBJECT1_ARM1);
    private static final Ae EVENT1_2_SUBJECT1 = getAeWithIdsSet(RAW_EVENT1_2, SUBJECT1_ARM1);
    private static final Ae EVENT1_3_SUBJECT1 = getAeWithIdsSet(RAW_EVENT1_3, SUBJECT1_ARM1);
    private static final Ae IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1 = getAeWithIdsSet(RAW_EVENT4, SUBJECT1_ARM1);
    private static final Ae EVENT1_SUBJECT2 = getAeWithIdsSet(RAW_EVENT1, SUBJECT2_ARM2);

    private static final Ae EVENT2_1_SUBJECT1 = getAeWithIdsSet(RAW_EVENT2_1, SUBJECT1_ARM1);
    private static final Ae EVENT2_SUBJECT2 = getAeWithIdsSet(RAW_EVENT2, SUBJECT2_ARM2);

    private static final Ae EVENT4_SUBJECT1 = getAeWithIdsSet(RAW_EVENT4_NULL_HLT_AND_SOC, SUBJECT1_ARM1);
    private static final Ae EVENT5_SUBJECT1 = getAeWithIdsSet(RAW_EVENT5_NULL_TERM, SUBJECT1_ARM1);
    private static final Ae EVENT6_SUBJECT1 = getAeWithIdsSet(RAW_EVENT6_NULL_START_DATE, SUBJECT1_ARM1);

    private static final ChartGroupByOptionsFiltered<Ae, AeGroupByOptions> EMPTY_FILTERED_SETTINGS
            = ChartGroupByOptionsFiltered.builder(SETTINGS)
            .build();

    @Autowired
    private AeChordDiagramService aeChordDiagramService;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsEventDataProvider;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;

    public static final List<Ae> EVENTS = newArrayList(EVENT1_SUBJECT1, EVENT1_2_SUBJECT1, EVENT1_3_SUBJECT1,
            EVENT2_SUBJECT1, EVENT3_SUBJECT1, EVENT4_SUBJECT1,
            EVENT1_SUBJECT2, EVENT2_SUBJECT2);

    @Test
    public void shouldGetNoDataForChordDiagramWhenOnePtOnly() {
        List<Ae> events = newArrayList(EVENT2_SUBJECT1, EVENT4_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS, Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.PT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData()).isEmpty();
    }

    @Test
    public void shouldGetNoDataForChordDiagramWhenAesIsEmpty() {
        List<Ae> events = newArrayList();

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.PT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData()).isEmpty();
    }

    @Test
    public void shouldGetNoDataForChordDiagramWhenOnlyAesWithNoStartDate() {
        List<Ae> events = newArrayList(EVENT6_SUBJECT1, EVENT1_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.PT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData()).isEmpty();
    }

    @Test
    public void shouldGetWidthChordOneForChordDiagramWhenOneDateIntersectionOfOnePatient() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT2_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0).getContributors().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("hlt1", "hlt2", 1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().get(0).getContributors().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("soc1", "soc2", 1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().get(0).getContributors().size()).isEqualTo(1);
    }

    @Test
    public void shouldSkipAesWithNoStartDateWhenGetChordDiagram() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT6_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.PT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook()).isEmpty();
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData()).isEmpty();
    }

    @Test
    public void shouldGetWidthChordTwoForChordDiagramWhenTwoDateIntersectionsOfTwoPatients() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT1_SUBJECT2, EVENT2_SUBJECT2);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS, Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 2);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("hlt1", "hlt2", 2);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("soc1", "soc2", 2);
    }

    @Test
    public void shouldGetOneChordForEveryTermWhenPercentageOfLinksIs100() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT1_SUBJECT2, EVENT2_SUBJECT2, EVENT3_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));

        Map<String, String> additionalSettings = new HashMap<String, String>() {{
            put("daysBetween", "0");
            put("percentageOfLinks", "100");
        }};

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS, additionalSettings,
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 2);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("hlt1", "hlt2", 2);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("soc1", "soc2", 2);
    }

    //due to the new requirements - if two similar events intersect by periods, they should be merged into one
    @Test
    public void shouldGetWidthChordOneForChordPlotWhenTwoSimilarEventsIntersect() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT1_2_SUBJECT1, EVENT2_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 1);
    }

    @Test
    public void shouldGetWidthChordOneForChordPlotWhenTwoSimilarEventsIntersectWithTimeframe() {
        Map<String, String> additionalSettings = new HashMap<>();
        additionalSettings.put("timeFrame", "3");
        List<Ae> events = newArrayList(EVENT1_2_SUBJECT1, EVENT1_3_SUBJECT1, IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                additionalSettings,
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt4", 1); //merged(EVENT1_2_SUBJECT1,EVENT1_3_SUBJECT1)  -> IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0).getContributors().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0).getContributors().get("E01")).isEqualTo(1);

        additionalSettings.put("timeFrame", "1");
        data = aeChordDiagramService.getAesOnChordDiagram(DATASETS, additionalSettings,
                AeFilters.empty(), PopulationFilters.empty());
        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getColorBook().size()).isEqualTo(2);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt4", 2); //EVENT1_2_SUBJECT1 -> IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1, EVENT1_3_SUBJECT1 -> IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0).getContributors().size()).isEqualTo(1);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0).getContributors().get("E01")).isEqualTo(2);
    }

    @Test
    public void shouldGetNoLinksForChordPlotWhenTwoSimilarEventsDoNotIntersectWithTimeFrame() {
        Map<String, String> timeFrames = new HashMap<>();
        timeFrames.put("timeFrame", "0");
        List<Ae> events = newArrayList(EVENT1_2_SUBJECT1, EVENT1_3_SUBJECT1, IN_THE_MIDDLE_OF_1_2_AND_1_3_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                timeFrames,
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().size()).isEqualTo(0);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().size()).isEqualTo(0);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().size()).isEqualTo(0);
    }

    @Test
    public void shouldProcessNullValuesOfTermsAsEmptyValueWhenGettingChordDiagram() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT5_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS, Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly(DEFAULT_EMPTY_VALUE, "pt1", 1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData().get(0)).extracting("start", "end", "width")
                .containsExactly(DEFAULT_EMPTY_VALUE, "soc1", 1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly(DEFAULT_EMPTY_VALUE, "hlt1", 1);
    }

    @Test
    public void shouldNotCountChordOfEqualSocTermsWhenGettingChordDiagram() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT2_1_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 1);
        softly.assertThat(data.get(Ae.TermLevel.SOC).getData()).isEmpty();
    }

    @Test
    public void shouldNotCountChordOfEqualHltTermsWhenGettingChordDiagram() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT2_1_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        Map<Ae.TermLevel, OutputChordDiagramData> data = aeChordDiagramService.getAesOnChordDiagram(DATASETS,
                Collections.emptyMap(),
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(data).isNotNull();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(Ae.TermLevel.PT).getData().get(0)).extracting("start", "end", "width")
                .containsExactly("pt1", "pt2", 1);
        softly.assertThat(data.get(Ae.TermLevel.HLT).getData()).isEmpty();
    }

    @Test
    public void testGetChordDiagramColorByOptionByPT() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT6_SUBJECT1);
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(singletonList(SUBJECT1_ARM1));

        List<TrellisOptions<AeGroupByOptions>> colorBy = aeChordDiagramService.getChordDiagramColorByOptions(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), getAeSettingsForColorby(AeGroupByOptions.PT));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy).containsExactly(AeGroupByOptions.PT);
    }

    @Test
    public void testGetChordDiagramColorByOptionBySOC() {
        List<Ae> events = newArrayList(EVENT1_SUBJECT1, EVENT6_SUBJECT1);
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(singletonList(SUBJECT1_ARM1));

        List<TrellisOptions<AeGroupByOptions>> colorBy = aeChordDiagramService.getChordDiagramColorByOptions(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), getAeSettingsForColorby(AeGroupByOptions.SOC));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy).containsExactly(AeGroupByOptions.SOC);
    }

    private ChartGroupByOptions<Ae, AeGroupByOptions> getAeSettingsForColorby(AeGroupByOptions seriesBy) {
        return ChartGroupByOptions.<Ae, AeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY,
                        seriesBy.getGroupByOptionAndParams()).build();
    }

    @Test
    public void testGetChordSelection() {

        final Map<String, String> additionalSettings = new HashMap<>();
        additionalSettings.put("termLevel", "SOC");

        ChordDiagramSelectionDetail result = getChordDiagramSelectionDetail(additionalSettings);

        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("sid1", "sid2");
        softly.assertThat(result).extracting(ChordDiagramSelectionDetail::getTotalEvents,
                ChordDiagramSelectionDetail::getTotalSubjects)
                .containsExactly(8, 2);
        softly.assertThat(result.getEventIds())
                .extracting(ChordContributor::getStartEventIds, ChordContributor::getEndEventIds)
                .containsExactlyInAnyOrder(
                        // "id4sid1" event has soc = null, but it will be returned by selection by now - easiest
                        // way to handle incorrect data
                        tuple(newHashSet("id1sid1", "id12sid1"), newHashSet("id4sid1")),
                        tuple(newHashSet("id1sid1", "id12sid1"), newHashSet("id3sid1")),
                        tuple(newHashSet("id1sid1", "id12sid1"), newHashSet("id2sid1")),
                        tuple(newHashSet("id1sid2"), newHashSet("id2sid2")));
    }

    @Test
    public void testGetChordSelectionWithLinksFiltered() {

        final Map<String, String> additionalSettings = new HashMap<>();
        additionalSettings.put("termLevel", "SOC");
        additionalSettings.put("percentageOfLinks", "100");

        ChordDiagramSelectionDetail result = getChordDiagramSelectionDetail(additionalSettings);

        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("sid1", "sid2");
        softly.assertThat(result).extracting(ChordDiagramSelectionDetail::getEventCount,
                ChordDiagramSelectionDetail::getTotalEvents,
                ChordDiagramSelectionDetail::getTotalSubjects)
                .containsExactly(6, 8, 2);
        softly.assertThat(result.getEventIds())
                .extracting(ChordContributor::getStartEventIds, ChordContributor::getEndEventIds)
                .containsExactlyInAnyOrder(tuple(newHashSet("id1sid1", "id12sid1"), newHashSet("id4sid1")),
                        tuple(newHashSet("id1sid1", "id12sid1"), newHashSet("id2sid1")),
                        tuple(newHashSet("id1sid2"), newHashSet("id2sid2")));
    }

    @Test
    public void testGetBoxPlotSelectionWith10DaysBetweenEvents() {

        final Map<String, String> additionalSettings = new HashMap<>();
        additionalSettings.put("termLevel", "SOC");
        additionalSettings.put("timeFrame", "10");

        ChordDiagramSelectionDetail result = getChordDiagramSelectionDetail(additionalSettings);

        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("sid1", "sid2");
        softly.assertThat(result).extracting(ChordDiagramSelectionDetail::getEventCount,
                ChordDiagramSelectionDetail::getTotalEvents,
                ChordDiagramSelectionDetail::getTotalSubjects)
                .containsExactly(8, 8, 2);
        softly.assertThat(result.getEventIds())
                .extracting(ChordContributor::getStartEventIds, ChordContributor::getEndEventIds)
                .containsExactlyInAnyOrder(
                        tuple(newHashSet("id1sid1", "id12sid1", "id13sid1"), newHashSet("id2sid1", "id4sid1")),
                        tuple(newHashSet("id1sid1", "id12sid1", "id13sid1"), newHashSet("id3sid1")),
                        tuple(newHashSet("id1sid2"), newHashSet("id2sid2")));
    }

    private ChordDiagramSelectionDetail getChordDiagramSelectionDetail(Map<String, String> additionalSettings) {
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));

        final ChartGroupByOptions<ChordCalculationObject, ChordGroupByOptions> settings = ChartGroupByOptions
                .<ChordCalculationObject, ChordGroupByOptions>builder()
                .withOption(START, ChordGroupByOptions.START.getGroupByOptionAndParams())
                .withOption(END, ChordGroupByOptions.END.getGroupByOptionAndParams())
                .build();

        final HashMap<ChordGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(START, "soc1");
        selectedItem1.put(END, "soc2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(START, "soc1");
        selectedItem2.put(END, "soc3");

        return aeChordDiagramService.getChordDiagramSelectionDetails(
                DATASETS, AeFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, Arrays.asList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2))),
                additionalSettings);
    }

    @Test
    public void testGetChordDetailsOnDemandData() {

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));

        final HashSet<ChordContributor> chordContributors = newHashSet(new ChordContributor(newHashSet("id1sid1", "id12sid1", "id13sid1"), newHashSet("id2sid1", "id4sid1")),
                new ChordContributor(newHashSet("id1sid1", "id12sid1", "id13sid1"), newHashSet("id3sid1")),
                new ChordContributor(newHashSet("id1sid2"), newHashSet("id2sid2")));
        List<Map<String, String>> doDData = aeChordDiagramService.getChordDetailsOnDemandData(DATASETS,
                chordContributors,
                Collections.emptyList(), 0, Integer.MAX_VALUE);
        softly.assertThat(doDData).hasSize(3);
        Map<String, String> dod = doDData.get(2); // row for subject E02 will be the last
        softly.assertThat(doDData).hasSize(chordContributors.size());
        softly.assertThat(dod.size()).isEqualTo(20);
        softly.assertThat(dod.get("studyId")).isEqualTo(SUBJECT2_ARM2.getClinicalStudyCode());
        softly.assertThat(dod.get("studyPart")).isEqualTo(SUBJECT2_ARM2.getStudyPart());
        softly.assertThat(dod.get("subjectId")).isEqualTo(SUBJECT2_ARM2.getSubjectCode());
        softly.assertThat(dod.get("ptLinks")).isEqualTo("pt1 (A) to pt2 (B)");
        softly.assertThat(dod.get("hltLinks")).isEqualTo("hlt1 (A) to hlt2 (B)");
        softly.assertThat(dod.get("socLinks")).isEqualTo("soc1 (A) to soc2 (B)");
        softly.assertThat(dod.get("seriousA")).isEqualTo("No");
        softly.assertThat(dod.get("seriousB")).isEqualTo("Yes");
        softly.assertThat(dod.get("maxSeverityA")).isEqualTo("CTC Grade 2");
        softly.assertThat(dod.get("maxSeverityB")).isEqualTo("CTC Grade 1");
        softly.assertThat(DaysUtil.toDate("2015-08-01T03:00:00")).isInSameDayAs(dod.get("startDateA"));
        softly.assertThat(DaysUtil.toDate("2015-08-05T03:00:00")).isInSameDayAs(dod.get("endDateA"));
        softly.assertThat(dod.get("daysOnStudyAtStartA")).isEqualTo("0");
        softly.assertThat(dod.get("daysOnStudyAtEndA")).isEqualTo("4");
        softly.assertThat(DaysUtil.toDate("2015-08-03T03:00:00")).isInSameDayAs(dod.get("startDateB"));
        softly.assertThat(DaysUtil.toDate("2015-08-04T03:00:00")).isInSameDayAs(dod.get("endDateB"));
        softly.assertThat(dod.get("daysOnStudyAtStartB")).isEqualTo("2");
        softly.assertThat(dod.get("daysOnStudyAtEndB")).isEqualTo("3");
        softly.assertThat(dod.get("causalityA")).isEqualTo("drug1: Yes");
        softly.assertThat(dod.get("causalityB")).isEmpty();
    }

    @Test
    public void testGetDoDColumns() {

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));

        Map<String, String> doDColumns = aeChordDiagramService.getDoDColumns(DATASETS, SETTINGS_WITH_MAX_TIME_FRAME);

        softly.assertThat(doDColumns.keySet()).containsExactly(
                "studyId",
                "studyPart",
                "subjectId",
                "ptLinks",
                "hltLinks",
                "socLinks",
                "seriousA",
                "seriousB",
                "maxSeverityA",
                "maxSeverityB",
                "startDateA",
                "endDateA",
                "daysOnStudyAtStartA",
                "daysOnStudyAtEndA",
                "startDateB",
                "endDateB",
                "daysOnStudyAtStartB",
                "daysOnStudyAtEndB",
                "causalityA");
        softly.assertThat(doDColumns.values()).containsExactly(
                "Study id",
                "Study part",
                "Subject id",
                "Preferred term links",
                "High level term links",
                "System organ class links",
                "Serious (A)",
                "Serious (B)",
                "Max severity (A)",
                "Max severity (B)",
                "Start date (A)",
                "End date (A)",
                "Days on study at ae start (A)",
                "Days on study at ae end (A)",
                "Start date (B)",
                "End date (B)",
                "Days on study at ae start (B)",
                "Days on study at ae end (B)",
                "Causality (A)");
    }

    @Test
    public void testGetDoDColumnsNoEvents() {

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.emptyList());
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));
        Map<String, String> doDColumns = aeChordDiagramService.getDoDColumns(DATASETS, SETTINGS_WITH_MAX_TIME_FRAME);
        softly.assertThat(doDColumns).isEmpty();
    }

    @Test
    public void testGetDoDColumnsNoChords() {

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2));
        Map<String, String> doDColumns = aeChordDiagramService.getDoDColumns(DATASETS, SETTINGS_WITH_MAX_TIME_FRAME);
        softly.assertThat(doDColumns).isEmpty();
    }

    // set event's subjectId the same as subjects-id;
    // set event's id as a concatenation of event's id and subject's id to make ids unique
    private static Ae getAeWithIdsSet(AeRaw aeRaw, Subject subject) {
        return new Ae(aeRaw.toBuilder()
                .id(aeRaw.getId() + subject.getId())
                .subjectId(subject.getSubjectId()).build(), subject);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfTimeFrameMoreThanThirty() {
        Map<String, String> additionalSettings = new HashMap<>();
        additionalSettings.put("timeFrame", "35");
        List<Ae> events = newArrayList(EVENT1_2_SUBJECT1, EVENT1_3_SUBJECT1);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        aeChordDiagramService.getAesOnChordDiagram(DATASETS, additionalSettings,
                AeFilters.empty(), PopulationFilters.empty());
    }
}
