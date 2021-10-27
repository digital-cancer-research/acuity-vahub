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
