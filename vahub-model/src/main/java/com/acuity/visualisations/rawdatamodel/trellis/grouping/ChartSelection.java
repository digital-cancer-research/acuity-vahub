package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by knml167 on 8/23/2017.
 * This is a generic VO to pass chart selection information to get some details on the selection
 */
@Getter
@EqualsAndHashCode
@ToString
public final class ChartSelection<T, G extends Enum<G> & GroupByOption<T>, I extends ChartSelectionItem<T, G>> implements Serializable {
    @Getter
    @JsonProperty
    private ChartGroupByOptions<T, G> settings;
    @Getter
    @JsonProperty
    private Set<I> selectionItems;

    @JsonCreator
    public ChartSelection(@JsonProperty("settings") ChartGroupByOptions<T, G> settings, @JsonProperty("selectionItems") Collection<I> selectionItems) {
        this.settings = settings;
        this.selectionItems = Collections.unmodifiableSet(new HashSet<I>(selectionItems));
    }

    public static <T, G extends Enum<G> & GroupByOption<T>, I extends ChartSelectionItem<T, G>> ChartSelection<T, G, I> of(
            ChartGroupByOptions<T, G> settings, Collection<I> selectionItems) {
        return new ChartSelection<>(settings, selectionItems);
    }
}
