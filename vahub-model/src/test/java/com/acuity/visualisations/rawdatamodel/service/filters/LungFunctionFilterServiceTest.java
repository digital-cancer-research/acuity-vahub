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

import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class LungFunctionFilterServiceTest {

    @Autowired
    private LungFunctionFilterService lungFunctionFilterService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final Subject subject1 = Subject.builder()
            .subjectId("E001")
            .firstTreatmentDate(toDate("11.11.2011"))
            .build();

    private final Subject subject2 = Subject.builder()
            .subjectId("E002")
            .firstTreatmentDate(toDate("12.12.2012"))
            .build();

    private final LungFunction lung1 = new LungFunction(LungFunctionRaw.builder()
            .visit(12.0)
            .value(1.23)
            .measurementNameRaw("FEV1P (%)")
            .measurementTimePoint(toDate("01.02.2013"))
            .baselineValue(1.0)
            .baselineFlag("Y")
            .build().runPrecalculations(), subject1);

    private final LungFunction lung2 = new LungFunction(LungFunctionRaw.builder()
            .visit(11.0)
            .value(2.34)
            .measurementNameRaw("FEV1R (L)")
            .measurementTimePoint(toDate("05.06.2014"))
            .baselineValue(2.0)
            .build().runPrecalculations(), subject2);

    private final List<LungFunction> lungFunctions = Arrays.asList(lung1, lung2);

    @Test
    public void shouldGetAvailableFilters() {
        FilterQuery<LungFunction> filterQuery = new FilterQuery<>(
                Arrays.asList(lung1, lung2), LungFunctionFilters.empty(),
                Arrays.asList(subject1, subject2), PopulationFilters.empty()
        );

        LungFunctionFilters result = (LungFunctionFilters) lungFunctionFilterService.getAvailableFilters(filterQuery);

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);

        softly.assertThat(result.getMeasurementName().getValues()).containsOnly("FEV1P", "FEV1R");

        softly.assertThat(result.getMeasurementTimePoint().getFrom()).isInSameDayAs("2013-02-01");

        softly.assertThat(ZonedDateTime.ofInstant(result.getMeasurementTimePoint().getTo().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDate("05.06.2014").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(result.getDaysOnStudy().getFrom()).isEqualTo(448);
        softly.assertThat(result.getDaysOnStudy().getTo()).isEqualTo(540);

        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(11);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(12);

        softly.assertThat(result.getResultValue().getFrom()).isEqualTo(1.23);
        softly.assertThat(result.getResultValue().getTo()).isEqualTo(2.34);

        softly.assertThat(result.getResultUnit().getValues()).containsOnly("%", "L");

        softly.assertThat(result.getChangeFromBaseline().getFrom()).isEqualTo(0.23);
        softly.assertThat(result.getChangeFromBaseline().getTo()).isEqualTo(0.34);

        softly.assertThat(result.getPercentChangeFromBaseline().getFrom()).isEqualTo(17);
        softly.assertThat(result.getPercentChangeFromBaseline().getTo()).isEqualTo(23);

        softly.assertThat(result.getBaselineFlag().getValues()).containsOnly("Y", null);
        softly.assertThat(result.getBaselineFlag().getIncludeEmptyValues()).isTrue();

    }

    @Test
    public void shouldFilterByVisitNumber() {
        LungFunctionFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(11.0, 11.1)));
        LungFunctionFilters result2 = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(11.0, 12.0)));
        LungFunctionFilters result3 = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(13.0, 14.0)));

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
        softly.assertThat(result2.getMatchedItemsCount()).isEqualTo(2);
        softly.assertThat(result3.getMatchedItemsCount()).isEqualTo(0);
    }

    private LungFunctionFilters givenFilterSetup(Consumer<LungFunctionFilters> filterSetter) {
        List<Subject> subjects = lungFunctions.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        LungFunctionFilters lungFunctionFilters = new LungFunctionFilters();
        filterSetter.accept(lungFunctionFilters);
        return (LungFunctionFilters) lungFunctionFilterService.getAvailableFilters(lungFunctions, lungFunctionFilters, subjects, PopulationFilters.empty());
    }
}
