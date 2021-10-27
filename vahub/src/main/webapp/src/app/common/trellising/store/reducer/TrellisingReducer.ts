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

import {fromJS, List, Map} from 'immutable';
import {createSelector} from 'reselect';

import {TabStoreUtils} from '../utils/TabStoreUtils';
import {
    AxisRecord,
    BIOMARKERS_X_MAX,
    BIOMARKERS_Y_MAX,
    DetailsOnDemand,
    DetailsOnDemandRecord,
    DynamicAxisRecord,
    IChartSelection,
    IDetail,
    IDetailsOnDemand,
    IPlot,
    IPlots,
    ITrellises,
    ITrellisingOptions,
    IZoom,
    MAX_BARS_ALL_PRIOR_THERAPIES,
    NoData,
    PageRecord,
    PlotSettingsWithTab,
    ScaleTypes,
    TabId,
    TabStore,
    TherapiesType,
    TrellisCategory,
    TrellisDesign,
    ZoomRanges,
    ZoomRecord
} from '../ITrellising';
import {
    ActionTypes,
    TRELLIS_CACHE_COLORBY_FOR_AXIS,
    TRELLIS_CHANGE_AXIS,
    TRELLIS_CHANGE_AXIS_OPTIONS,
    TRELLIS_CHANGE_PAGE,
    TRELLIS_CHANGE_TRELLISING,
    TRELLIS_CLEAR_SELECTION,
    TRELLIS_CLEAR_SELECTIONS,
    TRELLIS_CLEAR_SUB_PLOT,
    TRELLIS_GENERATE_EMPTY_PLOTS,
    TRELLIS_INITIALISED,
    TRELLIS_LEGEND_DISABLED_CHANGE,
    TRELLIS_LOAD_SAVED_STATE,
    TRELLIS_LOADING,
    TRELLIS_NO_DATA,
    TRELLIS_OPTION_CHANGE_TRELLISING,
    TRELLIS_PLOT_SETTINGS_CHANGE,
    TRELLIS_RESET,
    TRELLIS_SCALE_CHANGE,
    TRELLIS_SET_INIT_STATE,
    TRELLIS_UPDATE_COLOR_MAP,
    TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_HEIGHT,
    TRELLIS_UPDATE_MULTI_SELECTION_DETAIL,
    TRELLIS_UPDATE_PLOTS,
    TRELLIS_UPDATE_SELECTION,
    TRELLIS_UPDATE_SELECTION_DETAIL,
    TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_ZOOM
} from '../actions/TrellisingActions';
import {InitialTrellisService} from '../services/InitialTrellisService';
import {PlotUtilsFactory} from '../utils/PlotUtilsFactory';
import {EmptyPlotsService} from '../services/EmptyPlotsService';
import {BaseChartUtilsService} from '../plotutils';
import {PagesService} from '../services/PagesService';
import {
    ActionWithPayload,
    ChangeGroupByParams,
    ChangeGroupBySetting,
    ChangeZoomRange,
    FetchGroupByOptions,
    GroupBySetting
} from '../actions/TrellisingActionCreator';
import {ApplicationState} from '../../../store/models/ApplicationState';
import {getTabId} from '../../../store/reducers/SharedStateReducer';
import {getSelectedOption} from '../../axislabel/newxaxis/XAxisLabelService';
import {TRELLIS_UPDATE_MULTI_EVENT_DETAILS_ON_DEMAND} from '../';

const initialState: TabStore = TabStoreUtils.buildInitialStore();

