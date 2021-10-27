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

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by knml167 on 9/26/2017.
 */
@Service
public class TumorLineChartUIModelService<T, G extends Enum<G> & GroupByOption<T>> extends LineChartUIModelService<T, G> {

    @Autowired
    private TumourChartColoringService coloringService;

    @Override
    protected String getColor(Object colorBy, Datasets datasets, ChartGroupByOptions.GroupByOptionAndParams<T, G> colorByOption) {
        return coloringService.getColor(colorBy);
    }
}
