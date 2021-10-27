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
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED;

/**
 * Created by glen on 9/22/2017.
 */
@Service
public class PopulationModuleMetadata extends AbstractModuleMetadata<Subject, Subject> {

    @Autowired
    protected PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    protected PopulationService populationService;

    @Override
    protected DatasetsDataProvider<Subject, Subject> getEventDataProvider() {
        return populationDatasetsDataProvider;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    @Override
    protected String tab() {
        return "population";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.addProperty("availableYAxisOptions", Arrays.asList(COUNT_OF_SUBJECTS, PERCENTAGE_OF_ALL_SUBJECTS,
                PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED));
        metadataItem.addProperty("hasSafetyAsNoInPopulation", populationService.hasSafetyAsNoInPopulation(datasets));
        metadataItem.add("patientList", populationService.getPatientList(datasets));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("patientList", new ArrayList<>());
        metadataItem.add("availableYAxisOptions", new ArrayList<>());
        return metadataItem;
    }
}
