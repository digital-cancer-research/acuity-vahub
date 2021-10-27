package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface OverTimeChartSupportService<T, G extends Enum<G> & GroupByOption<T>> {

    List<TrellisedOvertime<T, G>> getLineBarChart(Datasets datasets,
                                                  ChartGroupByOptionsFiltered<T, G> eventSettings,
                                                  Filters<T> filters, PopulationFilters populationFilters);

    AxisOptions<G> getAvailableOverTimeChartXAxis(Datasets datasets, Filters<T> filters,
                                                  PopulationFilters populationFilters);

    /**
     * This method returns chart settings for population line on overtime charts
     * */
    default ChartGroupByOptions<Subject, PopulationGroupByOptions> getPopulationLineSettings(
            ChartGroupByOptionsFiltered<T, G> eventSettings, Collection<T> filteredResult) {
        final ChartGroupByOptions.GroupByOptionAndParams<T, G> xAxisOption = eventSettings.getSettings()
                .getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS);
        final GroupByOption.Params xAxisParams = xAxisOption == null ? null : xAxisOption.getParams();
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Subject, PopulationGroupByOptions> populationOptions
                = eventSettings.getSettings().limitedByPopulationTrellisOptions().toBuilder();
        return xAxisOption == null ? populationOptions.build() : populationOptions
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                        PopulationGroupByOptions.ON_STUDY.getGroupByOptionAndParams(
                                (xAxisParams == null ? GroupByOption.Params.builder() : xAxisParams.toBuilder())
                                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                                        .with(GroupByOption.Param.AXIS_START,
                                                filteredResult.stream()
                                                        .map(e -> Attributes.get(xAxisOption, e))
                                                        .flatMap(e -> e instanceof Collection ? ((Collection) e).stream()
                                                                .map(t -> t) : Collections.singleton((Comparable) e).stream())
                                                        .min(Comparator.naturalOrder()).orElse(null))
                                        .with(GroupByOption.Param.AXIS_END,
                                                filteredResult.stream().map(e -> Attributes.get(xAxisOption, e))
                                                        .flatMap(e -> e instanceof Collection ? ((Collection) e).stream()
                                                                .map(t -> t) : Collections.singleton((Comparable) e).stream())
                                                        .max(Comparator.naturalOrder()).orElse(null))
                                        .build()
                        ))
                .build();
    }


}
