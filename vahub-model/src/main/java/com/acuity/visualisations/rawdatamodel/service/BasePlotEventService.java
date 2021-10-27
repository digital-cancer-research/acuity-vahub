package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by knml167 on 6/16/2017.
 * This is extension of {@link BaseEventService} adding method to get available grouping(axis) options
 */
public abstract class BasePlotEventService<R extends HasSubjectId & HasStringId, T extends SubjectAwareWrapper<R>,
        G extends Enum<G> & GroupByOption<T>> extends BaseEventService<R, T, G> {

    /**
     * Gets available grouping(axis) options according to filtered data
     */
    @SafeVarargs
    protected final AxisOptions<G> getAxisOptions(Datasets datasets, Filters<T> filters,
                                                  PopulationFilters populationFilters, G... options) {
        return getAxisOptions(datasets, filters, populationFilters, null, null, options);
    }

    /**
     * Gets available grouping(axis) options according to filtered data
     */
    @SafeVarargs
    protected final AxisOptions<G> getAxisOptions(Datasets datasets, Filters<T> filters,
                                                  PopulationFilters populationFilters,
                                                  ChartGroupByOptionsFiltered<T, G> eventSettings,
                                                  Predicate<T> eventPredicate, G... options) {
        final FilterResult<T> filteredData = getFilteredData(datasets, filters, populationFilters, eventSettings, eventPredicate);
        return getAxisOptions(filteredData.getFilteredResult(), options);
    }

    /**
     * Gets available grouping(axis) options according to the specified events
     */
    @SafeVarargs
    protected final AxisOptions<G> getAxisOptions(Collection<T> events, G... options) {

        //getting distinct drugs to provide with drug-aware options like "days since first dose of <drug>"
        final List<String> drugs = events.stream()
                .flatMap(e -> e.getSubject().getDrugFirstDoseDate().entrySet().stream().filter(d -> d.getValue() != null)
                        .map(Map.Entry::getKey)).distinct().collect(Collectors.toList());
        //checking if any subjects have randomisation date to decide on are randimisation-related options available
        final boolean hasRand = events.stream().anyMatch(e -> e.getSubject().getDateOfRandomisation() != null);

        return new AxisOptions<>(
                Stream.of(options)
                        //checking if suggested option has any not-empty values in current data
                        .filter(o ->
                                events.stream()
                                        .map(e -> Attributes.<T, G>get(o.getGroupByOptionAndParams(), e))
                                        .flatMap(e -> {
                                            if (e instanceof Collection) {
                                                return ((Collection<?>) e).stream();
                                            }
                                            return Stream.of(e);
                                        })
                                        .anyMatch(e -> e != null && !Attributes.DEFAULT_EMPTY_VALUE.equals(e.toString()))
                        )

                        //returning filtered options having some data with properties
                        .map(o -> new AxisOption<>(o,
                                Attributes.isTimestampOption(o),
                                Attributes.isTimestampOptionSupportDuration(o),
                                Attributes.isBinableOption(o),
                                Attributes.hasDrugOption(o)))
                        .collect(Collectors.toList()), hasRand, drugs);
    }
}
