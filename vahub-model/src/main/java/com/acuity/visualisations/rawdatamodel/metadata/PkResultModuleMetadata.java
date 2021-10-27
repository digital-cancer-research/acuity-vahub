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

import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultService;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PkResultModuleMetadata extends PkResultAbstractModuleMetadata {

    @Autowired
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;
    @Autowired
    private PkResultService pkResultService;

    @Override
    protected String tab() {
        return "pkResult";
    }

    @Override
    protected DatasetsDataProvider<PkResultRaw, PkResult> getEventDataProvider() {
        return pkResultDatasetsDataProvider;
    }

    @Override
    protected boolean hasXAxis(Datasets datasets) {
        return !pkResultService
                .getAvailableBoxPlotXAxis(datasets, PkResultFilters.empty(), PopulationFilters.empty())
                .getOptions()
                .isEmpty();
    }
}
