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

import com.acuity.visualisations.rawdatamodel.dataproviders.NicotineDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.*;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class NicotineFilterServiceTest {

    @Autowired
    private NicotineFilterService nicotineFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private NicotineDatasetsDataProvider nicotineDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String CATEGORY = "category_1";
    private static final String TYPE = "type_1";
    private static final String OTHER_TYPE_SPEC = "other_type_spec_1";
    private static final String USE_OCCURRENCE = "use_occurrence_1";
    private static final String CURRENT_USE_SPEC = "scurrent_use_spec_1";
    private static final String SUB_TYPE_USE_OCCURRENCE = "sub_type_use_occurrence_1";
    private static final Date START_DATE = toDate("02.01.2000");
    private static final Date END_DATE = toDate("02.01.2001");
    private static final Integer CONSUMPTION = 10;
    private static final String FREQUENCY_INTERVAL = "frequency_2";
    private static final Integer NUMBER_PACK_YEARS = 7;

    private static final Subject SUBJ_1 = Subject.builder()
            .subjectId("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(toDate("01.01.2000"))
            .build();

    private static final Nicotine NICOTINE_1 = new Nicotine(
            NicotineRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .category(CATEGORY)
                    .type(TYPE)
                    .currentUseSpec(CURRENT_USE_SPEC)
                    .otherTypeSpec(OTHER_TYPE_SPEC)
                    .subTypeUseOccurrence(SUB_TYPE_USE_OCCURRENCE)
                    .useOccurrence(USE_OCCURRENCE)
                    .startDate(new Date())
                    .endDate(new Date())
                    .consumption(100)
                    .frequencyInterval("frequency_1")
                    .numberPackYears(1)
                    .build(), SUBJ_1);

    private static final Nicotine NICOTINE_2 = new Nicotine(
            NicotineRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .category("category_2")
                    .type("type_2")
                    .currentUseSpec("current_use_spec_2")
                    .otherTypeSpec("other_type_spec_2")
                    .subTypeUseOccurrence("sub_type_use_occurrence_2")
                    .useOccurrence("use_occurrence_2")
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .consumption(CONSUMPTION)
                    .frequencyInterval(FREQUENCY_INTERVAL)
                    .numberPackYears(NUMBER_PACK_YEARS)
                    .build(), SUBJ_1);

    private static final Nicotine NICOTINE_3 = new Nicotine(
            NicotineRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .build(), SUBJ_1);

    private static final List<Nicotine> NICOTINE_LIST =
            Arrays.asList(NICOTINE_1, NICOTINE_2, NICOTINE_3);

    @Test
    public void shouldFilterByType() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setType(
                new SetFilter<>(Collections.singletonList(TYPE))));

        softly.assertThat(result.getType().getValues()).containsOnly(TYPE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByOtherTypeSpec() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setOtherTypeSpec(
                new SetFilter<>(Collections.singletonList(OTHER_TYPE_SPEC))));

        softly.assertThat(result.getOtherTypeSpec().getValues()).containsOnly(OTHER_TYPE_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByUseOccurrence() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setUseOccurrence(
                new SetFilter<>(Collections.singletonList(USE_OCCURRENCE))));

        softly.assertThat(result.getUseOccurrence().getValues()).containsOnly(USE_OCCURRENCE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCurrentUseSpec() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setCurrentUseSpec(
                new SetFilter<>(Collections.singletonList(CURRENT_USE_SPEC))));

        softly.assertThat(result.getCurrentUseSpec().getValues()).containsOnly(CURRENT_USE_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubTypeUseOccurrence() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setSubTypeUseOccurrence(
                new SetFilter<>(Collections.singletonList(SUB_TYPE_USE_OCCURRENCE))));

        softly.assertThat(result.getSubTypeUseOccurrence().getValues()).containsOnly(SUB_TYPE_USE_OCCURRENCE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCategory() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setCategory(
                new SetFilter<>(Collections.singletonList(CATEGORY))));

        softly.assertThat(result.getCategory().getValues()).containsOnly(CATEGORY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByConsumption() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setConsumption(
                new RangeFilter<>(CONSUMPTION, CONSUMPTION)));

        softly.assertThat(result.getConsumption().getFrom()).isEqualTo(CONSUMPTION);
        softly.assertThat(result.getConsumption().getTo()).isEqualTo(CONSUMPTION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByFrequencyInterval() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setFrequencyInterval(
                new SetFilter<>(Collections.singletonList(FREQUENCY_INTERVAL))));

        softly.assertThat(result.getFrequencyInterval().getValues()).containsOnly(FREQUENCY_INTERVAL);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByNumberPackYears() {
        NicotineFilters result = givenFilterSetup(filters -> filters.setNumberPackYears(
                new RangeFilter<>(NUMBER_PACK_YEARS, NUMBER_PACK_YEARS)));

        softly.assertThat(result.getNumberPackYears().getFrom()).isEqualTo(NUMBER_PACK_YEARS);
        softly.assertThat(result.getNumberPackYears().getTo()).isEqualTo(NUMBER_PACK_YEARS);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private NicotineFilters givenFilterSetup(final Consumer<NicotineFilters> filterSetter) {
        List<Nicotine> events = NICOTINE_LIST;
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        NicotineFilters nicotineFilters = new NicotineFilters();
        filterSetter.accept(nicotineFilters);
        return (NicotineFilters) nicotineFilterService
                .getAvailableFilters(events, nicotineFilters, subjects, PopulationFilters.empty());
    }
}
