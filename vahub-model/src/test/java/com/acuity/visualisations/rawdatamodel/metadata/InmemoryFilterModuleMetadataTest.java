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

package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.Constants;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.NicotineFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.AlcoholService;
import com.acuity.visualisations.rawdatamodel.service.event.BiomarkerService;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.service.event.CardiacService;
import com.acuity.visualisations.rawdatamodel.service.event.CerebrovascularService;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.service.event.CtDnaService;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.service.event.DeathService;
import com.acuity.visualisations.rawdatamodel.service.event.DoseDiscService;
import com.acuity.visualisations.rawdatamodel.service.event.DrugDoseService;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.service.event.ExposureService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.event.LiverDiagService;
import com.acuity.visualisations.rawdatamodel.service.event.LiverRiskService;
import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.service.event.MedicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.NicotineService;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.service.event.SeriousAeService;
import com.acuity.visualisations.rawdatamodel.service.event.SurgicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.TumourColumnRangeService;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Category(LabTests.class)
public class InmemoryFilterModuleMetadataTest {

    @InjectMocks
    private InmemoryFilterModuleMetadata inmemoryFilterModuleMetadata;
    @Mock
    private PopulationService populationService;
    @Mock
    private AeService aeService;
    @Mock
    private CIEventService ciEventService;
    @Mock
    private CerebrovascularService cerebrovascularService;
    @Mock
    private CvotEndpointService cvotEndpointService;
    @Mock
    private LabService labService;
    @Mock
    private ExposureService exposureService;
    @Mock
    private VitalService vitalService;
    @Mock
    private TumourColumnRangeService tumourColumnRangeService;
    @Mock
    private LiverService liverService;
    @Mock
    private LiverRiskService liverRiskService;
    @Mock
    private LiverDiagService liverDiagService;
    @Mock
    private AlcoholService alcoholService;
    @Mock
    private SeriousAeService seriousAeService;
    @Mock
    private RenalService renalService;
    @Mock
    private ExacerbationService exacerbationService;
    @Mock
    private LungFunctionService lungFunctionService;
    @Mock
    private NicotineService nicotineService;
    @Mock
    private MedicalHistoryService medicalHistoryService;
    @Mock
    private DeathService deathService;
    @Mock
    private CardiacService cardiacService;
    @Mock
    private DrugDoseService drugDoseService;
    @Mock
    private DoseDiscService doseDiscService;
    @Mock
    private SurgicalHistoryService surgicalHistoryService;
    @Mock
    private ConmedsService conmedsService;
    @Mock
    private CtDnaService ctDnaService;
    @Mock
    private BiomarkerService biomarkerService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetMetadataItemWhenAllFiltersWithNullValues() {

        when(ciEventService.getAvailableFilters(any(Datasets.class), any(CIEventFilters.class), any(PopulationFilters.class)))
                .thenReturn(new CIEventFilters());

        when(cerebrovascularService.getAvailableFilters(any(Datasets.class), any(CerebrovascularFilters.class), any(PopulationFilters.class)))
                .thenReturn(new CerebrovascularFilters());

        when(cvotEndpointService.getAvailableFilters(any(Datasets.class), any(CvotEndpointFilters.class), any(PopulationFilters.class)))
                .thenReturn(new CvotEndpointFilters());

        when(populationService.getAvailableFilters(any(Datasets.class), any(PopulationFilters.class))).thenReturn(new PopulationFilters());

        when(aeService.getAvailableFilters(any(Datasets.class), any(AeFilters.class), any(PopulationFilters.class))).thenReturn(new AeFilters());

        when(labService.getAvailableFilters(any(Datasets.class), any(LabFilters.class), any(PopulationFilters.class))).thenReturn(new LabFilters());

        when(exposureService.getAvailableFilters(any(Datasets.class), any(ExposureFilters.class), any(PopulationFilters.class))).thenReturn(new ExposureFilters());

        when(vitalService.getAvailableFilters(any(Datasets.class), any(VitalFilters.class), any(PopulationFilters.class))).thenReturn(new VitalFilters());

        when(tumourColumnRangeService.getAvailableTherapyFilters(any(Datasets.class), any(TherapyFilters.class), any(PopulationFilters.class),
                Matchers.any()))
                .thenReturn(new TherapyFilters());

        when(liverService.getAvailableFilters(any(Datasets.class), any(LiverFilters.class), any(PopulationFilters.class)))
                .thenReturn(new LiverFilters());

        when(liverRiskService.getAvailableFilters(any(Datasets.class), any(LiverRiskFilters.class), any(PopulationFilters.class)))
                .thenReturn(new LiverRiskFilters());

        when(liverDiagService.getAvailableFilters(any(Datasets.class), any(LiverDiagFilters.class), any(PopulationFilters.class)))
                .thenReturn(new LiverDiagFilters());

        when(alcoholService.getAvailableFilters(any(Datasets.class), any(AlcoholFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AlcoholFilters());

        when(seriousAeService.getAvailableFilters(any(Datasets.class), any(SeriousAeFilters.class), any(PopulationFilters.class)))
                .thenReturn(new SeriousAeFilters());

        when(renalService.getAvailableFilters(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class)))
                .thenReturn(new RenalFilters());

        when(exacerbationService.getAvailableFilters(any(Datasets.class), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(new ExacerbationFilters());

        when(lungFunctionService.getAvailableFilters(any(Datasets.class), any(LungFunctionFilters.class), any(PopulationFilters.class)))
                .thenReturn(new LungFunctionFilters());

        when(nicotineService.getAvailableFilters(any(Datasets.class), any(NicotineFilters.class), any(PopulationFilters.class)))
                .thenReturn(new NicotineFilters());
        when(medicalHistoryService.getAvailableFilters(any(Datasets.class), any(MedicalHistoryFilters.class), any(PopulationFilters.class)))
                .thenReturn(new MedicalHistoryFilters());
        when(drugDoseService.getAvailableFilters(any(Datasets.class), any(DrugDoseFilters.class), any(PopulationFilters.class)))
                .thenReturn(new DrugDoseFilters());

        when(doseDiscService.getAvailableFilters(any(Datasets.class), any(DoseDiscFilters.class), any(PopulationFilters.class)))
                .thenReturn(new DoseDiscFilters());

        when(deathService.getAvailableFilters(any(Datasets.class), any(DeathFilters.class), any(PopulationFilters.class)))
                .thenReturn(new DeathFilters());

        when(cardiacService.getAvailableFilters(any(Datasets.class), any(CardiacFilters.class), any(PopulationFilters.class)))
                .thenReturn(new CardiacFilters());

        when(surgicalHistoryService.getAvailableFilters(any(Datasets.class), any(SurgicalHistoryFilters.class), any(PopulationFilters.class)))
                .thenReturn(new SurgicalHistoryFilters());
        when(conmedsService.getAvailableFilters(any(Datasets.class),any(ConmedFilters.class), any(PopulationFilters.class)))
                .thenReturn(new ConmedFilters());
        when(ctDnaService.getAvailableFilters(any(Datasets.class), any(CtDnaFilters.class), any(PopulationFilters.class)))
                .thenReturn(new CtDnaFilters());
        when(biomarkerService.getAvailableFilters(any(Datasets.class), any(BiomarkerFilters.class), any(PopulationFilters.class)))
                .thenReturn(new BiomarkerFilters());

        //When
        String result = inmemoryFilterModuleMetadata.getMetadataItem(Constants.CEREBRO_DATASETS).build();

        //Then
        assertThatJson(result).node("inMemoryEmptyFilters.cievents").isArray().ofLength(21);
        assertThatJson(result).node("inMemoryEmptyFilters.cerebrovascular").isArray().ofLength(13);
        assertThatJson(result).node("inMemoryEmptyFilters.cvotEndpoint").isArray().ofLength(12);
        assertThatJson(result).node("inMemoryEmptyFilters.pop").isPresent();
        assertThatJson(result).node("inMemoryEmptyFilters.aes").isPresent();
        assertThatJson(result).node("inMemoryEmptyFilters.labs").isArray().ofLength(22);
        assertThatJson(result).node("inMemoryEmptyFilters.exposure").isArray().ofLength(8);
        assertThatJson(result).node("inMemoryEmptyFilters.vitals").isPresent();
        assertThatJson(result).node("inMemoryEmptyFilters.therapy").isArray().ofLength(11);
        assertThatJson(result).node("inMemoryEmptyFilters.liver").isArray().ofLength(13);
        assertThatJson(result).node("inMemoryEmptyFilters.liverRisk").isArray().ofLength(10);
        assertThatJson(result).node("inMemoryEmptyFilters.liverDiag").isArray().ofLength(6);
        assertThatJson(result).node("inMemoryEmptyFilters.alcohol").isArray().ofLength(9);
        assertThatJson(result).node("inMemoryEmptyFilters.seriousAe").isArray().ofLength(29);
        assertThatJson(result).node("inMemoryEmptyFilters.lungFunction").isArray().ofLength(11);
        assertThatJson(result).node("inMemoryEmptyFilters.exacerbation").isArray().ofLength(14);
        assertThatJson(result).node("inMemoryEmptyFilters.nicotine").isArray().ofLength(11);
        assertThatJson(result).node("inMemoryEmptyFilters.medicalHistory").isArray().ofLength(9);
        assertThatJson(result).node("inMemoryEmptyFilters.renal").isArray().ofLength(11);
        assertThatJson(result).node("inMemoryEmptyFilters.death").isArray().ofLength(9);
        assertThatJson(result).node("inMemoryEmptyFilters.surgicalHistory").isArray().ofLength(6);
        assertThatJson(result).node("inMemoryEmptyFilters.dose").isArray().ofLength(31);
        assertThatJson(result).node("inMemoryEmptyFilters.doseDisc").isArray().ofLength(7);
        assertThatJson(result).node("inMemoryEmptyFilters.conmeds").isArray().ofLength(15);
        assertThatJson(result).node("inMemoryEmptyFilters.ctDna").isArray().ofLength(3);
        assertThatJson(result).node("inMemoryEmptyFilters.biomarker").isArray().ofLength(3);
    }
}