export function trellisingReducer(currentState: TabStore = initialState, currentAction: ActionWithPayload<any>): Map<String, any> {
    switch (currentAction.type) {
        case TRELLIS_SET_INIT_STATE:
            return initialiseState(currentState, currentAction);
        case TRELLIS_LOAD_SAVED_STATE:
            return setState(currentState, currentAction);
        case TRELLIS_CHANGE_PAGE:
            return changePage(currentState, currentAction);
        case TRELLIS_RESET:
            return resetState(currentState, currentAction);
        case TRELLIS_UPDATE_ZOOM:
            return updateZoom(currentState, currentAction);
        case TRELLIS_CHANGE_TRELLISING:
            return changeTrellising(currentState, currentAction);
        case TRELLIS_OPTION_CHANGE_TRELLISING:
            return changeOptionTrellising(currentState, currentAction);
        case TRELLIS_CHANGE_AXIS:
            return changeAxisOption(currentState, currentAction);
        case TRELLIS_CHANGE_AXIS_OPTIONS:
            return changeAxisOptions(currentState, currentAction);
        case TRELLIS_LOADING:
            return setLoading(currentState, currentAction);
        case TRELLIS_INITIALISED:
            return setInitialised(currentState, currentAction);
        case TRELLIS_GENERATE_EMPTY_PLOTS:
            return generateEmptyPlots(currentState, currentAction);
        case TRELLIS_UPDATE_PLOTS:
            return updatePlots(currentState, currentAction);
        case TRELLIS_UPDATE_HEIGHT:
            return setHeight(currentState, currentAction);
        case TRELLIS_NO_DATA:
            return setNoData(currentState, currentAction);
        case TRELLIS_UPDATE_SELECTION:
            return updateSelection(currentState, currentAction);
        case TRELLIS_UPDATE_SELECTION_DETAIL:
            return updateSelectionDetail(currentState, currentAction);
        case TRELLIS_UPDATE_MULTI_SELECTION_DETAIL:
            return updateMultiSelectionDetail(currentState, currentAction);
        case TRELLIS_CLEAR_SELECTIONS:
            return clearSelections(currentState, currentAction);
        case TRELLIS_CLEAR_SELECTION:
            return clearSelection(currentState, currentAction);
        case TRELLIS_CLEAR_SUB_PLOT:
            return clearSubPlot(currentState, currentAction);
        case TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND:
            return updateEventDetailsOnDemand(currentState, currentAction);
        case TRELLIS_UPDATE_MULTI_EVENT_DETAILS_ON_DEMAND:
            return updateMultiEventDetailsOnDemand(currentState, currentAction);
        case TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND:
            return updateSubjectDetailsOnDemand(currentState, currentAction);
        case TRELLIS_UPDATE_COLOR_MAP:
            return updateColorMap(currentState, currentAction);
        case TRELLIS_PLOT_SETTINGS_CHANGE:
            return updatePlotSettings(currentState, currentAction);
        case TRELLIS_SCALE_CHANGE:
            return updateScale(currentState, currentAction);
        case TRELLIS_LEGEND_DISABLED_CHANGE:
            return updateLegendDisabled(currentState, currentAction);
        case ActionTypes.TRELLIS_FETCH_GROUPBY_OPTIONS:
            return fetchGroupByOptions(currentState, currentAction);
        case ActionTypes.TRELLIS_CHANGE_GROUPBY_OPTION:
            return changeGroupByOption(currentState, currentAction);
        case ActionTypes.TRELLIS_CHANGE_ZOOM_RANGE:
            return changeZoomRange(currentState, currentAction);
        case ActionTypes.TRELLIS_CHANGE_COLORBY:
            return changeTrellisingColorBy(currentState, currentAction);
        case TRELLIS_CACHE_COLORBY_FOR_AXIS:
            return cacheColorByForYAxis(currentState, currentAction);
        default:
            return currentState;
    }

    // TODO: set types instead of any to ActionWithPayload interface

    function initialiseState(state: TabStore, action: ActionWithPayload<any>): Map<string, any> {
        return TabStoreUtils.buildInitialStore();
    }

    function setState(state: TabStore, action: ActionWithPayload<{ state: TabStore }>): Map<string, any> {
        return action.payload.state;
    }

    function changePage(state: TabStore, action: ActionWithPayload<{ tabId: TabId, pageNumber: number }>): Map<string, any> {
        if (action.payload.pageNumber && action.payload.tabId) {
            const limit = TabStoreUtils.paginationLimit(action.payload.tabId);
            const offset = 1 + (action.payload.pageNumber - 1) * limit;
            const trellising = state.getIn(['tabs', action.payload.tabId, 'trellising']);
            const columnLimit = state.getIn(['tabs', action.payload.tabId, 'columnLimit']);
            const plotsLength = state.getIn(['tabs', action.payload.tabId, 'plots']).toArray().length;
            const pageValue = PagesService.page(limit, offset);
            return state.withMutations(mutatorState => {
                mutatorState.setIn(['tabs', action.payload.tabId, 'page'], new PageRecord({
                    limit: limit,
                    offset: offset,
                    value: pageValue
                }));
                mutatorState.setIn(['tabs', action.payload.tabId, 'pages'],
                    PagesService.pages(trellising, limit));
                mutatorState.setIn(['tabs', action.payload.tabId, 'columns'],
                    PagesService.actualColumns(columnLimit, plotsLength, pageValue, limit));
                mutatorState.setIn(['tabs', action.payload.tabId, 'actualLimit'],
                    PagesService.actualLimit(columnLimit, plotsLength, pageValue, limit));
            });
        }
        return state;
    }

    function changeAxisOption(state: TabStore, action: ActionWithPayload<any>): Map<string, any> {
        if (action.payload.tabId && action.payload.option && (action.payload.xAxis || action.payload.yAxis)) {
            const option = action.payload.xAxis ? new DynamicAxisRecord({
                value: action.payload.option.value,
                intarg: action.payload.option.intarg,
                stringarg: action.payload.option.stringarg
            }) : action.payload.option;
            const currentOption = state.getIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'option']);
            if (action.payload.xAxis && option.equals(currentOption) || action.payload.yAxis && option === currentOption) {
                return state;
            } else {
                const options = state.getIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'options']);
                const xAxisTextZoomRequired = state.getIn(['tabs', action.payload.tabId, 'xAxisTextZoomRequired']);
                let zoom;
                if (action.payload.xAxis && xAxisTextZoomRequired) {
                    zoom = state.getIn(['tabs', action.payload.tabId, 'xAxis', 'zoom']);
                } else {
                    zoom = new ZoomRecord();
                }
                return state.withMutations(mutatorState => {
                    mutatorState.setIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis'],
                        new AxisRecord({
                            option: option,
                            options: options,
                            zoom: zoom
                        }));
                    if (action.payload.xAxis) {
                        mutatorState.setIn(['tabs', action.payload.tabId, 'trellisDesign'],
                            TabStoreUtils.trellisDesign(action.payload.tabId, action.payload.option.value, action.payload.option.intarg));
                    }
                });
            }
        }
        return state;
    }

    function changeAxisOptions(state: TabStore, action: ActionWithPayload<any>): Map<string, any> {
        if (action.payload.tabId && action.payload.options && (action.payload.xAxis || action.payload.yAxis)) {
            // Only change if changed
            const options = state.getIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'options']);
            if (action.payload.options.equals(options)) {
                return state;
            } else {
                // If changed then reset zoom and option unless option present in options
                let option = state.getIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'option']);
                option = (option && action.payload.options.indexOf(option) > -1
                    || !action.payload.options || action.payload.options.size === 0)
                    ? option
                    : action.payload.options.get(0);
                if (action.payload.xAxis) {
                    const optionObject = option.toJS();
                    const trellisDesign = TabStoreUtils.trellisDesign(action.payload.tabId, optionObject.value, optionObject.intarg);
                    return state.withMutations(mutatorState => {
                        mutatorState.setIn(['tabs', action.payload.tabId, 'xAxis'],
                            new AxisRecord({
                                option: option,
                                options: action.payload.options,
                                zoom: new ZoomRecord()
                            }));
                        mutatorState.setIn(['tabs', action.payload.tabId, 'trellisDesign'], trellisDesign);
                    });
                } else {
                    return state.setIn(['tabs', action.payload.tabId, 'yAxis'],
                        new AxisRecord({
                            option: option,
                            options: action.payload.options,
                            zoom: new ZoomRecord()
                        }));
                }
            }
        }
        return state;
    }

    function resetState(state: TabStore, action: ActionWithPayload<any>): Map<string, any> {
        return initialState;
    }

    function updateZoom(state: TabStore, action: ActionWithPayload<any>): Map<string, any> {
        if (action.payload.zoom && (action.payload.xAxis || action.payload.yAxis)) {
            // we need to calculate zoom range specifically for biomarkers
            // because we can't show more than a number of subjects, check BIOMARKERS_X_MAX, BIOMARKERS_Y_MAX
            if (action.payload.tabId === TabId.BIOMARKERS_HEATMAP_PLOT) {
                const {
                    zoom: {
                        zoomMax, zoomMin, absMax, absMin
                    },
                } = action.payload;
                const biomarkersMax = action.payload.xAxis ? BIOMARKERS_X_MAX : BIOMARKERS_Y_MAX;
                const range = zoomMax - zoomMin + 1;

                return state.setIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'zoom'],
                    new ZoomRecord({
                        absMax: absMax,
                        absMin: absMin,
                        zoomMax: range > biomarkersMax ? (zoomMin + biomarkersMax) - 1
                            : zoomMax,
                        zoomMin: zoomMin > zoomMax ? zoomMax : zoomMin
                    }));
            } else {
                return state.setIn(['tabs', action.payload.tabId, action.payload.xAxis ? 'xAxis' : 'yAxis', 'zoom'],
                    new ZoomRecord({
                        absMax: action.payload.zoom.absMax,
                        absMin: action.payload.zoom.absMin,
                        zoomMax: action.payload.zoom.zoomMax,
                        zoomMin: action.payload.zoom.zoomMin
                    }));
            }
        } else {
            const plots: List<IPlot> = state.getIn(['tabs', action.payload.tabId, 'plots']);
            const trellisDesign: TrellisDesign = state.getIn(['tabs', action.payload.tabId, 'trellisDesign']);
            // new ranges calculated based on data
            // !!!!
            const plotUtilsService = PlotUtilsFactory.getPlotUtilsService(plots.get(0));
            const zoomRanges: ZoomRanges = plotUtilsService.calculateZoomRanges(plots, trellisDesign, action.payload.tabId);
            // existing ranges
            const existingZoomX: IZoom = state.getIn(['tabs', action.payload.tabId, 'xAxis', 'zoom'], undefined);
            const existingZoomY: IZoom = state.getIn(['tabs', action.payload.tabId, 'yAxis', 'zoom'], undefined);
            const hasZoomX: boolean = !action.payload.resetToDefault && existingZoomX
                && (existingZoomX.absMax !== existingZoomX.zoomMax || existingZoomX.absMin !== existingZoomX.zoomMin);
            const hasZoomY: boolean = !action.payload.resetToDefault && existingZoomY
                && (existingZoomY.absMax !== existingZoomY.zoomMax || existingZoomY.absMin !== existingZoomY.zoomMin);
            return state.withMutations(mutatorState => {
                if (action.payload.tabId !== TabId.BIOMARKERS_HEATMAP_PLOT) {
                    const setting = mutatorState.getIn(['tabs', action.payload.tabId, 'plotSettings']);
                    mutatorState.setIn(['tabs', action.payload.tabId, 'xAxis', 'zoom'], new ZoomRecord({
                        absMax: zoomRanges.x.max,
                        absMin: zoomRanges.x.min,
                        zoomMax: (!hasZoomX || zoomRanges.x.max < existingZoomX.zoomMax) ? zoomRanges.x.max : existingZoomX.zoomMax,
                        zoomMin: (!hasZoomX || zoomRanges.x.min > existingZoomX.zoomMin) ? zoomRanges.x.min : existingZoomX.zoomMin
                    }));
                    let zoomYMax = (!hasZoomY || zoomRanges.y.max < existingZoomY.zoomMax) ? zoomRanges.y.max : existingZoomY.zoomMax;
                    let zoomYMin = (!hasZoomY || zoomRanges.y.min > existingZoomY.zoomMin) ? zoomRanges.y.min : existingZoomY.zoomMin;
                    if (action.payload.tabId === TabId.TUMOUR_RESPONSE_PRIOR_THERAPY && !hasZoomY) {
                        if (setting.get('trellisedBy') === TherapiesType.ALL_PRIOR_THERAPIES) {
                            zoomYMax = zoomRanges.y.max > MAX_BARS_ALL_PRIOR_THERAPIES - 1 ? MAX_BARS_ALL_PRIOR_THERAPIES - 1
                                : zoomRanges.y.max;
                            zoomYMin = zoomRanges.y.min;
                        } else {
                            zoomYMin = zoomRanges.y.min;
                            zoomYMax = zoomRanges.y.max;
                        }
                    }
                    mutatorState.setIn(['tabs', action.payload.tabId, 'yAxis', 'zoom'], new ZoomRecord({
                        absMax: zoomRanges.y.max,
                        absMin: zoomRanges.y.min,
                        zoomMax: zoomYMax,
                        zoomMin: zoomYMin
                    }));
                } else {
                    let zoomXMin, zoomXMax;

                    if (!hasZoomX) {
                        // For biomarkers we need to start from the top left corner
                        zoomXMin = zoomRanges.x.min;
                        zoomXMax = Math.min(zoomRanges.x.max, zoomRanges.x.min + BIOMARKERS_X_MAX - 1);
                    } else {
                        // if we have existing zoom set to 9 and our new zoom comes as 1, we'll set the zoom to 9 which is wrong
                        zoomXMin = Math.max(existingZoomX.zoomMin, zoomRanges.x.min);
                        zoomXMax = Math.min(existingZoomX.zoomMax, zoomRanges.x.max);

                        if (zoomXMax - zoomXMin + 1 > BIOMARKERS_X_MAX) {
                            zoomXMax = zoomXMin + BIOMARKERS_X_MAX - 1;
                        }
                    }

                    let zoomYMin, zoomYMax;

                    if (!hasZoomY) {
                        // For biomarkers we need to start from the top left corner
                        zoomYMax = zoomRanges.y.max;
                        zoomYMin = Math.max(zoomRanges.y.min, zoomRanges.y.max - BIOMARKERS_Y_MAX + 1);
                    } else {
                        zoomYMin = Math.max(existingZoomY.zoomMin, zoomRanges.y.min);
                        zoomYMax = Math.min(existingZoomY.zoomMax, zoomRanges.y.max);

                        if (zoomYMax - zoomYMin + 1 > BIOMARKERS_Y_MAX) {
                            zoomYMin = zoomYMax - BIOMARKERS_Y_MAX + 1;
                        }
                    }

                    mutatorState.setIn(['tabs', action.payload.tabId, 'xAxis', 'zoom'], new ZoomRecord({
                        absMax: zoomRanges.x.max,
                        absMin: zoomRanges.x.min,
                        zoomMax: zoomXMax,
                        zoomMin: zoomXMin
                    }));

                    mutatorState.setIn(['tabs', action.payload.tabId, 'yAxis', 'zoom'], new ZoomRecord({
                        absMax: zoomRanges.y.max,
                        absMin: zoomRanges.y.min,
                        zoomMax: zoomYMax,
                        zoomMin: zoomYMin
                    }));
                }
            });
        }
    }

    function changeTrellising(state: TabStore, action: ActionWithPayload<ITrellisingOptions>): Map<string, any> {
        const tabId = action.payload.tabId;
        if (action.payload.trellising && tabId && action.payload.onGoing !== undefined) {
            return state.withMutations(mutatorState => {
                mutatorState.setIn(['tabs', tabId, 'baseTrellising'], action.payload.trellising);
                const currentTrellising = mutatorState.getIn(['tabs', tabId, 'trellising'], List());
                const previousTrellising = mutatorState.getIn(['tabs', tabId, 'previousTrellising'], List());
                const isInitialised = <boolean>mutatorState.getIn(['tabs', tabId, 'isInitialised']);
                const allTrellising = <boolean>mutatorState.getIn(['tabs', tabId, 'allTrellising']);
                const allSeries = <boolean>mutatorState.getIn(['tabs', tabId, 'allSeries']);
                const limit = mutatorState.getIn(['tabs', tabId, 'page', 'limit']);
                const groupBy = TabStoreUtils.transformedYAxisForColorBy(tabId,
                    mutatorState.getIn(['tabs', tabId, 'groupBySettings', 'Y_AXIS', 'groupByOption']));
                const colorByForAxis = mutatorState.getIn(['tabs', tabId, 'colorByForAxis', groupBy]);
                let newTrellising: any;
                const currentTrellisingIsEmpty = currentTrellising.size === 0 && !allTrellising && !allSeries;
                newTrellising = InitialTrellisService.generateInitialTrellis(
                    currentTrellisingIsEmpty ? previousTrellising : currentTrellising,
                    action.payload.trellising,
                    tabId,
                    action.payload.onGoing,
                    !isInitialised,
                    allSeries,
                    allTrellising,
                    colorByForAxis
                );

                const nextAllTrellising = InitialTrellisService.isAllTrellising(newTrellising, action.payload.trellising);
                const nextAllSeries = InitialTrellisService.isAllSeries(newTrellising, action.payload.trellising);
                const newPages = PagesService.pages(newTrellising, limit);
                if (newTrellising.size === 0 && !nextAllTrellising && !nextAllSeries) {
                    mutatorState.setIn(['tabs', tabId, 'previousTrellising'], previousTrellising);
                } else {
                    mutatorState.setIn(['tabs', tabId, 'previousTrellising'], currentTrellising);
                }
                mutatorState.setIn(['tabs', tabId, 'trellising'], newTrellising);
                mutatorState.setIn(['tabs', tabId, 'allTrellising'], nextAllTrellising);
                mutatorState.setIn(['tabs', tabId, 'allSeries'], nextAllSeries);
                mutatorState.setIn(['tabs', tabId, 'pages'], newPages);
            });
        }
        return state;
    }

    function changeOptionTrellising(state: TabStore, action: ActionWithPayload<ITrellisingOptions>): Map<string, any> {
        if (action.payload.trellising && action.payload.tabId) {
            return state.withMutations(mutatorState => {
                const currentTrellising = mutatorState.getIn(['tabs', action.payload.tabId, 'trellising'], undefined);
                const limit = mutatorState.getIn(['tabs', action.payload.tabId, 'page', 'limit']);
                mutatorState.setIn(['tabs', action.payload.tabId, 'previousTrellising'], currentTrellising);
                mutatorState.setIn(['tabs', action.payload.tabId, 'trellising'], action.payload.trellising);
                const baseTrellising = mutatorState.getIn(['tabs', action.payload.tabId, 'baseTrellising']);

                const newPages = PagesService.pages(action.payload.trellising, limit);

                mutatorState.setIn(['tabs', action.payload.tabId, 'allTrellising'],
                    InitialTrellisService.isAllTrellising(action.payload.trellising, baseTrellising));
                mutatorState.setIn(['tabs', action.payload.tabId, 'allSeries'],
                    InitialTrellisService.isAllSeries(action.payload.trellising, baseTrellising));
                mutatorState.setIn(['tabs', action.payload.tabId, 'pages'], newPages);
            });
        }
        return state;
    }

    function generateEmptyPlots(state: TabStore, action: ActionWithPayload<{ tabId: TabId }>): Map<string, any> {
        if (!action.payload.tabId) {
            return state;
        }
        const limit = state.getIn(['tabs', action.payload.tabId, 'page', 'limit']);
        const offset = state.getIn(['tabs', action.payload.tabId, 'page', 'offset']);
        const trellises = state.getIn(['tabs', action.payload.tabId, 'trellising']);
        const plotType = state.getIn(['tabs', action.payload.tabId, 'plots', 0, 'plotType']);
        return state.setIn(['tabs', action.payload.tabId, 'plots'], EmptyPlotsService.generateEmptyPlots(limit, offset, trellises));
    }

    function updatePlots(state: TabStore, action: ActionWithPayload<IPlots>): Map<string, any> {
        if (!action.payload.tabId || !action.payload.plots) {
            return state;
        }
        const plots = state.getIn(['tabs', action.payload.tabId, 'plots']);
        return state.withMutations(map => {
            const colorMap = map.getIn(['tabs', action.payload.tabId, 'colorsMap']);
            const trellises = map.getIn(['tabs', action.payload.tabId, 'trellising']);
            map.setIn(['tabs', action.payload.tabId, 'colorsMap'], colorMap);
            const orderedPlots = BaseChartUtilsService.orderPlots(plots, action.payload.plots);
            if (!orderedPlots.find((plot: IPlot) => !plot.data.isEmpty())) {
                map.setIn(['tabs', action.payload.tabId, 'zoomIsHidden'], true);
            } else {
                map.setIn(['tabs', action.payload.tabId, 'zoomIsHidden'], false);
            }
            map.setIn(['tabs', action.payload.tabId, 'plots'], orderedPlots);

            const legend = PlotUtilsFactory.getPlotUtilsService(action.payload.plots.get(0))
                .extractLegend(action.payload.plots, action.payload.tabId, trellises);

            map.setIn(['tabs', action.payload.tabId, 'legend'], legend);

            const columnLimit = map.getIn(['tabs', action.payload.tabId, 'columnLimit']);
            const plotsLength = orderedPlots.toArray().length;
            const pageValue = map.getIn(['tabs', action.payload.tabId, 'page', 'value']);
            const limit = map.getIn(['tabs', action.payload.tabId, 'page', 'limit']);
            map.setIn(['tabs', action.payload.tabId, 'columns'], PagesService.actualColumns(columnLimit, plotsLength, pageValue, limit));
            map.setIn(['tabs', action.payload.tabId, 'actualLimit'], PagesService.actualLimit(columnLimit, plotsLength, pageValue, limit));

        });
    }

    function updateSelection(state: TabStore, action: ActionWithPayload<{ tabId: TabId, selection: any }>): Map<string, any> {
        if (action.payload.tabId && action.payload.selection && action.payload.selection.size > 0) {
            return state.withMutations(mutatorState => {
                let tabs: TabId[];
                let currentTab = action.payload.tabId;
                if (currentTab) {
                    currentTab = mutatorState.getIn(['tabs', currentTab, 'parentSelectionPlot']) || currentTab;
                    tabs = TabStoreUtils.relatedSubTabs(currentTab);
                } else {
                    tabs = mutatorState.get('tabs').keySeq().toArray();
                }
                tabs.forEach((tab) => {
                    mutatorState.setIn(['tabs', tab, 'selections'], List<IChartSelection>());
                    mutatorState.setIn(['tabs', tab, 'detail'], null); // TODO: replace with record
                    mutatorState.setIn(['tabs', tab, 'multiSelectionDetail'], null);
                });
                mutatorState.setIn(['tabs', currentTab, 'selections'], action.payload.selection);
            });
        }
        return state;
    }

    function updateSelectionDetail(state: TabStore, action: ActionWithPayload<IDetail>): Map<string, any> {
        if (action.payload.tabId) {
            const parentTab = state.getIn(['tabs', action.payload.tabId, 'parentSelectionPlot']);
            const currentTabId = parentTab ? parentTab : action.payload.tabId;
            return state.setIn(['tabs', currentTabId, 'detail'], action.payload.detail);
        }
        return state;
    }

    function updateMultiSelectionDetail(state: TabStore, action: ActionWithPayload<IDetail>): Map<string, any> {
        if (action.payload.tabId) {
            return state.setIn(['tabs', action.payload.tabId, 'multiSelectionDetail'], action.payload.detail);
        }
        return state;
    }

    function clearSelection(state: TabStore, action: ActionWithPayload<{ tabId: TabId }>): Map<string, any> {
        if (action.payload.tabId) {
            return state.withMutations(mutatorState => {
                mutatorState.setIn(['tabs', action.payload.tabId, 'selections'], List<IChartSelection>());
                mutatorState.setIn(['tabs', action.payload.tabId, 'detail'], null);
                mutatorState.setIn(['tabs', action.payload.tabId, 'multiSelectionDetail'], null);
                mutatorState.setIn(['tabs', action.payload.tabId, 'eventDetailsOnDemand'], new DetailsOnDemandRecord());
                mutatorState.setIn(['tabs', action.payload.tabId, 'subjectDetailsOnDemand'], new DetailsOnDemandRecord());
                mutatorState.setIn(['tabs', action.payload.tabId, 'multiEventDetailsOnDemand'], Map<any, IDetailsOnDemand>());
            });
        }
        return state;
    }

    function clearSelections(state: TabStore, action: ActionWithPayload<{ tabId: TabId }>): Map<string, any> {
        return state.withMutations(mutatorState => {
            let tabs: TabId[];
            if (action.payload.tabId) {
                const parentTab = mutatorState.getIn(['tabs', action.payload.tabId, 'parentSelectionPlot']);
                if (parentTab) {
                    tabs = [parentTab];
                } else {
                    tabs = TabStoreUtils.relatedSubTabs(action.payload.tabId);
                }
            } else {
                tabs = mutatorState.get('tabs').keySeq().toArray();
            }
            // if (currentAction.payload.tabId) {
            //     tabs = TabStoreUtils.relatedSubTabs(currentAction.payload.tabId);
            // } else {
            //     tabs = currentState.get('tabs').keySeq().toArray();
            // }
            tabs.forEach((tab) => {
                mutatorState.setIn(['tabs', tab, 'selections'], List<IChartSelection>());
                mutatorState.setIn(['tabs', tab, 'detail'], null);
                mutatorState.setIn(['tabs', tab, 'multiSelectionDetail'], null);
                mutatorState.setIn(['tabs', tab, 'eventDetailsOnDemand'], new DetailsOnDemandRecord());
                mutatorState.setIn(['tabs', tab, 'subjectDetailsOnDemand'], new DetailsOnDemandRecord());
                mutatorState.setIn(['tabs', tab, 'multiEventDetailsOnDemand'], Map<any, IDetailsOnDemand>());
            });
        });
    }

    function clearSubPlot(state: TabStore, action: ActionWithPayload<{ tabId: TabId }>): Map<string, any> {
        if (!action.payload.tabId || !state.getIn(['tabs', action.payload.tabId, 'subPlotTabId'])) {
            return state;
        } else {
            const subTabId = state.getIn(['tabs', action.payload.tabId, 'subPlotTabId']);
            return state.setIn(['tabs', subTabId, 'isInitialised'], false);
        }
    }

    function updateEventDetailsOnDemand(state: TabStore, action: ActionWithPayload<DetailsOnDemand>): Map<string, any> {
        if (action.payload.tabId) {
            return state.setIn(['tabs', action.payload.tabId, 'eventDetailsOnDemand'], action.payload.detailsOnDemand);
        }
        return state;
    }

    function updateMultiEventDetailsOnDemand(state: TabStore, action: ActionWithPayload<DetailsOnDemand>): Map<string, any> {
        if (action.payload.tabId) {
            return state.setIn(['tabs', action.payload.tabId, 'multiEventDetailsOnDemand'], action.payload.detailsOnDemand);
        }
        return state;
    }

    function updateSubjectDetailsOnDemand(state: TabStore,
                                          action: ActionWithPayload<DetailsOnDemand>): Map<string, any> {
        if (action.payload.tabId) {
            return state.setIn(['tabs', action.payload.tabId, 'subjectDetailsOnDemand'], action.payload.detailsOnDemand);
        }
        return state;
    }

    function setHeight(state: TabStore, action: ActionWithPayload<{ tabId: TabId, height: number }>): Map<string, any> {
        if (action.payload.height) {
            return state.setIn(['height'], action.payload.height);
        } else {
            return state;
        }
    }

    function setLoading(state: TabStore, action: ActionWithPayload<{ tabId: TabId, isLoading: boolean }>): Map<string, any> {
        return state.setIn(['tabs', action.payload.tabId, 'isLoading'], action.payload.isLoading);
    }

    function setNoData(state: TabStore, action: ActionWithPayload<NoData>): Map<string, any> {
        return state.setIn(['tabs', action.payload.tabId, 'noData'], action.payload.noData);
    }

    function updateColorMap(state: TabStore, action: ActionWithPayload<any>): Map<string, string> {
        return state.setIn(['tabs', action.payload.tabId, 'colorsMap'], action.payload.colorsMap);
    }

    function setInitialised(state: TabStore, action: ActionWithPayload<{ tabId: TabId, isInitialised: boolean }>): Map<string, any> {
        if (action.payload.tabId) {
            return state.setIn(['tabs', action.payload.tabId, 'isInitialised'], action.payload.isInitialised);
        } else {
            return state.withMutations(mutatorState => {
                const tabs: TabId[] = mutatorState.get('tabs').keySeq().toArray();
                tabs.forEach((tab) => {
                    mutatorState.setIn(['tabs', tab, 'isInitialised'], action.payload.isInitialised);
                });
            });
        }
    }

    function updatePlotSettings(state: TabStore, action: ActionWithPayload<PlotSettingsWithTab>): Map<string, any> {
        return state.setIn(['tabs', action.payload.tabId, 'plotSettings'], action.payload.plotSettings);
    }

    function updateScale(state: TabStore, action: ActionWithPayload<{ tabId: TabId, scaleType: ScaleTypes }>): Map<string, any> {
        return state.setIn(['tabs', action.payload.tabId, 'scaleType'], action.payload.scaleType);
    }

    function updateLegendDisabled(state: TabStore, action: ActionWithPayload<{ tabId: TabId, legendDisabled: boolean }>): Map<string, any> {
        return state.setIn(['tabs', action.payload.tabId, 'legendDisabled'], action.payload.legendDisabled);
    }

    // new axis options implemented in 529
    function fetchGroupByOptions(state: TabStore, action: FetchGroupByOptions): TabStore {
        return <TabStore>state.withMutations(mutatorState => {

            mutatorState.updateIn(['tabs', action.payload.tabId, 'groupByOptions'], (groupByOptions) => {
                return groupByOptions.set(action.payload.groupBy, Map(fromJS(action.payload.groupByOptions)));
            });
        });
    }

    function changeGroupByOption(state: TabStore, {payload: {tabId, groupBy, groupBySetting}}: ChangeGroupBySetting): TabStore {
        const option = fromJS(groupBySetting);
        if (groupBy === 'X_AXIS') {
            return <TabStore>state.withMutations(mutatorState => {
                mutatorState.updateIn(['tabs', tabId, 'groupBySettings'], (groupBySettings) => {
                    return groupBySettings.set(groupBy, option);
                });
                const displayedOption = getSelectedOption(<GroupBySetting>option);
                mutatorState.setIn(['tabs', currentAction.payload.tabId, 'trellisDesign'],
                    TabStoreUtils.trellisDesign(currentAction.payload.tabId, displayedOption.displayedOption,
                        displayedOption.params ? displayedOption.params.BIN_SIZE : null));
                const xAxisTextZoomRequired = mutatorState.getIn(['tabs', currentAction.payload.tabId, 'xAxisTextZoomRequired']);
                const zoom = xAxisTextZoomRequired
                    ? mutatorState.getIn(['tabs', currentAction.payload.tabId, 'xAxis', 'zoom'])
                    : new ZoomRecord();
                mutatorState.setIn(['tabs', currentAction.payload.tabId, 'xAxis', 'zoom'], zoom);
                mutatorState.setIn(['tabs', currentAction.payload.tabId, 'yAxis', 'zoom'], new ZoomRecord());
            });
        } else {
            return <TabStore>state.withMutations(mutatorState => {
                mutatorState.updateIn(['tabs', tabId, 'groupBySettings'], (groupBySettings) => {
                    return groupBySettings.set(groupBy, option);
                });
                if (groupBy === 'Y_AXIS') {
                    mutatorState.setIn(['tabs', currentAction.payload.tabId, 'yAxis', 'zoom'], new ZoomRecord());
                }
            });
        }
    }

    function changeGroupByParams(state: TabStore, action: ChangeGroupByParams): TabStore {
        return state;
    }

    function changeZoomRange(state: TabStore, action: ChangeZoomRange): TabStore {
        return <TabStore>state.updateIn(['tabs', action.payload.tabId, action.payload.axisType], (axis) => {
            const zoom = axis.get('zoom');
            let zoomMax = zoom.get('zoomMin') + (+action.payload.range - 1);
            let zoomMin = zoom.get('zoomMin');
            const absMax = zoom.get('absMax');
            if (zoomMax > absMax) {
                zoomMax = absMax;
                zoomMin = absMax - action.payload.range + 1;
            }
            return axis.set('zoom', new ZoomRecord({absMin: zoom.absMin, absMax, zoomMin, zoomMax}));
        });
    }

    // Here it is simple update of "color by" trellising.
    // We don't update pagination etc here, so function needs to be extended if some other elements also should be updated
    function changeTrellisingColorBy(state: TabStore, action: ActionWithPayload<ITrellisingOptions>): Map<string, any> {
        if (action.payload.trellising && action.payload.tabId) {
            return state.withMutations(mutatorState => {
                mutatorState.setIn(['tabs', action.payload.tabId, 'baseTrellising'], action.payload.trellising);
                const currentTrellising = mutatorState.getIn(['tabs', action.payload.tabId, 'trellising'], undefined);
                mutatorState.setIn(['tabs', action.payload.tabId, 'previousTrellising'], currentTrellising);
                mutatorState.setIn(['tabs', action.payload.tabId, 'trellising'], action.payload.trellising);
                const plots = mutatorState.getIn(['tabs', action.payload.tabId, 'plots']);

                const legend = PlotUtilsFactory.getPlotUtilsService(plots.get(0))
                    .extractLegend(plots, action.payload.tabId, action.payload.trellising);
                mutatorState.setIn(['tabs', action.payload.tabId, 'legend'], legend);
            });
        }
        return state;
    }
}

