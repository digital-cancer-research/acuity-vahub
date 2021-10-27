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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputErrorLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputErrorLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.ErrorLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 9/26/2017.
 */
@Service
public class ExposureLineChartUIModelService<T, G extends Enum<G> & GroupByOption<T>>
        extends LineChartUIModelService<T, G> {
    @Autowired
    private ExposureLineChartColoringService coloringService;

    @Override
    protected String getColor(Object colorByValue, Datasets datasets,
                              GroupByOptionAndParams<T, G> colorByOption) {
        String datasetColorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets,
                Optional.ofNullable(colorByOption).map(opt -> opt.getGroupByOption().toString()).orElse(null));
        return coloringService.getColor(colorByValue, datasetColorByOption);
    }

    @Override
    protected List<OutputLineChartData> getLineChartData(Datasets datasets,
                                                         GroupByOptionAndParams<T, G> colorByOption,
                                                         Entry<GroupByKey<T, G>,
                                                                 List<Entry<GroupByKey<T, G>,
                                                                         LineChartData>>> trellisEntry) {

        final Function<ErrorLineChartEntry, OutputErrorLineChartEntry> converter =
                se -> new OutputErrorLineChartEntry(se, getColor(se.getColorBy(), datasets, colorByOption));

        return trellisEntry.getValue().stream()
                .map(e -> convert(e, converter))
                .collect(Collectors.toList());
    }

    public void generateColors(Datasets datasets, List<TrellisOptions<G>> colorByOptions) {
        colorByOptions
                .forEach(colorByOption -> colorByOption.getTrellisOptions()
                        .forEach(opt -> getColor(opt, datasets,
                                colorByOption.getTrellisedBy().getGroupByOptionAndParams())));
    }

    private OutputErrorLineChartData convert(Entry<GroupByKey<T, G>, LineChartData> e,
                                             Function<ErrorLineChartEntry, OutputErrorLineChartEntry> converter) {
        final List<OutputErrorLineChartEntry> series
                = e.getValue()
                .getSeries().stream()
                .map(se -> (ErrorLineChartEntry) se)
                .map(converter)
                .collect(Collectors.toList());
        return new OutputErrorLineChartData(Objects.toString(e.getValue().getSeriesBy(),
                Attributes.DEFAULT_EMPTY_VALUE), series);
    }
}
