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

import com.acuity.visualisations.config.util.TestConstants;
import com.acuity.visualisations.rawdatamodel.dataproviders.CvotEndpointDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CvotEndpointOverTimePerformanceTest {


    @Autowired
    private CvotEndpointService cvotEndpointService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CvotEndpointDatasetsDataProvider cvotEndpointDatasetsDataProvider;
    @Autowired
    private MockHttpServletRequest request;


    @Test
    @Ignore("Supposed to run manually when needed to do profiling")
    public void test() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        final List<CvotEndpoint> mocked = generateRandomCIEventList(1000000, 500, 20, 2014, 2015);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(mocked);
        //Thread.sleep(10000);
        stopWatch.start("overtime calc");


        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
//        settings.withOption(COLOR_BY, new ChartGroupByOptions.GroupByOptionAndParams<>(CvotEndpointGroupByOptions.EVENT_TYPE));
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 5)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));



        List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> overtime = cvotEndpointService.getLineBarChart(TestConstants.DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), CvotEndpointFilters.empty(), PopulationFilters.empty());

        stopWatch.stop();
        System.out.println(overtime);
        System.out.println(stopWatch.prettyPrint());
        //System.out.println(Bin.cnt.get());

    }

    public static List<CvotEndpoint> generateRandomCIEventList(long count, long subjectCount, int categoriesDictSize,
                                                               int startDateYearFrom, int startDateYearTo) {

        final Random random = new Random();

        List<List<String>> cat = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> dict = new ArrayList<>();
            for (int j = 0; j < categoriesDictSize; j++) {
                dict.add(UUID.randomUUID().toString());
            }
            cat.add(dict);
        }
        final ArrayList<Subject> subjects = new ArrayList<>();
        for (long i = 0; i < subjectCount; i++) {
            subjects.add(Subject.builder()
                    .subjectId(UUID.randomUUID().toString())
                    .subjectCode(UUID.randomUUID().toString())
                    .clinicalStudyCode(UUID.randomUUID().toString())
                    .studyPart(UUID.randomUUID().toString())
                    .datasetId(UUID.randomUUID().toString())
                    .attendedAnalysisVisits(UUID.randomUUID().toString())
                    .durationOnStudy(random.nextInt())
                    .randomised(UUID.randomUUID().toString())
                    .dateOfRandomisation(new Date(random.nextInt()))
                    .firstTreatmentDate(new Date(random.nextInt()))
                    .lastTreatmentDate(new Date(random.nextInt()))
                    .withdrawal(UUID.randomUUID().toString())
                    .dateOfWithdrawal(new Date(random.nextInt()))
                    .reasonForWithdrawal(UUID.randomUUID().toString())
                    .deathFlag(UUID.randomUUID().toString())
                    .phase(UUID.randomUUID().toString())
                    .dateOfDeath(new Date(random.nextInt()))
                    .plannedArm(UUID.randomUUID().toString())
                    .actualArm(UUID.randomUUID().toString())
                    .doseCohort(UUID.randomUUID().toString())
                    .otherCohort(UUID.randomUUID().toString())
                    .sex(UUID.randomUUID().toString())
                    .race(UUID.randomUUID().toString())
                    .ethnicGroup(UUID.randomUUID().toString())
                    .age(random.nextInt())
                    .siteId(UUID.randomUUID().toString())
                    .region(UUID.randomUUID().toString())
                    .country(UUID.randomUUID().toString())
                    .specifiedEthnicGroup(UUID.randomUUID().toString())
                    .medicalHistories(Sets.newHashSet(UUID.randomUUID().toString()))
                    .build());
        }

        final ArrayList<CvotEndpoint> res = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            Subject subject = subjects.get(random.nextInt(subjects.size()));

            final int from = (int) LocalDate.of(startDateYearFrom, 1, 1).toEpochDay();
            final int to = (int) LocalDate.of(startDateYearTo, 12, 31).toEpochDay();
            long randomDay = from + random.nextInt(to - from);
            final LocalDate start = LocalDate.ofEpochDay(randomDay);

            res.add(new CvotEndpoint(CvotEndpointRaw.builder()
                    .id(UUID.randomUUID().toString())
                    .subjectId(subject.getSubjectId())
                    .aeNumber(random.nextInt())
                    .startDate(Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .term(UUID.randomUUID().toString())
                    .category1(cat.get(0).get(random.nextInt(categoriesDictSize)))
                    .category2(cat.get(1).get(random.nextInt(categoriesDictSize)))
                    .category3(cat.get(2).get(random.nextInt(categoriesDictSize)))
                    .description1(cat.get(3).get(random.nextInt(categoriesDictSize)))
                    .description2(cat.get(4).get(random.nextInt(categoriesDictSize)))
                    .description3(cat.get(5).get(random.nextInt(categoriesDictSize)))
                    .build(),
                    subject
                    ));
        }
        return res;
    }
}