function cacheColorByForYAxis(state: TabStore, action: ActionWithPayload<{
    chartId: TabId,
    yAxis: Map<string, any>, colorBy: Map<any, any>
}>): Map<string, any> {
    const transformedYAxisForColorBy = TabStoreUtils.transformedYAxisForColorBy(action.payload.chartId,
        action.payload.yAxis.get('groupByOption'));
    const colorBy = action.payload.colorBy.getIn([0, 'trellisedBy']);
    return state.withMutations(mutatorState => {
        mutatorState.updateIn(['tabs', action.payload.chartId, 'colorByForAxis'], (groupBySettings) => {
            return groupBySettings.set(transformedYAxisForColorBy, colorBy);
        });
    });

}

export const getTrellisingState = (state: ApplicationState): TabStore => {
    return state.trellisingReducer;
};

export const getColorByRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'colorByRequired']);
    });

export const getPlotSettingsRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'plotSettingsRequired']);
    });

export const getColorByUpdateOnSettingsChangeRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'colorByUpdateOnSettingsChangeRequired']);
    });
export const getFiltersUpdateOnYAxisChangeRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'filtersUpdateOnYAxisChangeRequired']);
    });

export const getPreserveYAxisIfItsNotPresent = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'preserveYAxisIfItsNotPresent']);
    });

export const getMultiEventDetailsOnDemandRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'multiDetailsOnDemandRequired']);
    });

export const getParentSelections = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const parentSelectionTab = trellisingState.getIn(['tabs', tabId, 'parentSelectionPlot']);
        return parentSelectionTab ? trellisingState.getIn(['tabs', parentSelectionTab, 'selections']) : undefined;
    });

export const getParentSelectionPlot = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'parentSelectionPlot']);
    });

export const getSubPlotTabId = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'subPlotTabId']);
    });

export const getSubPlotInitialised = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const subPlotTabId = trellisingState.getIn(['tabs', tabId, 'subPlotTabId']);
        return trellisingState.getIn(['tabs', subPlotTabId, 'isInitialised']);
    });

export const getTrellisingRequired = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'trellisingRequired']);
    });

export const getCurrentLimit = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'page', 'limit']);
    });

export const getCurrentOffset = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'page', 'offset']);
    });

export const getCurrentYAxisOption = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'yAxis', 'option']);
    });

export const getCurrentXAxisOption = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'xAxis', 'option']);
    });

export const getCurrentNewYAxisOption = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'groupBySettings', 'Y_AXIS']);
    });

export const getCurrentNewXAxisOption = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'groupBySettings', 'X_AXIS']);
    });

export const getCurrentSubPlotNewXAxisOption = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const subPlotTabId = trellisingState.getIn(['tabs', tabId, 'subPlotTabId']);
        return trellisingState.getIn(['tabs', subPlotTabId, 'groupBySettings', 'X_AXIS']);
    });

