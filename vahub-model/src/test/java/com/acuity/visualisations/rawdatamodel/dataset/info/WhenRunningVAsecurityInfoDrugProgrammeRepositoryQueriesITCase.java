package com.acuity.visualisations.rawdatamodel.dataset.info;

import com.acuity.visualisations.config.ApplicationEnableExecutorConfig;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.vasecurity.DrugProgrammeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class, ApplicationEnableExecutorConfig.class})
@TransactionalOracleITTest
public class WhenRunningVAsecurityInfoDrugProgrammeRepositoryQueriesITCase {

    @Autowired
    private DrugProgrammeRepository acuityDrugProgrammeRepository;

    private DrugProgramme dp = new DrugProgramme("demo");

    @Test
    public void shouldGetDrugProgrammeInfoForStudy() {
        DrugProgrammeInfo drugProgrammeInfo = acuityDrugProgrammeRepository.getDrugProgrammeInfo(dp);
        assertThat(drugProgrammeInfo).isNotNull();
    }

    @Test
    public void shouldListDrugProgrammeNames() {
        List<String> drugProgrammeNames = acuityDrugProgrammeRepository.listDrugProgrammes();
        assertThat(drugProgrammeNames).isNotEmpty();
    }
}
