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

import {Action} from '@ngrx/store';
import {List, Map} from 'immutable';
import 'rxjs/add/operator/take';

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
    TRELLIS_LOADING,
    TRELLIS_NO_DATA,
    TRELLIS_OPTION_CHANGE_TRELLISING,
    TRELLIS_PLOT_SETTINGS_CHANGE,
    TRELLIS_REFRESH_PLOTS,
    TRELLIS_RESET,
    TRELLIS_SCALE_CHANGE,
    TRELLIS_UPDATE_COLOR_MAP,
    TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_HEIGHT,
    TRELLIS_UPDATE_MULTI_EVENT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_MULTI_SELECTION_DETAIL,
    TRELLIS_UPDATE_PLOTS,
    TRELLIS_UPDATE_SELECTION,
    TRELLIS_UPDATE_SELECTION_DETAIL,
    TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_ZOOM
} from './TrellisingActions';
import {
    DetailsOnDemand,
    DynamicAxis,
    IChartSelection,
    IDetail,
    IMultiSelectionDetail,
    IPlot,
    IPlots,
    ISelectionDetail,
    ITrellises,
    ITrellisingOptions,
    IXAxis,
    IYAxis,
    IZoom,
    NoData,
    PlotSettings,
    PlotSettingsWithTab,
    ScaleTypes,
    TabId
} from '../ITrellising';
import {UPDATE_INITIAL_OPENING_STATE} from '../../../../plugins/timeline/store/TimelineAction';
import {Track} from '../services/TimelineConfigService';
import {TimelineId} from '../../../../plugins/timeline/store/ITimeline';

// Axis Options
export interface AxisOption {
    groupByOption: any;
    binableOption: boolean;
    hasDrugOption: boolean;
    supportsDuration: boolean;
    timestampOption: boolean;
}

export interface XAxisOptions extends Map<string, any> {
    drugs: string[];
    hasRandomization: boolean;
    options: AxisOption[];
}

// Group by options
export interface GroupByOptions {
    X_AXIS: XAxisOptions;
}

// Group by settings
export interface GroupBySetting extends Map<string, any> {
    groupByOption: string;  // value option from x-axis response
    params: any;
}

// Group by settings
export interface DisplayedGroupBySetting extends GroupBySetting {
    displayedOption: string;
}

export interface GroupBySettings {
    X_AXIS: GroupBySetting;
}

// Possible group by variants
type GroupBy = 'COLOR_BY' | 'X_AXIS' | 'Y_AXIS' | 'SERIES_BY' | 'VALUE' | 'NAME' | 'ORDER_BY' | 'UNIT';

// in ngrx v4 payload was removed from Action interface
// this extended interface is used instead
export interface ActionWithPayload<T> extends Action {
    payload: T;
}

export class FetchGroupByOptions implements Action {
    readonly type = ActionTypes.TRELLIS_FETCH_GROUPBY_OPTIONS;

    constructor(public payload?: {
        groupBy: GroupBy,
        groupByOptions: Request.AxisOptions<string>,
        tabId: TabId
    }) {
    }
}

export class ChangeGroupBySetting implements Action {
    readonly type = ActionTypes.TRELLIS_CHANGE_GROUPBY_OPTION;

    constructor(public payload?: {
        groupBy: GroupBy,
        groupBySetting: GroupBySetting,
        tabId: TabId
    }) {
    }
}

export class ChangeGroupByParams implements Action {
    readonly type = ActionTypes.TRELLIS_CHANGE_GROUPBY_PARAMS;

    constructor(public payload?: {
        groupBy: GroupBy,
        params: any,
        tabId: TabId
    }) {
    }
}

export class ChangeZoomRange implements Action {
    readonly type = ActionTypes.TRELLIS_CHANGE_ZOOM_RANGE;

    constructor(public payload?: {
        axisType: 'xAxis' | 'yAxis',
        range: number,
        tabId: TabId
    }) {
    }
}

export class TrellisingActionCreator {

    constructor(private tabId: TabId) {
    }

    // -------COMMON STATIC ACTIONS-------
    public static makeResetAction(): ActionWithPayload<any> {
        return {type: TRELLIS_RESET, payload: {}};
    }

    public static makeRefreshAction(): ActionWithPayload<any> {
        return {type: TRELLIS_REFRESH_PLOTS, payload: {}};
    }

    public static makeUpdateHeightAction(height: number): ActionWithPayload<{ height: number }> {
        return {
            type: TRELLIS_UPDATE_HEIGHT,
            payload: {
                height: height
            }
        };
    }

    public static makeGlobalInitialisingAction(initialising: boolean): ActionWithPayload<{ isInitialised: boolean }> {
        return {
            type: TRELLIS_INITIALISED,
            payload: {
                isInitialised: initialising
            }
        };
    }

    // -------OTHER STATIC ACTIONS-------

