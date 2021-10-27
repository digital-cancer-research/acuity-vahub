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

package com.acuity.visualisations.rest.resources.cardiac;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.CardiacService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacMeanRangeValuesRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacTrellisRequest;
import com.acuity.visualisations.rest.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/resources/cardiac/mean-range-plot")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class CardiacMeanRangePlotResource {

    private final CardiacService cardiacService;

    @PostMapping("x-axis")
    @Cacheable
    public AxisOptions<CardiacGroupByOptions> getXAxis(@RequestBody @Valid CardiacRequest requestBody) {
        return cardiacService.getAvailableRangePlotXAxis(requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("trellising")
    @Cacheable
    public List<TrellisOptions<CardiacGroupByOptions>> getAvailableTrellising(@RequestBody @Valid CardiacTrellisRequest requestBody) {
        return cardiacService.getMeanRangeTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption());
    }

    @PostMapping("values")
    @Cacheable
    public List<TrellisedRangePlot<Cardiac, CardiacGroupByOptions>> getValues(
            @RequestBody @Valid CardiacMeanRangeValuesRequest requestBody) {
        return cardiacService.getRangePlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(),
                StatType.MEDIAN);
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(
            @RequestBody @Valid CardiacMeanRangeSelectionRequest requestBody) {
        return cardiacService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
