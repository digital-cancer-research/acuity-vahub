package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.common.aspect.TimeMe;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Base class for events filters, ie Labs and Vitals. Not population
 */
public abstract class AbstractEventFilterService<T, V extends Filters<T>> extends AbstractFilterService<T, V> {

    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private PopulationRawDataFilterService subjectService;

    @TimeMe
    @Override
    public FilterResult<T> query(FilterQuery<T> filterQuery) {
        Validate.isTrue(filterQuery.isEventFilterQuery(), "FilterQuery needs to be of type event filter and not population filter");
        
        FilterResult<Subject> filteredSubjects = subjectService.getPopulationFilterResult(filterQuery.getPopulationFilterQuery());
        return queryImpl(filterQuery, filteredSubjects);
    }

    public V getAvailableFilters(Collection<T> events, Filters<T> eventFilters, Collection<Subject> population, PopulationFilters populationFilters) {
        return getAvailableFilters(new FilterQuery<>(events, eventFilters, population, populationFilters));
    }
}
