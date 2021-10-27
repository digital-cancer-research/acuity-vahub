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
import com.acuity.visualisations.rawdatamodel.dataproviders.ChemotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.RadiotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.event.TumourColumnRangeService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.endsBeforeFirstTreatmentDate;

@Service
public class TumourTherapyModuleMetadata extends AbstractModuleColoringMetadata<DrugDoseRaw, DrugDose> {
    @Autowired
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;
    @Autowired
    private RadiotherapyDatasetsDataProvider radiotherapyDatasetsDataProvider;
    @Autowired
    private TumourColumnRangeService tumourColumnRangeService;

    @Override
    protected String tab() {
        return "tumour-therapy";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {

        Collection<Chemotherapy> previousChemotherapy = getPreviousChemotherapy(datasets);
        Collection<Radiotherapy> previousRadiotherapy = getPreviousRadiotherapy(datasets);
        Collection<DrugDose> drugDoses = ((DrugDoseDatasetsDataProvider) getEventDataProvider()).loadDosesForTumourColumnRangeService(datasets).stream()
                .filter(DrugDose::isActive)
                .collect(Collectors.toList());
        int count = previousChemotherapy.size() + previousRadiotherapy.size() + drugDoses.size();
        final Map<String, List<TumourTherapy>> subjectsLastTherapy = tumourColumnRangeService.getSubjectLastTherapy(datasets,
                TherapyFilters.empty(), PopulationFilters.empty());
        metadataItem.addProperty("count", count);
        metadataItem.addProperty("hasData", count > 0);
        metadataItem.addProperty("hasPriorTherapy", !subjectsLastTherapy.isEmpty());
        return metadataItem;
    }

    protected List<Radiotherapy> getPreviousRadiotherapy(Datasets datasets) {
        return radiotherapyDatasetsDataProvider.loadData(datasets).stream()
                .filter(r -> endsBeforeFirstTreatmentDate(r))
                .collect(Collectors.toList());
    }

    protected List<Chemotherapy> getPreviousChemotherapy(Datasets datasets) {
        return chemotherapyDatasetsDataProvider.loadData(datasets).stream()
                .filter(c -> endsBeforeFirstTreatmentDate(c))
                .collect(Collectors.toList());
    }

    @Override
    ColorInitializer getColorInitializer() {
        return tumourColumnRangeService;
    }
}
