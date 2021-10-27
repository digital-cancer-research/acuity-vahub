package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_VA_ID;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Ignore;
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
import com.acuity.va.security.acl.domain.DetectDataset;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AeIncidenceDatasetsDataProviderITCase {

    @Autowired
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;

    private Dataset acuityDataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);
    private Dataset detectDataset = new DetectDataset(DUMMY_DETECT_VA_ID);

    @Test
    public void testLoadDataFromAcuityUploadedFile() throws Exception {

        Collection<Ae> result = aeIncidenceDatasetsDataProvider.loadData(new Datasets(acuityDataset));

        assertThat(result).hasSize(2076);
    }
    
    @Test
    @Ignore
    public void testLoadDataFromDetectUploadedFile() throws Exception {

        Collection<Ae> result = aeIncidenceDatasetsDataProvider.loadData(new Datasets(detectDataset));
        
        assertThat(result).hasSize(1884);
    }

   // @Test
    public void testGetDataMultipleThreads() throws Exception {

        int nThreads = 10;

        ExecutorService executors = Executors.newFixedThreadPool(nThreads);
        List<Future<Integer>> executionResults = new ArrayList<>();

        for (int i = 0; i < nThreads; ++i) {
            Future<Integer> executionResult = executors.submit(() -> {
                Collection<Ae> result = aeIncidenceDatasetsDataProvider.loadData(new Datasets(acuityDataset));
                return result.size();
            });
            executionResults.add(executionResult);
        }
        for (Future<Integer> executionResult : executionResults) {
            assertThat(executionResult.get()).isEqualTo(2064);
        }
        executors.shutdown();
        executors.awaitTermination(1, TimeUnit.MINUTES);
    }
}
