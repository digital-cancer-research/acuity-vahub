package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class PopulationDatasetsDataProviderITCase {

    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Dataset dataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Test
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<Subject> result = populationDatasetsDataProvider.loadData(new Datasets(dataset));

        softly.assertThat(result).hasSize(124);
    }

    @Test
    public void testGetDataMultipleThreads() throws Exception {

        int nThreads = 10;

        ExecutorService executors = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> executionResults = new ArrayList<>();

        for (int i = 0; i < nThreads; ++i) {
            Future<Integer> executionResult = executors.submit(() -> {
                Collection<Subject> result = populationDatasetsDataProvider.loadData(new Datasets(dataset));
                return result.size();
            });
            executionResults.add(executionResult);
        }
        for (Future<Integer> executionResult : executionResults) {
            softly.assertThat(executionResult.get()).isEqualTo(124);
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void shouldReturnCorrectDrugDoseData() {
        Collection<Subject> result = populationDatasetsDataProvider.loadData(new Datasets(dataset));

        List<Integer> sortedDurationInclBreaks = result.stream().flatMap(e -> e.getDrugTotalDurationInclBreaks().values().stream()).sorted(Integer::compareTo).collect(Collectors.toList());
        List<Integer> sortedDurationExclBreaks = result.stream().flatMap(e -> e.getDrugTotalDurationExclBreaks().values().stream()).sorted(Integer::compareTo).collect(Collectors.toList());

        softly.assertThat(sortedDurationExclBreaks.get(0)).isEqualTo(1);
        softly.assertThat(sortedDurationExclBreaks.get(sortedDurationExclBreaks.size() - 1)).isEqualTo(1720);
        softly.assertThat(sortedDurationInclBreaks.get(0)).isEqualTo(1);
        softly.assertThat(sortedDurationInclBreaks.get(sortedDurationInclBreaks.size() - 1)).isEqualTo(1757);

        Subject subject = result.stream()
                .filter(e -> "2e028dbfd09b4c65afd3b7af4d6d7c53".equals(e.getSubjectId()))
                .findFirst().get();

        softly.assertThat(subject.getDrugsDosed().keySet()).containsExactly("AZD1234");
        softly.assertThat(subject.getDrugsDosed().get("AZD1234")).isEqualTo("Yes");
        softly.assertThat(subject.getDrugFirstDoseDate().get("AZD1234")).isInSameDayAs(toDate("24.03.2015"));
        softly.assertThat(subject.getDrugsMaxDoses().get("AZD1234")).isEqualTo("10 mg");
        softly.assertThat(subject.getDrugsMaxFrequencies().get("AZD1234")).isEqualTo("BID");
        softly.assertThat(subject.getDrugTotalDurationInclBreaks().get("AZD1234")).isEqualTo(353);
        softly.assertThat(subject.getDrugTotalDurationExclBreaks().get("AZD1234")).isEqualTo(54);
        softly.assertThat(subject.getDrugsDiscontinued().keySet()).containsExactly("AZD1234");
        softly.assertThat(subject.getDrugsDiscontinued().get("AZD1234")).isEqualTo("Yes");

        softly.assertThat(ZonedDateTime.ofInstant(subject.getDrugDiscontinuationDate().get("AZD1234").toInstant(), ZoneId.of("GMT+3")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDate("06.10.2016").toInstant(), ZoneId.of("GMT+3")));

        softly.assertThat(subject.getDrugDiscontinuationMainReason().get("AZD1234")).isEqualTo("Condition Under Investigation Worsened");
    }
}