export const getCurrentTrellising = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'trellising']);
    });

export const getCurrentBaseTrellising = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'baseTrellising']);
    });

export const getCurrentPlotSettings = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const plotSettingsRequired = trellisingState.getIn(['tabs', tabId, 'plotSettingsRequired']);
        return plotSettingsRequired ? trellisingState.getIn(['tabs', tabId, 'plotSettings']) : null;
    });

export const getCurrentNewColorBy = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const trellising = trellisingState.getIn(['tabs', tabId, 'trellising']);
        return trellising && trellising.size > 0 ? trellising.filter((trellis: ITrellises) => {
            return trellis.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }) : List();
    });

export const getFilterTrellisByOptions = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const plots = trellisingState.getIn(['tabs', tabId, 'plots']);
        return plots && plots.size > 0 ? plots.map((plot: IPlot) => plot.get('trellising')) : List();
    });

export const getPlotDataRequestOptions = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const trellising = trellisingState.getIn(['tabs', tabId, 'trellising']);
        const plots = trellisingState.getIn(['tabs', tabId, 'plots']);
        const mainPlotTab = trellisingState.getIn(['tabs', tabId, 'parentSelectionPlot']);
        return {
            xAxisOption: trellisingState.getIn(['tabs', tabId, 'groupBySettings', 'X_AXIS']),
            yAxisOption: trellisingState.getIn(['tabs', tabId, 'groupBySettings', 'Y_AXIS']),
            limit: trellisingState.getIn(['tabs', tabId, 'page', 'limit']),
            offset: trellisingState.getIn(['tabs', tabId, 'page', 'offset']),
            trellising: trellising && trellising.size > 0 ? trellising.filter((trellis: ITrellises) => {
                return trellis.get('category') === TrellisCategory.MANDATORY_TRELLIS;
            }) : List(),
            colorByOptions: trellising && trellising.size > 0 ? trellising.filter((trellis: ITrellises) => {
                return trellis.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
            }) : List(),
            plotSettings: trellisingState.getIn(['tabs', tabId, 'plotSettings']),
            filterTrellisByOptions: plots && plots.size > 0 ? plots.map((plot: IPlot) => plot.get('trellising')) : List(),
            // mainPlotSelection: mainPlotTab ? trellisingState.getIn(['tabs', mainPlotTab, 'selections']) : undefined
        };
    });

