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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.MultiValueFunction;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.googlecode.cqengine.query.QueryFactory.attribute;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.has;
import static com.googlecode.cqengine.query.QueryFactory.in;
import static com.googlecode.cqengine.query.QueryFactory.not;
import static com.googlecode.cqengine.query.QueryFactory.nullableAttribute;
import static java.util.stream.Collectors.toList;

/**
 * Generic bean for Filters
 */
@ToString(of = "matchedItemsCount")
@Slf4j
public abstract class Filters<T> implements Cloneable, Serializable {

    /**
     * Count matched items for the current set of filters We need this count in the header of the detail-on-demand table
     */
    @Getter
    @Setter
    private Integer matchedItemsCount = 0;

    protected <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getFilterQuery(
            GroupByOptionAndParams<T, G> option, F filter) {
        return getFilterQuery(option.getAttribute().getCqEngineAttr(), filter);
    }

    protected <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getFilterQuery(G option, F filter) {
        return getFilterQuery(option.getAttribute().getCqEngineAttr(), filter);
    }

    protected <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getFilterQueryForMapFilter(
            G option, MapFilter<N, F> filter) {
        if (filter.isValid()) {
            return this.<N, F, G>getQueryForMapFilter(option.getGroupByOptionAndParams(), filter);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getQueryForMapFilter(
            GroupByOptionAndParams<T, G> option, MapFilter<N, F> mapFilter) {
        Query query = null;

        Map<String, F> filterKeyMap = mapFilter.getMap();
        for (String key : filterKeyMap.keySet()) {
            Attribute mapValueAttribute = nullableAttribute(option.getGroupByOption().name(), (T e) -> {
                final Map<String, N> stringObjectMap = (Map<String, N>) Attributes.get(option, e);
                return stringObjectMap == null ? null : stringObjectMap.get(key);
            });
            if (query == null) {
                query = getFilterQuery(mapValueAttribute, mapFilter.getMap().get(key));
            } else {
                query = QueryFactory.and(query, getFilterQuery(mapValueAttribute, mapFilter.getMap().get(key)));
            }
        }
        return query;
    }

    private <N extends Comparable<N>, F extends Filter<N>> Query<T> getFilterQuery(Attribute attribute, F filter) {
        if (filter == null) {
            throw new NullPointerException(attribute.getAttributeName()
                    + " is null in filter. Please make sure you have initialised the class attribute in the Filter class.  Ie "
                    + "SetFilter<String> " + attribute.getAttributeName().toLowerCase() + " = new SetFilter<>();");
        }
        if (filter.isValid()) {
            Query query = getQueryForFilterType(attribute, filter); // query can be null if includeEmptyValues = true
            if (!(filter instanceof RangeFilter)) {
                // range filter already handled IncludeEmptyValues, need proper thinking about as its not correct (sql wrong too)
                if (filter.getIncludeEmptyValues() != null && filter.getIncludeEmptyValues()) {
                    return query == null ? not(has(attribute)) : QueryFactory.or(query, not(has(attribute))); // value == null
                } else {
                    return query;
                }
            } else {
                return query;
            }
        } else {
            return null;
        }
    }

    protected <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getFilterQueryForMultimapFilter(
            Class<N> elementClass, G option, MapFilter<N, F> filter) {
        if (filter.isValid()) {
            return this.<N, F, G>getQueryForMultimapFilter(elementClass, option.getGroupByOptionAndParams(), filter);
        } else {
            return null;
        }
    }

    private <N extends Comparable<N>, F extends Filter<N>, G extends Enum<G> & GroupByOption<T>> Query<T> getQueryForMultimapFilter(
             Class<N> elementClass, GroupByOptionAndParams<T, G> option, MapFilter<N, F> mapFilter) {
        Query query = null;

        Map<String, F> filterKeyMap = mapFilter.getMap();
        for (String key : filterKeyMap.keySet()) {
            final MultiValueFunction<T, N, Collection<N>> oaiMultiValueFunction = (T e) -> {
                final Multimap<String, N> stringObjectMultimap = (Multimap<String, N>) Attributes.get(option, e);
                return stringObjectMultimap.get(key);
            };
            Attribute listMapValueAttribute = nullableAttribute(elementClass, option.getAttribute().getName(), oaiMultiValueFunction);

            if (query == null) {
                query = getFilterQuery(listMapValueAttribute, mapFilter.getMap().get(key));
            } else {
                Query keyQuery = getFilterQuery(listMapValueAttribute, mapFilter.getMap().get(key));
                if (keyQuery != null) {
                    query = QueryFactory.and(query, keyQuery);
                }
            }
        }
        return query;
    }

    private <N extends Comparable<N>> Query<T> getQueryForFilterType(Attribute attribute, Filter<N> filter) {
        Query query = null;
        if (filter instanceof RangeFilter) {
            query = createRangeQuery(attribute, (RangeFilter) filter);
        } else if (filter instanceof MultiValueSetFilter) {
            if (filter.getIncludeEmptyValues() != null && filter.getIncludeEmptyValues()) {
                Attribute nullAttribute = attribute(attribute.getAttributeName(), e -> {
                    for (Object attributeValue : Attributes.getValues(attribute, e)) {
                        if (attributeValue == null) {
                            return null;
                        }
                    }
                    return Attributes.getValues(attribute, e);
                });
                query = not(has(nullAttribute));
            }
            for (Object val : ((MultiValueSetFilter) filter).getValues()) {
                if (query == null) {
                    query = equal(attribute, val);
                } else {
                    query = QueryFactory.or(query, equal(attribute, val));
                }
            }

            if (filter instanceof InverseMultiValueSetFilter) {
                query = not(query);
            }
        } else if (filter instanceof SetFilter) {
            query = in(attribute, ((SetFilter<N>) filter).getValues());
        } else {
            throw new FiltersException("Unknown filter type: " + filter.getClass().getCanonicalName());
        }
        return query;
    }

    /**
     * Clone object, but without specified field
     *
     * @param fieldName
     * @return
     */
    @JsonIgnore
    public Filters cloneWithout(String fieldName) {
        try {
            Filters clone = (Filters) clone();
            Field field = getClass().getDeclaredField(fieldName);
            if (field != null) {
                field.set(clone, null);
            }
            return clone;
        } catch (CloneNotSupportedException | ReflectiveOperationException e) {
            log.error("Ignored exception", e);
            return null;
        }
    }

    /**
     * gets the query with out populations filter (ie subjectIds)
     */
    @JsonIgnore
    public Query<T> getQuery() {
        return getQuery(newArrayList());
    }

    /**
     * gets the query with populations filter (ie subjectIds)
     */
    @JsonIgnore
    public abstract Query<T> getQuery(Collection<String> subjectIds);

    /**
     * Count actual filters
     *
     * @return
     */
    @JsonIgnore
    public int countValidFilters() {
        return getValidFilters().size();
    }

    /*
     * Null invalid filters
     */
    @JsonIgnore
    public void nullInvalidFilters() {
        getFilters().stream().
                filter(f -> !isValidFilter(f.getName())).
                forEach(f -> {
                    try {
                        ReflectionUtils.makeAccessible(f);
                        ReflectionUtils.setField(f, this, null);
                    } catch (Exception ignore) {
                        log.error("Exception ignored", ignore);
                    }
                });
    }

    /*
     * Gets all filters fields
     */
    @JsonIgnore
    public List<Field> getFilters() {
        return newArrayList(getClass().getDeclaredFields());
    }

    /*
     * Gets all filters fields
     */
    @JsonIgnore
    public Field getFilterByName(String name) {
        return getFilters().stream().filter(f -> f.getName().equals(name)).findFirst().orElse(null);
    }

    /*
     * Gets all filters fields.  This doesnt work for Map Filter
     */
    @JsonIgnore
    public List<Field> getValidFilters() {
        return getFilters().stream().filter(f -> isValidFilter(f.getName())).collect(toList());
    }

    /*
     * Gets all none null filters fields
     */
    @JsonIgnore
    public List<Field> getNoneNullFilters() {
        return getFilters().stream().filter(f -> {
            try {
                ReflectionUtils.makeAccessible(f);
                Object value = ReflectionUtils.getField(f, this);

                return value != null;
            } catch (Exception ignore) {
                log.error("Exception ignored", ignore);
                return true;
            }
        }).collect(toList());
    }

    /**
     * Checks if the filters are empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return countValidFilters() == 0;
    }

    /**
     * Detect if Filters bean contains actual filter with specified field name
     *
     * @param fieldName
     * @return
     */
    public boolean isValidFilter(String fieldName) {
        try {

            Field field = getClass().getDeclaredField(fieldName);
            ReflectionUtils.makeAccessible(field);
            Object filter = ReflectionUtils.getField(field, this);
            if (filter != null) {
                if (filter instanceof Filter) {
                    return ((Filter) filter).isValid();
                }
                if (filter instanceof Filters) {
                    return !((Filters) filter).isEmpty();
                }
                if (filter instanceof MapFilter) {
                    return ((MapFilter) filter).isValid();
                }
            }
        } catch (ReflectiveOperationException ignore) {
            log.error("Exception ignored", ignore);
        }
        return false;
    }

    private Query<T> createRangeQuery(Attribute attribute, RangeFilter filter) {
        Query greaterThan = QueryFactory.greaterThanOrEqualTo(attribute, filter.getFrom());
        Query lessThan = QueryFactory.lessThanOrEqualTo(attribute, filter.getTo());
        Query between;

        if (filter.getFrom() == null && filter.getTo() == null) {
            between = QueryFactory.has(attribute); // only items with a value
        } else if (filter.getFrom() == null) {
            between = lessThan;
        } else if (filter.getTo() == null) {
            between = greaterThan;
        } else {
            between = QueryFactory.and(greaterThan, lessThan);
        }

        if (filter.getIncludeEmptyValues() != null && filter.getIncludeEmptyValues()) {
            return QueryFactory.or(
                    between,
                    not(has(attribute)) // value == null
            );
        }

        return between;
    }

    public List<String> getEmptyFilterNames() {
        List<String> emptyFieldNames = new ArrayList<>();
        for (Field field : getClass().getDeclaredFields()) {
            ReflectionUtils.makeAccessible(field);
            Object filter = ReflectionUtils.getField(field, this);
            if (filter instanceof HideableFilter) {
                HideableFilter hideableFilter = (HideableFilter) ReflectionUtils.getField(field, this);
                if (hideableFilter != null && hideableFilter.canBeHidden()) {
                    emptyFieldNames.add(field.getName());
                }
            }
        }
        return emptyFieldNames;
    }

    public List<String> getEmptyFilterNames(Datasets datasets) {
        return getEmptyFilterNames();
    }
}
