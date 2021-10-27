package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by knml167 on 8/24/2017.
 * This declares methods for classes aware of chart trellis and grouping(axis) options
 * to support returning copies limited by certain options
 */
public interface LimitableBySettings<T, P> extends Serializable {
    /**
     * Returns instance retaining only provided settings excluding any others
     * */
    T limitedBySettings(Set<ChartGroupByOptions.ChartGroupBySetting> settings);

    /**
     * Returns instance retaining only settings annotated as {@link PopulationGroupingOption} excluding any others.
     * Returned instance type is changed to Population settings type
     * */
    P limitedByPopulationTrellisOptions();

    /**
     * Returns instance retaining only provided settings excluding any others
     * */
    default T limitedBySettings(ChartGroupByOptions.ChartGroupBySetting... settings) {
        return limitedBySettings(new HashSet<>(Arrays.asList(settings)));
    }

    /**
     * Returns instance retaining only trellis options excluding any chart options
     * */
    default T limitedByTrellisOptions() {
        return limitedBySettings();
    }
}