export const getMainPlotSelection = createSelector(
    getTrellisingState, getTabId,
    (trellisingState: TabStore, tabId: string) => {
        const subPlotTab = trellisingState.getIn(['tabs', tabId, 'subPlotTabId']);
        const trellising = trellisingState.getIn(['tabs', subPlotTab, 'trellising']);
        const plots = trellisingState.getIn(['tabs', subPlotTab, 'plots']);
        return {
            subXAxisOption: trellisingState.getIn(['tabs', subPlotTab, 'groupBySettings', 'X_AXIS']),
            subYAxisOption: trellisingState.getIn(['tabs', subPlotTab, 'groupBySettings', 'Y_AXIS']),
            subLimit: trellisingState.getIn(['tabs', subPlotTab, 'page', 'limit']),
            subOffset: trellisingState.getIn(['tabs', subPlotTab, 'page', 'offset']),
            subTrellising: trellising && trellising.size > 0 ? trellising.filter((trellis: ITrellises) => {
                return trellis.get('category') === TrellisCategory.MANDATORY_TRELLIS;
            }) : List(),
            subColorByOptions: trellising && trellising.size > 0 ? trellising.filter((trellis: ITrellises) => {
                return trellis.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
            }) : List(),
            subPlotSettings: trellisingState.getIn(['tabs', subPlotTab, 'plotSettings']),
            subFilterTrellisByOptions: plots && plots.size > 0 ? plots.map((plot: IPlot) => plot.get('trellising')) : List(),
            subMainPlotSelection: trellisingState.getIn(['tabs', tabId, 'selections'])
        };
    });

export const getCBioJumpRequired = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'cBioJumpRequired']);
    }
);

export const getAdvancedAxisControlRequired = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'advancedAxisControlRequired']);
    }
);

export const getPreserveTitle = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'preserveTitle']);
    }
);

/*
* ZOOM
* */
export const getZoomRangeRequired = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'zoomRangeRequired']);
    }
);

export const getXZoom = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId: string) => {
        return trellisingState.getIn(['tabs', tabId, 'xAxis', 'zoom']);
    }
);

export const getYZoom = createSelector(
    getTrellisingState,
    getTabId,
    (trellisingState, tabId) => {
        return trellisingState.getIn(['tabs', tabId, 'yAxis', 'zoom']);
    }
);


export const getSelectionDetail = createSelector(
    getTrellisingState, getTabId,
    (state, tabId) => {
        return state.getIn(['tabs', tabId, 'detail']);
    }
);

export const getSelections = createSelector(
    getTrellisingState, getTabId,
    (state, tabId) => {
        return state.getIn(['tabs', tabId, 'selections']);
    }
);

