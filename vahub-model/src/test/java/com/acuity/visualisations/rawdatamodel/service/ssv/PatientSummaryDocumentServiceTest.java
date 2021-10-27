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

package com.acuity.visualisations.rawdatamodel.service.ssv;

import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedNonTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessmentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ChemotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DiseaseExtentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.MedicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.NonTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PathologyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.RadiotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SecondTimeOfProgressionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SurgicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SurvivalStatusDatasesDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.TargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.PopulationServiceTest.SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.service.event.MedicalHistoryServiceTest.MEDICAL_HISTORIES;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.DEATHS;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.DOSE_DISCS;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.SERIOUS_AES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PatientSummaryDocumentServiceTest {

    @Autowired
    private PatientSummaryDocumentService documentService;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DeathDatasetsDataProvider deathDatasetsDataProvider;
    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;
    @MockBean
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;
    @MockBean
    private MedicalHistoryDatasetsDataProvider medicalHistoryDatasetsDataProvider;
    @MockBean
    private SurgicalHistoryDatasetsDataProvider surgicalHistoryDatasetsDataProvider;
    @MockBean
    private ConmedDatasetsDataProvider conmedDatasetsDataProvider;
    @MockBean
    private PathologyDatasetsDataProvider pathologyDatasetsDataProvider;
    @MockBean
    private DiseaseExtentDatasetsDataProvider disextDatasetsDataProvider;
    @MockBean
    private LabDatasetsDataProvider labDatasetsDataProvider;
    @MockBean
    private RadiotherapyDatasetsDataProvider radiotherapyDatasetsDataProvider;
    @MockBean
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @MockBean
    private SecondTimeOfProgressionDatasetsDataProvider secondTimeOfProgressionDatasetsDataProvider;
    @MockBean
    private AssessedNonTargetLesionDatasetsDataProvider assessedNonTargetLesionDatasetsDataProvider;
    @MockBean
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;
    @MockBean
    private SurvivalStatusDatasesDataProvider survivalStatusDatasesDataProvider;
    @MockBean
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;
    @MockBean
    private InfoService mockInfoService;

    @Value(value = "classpath:template/patient_summary_template.docx")
    private Resource templateResource;

    @Value(value = "classpath:template/patient_summary_style.docx")
    private Resource styleTemplateResource;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetDocumentTemplate() throws IOException {
        final File file = templateResource.getFile();
        softly.assertThat(file.exists()).isTrue();
        softly.assertThat(file.isFile()).isTrue();
    }

    @Test
    public void testGetStyleDocumentTemplate() throws IOException {
        final File file = styleTemplateResource.getFile();
        Assert.assertEquals(file.exists(), true);
        Assert.assertEquals(file.isFile(), true);
    }

    @Test
    public void testReturnNonNullDocumentWhenValidRequest() throws IOException, Docx4JException, JAXBException {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DEATHS);
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DOSE_DISCS);
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SERIOUS_AES);
        when(medicalHistoryDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(MEDICAL_HISTORIES);

        final Optional<ByteArrayOutputStream> doc = documentService.generateDocument(DATASETS, "sid1", true, "03:00");
        assertThat(doc).isPresent();
        assertThat(doc.get().toByteArray().length).isGreaterThan(0);
    }

    @Test
    public void templateShouldHaveFooter() throws IOException, Docx4JException, JAXBException {
        WordprocessingMLPackage template = WordprocessingMLPackage.load(templateResource.getInputStream());
        FooterPart footer = (FooterPart) template.getParts().getParts().values().stream().filter(p -> p instanceof FooterPart).findFirst().get();
        Assert.assertNotNull(footer);
        Assert.assertTrue(footer instanceof FooterPart);
        Assert.assertNotNull(footer.getContent());
        Assert.assertTrue(footer.getContent().size() > 0);
    }

    @Test
    public void reportShouldHaveFooter() throws IOException, Docx4JException, JAXBException {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DEATHS);
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DOSE_DISCS);
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SERIOUS_AES);
        when(medicalHistoryDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(MEDICAL_HISTORIES);

        final Optional<ByteArrayOutputStream> document = documentService.generateDocument(DATASETS, "sid1", true, "03:00");
        WordprocessingMLPackage doc = WordprocessingMLPackage.load(new ByteArrayInputStream(document.get().toByteArray()));
        FooterPart footer = (FooterPart) doc.getParts().getParts().values().stream().filter(p -> p instanceof FooterPart).findFirst().get();
        Assert.assertNotNull(footer);
        Assert.assertTrue(footer instanceof FooterPart);
        Assert.assertNotNull(footer.getContent());
        Assert.assertTrue(footer.getContent().size() > 0);
    }
}
