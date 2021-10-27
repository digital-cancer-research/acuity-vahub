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

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CtDnaLineChartUIModelService extends LineChartUIModelService<CtDna, CtDnaGroupByOptions> {

    @Autowired
    private CtDnaLineChartColoringService coloringService;

    @Override
    protected String getColor(Object colorByValue, Datasets datasets,
                              GroupByOptionAndParams<CtDna, CtDnaGroupByOptions> colorByOption) {

        String datasetColorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets,
                Optional.ofNullable(colorByOption)
                .map(o -> o.getGroupByOption().toString())
                .orElse(null));
        return coloringService.getColor(colorByValue, datasetColorByOption);
    }

    public void generateColors(Datasets datasets, List<TrellisOptions<CtDnaGroupByOptions>> colorByOptions) {
        colorByOptions
                .forEach(colorByOption -> colorByOption.getTrellisOptions()
                        .forEach(opt -> getColor(opt, datasets,
                                colorByOption.getTrellisedBy().getGroupByOptionAndParams())));
    }
}
