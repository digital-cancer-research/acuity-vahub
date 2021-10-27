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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.dataproviders.LungFunctionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
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
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LungFunctionTimelineServiceTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private LungFunctionTimelineService lungFunctionTimelineService;

    @MockBean
    private LungFunctionDatasetsDataProvider lungFunctionDatasetsDataProvider;

    @MockBean
    private InfoService mockInfoService;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    private static final Subject SUBJECT_1 = Subject.builder()
            .subjectId("sid1")
            .clinicalStudyCode("STUDYID001")
            .studyPart("A")
            .subjectCode("E01")
            .actualArm("Placebo")
            .drugFirstDoseDate("Placebo", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-02"))
            .dateOfRandomisation(toDate("2015-01-02"))
            .build();

    private static final LungFunction LUNG_1 = new LungFunction(LungFunctionRaw
            .builder().id("1").subjectId("sid1")
            .measurementTimePoint(toDate("2015-02-02"))
            .value(12.34)
            .build(), SUBJECT_1);

    private static final LungFunction LUNG_2 = new LungFunction(LungFunctionRaw
            .builder().id("1").subjectId("sid2")
            .measurementTimePoint(toDate("2015-02-03"))
            .value(23.45)
            .build(), SUBJECT_1);

    @Test
    public void shouldWorkWithNullVisits() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1));
        when(lungFunctionDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(LUNG_1, LUNG_2));

        List<SubjectLungFunctionSummary> result = lungFunctionTimelineService.getLungFunctionSummaries(DATASETS,
                LungFunctionFilters.empty(), PopulationFilters.empty(),
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getEvents()).hasSize(2);

        LungFunctionSummaryEvent lungFunctionSummaryEvent = result.get(0).getEvents().get(0);
        softly.assertThat(lungFunctionSummaryEvent.getStart()).isNotNull();
        softly.assertThat(lungFunctionSummaryEvent.getVisitNumber()).isNull();

    }

}
