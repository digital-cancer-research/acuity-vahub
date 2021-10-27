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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.common.lookup.BeanLookupService;
import com.acuity.visualisations.rawdatamodel.dao.CtDnaRepository;
import com.acuity.visualisations.rawdatamodel.dao.DeathRepository;
import com.acuity.visualisations.rawdatamodel.dao.DoseDiscRepository;
import com.acuity.visualisations.rawdatamodel.dao.DrugDoseRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.va.security.acl.domain.Dataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASET;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class CtDnaDatasetsDataProviderTest extends DataProviderAwareTest {

    private static final String SAMPLE_DATE_1 = "2000-01-10";
    private static final String SAMPLE_DATE_2 = "2000-01-15";
    private static final String SAMPLE_DATE_3 = "2000-01-30";
    private static final String SAMPLE_DATE_4 = "2000-01-31";
    private static final String SAMPLE_DATE_5 = "2000-02-05";

    private CtDnaRaw ctDna1 = CtDnaRaw.builder().id("ctdna1").subjectId("subjectId1").gene("gene1")
            .mutation("mutation1").trackedMutation(YES.toUpperCase())
            .sampleDate(toDate(SAMPLE_DATE_1)).visitNumber(1.)
            .reportedVaf(.3).reportedVafPercent(30.).build();
    private CtDnaRaw ctDna21 = CtDnaRaw.builder().id("ctdna21").subjectId("subjectId1").gene("gene1")
            .mutation("mutation2").trackedMutation(YES)
            .sampleDate(toDate(SAMPLE_DATE_1)).visitNumber(1.)
            .reportedVaf(.6).reportedVafPercent(60.).build();
    private CtDnaRaw ctDna22 = CtDnaRaw.builder().id("ctdna22").subjectId("subjectId1").gene("gene1")
            .mutation("mutation2").trackedMutation(YES)
            .sampleDate(toDate(SAMPLE_DATE_2)).visitNumber(2.)
            .reportedVaf(.001).reportedVafPercent(.1).build();
    private CtDnaRaw ctDna31 = CtDnaRaw.builder().id("ctdna31").subjectId("subjectId2").gene("gene2")
            .mutation("mutation1").trackedMutation(NO)
            .sampleDate(toDate(SAMPLE_DATE_3))
            .reportedVaf(.5).reportedVafPercent(50.).build();
    private CtDnaRaw ctDna32 = CtDnaRaw.builder().id("ctdna32").subjectId("subjectId2")
            .mutation("mutation1").trackedMutation(NO)
            .sampleDate(toDate(SAMPLE_DATE_3))
            .reportedVaf(.6).reportedVafPercent(60.).build();
    private CtDnaRaw ctDna34 = CtDnaRaw.builder().id("ctdna34").subjectId("subjectId2")
            .mutation("mutation1").trackedMutation(NO)
            .sampleDate(toDate(SAMPLE_DATE_3))
            .reportedVaf(.122).reportedVafPercent(12.17).build();
    private CtDnaRaw ctDna4 = CtDnaRaw.builder().id("ctdna4").subjectId("subjectId1")
            .mutation(NO_MUTATIONS_DETECTED)
            .sampleDate(toDate(SAMPLE_DATE_4)).build();
    private CtDnaRaw ctDna5 = CtDnaRaw.builder().id("ctdna5").subjectId("subjectId1")
            .mutation("mutation1").trackedMutation(NO)
            .sampleDate(toDate(SAMPLE_DATE_4)).build();
    private CtDnaRaw ctDna6 = CtDnaRaw.builder().id("ctdna6").subjectId("subjectId1")
            .mutation("None").trackedMutation(NO)
            .sampleDate(toDate(SAMPLE_DATE_5)).visitNumber(5.0).build();


    private List<CtDnaRaw> ctDnas = newArrayList(ctDna1, ctDna21, ctDna22, ctDna31, ctDna32, ctDna34, ctDna4, ctDna5, ctDna6);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CtDnaDatasetsDataProvider ctDnaDatasetsDataProvider;
    @Autowired
    private BeanLookupService beanLookupService;
    @MockBean
    private CtDnaRepository ctDnaRepository;
    @MockBean
    private DrugDoseRepository drugDoseRepository;
    @MockBean
    private DeathRepository deathRepository;
    @MockBean
    private DoseDiscRepository doseDiscRepository;
    @MockBean
    private StudyInfoRepository studyInfoRepository;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Test
    public void testLoadDataTrackedMutationYesCaseNormalizedNoMutDetectedGenerated() {
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(ctDnaRepository);
        when(ctDnaRepository.getRawData(eq(DATASET.getId()))).thenReturn(ctDnas);
        when(studyInfoRepository.getRawData(eq(DATASET.getId()))).thenReturn(Collections.emptyList());
        final Collection<CtDnaRaw> res = ctDnaDatasetsDataProvider.getData(DATASET);
        softly.assertThat(res).extracting(CtDnaRaw::getSubjectId,
                CtDnaRaw::getSampleDate,
                CtDnaRaw::getVisitNumber,
                CtDnaRaw::getTrackedMutation,
                CtDnaRaw::getGene,
                CtDnaRaw::getMutation,
                CtDnaRaw::getReportedVaf,
                CtDnaRaw::getReportedVafPercent,
                CtDnaRaw::getReportedVafCalculated,
                CtDnaRaw::getReportedVafCalculatedPercent)
                .containsExactlyInAnyOrder(
                        tuple("subjectId1", toDate(SAMPLE_DATE_1), 1.0, YES, "gene1", "mutation1", 0.3, 30., 0.3, 30.),
                        tuple("subjectId1", toDate(SAMPLE_DATE_2), 2.0, null, "gene1", "mutation1", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_4), null, null, "gene1", "mutation1", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_5), 5.0, null, "gene1", "mutation1", null, null, 0.002, 0.2),

                        tuple("subjectId1", toDate(SAMPLE_DATE_1), 1.0, YES, "gene1", "mutation2", 0.6, 60., 0.6, 60.),
                        tuple("subjectId1", toDate(SAMPLE_DATE_2), 2.0, YES, "gene1", "mutation2", 0.001, 0.1, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_4), null, null, "gene1", "mutation2", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_5), 5.0, null, "gene1", "mutation2", null, null, 0.002, 0.2),

                        tuple("subjectId1", toDate(SAMPLE_DATE_1), 1.0, null, null, "mutation1", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_2), 2.0, null, null, "mutation1", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_4), null, NO, null, "mutation1", null, null, 0.002, 0.2),
                        tuple("subjectId1", toDate(SAMPLE_DATE_5), 5.0, null, null, "mutation1", null, null, 0.002, 0.2),

                        tuple("subjectId2", toDate(SAMPLE_DATE_3), null, NO, "gene2", "mutation1", 0.5, 50., 0.5, 50.),
                        tuple("subjectId2", toDate(SAMPLE_DATE_3), null, NO, null, "mutation1", 0.6, 60., 0.6, 60.),
                        tuple("subjectId2", toDate(SAMPLE_DATE_3), null, NO, null, "mutation1", 0.122, 12.17, 0.122, 12.17));
    }
}
