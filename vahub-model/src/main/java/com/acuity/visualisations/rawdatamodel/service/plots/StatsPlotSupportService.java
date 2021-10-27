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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;

public interface StatsPlotSupportService<T,  G extends Enum<G> & GroupByOption<T>> {
    SelectionDetail getRangedSelectionDetails(Datasets datasets, Filters<T> filters,
                                        PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItemRange<T, G, Double>> selection);
    SelectionDetail getSelectionDetails(Datasets datasets, Filters<T> filters,
                                        PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItem<T, G>> selection);
}
