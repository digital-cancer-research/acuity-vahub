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

package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.AmlCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.CBioCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.dod.SsvCommonService;
import com.acuity.visualisations.rawdatamodel.service.filters.AbstractEventFilterService;
import com.acuity.visualisations.rawdatamodel.service.filters.PopulationRawDataFilterService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasAssociatedAe;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DodUtil.EVENT_ID;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.STUDY_ID;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.STUDY_PART;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.SUBJECT_ID;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Created by knml167 on 6/16/2017.
 * This is base class implementing common methods for chart services
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEventService<R extends HasSubjectId & HasStringId, T extends SubjectAwareWrapper<R>, G extends Enum<G> & GroupByOption<T>>
        implements DoDService<T> {

    @Autowired
    protected DoDCommonService doDCommonService;
    @Autowired
    protected SsvCommonService ssvCommonService;
    @Autowired
    protected AmlCommonService amlCommonService;
    @Autowired
    protected CBioCommonService cBioCommonService;
    /**
     * Added to allow multiple SubjectAwareDatasetsDataProvider for a view, ie Ae
     */
    @Autowired
    private List<SubjectAwareDatasetsDataProvider<R, T>> eventDataProviders;
    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    protected AbstractEventFilterService<T, Filters<T>> eventFilterService;
    @Autowired
    private PopulationRawDataFilterService populationFilterService;

    private static final List<String> COMMON_COLUMN_NAMES = asList(STUDY_ID, STUDY_PART, SUBJECT_ID, EVENT_ID);

    /*
     * Hacked in with discussion from glen and nikolay.  This is they least messy way to implement without affecting the rest of the app for
     * this edge case of multiple data providers for the same data type.  The AeService will override this method to determine
     * which eventDataProvider to use depending on the filters (or datasets).
     */
    protected SubjectAwareDatasetsDataProvider<R, T> getEventDataProvider(Datasets datasets, Filters<T> filters) {
        return eventDataProviders.get(0); // everyone will have 1 but aes, and ae overrides this method
    }

    protected SubjectAwareDatasetsDataProvider<R, T> getEventDataProvider(Datasets datasets) {
        return getEventDataProvider(datasets, null);
    }

    /**
     * Returns {@link FilterResult} according to provided event and population filters
     */
    public FilterResult<T> getFilteredData(Datasets datasets, Filters<T> filters,
                                           PopulationFilters populationFilters) {

        return getFilteredData(datasets, filters, populationFilters, null);
    }

    /**
     * Returns {@link FilterResult} according to provided event and population filters,
     * additionally filtered by selected trellis elements provided at {@link ChartGroupByOptionsFiltered} agrument
     */
    public FilterResult<T> getFilteredData(Datasets datasets, Filters<T> filters,
                                           PopulationFilters populationFilters, ChartGroupByOptionsFiltered<T, G> eventSettings) {

        return getFilteredData(datasets, filters, populationFilters, eventSettings, null);
    }

    /**
     * Returns {@link FilterResult} according to provided event and population filters,
     * additionally filtered by selected trellis elements provided at {@link ChartGroupByOptionsFiltered} agrument
     * and custom filter predicate
     */
    public FilterResult<T> getFilteredData(Datasets datasets, Filters<T> filters,
                                           PopulationFilters populationFilters, ChartGroupByOptionsFiltered<T, G> eventSettings, Predicate<T> eventPredicate) {

        FilterQuery<T> filterQuery = getFilterQuery(datasets, filters, populationFilters, eventSettings, eventPredicate);
        return eventFilterService.query(filterQuery);
    }

    public FilterResult<T> getFilteredData(Collection<T> events, Datasets datasets, Filters<T> eventFilters,
                                           PopulationFilters populationFilters) {

        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);
        FilterQuery<T> filterQuery = new FilterQuery<>(events, eventFilters, subjects, populationFilters);
        return eventFilterService.query(filterQuery);
    }

    protected FilterQuery<T> getFilterQuery(Datasets datasets, Filters<T> filters, PopulationFilters populationFilters) {

        return getFilterQuery(datasets, filters, populationFilters, null, null);
    }

    protected FilterQuery<T> getFilterQuery(Datasets datasets, Filters<T> filters, PopulationFilters populationFilters,
                                            ChartGroupByOptionsFiltered<T, G> eventSettings, Predicate<T> eventPredicate) {
        Collection<T> events = getEventDataProvider(datasets, filters).loadData(datasets).stream()
                .filter(composeTrellisOptionsPredicate(eventSettings))
                .filter(composeXAxisBasedPredicate(eventSettings))
                .filter(Optional.ofNullable(eventPredicate).orElse(t -> true))
                .collect(toList());

        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);
        return new FilterQuery<>(events, filters, subjects, populationFilters);
    }

    private Predicate<T> composeXAxisBasedPredicate(ChartGroupByOptionsFiltered<T, G> eventSettings) {
        return Optional.ofNullable(eventSettings)
                .map(s -> s.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                .map(xAxisOption -> composeTimestampTypeBasedPredicate(xAxisOption)
                        .and(composeEventTypeSpecificXAxisBasedPredicate(xAxisOption)))
                .orElse(t -> true);
    }

    private Predicate<T> composeTimestampTypeBasedPredicate(ChartGroupByOptions.GroupByOptionAndParams<T, G> xAxisOption) {
        GroupByOption.TimestampType timestampType = Optional.ofNullable(xAxisOption.getParams()).map(GroupByOption.Params::getTimestampType).orElse(null);
        if (timestampType == null || timestampType == GroupByOption.TimestampType.DATE) {
            return t -> true;
        }

        return t -> xAxisOption.getGroupByOption().getAttribute().getFunction().apply(t) != null
                && getDateExtractor(timestampType, xAxisOption).apply(t) != null;
    }

    private Function<T, Date> getDateExtractor(GroupByOption.TimestampType timestampType,
                                               ChartGroupByOptions.GroupByOptionAndParams<T, G> xAxisOption) {
        switch (timestampType) {
            case DAYS_SINCE_RANDOMISATION:
            case DAYS_HOURS_SINCE_RANDOMISATION:
            case WEEKS_SINCE_RANDOMISATION:
                return t -> t.getSubject().getDateOfRandomisation();
            case DAYS_SINCE_FIRST_DOSE:
            case DAYS_HOURS_SINCE_FIRST_DOSE:
            case WEEKS_SINCE_FIRST_DOSE:
                return t -> t.getSubject().getDateOfFirstDose();
            case DAYS_SINCE_FIRST_DOSE_OF_DRUG:
            case DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG:
            case WEEKS_SINCE_FIRST_DOSE_OF_DRUG:
                return t -> t.getSubject().getDateOfFirstDoseOfDrug(xAxisOption.getParams().getStr(GroupByOption.Param.DRUG_NAME));
            default:
                return t -> null;
        }
    }

    protected Predicate<T> composeEventTypeSpecificXAxisBasedPredicate(ChartGroupByOptions.GroupByOptionAndParams<T, G> xAxisOption) {
        return t -> true;
    }

    /**
     * All trellis options must match by at least one value
     * for example, eventSettings.getFilterByTrellisOptions() can look as following:
     * [{'option1' : 'value1'}, {'option1' : 'value2'}, {'option2' : 'value3'}]
     * In this case the event will match if its value corresponding to option1 is 'value1' OR 'value2',
     * AND its value corresponding to option2 is 'value3'
     */
    private Predicate<T> composeTrellisOptionsPredicate(ChartGroupByOptionsFiltered<T, G> eventSettings) {
        if (Optional.ofNullable(eventSettings)
                .map(ChartGroupByOptionsFiltered::getFilterByTrellisOptions)
                .map(Collection::isEmpty)
                .orElse(Boolean.TRUE)) {
            return t -> true;
        }

        Map<G, List<Object>> groupedByOption = eventSettings.getFilterByTrellisOptions().stream()
                .flatMap(e -> e.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        return t -> groupedByOption.entrySet().stream()
                .allMatch(option -> option.getValue().stream()
                        .anyMatch(optionValue -> {
                            // trellis option from settings.trellisOptions, corresponding to filterByTrellisOption,
                            // should be tried first, because it can have context in params (i.e. for Lab plots)
                            GroupByOptionAndParams<T, G> trellisOption = eventSettings.getSettings().getTrellisOption(option.getKey());
                            // trellis option corresponding to filterByTrellisOption can be not inside settings.trellisOptions,
                            // because sometimes it is necessary just to use the option only for filtering, not for trellising
                            if (trellisOption == null) {
                                trellisOption = option.getKey().getGroupByOptionAndParams();
                            }
                            final Object eventValue = Attributes.get(trellisOption, t);
                            return stringEquals(optionValue, eventValue);
                        }));
    }

    private boolean stringEquals(Object o1, Object o2) {
        final String s1 = o1 == null ? null : o1.toString();
        final String s2 = o2 == null ? null : o2.toString();
        return Objects.equals(s1, s2);
    }

    protected FilterResult<Subject> getFilteredPopulationData(Datasets datasets, PopulationFilters populationFilters) {
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);
        return populationFilterService.query(new FilterQuery<>(subjects, populationFilters));
    }

    /**
     * Returns available filters
     */
    public Filters<T> getAvailableFilters(Datasets datasets, Filters<T> eventFilters, PopulationFilters populationFilters) {
        FilterQuery<T> filterQuery = getFilterQuery(datasets, eventFilters, populationFilters);
        return eventFilterService.getAvailableFilters(filterQuery);
    }

    /**
     * This method can be used if any grouping options require some data context.
     * It will put {@link com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param#CONTEXT}
     * into provided {@link ChartGroupByOptions} and return updated settings
     */
    public final ChartGroupByOptions<T, G> getOptionsWithContext(ChartGroupByOptions<T, G> options, Supplier<Map<G, Object>> supplier) {
        final Map<G, Object> attributesContext = supplier.get();
        return attributesContext == null || attributesContext.isEmpty() ? options : options.supplyContext(attributesContext);
    }

    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, Set ids, List<SortAttrs> sortAttrs, long from, long count) {

        final Collection<T> events = getEventDataProvider(datasets).loadData(datasets).stream()
                .filter(e -> ids.contains(e.getId())).collect(Collectors.toList());
        return getNotEmptyDataRows(doDCommonService.getColumnData(
                DatasetType.fromDatasets(datasets), events, sortAttrs, from, count, true));
    }

    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, String subjectId, Filters<T> eventFilters) {

        final FilterResult<T> filteredData = getFilteredData(datasets, eventFilters, PopulationFilters.empty(),
                null, e -> Objects.equals(subjectId, e.getSubjectCode()) || Objects.equals(subjectId, e.getSubjectId()));
        return getNotEmptyDataRows(doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), filteredData.getFilteredEvents(),
                Collections.emptyList(), 0, Integer.MAX_VALUE, true));
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<T> eventFilters) {
        final FilterResult<T> filteredData = getFilteredData(datasets, eventFilters, PopulationFilters.empty(), null,
                s -> subjectId.equals(s.getSubjectId()) || subjectId.equals(s.getSubjectCode()));
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
    }

    @Override
    public void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<T> filters, PopulationFilters populationFilters) throws IOException {
        final FilterResult<T> filteredData = getFilteredData(datasets, filters, populationFilters);
        writeEventsDoDCsv(writer, DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
    }

    @Override
    public void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) throws IOException {
        final Collection<T> events = getEventDataProvider(datasets).loadData(datasets).stream()
                .filter(e -> ids.contains(e.getId())).collect(Collectors.toList());
        writeEventsDoDCsv(writer, DatasetType.fromDatasets(datasets), events);
    }

    protected void writeEventsDoDCsv(Writer writer, DatasetType datasetType, Collection<T> events) {
        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(datasetType, events);
        List<Map<String, String>> data = doDCommonService.getColumnData(datasetType, events);
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    public List<String> getAssociatedAeNumbersFromEventIds(
            Datasets datasets, Filters<T> filters, PopulationFilters populationFilters, List<String> eventIds) {

        FilterResult<T> filtered = getFilteredData(datasets, filters, populationFilters);

        return filtered.stream().
                filter(event -> eventIds.contains(event.getId())).
                filter(event -> event instanceof HasAssociatedAe).
                map(event -> (HasAssociatedAe) event).
                map(HasAssociatedAe::getAeNumber).
                distinct().collect(toList());
    }

    public List<String> getSubjects(Datasets datasetsObject, Filters<T> eventsFilters, PopulationFilters populationFilters) {
        FilterResult<T> filterResult = getFilteredData(datasetsObject, eventsFilters, populationFilters);
        return filterResult.getFilteredResult().stream().map(SubjectAwareWrapper::getSubjectId).distinct().collect(toList());
    }

    public void writeAMLDataCsv(Datasets datasets, Writer writer, Class<T> entityCls, Class<R> entityRawCls) {
        writeAMLDataCsv(datasets, writer, entityCls, entityRawCls, ds -> getEventDataProvider(ds).loadData(ds));
    }

    protected void writeAMLDataCsv(Datasets datasets, Writer writer, Class<T> entityCls, Class<R> entityRawCls,
                         Function<Datasets, Collection<T>> eventsResolver) {
        Collection<T> events = eventsResolver.apply(datasets);
        final DatasetType datasetType = DatasetType.fromDatasets(datasets);
        Map<String, String> columnsTitles = amlCommonService.getColumns(datasetType, entityCls, entityRawCls);
        List<Map<String, String>> data = amlCommonService.getColumnData(datasetType, events, false);
        amlCommonService.writeCsv(data, columnsTitles, writer);
    }

    protected SelectionDetail getSelectionBySubjectIds(FilterResult<T> filteredEvents, Set<String> subjectIds) {
        final Collection<T> allEvents = filteredEvents.getAllEvents();
        final List<T> matchedEvents = filteredEvents.getFilteredResult().stream()
                .filter(t -> subjectIds.contains(t.getSubjectId()))
                .collect(toList());

        return SelectionDetail.builder()
                .eventIds(matchedEvents.stream().map(EventWrapper::getId).collect(Collectors.toSet()))
                .subjectIds(subjectIds)
                .totalEvents(allEvents.size())
                .totalSubjects(filteredEvents.getPopulationFilterResult().size())
                .build();
    }

    private List<Map<String, String>> getNotEmptyDataRows(List<Map<String, String>> data) {
        return data.stream()
                .filter(row -> row.entrySet().stream()
                        .filter(e -> !COMMON_COLUMN_NAMES.contains(e.getKey())).anyMatch(e -> Objects.nonNull(e.getValue())))
                .collect(toList());
    }
}
