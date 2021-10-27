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

import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedScatterPlot;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Category(LabTests.class)
public class LiverServiceTest {

    @Autowired
    private LiverService liverService;
    @Autowired
    private DoDCommonService doDCommonService;

    @MockBean
    private LiverDatasetsDataProvider liverDatasetsDataProvider;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final Subject subject1 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
            .withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
            .subjectId("subject1").subjectCode("subject1").firstTreatmentDate(toDate("31.05.2015"))
            .studyLeaveDate(toDate("10.09.2017")).build();
    private final Subject subject2 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
            .withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
            .subjectId("subject2").subjectCode("subject2").firstTreatmentDate(toDate("01.06.2015")).studyPart("A")
            .studyLeaveDate(toDate("10.09.2017")).build();

    private Liver liver1 = new Liver(LiverRaw.builder()
            .id("liver1")
            .labCode("l1")
            .normalizedLabCode("ALT")
            .measurementTimePoint(toDate("10.09.2017"))
            .daysSinceFirstDose(1)
            .visitNumber(1.1)
            .value(1.2)
            .unit("mg/ml")
            .baseline(1.3)
            .baselineFlag("Y")
            .refLow(0.5)
            .refHigh(1.5)
            .build().runPrecalculations(), subject1);


    private Liver liver2 = new Liver(LiverRaw.builder()
            .id("liver2")
            .labCode("l2")
            .normalizedLabCode("ALT")
            .build().runPrecalculations(), subject2);

    @Test
    public void shouldGetAcuityDetailsOnDemandData() {
        List<Liver> events = Arrays.asList(liver1, liver2);
        when(liverDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        final Set<String> liverIdsList = events.stream().map(Liver::getId).collect(toSet());
        final List<Map<String, String>> detailsOnDemandData = liverService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, liverIdsList, Collections.emptyList(), 0, Integer.MAX_VALUE);
        final Map<String, String> dod = detailsOnDemandData.get(0);
        softly.assertThat(detailsOnDemandData).hasSize(events.size());
        softly.assertThat(dod.size()).isEqualTo(19);
        softly.assertThat(liver1.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(liver1.getSubject().getStudyPart()).isEqualTo(dod.get("studyPart"));
        softly.assertThat(liver1.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(liver1.getEvent().getId()).isEqualTo(dod.get("eventId"));
        softly.assertThat(liver1.getEvent().getVisitNumber().toString()).isEqualTo(dod.get("visitNumber"));
        softly.assertThat(liver1.getEvent().getReferenceRangeNormalisedValue().toString()).isEqualTo(dod.get("refRangeNormValue"));
        softly.assertThat(liver1.getEvent().getBaselineFlag()).isEqualTo(dod.get("baselineFlag"));
        softly.assertThat(liver1.getEvent().getChangeFromBaseline().toString()).isEqualTo(dod.get("changeFromBaseline"));
        softly.assertThat(liver1.getEvent().getTimesLowerReferenceRange().toString()).isEqualTo(dod.get("timesLowerRefValue"));
        softly.assertThat(liver1.getEvent().getUnit()).isEqualTo(dod.get("resultUnit"));
        softly.assertThat(liver1.getEvent().getLabCode()).isEqualTo(dod.get("measurementName"));
        softly.assertThat(liver1.getEvent().getRefHigh().toString()).isEqualTo(dod.get("upperRefRangeValue"));
        softly.assertThat(liver1.getEvent().getBaselineValue().toString()).isEqualTo(dod.get("baselineValue"));
        softly.assertThat(liver1.getEvent().getPercentChangeFromBaseline().toString()).isEqualTo(dod.get("percentChangeFromBaseline"));
        softly.assertThat(liver1.getEvent().getRefLow().toString()).isEqualTo(dod.get("lowerRefRangeValue"));

    }

    @Test
    public void shouldGetAvailableFilters() {
        List<Liver> events = Arrays.asList(liver1, liver2);
        when(liverDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        LiverFilters result = (LiverFilters) liverService.getAvailableFilters(
                DUMMY_ACUITY_DATASETS, LiverFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result.getBaselineFlag().getValues()).containsExactlyInAnyOrder(null, "Y");
    }

    @Test
    public void shouldGetScatterPlotData() {
        List<Map<LiverGroupByOptions, Object>> filterByTrellisOptions = Arrays.asList(
                ImmutableMap.of(MEASUREMENT, "AST"),
                ImmutableMap.of(MEASUREMENT, "ALT")
        );

        Subject subject1 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
                .subjectId("subject1").subjectCode("subject1").actualArm("Placebo").build();

        Subject subject2 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
                .subjectId("subject2").subjectCode("subject2").actualArm("SuperDex 20 mg").build();

        List<Liver> events = Arrays.asList(
                new Liver(LiverRaw.builder().id("liver1").labCode("l1").normalizedLabCode("AST")
                        .value(12d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver2").labCode("l2").normalizedLabCode("AST")
                        .value(11d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver3").labCode("l3").normalizedLabCode("XXX")
                        .value(13d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver4").labCode("l4").normalizedLabCode("BILI")
                        .value(14d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver5").labCode("l5").normalizedLabCode("BILI")
                        .value(101d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject2),
                new Liver(LiverRaw.builder().id("liver6").labCode("l6").normalizedLabCode("ALT")
                        .value(55d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject2)
        );

        when(liverDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        List<TrellisedScatterPlot<Liver, LiverGroupByOptions>> result =
                liverService.getPlotValues(DUMMY_ACUITY_DATASETS, filterByTrellisOptions,
                        LiverFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result).isNotNull();
        softly.assertThat(result).hasSize(2);
        softly.assertThat(result.get(0).getData().get(0).getX()).isEqualTo(14);
        softly.assertThat(result.get(0).getData().get(0).getY()).isEqualTo(12);
        softly.assertThat(result.get(0).getData().get(0).getName()).isEqualTo("subject1");
        softly.assertThat(result.get(1).getData().get(0).getX()).isEqualTo(101);
        softly.assertThat(result.get(1).getData().get(0).getY()).isEqualTo(55);
        softly.assertThat(result.get(1).getData().get(0).getName()).isEqualTo("subject2");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        List<Liver> events = Arrays.asList(liver1, liver2);

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, events);

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId",
                        "measurementName", "measurementTimePoint",
                        "daysOnStudy", "visitNumber", "resultValue", "resultUnit",
                        "baselineValue", "changeFromBaseline", "percentChangeFromBaseline", "baselineFlag",
                        "refRangeNormValue", "timesUpperRefValue", "timesLowerRefValue",
                        "lowerRefRangeValue", "upperRefRangeValue");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id",
                        "Measurement name", "Measurement time point",
                        "Days on study", "Visit number", "Result value", "Result unit",
                        "Baseline value", "Change from baseline", "Percent change from baseline", "Baseline flag",
                        "Ref range norm value", "Times upper ref value", "Times lower ref value",
                        "Lower ref range value", "Upper ref range value");
    }
}
