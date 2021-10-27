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

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.common.study.metadata.ModuleMetadata;
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
import com.acuity.visualisations.rawdatamodel.filters.Filters;
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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions.ALL_PRIOR_THERAPIES;

@Service
@RequiredArgsConstructor
@Slf4j
public class InmemoryFilterModuleMetadata implements ModuleMetadata {
    private final CIEventService ciEventService;
    private final AeService aeService;
    private final PopulationService populationService;
    private final LabService labService;
    private final ExposureService exposureService;
    private final CerebrovascularService cerebrovascularService;
    private final CvotEndpointService cvotEndpointService;
    private final VitalService vitalsService;
    private final TumourColumnRangeService tumourColumnRangeService;
    private final LiverService liverService;
    private final LiverRiskService liverRiskService;
    private final LiverDiagService liverDiagService;
    private final AlcoholService alcoholService;
    private final LungFunctionService lungFunctionService;
    private final ExacerbationService exacerbationService;
    private final DeathService deathService;
    private final NicotineService nicotineService;
    private final RenalService renalService;
    private final MedicalHistoryService medicalHistoryService;
    private final CardiacService cardiacService;
    private final SurgicalHistoryService surgicalHistoryService;
    private final DrugDoseService drugDoseService;
    private final DoseDiscService doseDiscService;
    private final SeriousAeService seriousAeService;
    private final ConmedsService conmedsService;
    private final BiomarkerService biomarkerService;
    private final CtDnaService ctDnaService;


