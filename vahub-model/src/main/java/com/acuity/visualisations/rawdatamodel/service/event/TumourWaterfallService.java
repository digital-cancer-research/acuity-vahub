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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.compatibility.TumourChartColoringService;
import com.acuity.visualisations.rawdatamodel.service.plots.SimpleSelectionSupportService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions.AssessmentType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LimitableBySettings;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputWaterfallData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputWaterfallEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedWaterfallChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.plots.WaterfallEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Service
public class TumourWaterfallService extends AssessedTargetLesionService
        implements SimpleSelectionSupportService<AssessedTargetLesion, ATLGroupByOptions> {

    public static final String WITH_BEST_RESPONSE_EVENTS = "withBestResponseEvents";
    @Autowired
    private TumourChartColoringService coloringService;

    public List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> getTumourDataOnWaterfall(Datasets datasets,
                                                                                                           AssessedTargetLesionFilters tumourFilters,
                                                                                                           PopulationFilters populationFilters,
                                                                                                           ChartGroupByOptionsFiltered<AssessedTargetLesion,
                                                                                                                                    ATLGroupByOptions>
                                                                                                                   settings) {

        FilterResult<AssessedTargetLesion> filtered = getFilteredData(datasets, tumourFilters, populationFilters, settings);
         /*Events should be unique within settings attributes combination, so simply transform to set*/
        final Map<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>, Set<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>>> groupedByTrellis =
                filtered.stream().map(e -> Attributes.get(settings.getSettings(), e)).collect(
                        Collectors.groupingBy(LimitableBySettings::limitedByTrellisOptions, Collectors.toSet())
                );
        return groupedByTrellis.entrySet().stream().map(trellisSet -> {
            final Set<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>> items = trellisSet.getValue();

            final List<String> xCategories = items.stream()
                    .sorted(
                            Comparator.<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>, Comparable>comparing(e -> (Comparable) e.getValue(Y_AXIS))
                                    .reversed()
                                    .thenComparing(e -> Objects.toString(e.getValue(X_AXIS)))
                    )
                    .map(e -> Objects.toString(e.getValue(X_AXIS))).distinct().collect(Collectors.toList());


            final List<TrellisOption<AssessedTargetLesion, ATLGroupByOptions>> trellisOptions = trellisSet.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());

            final List<WaterfallEntry> entries = items.stream().map(i -> {
                int x = xCategories.indexOf(i.getValue(X_AXIS).toString());
                Object value = i.getValue(Y_AXIS);
                return new WaterfallEntry(x, toDouble(value),
                        Objects.toString(i.getValue(COLOR_BY)));
            }).sorted(Comparator.comparing(WaterfallEntry::getX)).collect(Collectors.toList());
            return new TrellisedWaterfallChart<>(trellisOptions, new OutputWaterfallData(xCategories, entries.stream()
                    .map(e -> new OutputWaterfallEntry(e, coloringService.getColor(e.getName())))
                    .collect(Collectors.toList())));
        }).collect(Collectors.toList());
    }

    public SelectionDetail getWaterfallSelectionDetails(Datasets datasets, AssessedTargetLesionFilters filters, PopulationFilters populationFilters,
                                                        ChartSelection<AssessedTargetLesion, ATLGroupByOptions,
                                                                ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> selection,
                                                        ChartGroupByOptionsFiltered<AssessedTargetLesion,
                                                                ATLGroupByOptions> eventSettings) {

        FilterResult<AssessedTargetLesion> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);
        return getSelectionDetails(filtered, selection);
    }

    public AssessmentAxisOptions<ATLGroupByOptions> getAvailableWaterfallYAxis(Datasets datasets,
                                                                                                 Filters<AssessedTargetLesion> filters,
                                                                                                 PopulationFilters populationFilters) {

        AxisOptions<ATLGroupByOptions> axisOptions = getAxisOptions(datasets, filters, populationFilters, ATLGroupByOptions.PERCENTAGE_CHANGE);

        List<Integer> weeks = getAssessmentWeeks(datasets, filters, populationFilters, false);

        // AssessmentType.WEEK is not passed to assessmentTypes because weeks are already transformed into "Week N" option on the front-end
        return new AssessmentAxisOptions<>(axisOptions, weeks, new AssessmentType[] {AssessmentAxisOptions.AssessmentType.BEST_CHANGE});
    }


    public List<TrellisOptions<ATLGroupByOptions>> getWaterfallColorBy(Datasets datasets, PopulationFilters populationFilters,
                                                                       AssessedTargetLesionFilters assessedTargetLesionFilters,
                                                                       ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settings) {

        FilterResult<AssessedTargetLesion> filtered = getFilteredData(datasets, assessedTargetLesionFilters, populationFilters);
        Map<GroupByOption.Param, Object> yAxisParams = settings.getSettings().getOptions().get(Y_AXIS).getParamMap();
        AssessmentType assessmentType = AssessmentAxisOptions.getAssessmentType(yAxisParams);
        if (AssessmentAxisOptions.AssessmentType.WEEK.equals(assessmentType)) {
            return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ATLGroupByOptions.ASSESSMENT_RESPONSE, ATLGroupByOptions.BEST_RESPONSE);
        }
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ATLGroupByOptions.BEST_RESPONSE);
    }

    @Override
    public Filters<AssessedTargetLesion> getAvailableFilters(Datasets datasets, Filters<AssessedTargetLesion> eventFilters,
                                                             PopulationFilters populationFilters) {
        throw new UnsupportedOperationException("ChartGroupByOptionsFiltered parameter is required. "
                + "Use getAvailableWaterfallFilters method instead");
    }

    /**
     * Result depends on the Y-Axis type
     */
    public Filters<AssessedTargetLesion> getAvailableWaterfallFilters(Datasets datasets, Filters<AssessedTargetLesion> eventFilters,
                                                                      PopulationFilters populationFilters,
                                                                      ChartGroupByOptionsFiltered<AssessedTargetLesion,
                                                                              ATLGroupByOptions> eventSettings) {
        FilterQuery<AssessedTargetLesion> filterQuery = getFilterQuery(datasets, eventFilters, populationFilters, eventSettings, null);
        return eventFilterService.getAvailableFilters(filterQuery);
    }

    /**
     * The logic is as follows:
     * On the waterfall plot each column corresponds to one subject.
     * For each selected column, if the event to display (best percentage change or particular week's assessment,
     * depending on the selected Y axis option) and the best assessment event (the latest event with assessment response
     * equal to the best assessment response for particular subject) happened at the same visit,
     * we consider it as one event (event-to-display only). If it were different visits, we consider both events
     * (event-to-display and best-assessment-event) for all calculations that affect event count (selection, filters).
     * For the plot request itself always only events-to-display are considered.
     *
     */
    @Override
    protected FilterQuery<AssessedTargetLesion> getFilterQuery(Datasets datasets, Filters<AssessedTargetLesion> filters,
                                                               PopulationFilters populationFilters,
                                            ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> eventSettings,
                                                               Predicate<AssessedTargetLesion> eventPredicate) {
        if (eventSettings == null) {
            return super.getFilterQuery(datasets, filters, populationFilters, null, eventPredicate);
        }
        Map<GroupByOption.Param, Object> yAxisParams = eventSettings.getSettings().getOptions().get(Y_AXIS).getParamMap();
        AssessmentType assessmentType = AssessmentAxisOptions.getAssessmentType(yAxisParams);
        int weekNumber = (int) yAxisParams.getOrDefault(GroupByOption.Param.WEEK_NUMBER, 0);
        Pair<Predicate<AssessedTargetLesion>, Function<List<AssessedTargetLesion>,
                List<AssessedTargetLesion>>> predicateAndPostFilter = getFiltrationParams(assessmentType, weekNumber);

        List<AssessedTargetLesion> events = getEventDataProvider(datasets, filters).loadData(datasets).stream()
                .filter(e -> predicateAndPostFilter.getLeft().test(e)).collect(toList());
        List<AssessedTargetLesion> eventsWithPostFilterApplied = predicateAndPostFilter.getRight().apply(events);
        if (WITH_BEST_RESPONSE_EVENTS.equals(yAxisParams.get(GroupByOption.Param.VALUE))) {
            eventsWithPostFilterApplied = addBestResponseEvents(eventsWithPostFilterApplied);
        }
        Collection<Subject> subjects = getPopulationDatasetsDataProvider().loadData(datasets);
        return new FilterQuery<>(eventsWithPostFilterApplied, filters, subjects, populationFilters);
    }

    private List<AssessedTargetLesion> addBestResponseEvents(Collection<AssessedTargetLesion> eventsWithPostFilterApplied) {
        List<AssessedTargetLesion> eventsWithBestResponseEventsAdded = new ArrayList<>();
        eventsWithPostFilterApplied.forEach(e -> {
            if (!e.getEvent().getBestResponse().equals(e.getEvent().getResponse())
                    && e.getEvent().getBestResponseEvent() != null) {
                eventsWithBestResponseEventsAdded.add(new AssessedTargetLesion(e.getEvent().getBestResponseEvent(), e.getSubject()));
            }
            eventsWithBestResponseEventsAdded.add(e);
        });
        return eventsWithBestResponseEventsAdded;
    }

    private static Double toDouble(Object value) {
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    private Pair<Predicate<AssessedTargetLesion>,
            Function<List<AssessedTargetLesion>, List<AssessedTargetLesion>>> getFiltrationParams(AssessmentType yAxisType,
                                                                                                          int weekNumber) {
        Predicate<AssessedTargetLesion> predicate;
        Function<List<AssessedTargetLesion>, List<AssessedTargetLesion>> resultFilter;
        switch (yAxisType) {
            case WEEK:
                predicate = atl -> atl.isNotBeforeBaseline()
                        && atl.getEvent().getSumPercentageChangeFromBaseline() != null;
                resultFilter = result -> closestToIdealAssessmentDayFilter(weekNumber).apply(result);
                break;
            case BEST_CHANGE:
            default:
                predicate = atl -> atl.getEvent().isBestPercentageChange();
                resultFilter = result -> bestPercentageChangeLatestEvents().apply(result);
        }
        return Pair.of(predicate, resultFilter);
    }

    private Function<List<AssessedTargetLesion>, List<AssessedTargetLesion>> closestToIdealAssessmentDayFilter(int weekNumber) {
        return assessedTargetLesions -> getAtlsClosestToIdealAssessmentDaysAsList(assessedTargetLesions,
                singletonList(weekNumber), groupTumoursBySubject(), false);
    }

    private Function<List<AssessedTargetLesion>, List<AssessedTargetLesion>> bestPercentageChangeLatestEvents() {

        return assessedTargetLesions -> {
            Map<String, List<AssessedTargetLesion>> tumoursBySubject = groupTumoursBySubject().apply(assessedTargetLesions);
            return tumoursBySubject.values().stream()
                    .map(subjectTumours -> subjectTumours.stream()
                            .max(Comparator.comparing(atl -> atl.getEvent().getLesionDate())).get()
                    ).collect(Collectors.toList());
        };
    }


}