    public static makeLocalInitialisingAction(initialising: boolean,
                                              tabId: TabId): ActionWithPayload<{ tabId: TabId, isInitialised: boolean }> {
        return {
            type: TRELLIS_INITIALISED,
            payload: {
                tabId: tabId,
                isInitialised: initialising
            }
        };
    }

    // -------PLOT STATIC ACTIONS-------

    public static makeInitialStateOfTimelineAction(tracks: Track[], timelineId: TimelineId): ActionWithPayload<any> {
        return {
            type: UPDATE_INITIAL_OPENING_STATE,
            payload: {
                timelineId: timelineId,
                tracks: tracks,
                performedJumpToTimeline: true,
                isInitialized: false
            }
        };
    }

    public static makeNoDataOnTabAction(noData: boolean, tabId: TabId): ActionWithPayload<NoData> {
        return {
            type: TRELLIS_NO_DATA,
            payload: {
                tabId: tabId,
                noData: noData
            }
        };
    }

    public static makeClearSubPlotAction(tabId: TabId): ActionWithPayload<{ tabId: TabId }> {
        return {type: TRELLIS_CLEAR_SUB_PLOT, payload: {tabId: tabId}};
    }


    // -------COMMON ACTIONS-------

    public makeLoadingAction(loading: boolean, tabId?: TabId): ActionWithPayload<{ tabId: TabId, isLoading: boolean }> {
        return {
            type: TRELLIS_LOADING,
            payload: {
                tabId: tabId || this.tabId,
                isLoading: loading
            }
        };
    }

    // -----------------------------------
    // -------PLOT ACTIONS-------
    // -----------------------------------

    public makeNoDataAction(noData: boolean): ActionWithPayload<NoData> {
        return {
            type: TRELLIS_NO_DATA,
            payload: {
                tabId: this.tabId,
                noData: noData
            }
        };
    }

    public makeGenerateEmptyPlotsAction(): ActionWithPayload<{ tabId: TabId }> {
        return {
            type: TRELLIS_GENERATE_EMPTY_PLOTS,
            payload: {
                tabId: this.tabId
            }
        };
    }

    public makeUpdatePlotDataAction(data: List<IPlot>, tabId: TabId): ActionWithPayload<IPlots> {
        return {
            type: TRELLIS_UPDATE_PLOTS,
            payload: {
                tabId: tabId,
                plots: data
            }
        };
    }

    public makeUpdateSelectionDetailAction(detail: ISelectionDetail, tabId?: TabId): ActionWithPayload<IDetail> {
        return {
            type: TRELLIS_UPDATE_SELECTION_DETAIL,
            payload: {
                tabId: tabId || this.tabId,
                detail: detail
            }
        };
    }

    public makeUpdateMultiSelectionDetailAction(detail: IMultiSelectionDetail, tabId?: TabId): ActionWithPayload<IDetail> {
        return {
            type: TRELLIS_UPDATE_MULTI_SELECTION_DETAIL,
            payload: {
                tabId: tabId || this.tabId,
                detail: detail
            }
        };
    }

    public makeUpdateSelectionAction(selection: List<IChartSelection>): ActionWithPayload<{ tabId: TabId, selection: any }> {
        return {
            type: TRELLIS_UPDATE_SELECTION,
            payload: {
                tabId: this.tabId,
                selection: selection
            }
        };
    }

    public makeClearSelectionsAction(): ActionWithPayload<{ tabId: TabId }> {
        return {type: TRELLIS_CLEAR_SELECTIONS, payload: {tabId: this.tabId}};
    }

    public makeClearSelectionAction(tabId: TabId): ActionWithPayload<{ tabId: TabId }> {
        return {type: TRELLIS_CLEAR_SELECTION, payload: {tabId: tabId || this.tabId}};
    }

    public makeClearColorMapAction(): ActionWithPayload<{ tabId: TabId }> {
        return {
            type: TRELLIS_UPDATE_COLOR_MAP, payload: {
                tabId: this.tabId
            }
        };
    }

    public makeUpdateEventDetailsOnDemandAction(tableData: any): ActionWithPayload<DetailsOnDemand> {
        return {
            type: TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
            payload: {
                tabId: this.tabId,
                detailsOnDemand: tableData
            }
        };
    }

    public makeUpdateMultiEventDetailsOnDemandAction(tableData: any, tabId: TabId): ActionWithPayload<DetailsOnDemand> {
        return {
            type: TRELLIS_UPDATE_MULTI_EVENT_DETAILS_ON_DEMAND,
            payload: {
                tabId: tabId,
                detailsOnDemand: tableData
            }
        };
    }

    public makeUpdateSubjectDetailsOnDemandAction(tableData: any, tabId: TabId): ActionWithPayload<DetailsOnDemand> {
        return {
            type: TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
            payload: {
                tabId: tabId,
                detailsOnDemand: tableData
            }
        };
    }

