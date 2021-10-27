package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AeSeverityChangeDatasetsDataProviderITCase {

    @Autowired
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;

    private Dataset dataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Test
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<Ae> result = aeSeverityChangeDatasetsDataProvider.loadData(new Datasets(dataset));

        
        System.out.println(result.stream().map(Ae::getId).count());
        System.out.println(result.stream().map(Ae::getId).distinct().count());
        assertThat(result).hasSize(2538);
    }

   // @Test
    public void testGetDataMultipleThreads() throws Exception {

        int nThreads = 10;

        ExecutorService executors = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> executionResults = new ArrayList<>();

        for (int i = 0; i < nThreads; ++i) {
            Future<Integer> executionResult = executors.submit(() -> {
                Collection<Ae> result = aeSeverityChangeDatasetsDataProvider.loadData(new Datasets(dataset));
                return result.size();
            });
            executionResults.add(executionResult);
        }
        for (Future<Integer> executionResult : executionResults) {
            assertThat(executionResult.get()).isEqualTo(2521);
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);
    }
}
