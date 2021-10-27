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

import com.acuity.visualisations.rawdatamodel.dataproviders.AlcoholDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class AlcoholFilterServiceTest {
    @Autowired
    private AlcoholFilterService alcoholFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AlcoholDatasetsDataProvider alcoholDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String SUBSTANCE_CATEGORY = "category1";
    private static final String SUBSTANCE_USE_OCCURRENCE = "1";
    private static final String SUBSTANCE_TYPE = "2";
    private static final String OTHER_SUBSTANCE_TYPE_SPEC = "Malt";
    private static final String FREQUENCY = "Every week";
    private static final String SUBSTANCE_TYPE_USE_OCCURRENCE = "1";
    private static final Double SUBSTANCE_CONSUMPTION = 2000.;
    private static final Date START_DATE = DateUtils.toDateTime("03.08.2015 00:00");
    private static final Date END_DATE = DateUtils.toDateTime("08.08.2015 00:00");

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02")
            .firstTreatmentDate(DateUtils.toDate("05.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("07.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("19.08.2016")).build();

    private Alcohol ALCOHOL1 = new Alcohol(AlcoholRaw.builder().id("id1").substanceCategory(SUBSTANCE_CATEGORY).substanceUseOccurrence("2")
            .substanceType(SUBSTANCE_TYPE).otherSubstanceTypeSpec("Beer").frequency(FREQUENCY).substanceTypeUseOccurrence(SUBSTANCE_TYPE_USE_OCCURRENCE)
            .substanceConsumption(1000.).startDate(DateUtils.toDate("09.08.2015")).endDate(DateUtils.toDate("19.08.2015")).build(), SUBJECT1);
    private Alcohol ALCOHOL2 = new Alcohol(AlcoholRaw.builder().id("id2").substanceCategory("category2")
            .substanceUseOccurrence(SUBSTANCE_USE_OCCURRENCE).substanceType("1").otherSubstanceTypeSpec(OTHER_SUBSTANCE_TYPE_SPEC)
            .frequency("Every month").substanceTypeUseOccurrence("2").substanceConsumption(SUBSTANCE_CONSUMPTION)
            .startDate(START_DATE).endDate(END_DATE).build(), SUBJECT2);

    private List<Alcohol> ALCOHOLS = Arrays.asList(ALCOHOL1, ALCOHOL2);

    private AlcoholFilters givenFilterSetup(final Consumer<AlcoholFilters> filterSetter) {
        final List<Subject> subjects = ALCOHOLS.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        AlcoholFilters alcoholFilters = new AlcoholFilters();
        filterSetter.accept(alcoholFilters);
        return (AlcoholFilters) alcoholFilterService.getAvailableFilters(ALCOHOLS, alcoholFilters, subjects, PopulationFilters.empty());
    }

    @Test
    public void shouldFilterBySubstanceCategory() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setSubstanceCategory(
                new SetFilter<String>(Collections.singleton(SUBSTANCE_CATEGORY))));

        softly.assertThat(result.getSubstanceCategory().getValues()).containsOnly(SUBSTANCE_CATEGORY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubstanceUseOccurence() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setSubstanceUseOccurrence(
                new SetFilter<String>(Collections.singleton(SUBSTANCE_USE_OCCURRENCE))));

        softly.assertThat(result.getSubstanceUseOccurrence().getValues()).containsOnly(SUBSTANCE_USE_OCCURRENCE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubstanceType() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setSubstanceType(
                new SetFilter<String>(Collections.singleton(SUBSTANCE_TYPE))));

        softly.assertThat(result.getSubstanceType().getValues()).containsOnly(SUBSTANCE_TYPE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByOtherSubstanceTypeSpec() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setOtherSubstanceTypeSpec(
                new SetFilter<String>(Collections.singleton(OTHER_SUBSTANCE_TYPE_SPEC))));

        softly.assertThat(result.getOtherSubstanceTypeSpec().getValues()).containsOnly(OTHER_SUBSTANCE_TYPE_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByFrequency() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setFrequency(
                new SetFilter<String>(Collections.singleton(FREQUENCY))));

        softly.assertThat(result.getFrequency().getValues()).containsOnly(FREQUENCY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubstanceTypeUseOccurrence() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setSubstanceTypeUseOccurrence(
                new SetFilter<String>(Collections.singleton(SUBSTANCE_TYPE_USE_OCCURRENCE))));

        softly.assertThat(result.getSubstanceTypeUseOccurrence().getValues()).containsOnly(SUBSTANCE_TYPE_USE_OCCURRENCE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubstanceConsumption() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setSubstanceConsumption(
                new RangeFilter<Double>(SUBSTANCE_CONSUMPTION, SUBSTANCE_CONSUMPTION)));

        softly.assertThat(result.getSubstanceConsumption().getFrom()).isEqualTo(SUBSTANCE_CONSUMPTION);
        softly.assertThat(result.getSubstanceConsumption().getTo()).isEqualTo(SUBSTANCE_CONSUMPTION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo())
                .isInSameDayAs(org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        AlcoholFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isEqualTo(END_DATE);
        softly.assertThat(result.getEndDate().getTo())
                .isInSameDayAs(org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }
}
