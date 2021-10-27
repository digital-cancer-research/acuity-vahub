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

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Range;

import java.util.Map;

/**
 * Created by knml167 on 8/23/2017.
 * This extends {@link ChartSelectionItem} adding range, required to support selection on certain charts
 * having natural axis and supporting partial selection of chart elements, like boxplot
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ChartSelectionItemRange<T, G extends Enum<G> & GroupByOption<T>, R extends Comparable<R>> extends ChartSelectionItem<T, G> {

    @Data
    private static class JsonRange<R> {
        private R minimum;
        private R maximum;
    }
    @Getter
    private Range<R> range;

    public ChartSelectionItemRange(Map<G, Object> selectedTrellises,
                                   Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems,
                                   Range<R> range) {
        super(selectedTrellises, selectedItems);
        this.range = range;
    }

    public static <T, G extends Enum<G> & GroupByOption<T>, R extends Comparable<R>> ChartSelectionItemRange<T, G, R> of(
            Map<G, Object> selectedTrellises,
            Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems,
            R from, R to
    ) {
        return new ChartSelectionItemRange<>(selectedTrellises, selectedItems, Range.between(from, to));
    }

    @JsonCreator
    public static <T, G extends Enum<G> & GroupByOption<T>, R extends Comparable<R>> ChartSelectionItemRange<T, G, R> of(
            @JsonProperty("selectedTrellises") Map<G, Object> selectedTrellises,
            @JsonProperty("selectedItems") Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems,
            @JsonProperty("range") JsonRange<R> range
    ) {
        return new ChartSelectionItemRange<>(selectedTrellises, selectedItems,  Range.between(range.getMinimum(), range.getMaximum()));
    }
}