    @Override
    public MetadataItem getMetadataItem(Datasets datasets) {
        MetadataItem metadataItem = new MetadataItem("inMemoryEmptyFilters");

        try {

            Filters<CIEvent> ciEventAvailableFilters = ciEventService.getAvailableFilters(datasets, CIEventFilters.empty(), PopulationFilters.empty());
            Filters<Cerebrovascular> cerebrovascularAvailableFilters =
                    cerebrovascularService.getAvailableFilters(datasets, CerebrovascularFilters.empty(), PopulationFilters.empty());
            Filters<CvotEndpoint> cvotAvailableFilters =
                    cvotEndpointService.getAvailableFilters(datasets, CvotEndpointFilters.empty(), PopulationFilters.empty());
            Filters<Subject> populationAvailableFilters = populationService.getAvailableFilters(datasets, PopulationFilters.empty());
            Filters<Ae> aesAvailableFilters = aeService.getAvailableFilters(datasets, AeFilters.empty(), PopulationFilters.empty());
            Filters<Lab> labAvailableFilters = labService.getAvailableFilters(datasets, LabFilters.empty(), PopulationFilters.empty());
            Filters<Exposure> exposureAvailableFilters = exposureService.getAvailableFilters(datasets, ExposureFilters.empty(), PopulationFilters.empty());
            Filters<Vital> vitalsAvailableFilters = vitalsService.getAvailableFilters(datasets, VitalFilters.empty(), PopulationFilters.empty());
            Filters<Alcohol> alcoholAvailableFilters = alcoholService.getAvailableFilters(datasets, AlcoholFilters.empty(), PopulationFilters.empty());
            Filters<Nicotine> nicotineAvailableFilters = nicotineService.getAvailableFilters(datasets, NicotineFilters.empty(), PopulationFilters.empty());
            ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = ChartGroupByOptions.<TumourTherapy,
                    TumourTherapyGroupByOptions>builder()
                    .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ALL_PRIOR_THERAPIES.getGroupByOptionAndParams())
                    .build();
            TherapyFilters tumourTherapyFilters = tumourColumnRangeService.getAvailableTherapyFilters(datasets, TherapyFilters.empty(),
                    PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
            Filters<Liver> liverAvailableFilters = liverService.getAvailableFilters(datasets, LiverFilters.empty(), PopulationFilters.empty());
            Filters<LiverRisk> liverRiskAvailableFilters = liverRiskService.getAvailableFilters(datasets, LiverRiskFilters.empty(), PopulationFilters.empty());
            Filters<LiverDiag> liverDiagAvailableFilters = liverDiagService.getAvailableFilters(datasets, LiverDiagFilters.empty(), PopulationFilters.empty());
            Filters<SeriousAe> seriousAeAvailableFilters = seriousAeService.getAvailableFilters(datasets, SeriousAeFilters
                    .empty(), PopulationFilters.empty());
            Filters<LungFunction> lungFunctionAvailableFilters =
                    lungFunctionService.getAvailableFilters(datasets, LungFunctionFilters.empty(), PopulationFilters.empty());
            Filters<Exacerbation> exacerbationAvailableFilters = exacerbationService
                    .getAvailableFilters(datasets, ExacerbationFilters.empty(), PopulationFilters.empty());
            Filters<Renal> renalAvailableFilters = renalService.getAvailableFilters(datasets, RenalFilters.empty(), PopulationFilters.empty());
            Filters<MedicalHistory> medicalHistoryAvailableFilters =
                    medicalHistoryService.getAvailableFilters(datasets, MedicalHistoryFilters.empty(), PopulationFilters.empty());
            Filters<Death> deathAvailableFilters = deathService
                    .getAvailableFilters(datasets, DeathFilters.empty(), PopulationFilters.empty());
            Filters<Cardiac> cardiacAvailableFilters =
                    cardiacService.getAvailableFilters(datasets, CardiacFilters.empty(), PopulationFilters.empty());
            Filters<SurgicalHistory> surgicalHistoryAvailableFilters =
                    surgicalHistoryService.getAvailableFilters(datasets, SurgicalHistoryFilters.empty(), PopulationFilters.empty());
            Filters<DrugDose> drugDoseAvailableFilters = drugDoseService.getAvailableFilters(datasets, DrugDoseFilters.empty(), PopulationFilters.empty());
            Filters<DoseDisc> doseDiscAvailableFilters = doseDiscService.getAvailableFilters(datasets, DoseDiscFilters.empty(), PopulationFilters.empty());
            Filters<Conmed> conmedAvailableFilters = conmedsService.getAvailableFilters(datasets, ConmedFilters.empty(), PopulationFilters.empty());
            Filters<CtDna> ctDnaAvailableFilters = ctDnaService.getAvailableFilters(datasets, CtDnaFilters.empty(), PopulationFilters.empty());
            Filters<Biomarker> biomarkerFilters = biomarkerService.getAvailableFilters(datasets, BiomarkerFilters.empty(), PopulationFilters.empty());

            metadataItem.add("aes", aesAvailableFilters.getEmptyFilterNames());
            metadataItem.add("alcohol", alcoholAvailableFilters.getEmptyFilterNames());
            metadataItem.add("biomarker", biomarkerFilters.getEmptyFilterNames());
            metadataItem.add("cardiac", cardiacAvailableFilters.getEmptyFilterNames());
            metadataItem.add("cerebrovascular", cerebrovascularAvailableFilters.getEmptyFilterNames());
            metadataItem.add("cievents", ciEventAvailableFilters.getEmptyFilterNames());
            metadataItem.add("conmeds", conmedAvailableFilters.getEmptyFilterNames());
            metadataItem.add("ctDna", ctDnaAvailableFilters.getEmptyFilterNames());
            metadataItem.add("cvotEndpoint", cvotAvailableFilters.getEmptyFilterNames());
            metadataItem.add("death", deathAvailableFilters.getEmptyFilterNames());
            metadataItem.add("dose", drugDoseAvailableFilters.getEmptyFilterNames());
            metadataItem.add("doseDisc", doseDiscAvailableFilters.getEmptyFilterNames());
            metadataItem.add("exacerbation", exacerbationAvailableFilters.getEmptyFilterNames());
            metadataItem.add("exposure", exposureAvailableFilters.getEmptyFilterNames());
            metadataItem.add("labs", labAvailableFilters.getEmptyFilterNames());
            metadataItem.add("liver", liverAvailableFilters.getEmptyFilterNames());
            metadataItem.add("liverDiag", liverDiagAvailableFilters.getEmptyFilterNames());
            metadataItem.add("liverRisk", liverRiskAvailableFilters.getEmptyFilterNames());
            metadataItem.add("lungFunction", lungFunctionAvailableFilters.getEmptyFilterNames());
            metadataItem.add("medicalHistory", medicalHistoryAvailableFilters.getEmptyFilterNames());
            metadataItem.add("nicotine", nicotineAvailableFilters.getEmptyFilterNames());
            metadataItem.add("pop", populationAvailableFilters.getEmptyFilterNames(datasets));   // ?
            metadataItem.add("renal", renalAvailableFilters.getEmptyFilterNames());
            metadataItem.add("seriousAe", seriousAeAvailableFilters.getEmptyFilterNames());
            metadataItem.add("surgicalHistory", surgicalHistoryAvailableFilters.getEmptyFilterNames());
            metadataItem.add("therapy", tumourTherapyFilters.getEmptyFilterNames());
            metadataItem.add("vitals", vitalsAvailableFilters.getEmptyFilterNames());

        } catch (Exception e) {
            log.error("Unable to query for empty filters", e);
            metadataItem.add("aes", new ArrayList<>());
            metadataItem.add("alcohol", new ArrayList<>());
            metadataItem.add("biomarker", new ArrayList<>());
            metadataItem.add("cardiac", new ArrayList<>());
            metadataItem.add("cerebrovascular", new ArrayList<>());
            metadataItem.add("cievents", new ArrayList<>());
            metadataItem.add("conmeds", new ArrayList<>());
            metadataItem.add("ctDna", new ArrayList<>());
            metadataItem.add("cvotEndpoint", new ArrayList<>());
            metadataItem.add("death", new ArrayList<>());
            metadataItem.add("dose", new ArrayList<>());
            metadataItem.add("doseDisc", new ArrayList<>());
            metadataItem.add("exacerbation", new ArrayList<>());
            metadataItem.add("exposure", new ArrayList<>());
            metadataItem.add("labs", new ArrayList<>());
            metadataItem.add("liver", new ArrayList<>());
            metadataItem.add("liverRisk", new ArrayList<>());
            metadataItem.add("liverDiag", new ArrayList<>());
            metadataItem.add("lungFunction", new ArrayList<>());
            metadataItem.add("pop", new ArrayList<>());
            metadataItem.add("medicalHistory", new ArrayList<>());
            metadataItem.add("nicotine", new ArrayList<>());
            metadataItem.add("renal", new ArrayList<>());
            metadataItem.add("seriousAe", new ArrayList<>());
            metadataItem.add("surgicalHistory", new ArrayList<>());
            metadataItem.add("tumour-therapy", new ArrayList<>());
            metadataItem.add("vitals", new ArrayList<>());
        }
        return metadataItem;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }
}