    // -----------------------------------
    // -------AXES AND ZOOM ACTIONS-------
    // -----------------------------------

    public makeUpdateXAxisOption(option: DynamicAxis): ActionWithPayload<IXAxis> {
        return {type: TRELLIS_CHANGE_AXIS, payload: {tabId: this.tabId, xAxis: true, option: option}};
    }

    public makeUpdateYAxisOption(option: string, tabId: TabId): ActionWithPayload<IYAxis> {
        return {type: TRELLIS_CHANGE_AXIS, payload: {tabId: tabId || this.tabId, yAxis: true, option: option}};
    }

    public makeChangeXAxisOptionsAction(options: List<DynamicAxis>): ActionWithPayload<IXAxis> {
        return {
            type: TRELLIS_CHANGE_AXIS_OPTIONS,
            payload: {
                tabId: this.tabId,
                xAxis: true,
                options: options
            }
        };
    }

    public makeChangeYAxisOptionsAction(options: List<string>): ActionWithPayload<IYAxis> {
        return {
            type: TRELLIS_CHANGE_AXIS_OPTIONS,
            payload: {
                tabId: this.tabId,
                yAxis: true,
                options: options
            }
        };
    }

    public makeUpdateXZoomAction(zoom: IZoom, tabId: TabId): ActionWithPayload<IXAxis> {
        return {type: TRELLIS_UPDATE_ZOOM, payload: {tabId: tabId, xAxis: true, zoom: zoom}};
    }

    public makeUpdateYZoomAction(zoom: IZoom, tabId: TabId): ActionWithPayload<IYAxis> {
        return {type: TRELLIS_UPDATE_ZOOM, payload: {tabId: tabId, yAxis: true, zoom: zoom}};
    }

    public makeAutoUpdateZoomAction(tabId?: TabId, resetToDefault?: boolean)
        : ActionWithPayload<{ tabId: TabId, resetToDefault }> {
        return {
            type: TRELLIS_UPDATE_ZOOM,
            payload: {
                tabId: tabId || this.tabId,
                resetToDefault
            }
        };
    }

    // -----------------------------------
    // -------OTHER ACTIONS-------
    // -----------------------------------

    public makePageNumberAction(pageNumber: number): ActionWithPayload<{ tabId: TabId, pageNumber: number }> {
        return {
            type: TRELLIS_CHANGE_PAGE,
            payload: {
                tabId: this.tabId,
                pageNumber: pageNumber
            }
        };
    }

    public makeChangeTrellisingAction(trellising: List<ITrellises>, ongoing: boolean, tabId?: TabId)
        : ActionWithPayload<ITrellisingOptions> {
        return {
            type: TRELLIS_CHANGE_TRELLISING,
            payload: {
                tabId: tabId || this.tabId,
                trellising: trellising,
                onGoing: ongoing
            }
        };
    }

    public makeChangeTrellisingColorByAction(trellising: List<ITrellises>): ActionWithPayload<ITrellisingOptions> {
        return {
            type: ActionTypes.TRELLIS_CHANGE_COLORBY,
            payload: {
                tabId: this.tabId,
                trellising: trellising
            }
        };
    }

    public makeChangePlotSettingsAction(plotSettings: PlotSettings): ActionWithPayload<PlotSettingsWithTab> {
        return {
            type: TRELLIS_PLOT_SETTINGS_CHANGE,
            payload: {
                tabId: this.tabId,
                plotSettings: plotSettings
            }
        };
    }

    public makeChangeOptionTrellisingAction(trellising: List<ITrellises>, chartId?: TabId)
        : ActionWithPayload<ITrellisingOptions> {
        return {
            type: TRELLIS_OPTION_CHANGE_TRELLISING,
            payload: {
                tabId: chartId || this.tabId,
                trellising: trellising
            }
        };
    }

    public makeChangeScaleTrellisingAction(scaleType: ScaleTypes): ActionWithPayload<{ tabId: TabId, scaleType: ScaleTypes }> {
        return {
            type: TRELLIS_SCALE_CHANGE,
            payload: {
                tabId: this.tabId,
                scaleType: scaleType
            }
        };
    }

    public updateLegendDisabled(legendDisabled: boolean): ActionWithPayload<{ tabId: TabId, legendDisabled: boolean }> {
        return {
            type: TRELLIS_LEGEND_DISABLED_CHANGE,
            payload: {
                tabId: this.tabId,
                legendDisabled: legendDisabled
            }
        };
    }

    public cacheColorByForAxis(yAxis: string, colorBy: string, chartId?: TabId)
        : ActionWithPayload<{ chartId: TabId, yAxis: string, colorBy: string }> {
        return {
            type: TRELLIS_CACHE_COLORBY_FOR_AXIS,
            payload: {
                chartId: chartId || this.tabId,
                yAxis: yAxis,
                colorBy: colorBy
            }
        };
    }

}
