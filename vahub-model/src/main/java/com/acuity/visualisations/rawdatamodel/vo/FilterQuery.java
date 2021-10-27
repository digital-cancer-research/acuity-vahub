package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;


@EqualsAndHashCode
public class FilterQuery<T> implements Serializable {

    @Getter
    private Collection<T> events = newArrayList();
    @Getter
    private Filters<T> filters;
    @Getter
    private FilterQuery<Subject> populationFilterQuery;

    public FilterQuery(Collection<Subject> events, PopulationFilters populationFilters) {
        this.events = (Collection<T>) events;
        this.filters = (Filters<T>) populationFilters;
    }

    public FilterQuery(Collection<T> events, Filters<T> eventFilters, Collection<Subject> population, PopulationFilters populationFilters) {
        this.events = events;
        this.filters = eventFilters;
        this.populationFilterQuery = new FilterQuery<>(population, populationFilters);
    }

    public FilterQuery(Collection<Subject> events, Filters<Subject> populationFilters) {
        this.events = (Collection<T>) events;
        this.filters = (Filters<T>) populationFilters;
    }

    public boolean isEventFilterQuery() {
        return filters != null;
    }

    public boolean isPopulationFilterQuery() {
        return populationFilterQuery == null;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("isEventFilterQuery", isEventFilterQuery())
                .append("isPopulationFilterQuery", isPopulationFilterQuery());

        return builder.toString();
    }
}
