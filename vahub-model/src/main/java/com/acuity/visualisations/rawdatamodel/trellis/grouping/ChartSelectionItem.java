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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * Created by knml167 on 8/23/2017.
 * This stores exact chart element path (trellis and chart options required to find items matched user selection)
 */
@Getter
@EqualsAndHashCode
@ToString
public class ChartSelectionItem<T, G extends Enum<G> & GroupByOption<T>> implements Serializable {
    @Getter
    private Map<G, Object> selectedTrellises;
    @Getter
    private Map<ChartGroupBySetting, Object> selectedItems;

    protected ChartSelectionItem(Map<G, Object> selectedTrellises, Map<ChartGroupBySetting, Object> selectedItems) {
        this.selectedTrellises = Collections.unmodifiableMap(selectedTrellises);
        this.selectedItems = Collections.unmodifiableMap(selectedItems);
    }

    @JsonCreator
    public static <T, G extends Enum<G> & GroupByOption<T>> ChartSelectionItem<T, G> of(
            @JsonProperty("selectedTrellises") Map<G, Object> selectedTrellises,
            @JsonProperty("selectedItems") Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems
    ) {
        return new ChartSelectionItem<>(selectedTrellises, selectedItems);
    }
}
