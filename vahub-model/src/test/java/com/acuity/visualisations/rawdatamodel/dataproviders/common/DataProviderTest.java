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

package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.CvotEndpointGenerator;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET_42;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
@TestPropertySource(properties = "env.name=mock")
public class DataProviderTest extends DataProviderAwareTest {
    public static final List<Subject> SUBJECTS = generatePatientList();
    private static final Logger LOG = LoggerFactory.getLogger(DataProviderTest.class);
    private static final int N_READ_THREADS = 1000;
    private static final int N_DELETE_THREADS = 100;
    private static final int MAX_DELAY = 100;
    private ExecutorService executors = Executors.newFixedThreadPool(100);
    private Random rand = new Random();

    private static List<Subject> generatePatientList() {
        Subject subject = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET_42.getId()))
                .subjectId("sid1").age(60).build();
        return Collections.singletonList(subject);
    }

    @Test
    public void testGetDataDuringClearAllCacheFiles() throws Exception {


        List<Callable<Integer>> tasks = IntStream.range(0, N_DELETE_THREADS).mapToObj(n -> (Callable<Integer>) () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(rand.nextInt(MAX_DELAY));
                    } catch (InterruptedException ignored) {
                        // do nothing
                    }
                    dataProvider.clearAllCacheFiles();
                    return 1;
                }
        ).collect(Collectors.toList());

        executeMixedWithGetData(tasks, DUMMY_ACUITY_DATASET_42);
    }

    @Test
    public void testGetDataDuringClearCacheForAcuity() throws Exception {
        List<Callable<Integer>> tasks = IntStream.range(0, N_DELETE_THREADS).mapToObj(n -> (Callable<Integer>) () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(rand.nextInt(MAX_DELAY));
                    } catch (InterruptedException ignored) {
                        // do nothing
                    }
                    dataProvider.clearCacheForAcuity();
                    return 1;
                }
        ).collect(Collectors.toList());

        executeMixedWithGetData(tasks, DUMMY_ACUITY_DATASET_42);
    }

    @Test
    public void testGetDataDuringClearCacheForDetect() throws Exception {
        List<Callable<Integer>> tasks = IntStream.range(0, N_DELETE_THREADS).mapToObj(n -> (Callable<Integer>) () -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(rand.nextInt(MAX_DELAY));
                    } catch (InterruptedException ignored) {
                        // do nothing
                    }
                    dataProvider.clearCacheForDetect();
                    return 1;
                }
        ).collect(Collectors.toList());

        executeMixedWithGetData(tasks, DUMMY_ACUITY_DATASET_42);
    }

    @Test
    public void testGetDataDuringClearCacheForDataset() throws Exception {

        List<Callable<Integer>> tasks = IntStream.range(0, N_DELETE_THREADS).mapToObj(n -> (Callable<Integer>) () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(MAX_DELAY));
            } catch (InterruptedException ignored) {
            }
            dataProvider.clearCacheForDataset(DUMMY_ACUITY_DATASET_42);
            return 1;
        }).collect(Collectors.toList());

        executeMixedWithGetData(tasks, DUMMY_ACUITY_DATASET_42);
    }

    private void executeMixedWithGetData(List<Callable<Integer>> tasks, Dataset dataset) throws InterruptedException, ExecutionException {
        tasks.addAll(submitGetData(dataset));
        Collections.shuffle(tasks);
        List<Future<Integer>> executionResults = tasks.stream().map(t -> executors.submit(t)).collect(Collectors.toList());
        for (Future<Integer> executionResult : executionResults) {
            assertThat(executionResult.get()).isEqualTo(1);
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);
    }

    private List<Callable<Integer>> submitGetData(Dataset dataset) {
        List<Callable<Integer>> getDataTasks = IntStream.range(0, N_READ_THREADS).mapToObj(n -> (Callable<Integer>) () -> {
            TimeUnit.MILLISECONDS.sleep(rand.nextInt(MAX_DELAY));
            Collection<Subject> result = dataProvider.getData(Subject.class, dataset, ds -> SUBJECTS);
            return result.size();
        }).collect(Collectors.toList());
        return getDataTasks;
    }

    @Test
    @Ignore("Manual performance test, unignore when need to run")
    public void testCachingPerformance() throws InterruptedException {
        final List<Object> errorCnt = new ArrayList<>();
        List<Long> queryTimes = new ArrayList<>();

        ExecutorService executors = Executors.newFixedThreadPool(50);
        ExecutorService executors2 = Executors.newFixedThreadPool(150);

        final int eventCountPerEntry = 1000000;
        final List<CvotEndpoint> cvotEndpoints = CvotEndpointGenerator.generateRandomCvotEndpointList(eventCountPerEntry, 500, 20, 2014, 2015);


        List<Callable<Integer>> tasks1 = IntStream.range(10000, 10000 + 1000).boxed().map(i -> (Callable<Integer>) () -> {
            TimeUnit.MILLISECONDS.sleep(rand.nextInt(100));
            LOG.info("1started loading " + i.toString());
            try {
                dataProvider.getData(CvotEndpointRaw.class, new AcuityDataset(i.longValue(), "ds_" + i.toString()),
                        dataset -> {
                            LOG.info("1generating " + i.toString());
                            return cvotEndpoints.stream().map(e -> e.getEvent().toBuilder().id(UUID.randomUUID().toString())
                                    .build()).collect(Collectors.toList());
                        });
            } catch (Exception t) {
                errorCnt.add(t);
                throw t;
            }
            LOG.info("1completed loading " + i.toString());
            final Callable<Integer> readTask = () -> {
                TimeUnit.MILLISECONDS.sleep(rand.nextInt(20000));
                try {
                    final Date start = new Date();
                    LOG.info(String.format("2started loading data for %d", i));
                    final Collection<CvotEndpointRaw> data = dataProvider.getData(CvotEndpointRaw.class, new AcuityDataset(i.longValue(), "ds_" + i.toString()),
                            dataset -> {
                                LOG.info("2generating " + i.toString()); //this is not expected to be called ever
                                return cvotEndpoints.stream().map(e -> e.getEvent().toBuilder().id(UUID.randomUUID().toString())
                                        .build()).collect(Collectors.toList());
                            });
                    final Date end = new Date();
                    final long time = end.getTime() - start.getTime();
                    final int size = data == null ? 0 : data.size();
                    LOG.info(String.format("2completed loading %d items for %d in %d ms", size, i, time));
                    queryTimes.add(time);
                    if (size != eventCountPerEntry) {
                        errorCnt.add("Unexpected returned size " + size);
                    }
                    return 0;
                } catch (Exception t) {
                    errorCnt.add(t);
                    throw t;
                }
            };
            IntStream.rangeClosed(1, 20).forEach(e -> executors2.submit(readTask));
            return 0;
        }).collect(Collectors.toList());


        tasks1.forEach(executors::submit);

        executors.shutdown();
        executors.awaitTermination(10, TimeUnit.MINUTES);
        executors2.shutdown();
        executors2.awaitTermination(10, TimeUnit.MINUTES);
        LOG.info("query timings summary, {}", new ArrayList<>(queryTimes).stream().mapToLong(Long::longValue).collect(LongSummaryStatistics::new,
                LongSummaryStatistics::accept,
                LongSummaryStatistics::combine));
        System.out.flush();
        assertThat(errorCnt).hasSize(0);
    }

}
