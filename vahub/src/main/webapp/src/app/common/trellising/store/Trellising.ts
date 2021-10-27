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

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Action, Store} from '@ngrx/store';
import {fromJS, List, Set} from 'immutable';
import {filter, isEmpty, isEqual, isUndefined, union} from 'lodash';

import {DataService} from '../data/DataService';
import {
    TRELLIS_CHANGE_AXIS,
    TRELLIS_CHANGE_PAGE,
    TRELLIS_CHANGE_TRELLISING,
    TRELLIS_NO_DATA,
    TRELLIS_OPTION_CHANGE_TRELLISING,
    TRELLIS_PLOT_SETTINGS_CHANGE,
    TRELLIS_REFRESH_PLOTS,
    TRELLIS_RESET,
    TRELLIS_UPDATE_SELECTION,
    TRELLIS_UPDATE_ZOOM
} from './actions/TrellisingActions';
import {
    DynamicAxis,
    FilterId,
    IChartSelection,
    IContinuousSelection,
    IMultiSelectionDetail,
    IPlot,
    ISelectionDetail,
    ITrellis,
    ITrellises,
    IZoom,
    NEW_APPROACH_TAB_LIST,
    PlotSettings,
    TabId,
    TrellisCategory
} from './ITrellising';
import {TabStoreUtils} from './utils/TabStoreUtils';
import {MarkingUtils} from './utils/MarkingUtils';
import {SessionEventService} from '../../../session/module';
import {DefaultAxisService} from './services/DefaultAxisService';
import {
    ActionWithPayload,
    ChangeGroupBySetting,
    FetchGroupByOptions,
    GroupBySetting,
    TrellisingActionCreator
} from './actions/TrellisingActionCreator';
import {TrellisingObservables} from './observable/TrellisingObservables';
import {TrellisingDispatcher} from './dispatcher/TrellisingDispatcher';
import {PaginatedTrellisService} from './services/PaginatedTrellisService';
import {UpdateActiveTabId} from '../../store/actions/SharedStateActions';
import {getTabId} from '../../store/reducers/SharedStateReducer';
import {ApplicationState} from '../../store/models/ApplicationState';
import {constructDataRequestPayload, formatAxisOptions, formatSelection} from './utils/TrellisFormatting';
import {EmptyObservable} from 'rxjs/observable/EmptyObservable';
import {
    getColorByRequired,
    getCurrentLimit,
    getCurrentNewColorBy,
    getCurrentNewXAxisOption,
    getCurrentNewYAxisOption,
    getCurrentOffset,
    getCurrentPlotSettings,
    getCurrentSubPlotNewXAxisOption,
    getCurrentTrellising,
    getCurrentXAxisOption,
    getCurrentYAxisOption,
    getFiltersUpdateOnYAxisChangeRequired,
    getFilterTrellisByOptions,
    getMainPlotSelection,
    getMultiEventDetailsOnDemandRequired,
    getParentSelectionPlot,
    getParentSelections,
    getPlotDataRequestOptions,
    getPreserveYAxisIfItsNotPresent,
    getSubPlotInitialised,
    getSubPlotTabId,
    getTrellisingRequired
} from './reducer/TrellisingReducer';
import {FiltersUtils} from '../../../filters/utils/FiltersUtils';
import {NO_AVAILABLE_PARAMETERS} from './index';
import {forkJoin} from 'rxjs/observable/forkJoin';
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class Trellising {
    private actions$ = new BehaviorSubject<ActionWithPayload<any>>({type: null, payload: null});
    private trellisingActionCreator: TrellisingActionCreator;
    private tabId: TabId;

    constructor(private _store: Store<ApplicationState>,
                private dataService: DataService,
                private session: SessionEventService,
                private trellisingObservables: TrellisingObservables,
                private trellisingDispatcher: TrellisingDispatcher,
                private filtersUtils: FiltersUtils) {
        this.defineActions();
    }

    public setTabId(tabId: TabId): void {
        this.tabId = tabId;
        this.trellisingActionCreator = new TrellisingActionCreator(tabId);
        this.trellisingDispatcher.setTabId(tabId);
        this.trellisingObservables.setTabId(tabId);
        this._store.dispatch(new UpdateActiveTabId(tabId));
    }

    public init(): void {
        if (this.trellisingObservables.getCurrentIsInitialised()) {
            this.actions$.next(TrellisingActionCreator.makeRefreshAction());
        } else {
            this.actions$.next(TrellisingActionCreator.makeResetAction());
        }
    }

    public reset(): void {
        this.actions$.next(TrellisingActionCreator.makeResetAction());
    }

    public updateSelection(selection: List<IChartSelection>): void {
        this.actions$.next(this.trellisingActionCreator.makeUpdateSelectionAction(selection));
    }

    public setNoData(filterId: FilterId): void {
        TabStoreUtils.tabsRelatedToFilter(filterId).forEach((tabId) => {
            this.actions$.next(TrellisingActionCreator.makeNoDataOnTabAction(true, tabId));
        });
    }

    public updateXAxisOption(option: DynamicAxis): void {
        this.actions$.next(this.trellisingActionCreator.makeUpdateXAxisOption(option));
    }

    public updateYAxisOption(option: string, tabId?: TabId): void {
        this.actions$.next(this.trellisingActionCreator.makeUpdateYAxisOption(option, tabId));
    }

    public updateXAxisZoom(zoom: IZoom, tabId: TabId): void {
        this.actions$.next(this.trellisingActionCreator.makeUpdateXZoomAction(zoom, tabId));
    }

    public updateYAxisZoom(zoom: IZoom, tabId: TabId): void {
        this.actions$.next(this.trellisingActionCreator.makeUpdateYZoomAction(zoom, tabId));
    }

    public updatePage(pageNumber: number): void {
        this.actions$.next(this.trellisingActionCreator.makePageNumberAction(pageNumber));
    }

    public updateTrellisingOptions(trellising: List<ITrellises>, chartId?: TabId): void {
        this.actions$.next(this.trellisingActionCreator.makeChangeOptionTrellisingAction(trellising, chartId));
    }

    public updatePlotSettings(settings: PlotSettings): void {
        this.actions$.next(this.trellisingActionCreator.makeChangePlotSettingsAction(settings));
    }

    private defineActions(): void {
        const reset = this.resetAction();
        const trellisReset$ = this.trellisResetAction$();

        const updateTrellis = this.updateTrellisAction();
        const trellisUpdateTrellisAction$ = this.trellisUpdateTrellisAction$();

        const updatePage = this.updatePageAction();
        const trellisupdatePage = this.trellisUpdatePageAction$();

        const refreshPlots = this.refreshAction();
        const trellisRefreshPlots$ = this.trellisRefreshAction$();

        const updateXAxisOption = this.updateXAxisOptionAction();
        const trellisUpdateXAxisOptionAction$ = this.trellisUpdateXAxisOptionAction$();

        const updateYAxisOption = this.updateYAxisOptionAction();
        const trellisUpdateYAxisOptionAction$ = this.trellisUpdateYAxisOptionAction$();

        const updateZoom = this.updateZoomAction();
        const updateNoData = this.updateNoDataAction();
        const selection = this.selectionAction();
        const trellisSelection$ = this.trellisSelectionAction$();

        const trellisChangeSettings$ = this.trellisChangeSettings$();

        Observable.merge(
            reset,
            trellisReset$,
            updateTrellis,
            trellisUpdateTrellisAction$,
            updatePage,
            trellisupdatePage,
            updateXAxisOption,
            trellisUpdateXAxisOptionAction$,
            updateYAxisOption,
            trellisUpdateYAxisOptionAction$,
            refreshPlots,
            trellisRefreshPlots$,
            updateZoom,
            updateNoData,
            trellisSelection$,
            trellisChangeSettings$,
            selection
        ).subscribe((action: Action) => {
            this._store.dispatch(action);
        });
    }

    /**
     * Reset for OLD approach tabs
     */
    private resetAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(
                this._store.select(getTabId),
                this._store.select(getCurrentXAxisOption)
            )
            .filter(([action, tabId]) => action.type === TRELLIS_RESET && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(action => this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true)))
            .mergeMap(([action, tabId, currentXAxisOption]) => {
                // TODO find a way to avoid this magic
                return this.dataService.getXAxisOptions(tabId)
                    .map((payload: any) => {
                        let xAxisOption;
                        let currentXAxisOptionJS = currentXAxisOption ? currentXAxisOption.toJS() : null;
                        const defaultXAxisOption: any = DefaultAxisService.initialX(tabId, this.isOngoingDataset(), payload);
                        if (!isEmpty(defaultXAxisOption) && !currentXAxisOptionJS) {
                            currentXAxisOptionJS = defaultXAxisOption;
                            this.trellisingDispatcher.updateXAxisOptions(this.sortXAxisOptionsByDefaultOption(payload));
                        } else {
                            this.trellisingDispatcher.updateXAxisOptions(payload);
                        }
                        if (payload.find((option: any) =>
                            (currentXAxisOptionJS.intarg
                            || option.intarg
                                ? isEqual(option, currentXAxisOptionJS)
                                : option.value === currentXAxisOptionJS.value
                                && option.stringarg === currentXAxisOptionJS.stringarg)
                        ) !== undefined || payload.length === 0) {
                            xAxisOption = currentXAxisOptionJS;
                            this.trellisingDispatcher.updateXAxisOption(xAxisOption);
                        } else {
                            xAxisOption = payload[0];
                            this.updateXAxisOption(xAxisOption);
                        }
                        return {action, tabId, xAxisOption};
                    });
            })
            .withLatestFrom(this._store.select(getCurrentYAxisOption))
            .mergeMap(([{action, tabId, xAxisOption}, currentYAxisOption]) => {
                return this.dataService.getYAxisOptions(tabId)
                    .map((payload: string[]) => {
                        let yAxisOption;
                        // let currentYAxisOption: string = this.trellisingObservables.getCurrentYAxisOption();
                        const defaultYAxisOption = DefaultAxisService.initialY(tabId, payload);
                        if (!isEmpty(defaultYAxisOption) && !currentYAxisOption) {
                            currentYAxisOption = defaultYAxisOption;
                            this.trellisingDispatcher.updateYAxisOptions(this.sortYAxisOptionsByDefaultOption(payload));
                        } else {
                            this.trellisingDispatcher.updateYAxisOptions(payload);
                        }
                        if (payload.indexOf(currentYAxisOption) > -1) {
                            yAxisOption = currentYAxisOption;
                        } else {
                            yAxisOption = payload[0];
                            this.updateYAxisOption(yAxisOption);
                        }
                        return {action, tabId, xAxisOption, yAxisOption};
                    });
            })
            .mergeMap(({action, tabId, xAxisOption, yAxisOption}) => {
                return this.dataService.getTrellisOptions(tabId, yAxisOption)
                    .map((payload: TrellisOptions<any>[]) => {
                        this.updateTrellisOptions(payload, action.type);
                        return {action, tabId, xAxisOption, yAxisOption};
                    });
            })
            .withLatestFrom(
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentTrellising))
            .mergeMap(([{action, tabId, xAxisOption, yAxisOption}, offset, limit, trellising]) => {
                return this.fetchChartData(tabId, xAxisOption, yAxisOption, offset, limit, trellising)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        this._store.dispatch(TrellisingActionCreator.makeLocalInitialisingAction(true, tabId));
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    /**
     * Reset for NEW approach tabs
     */
    private trellisResetAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_RESET && NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true, tabId)))
            // we have to get parent selection, as tabId can already be changed by the end of x-axis request completion
            .withLatestFrom(this._store.select(getParentSelections))
            .mergeMap(([[action, tabId], parentSelectionsPlot]) => {
                // TODO find a way to avoid this magic
                return this.dataService.getXAxisOptions(tabId)
                    .map(formatAxisOptions)
                    .withLatestFrom(
                        this._store.select(getCurrentNewXAxisOption),
                        this._store.select(getCurrentSubPlotNewXAxisOption),
                        this._store.select(getSubPlotTabId),
                        this._store.select(getCurrentNewYAxisOption),
                        this._store.select(getCurrentNewColorBy)
                    )
                    .map(([xAxisOptions, currentXAxisOption, currentSubPlotNewXAxisOption,
                              subPlotTabId, currentYAxisOption, currentColorBy]) => {
                        const currentNewXAxisOption = subPlotTabId && tabId === subPlotTabId ?
                            currentSubPlotNewXAxisOption : currentXAxisOption;
                        let xAxisOption;
                        if (currentYAxisOption && currentColorBy) {
                            this.trellisingDispatcher.cacheColorByForYAxis(currentYAxisOption, currentColorBy, tabId);

                        }
                        const isPresent = xAxisOptions.options.some(option => {
                            return currentNewXAxisOption ?
                                currentNewXAxisOption.get('groupByOption') === option.groupByOption :
                                false;
                        });
                        this._store.dispatch(new FetchGroupByOptions({
                            tabId,
                            groupBy: 'X_AXIS',
                            groupByOptions: xAxisOptions
                        }));

                        if (isPresent || isEmpty(xAxisOptions.options) && !isUndefined(currentNewXAxisOption)) {
                            xAxisOption = currentNewXAxisOption.toJS();
                            return {tabId, xAxisOption, parentSelectionsPlot};
                        } else {

                            xAxisOption = <GroupBySetting>DefaultAxisService.initialX(tabId, this.isOngoingDataset(), xAxisOptions);
                            this._store.dispatch(new ChangeGroupBySetting({
                                tabId,
                                groupBy: 'X_AXIS',
                                groupBySetting: xAxisOption
                            }));

                            return {tabId, xAxisOption, parentSelectionsPlot};
                        }
                    });
            })
            .mergeMap(({tabId, xAxisOption, parentSelectionsPlot}) => {
                return this.dataService.getYAxisOptions(tabId)
                    .map(formatAxisOptions)
                    .withLatestFrom(this._store.select(getCurrentNewYAxisOption),
                        this._store.select(getFiltersUpdateOnYAxisChangeRequired),
                        this._store.select(getPreserveYAxisIfItsNotPresent))
                    .map(([yAxisOptions, currentYAxisOption, updateFilters, preserveYAxis]) => {
                        let yAxisOption;

                        const isYAxisPresent = yAxisOptions.options.some((option: any) => {
                            if (currentYAxisOption) {
                                const groupByOption = currentYAxisOption.get('groupByOption');
                                preserveYAxis = preserveYAxis && groupByOption !== NO_AVAILABLE_PARAMETERS;
                                return option.groupByOption.trellisOptions
                                    ? groupByOption === option.groupByOption || option.groupByOption.trellisOptions.includes(groupByOption)
                                    : groupByOption === option.groupByOption;

                            } else {
                                return false;
                            }
                        });

                        this._store.dispatch(new FetchGroupByOptions({
                            tabId,
                            groupBy: 'Y_AXIS',
                            groupByOptions: yAxisOptions
                        }));

                        const useCurrentYAxis = currentYAxisOption && (isYAxisPresent || isEmpty(yAxisOptions.options) || preserveYAxis);
                        if (useCurrentYAxis) {
                            yAxisOption = currentYAxisOption.toJS();
                            return {tabId, xAxisOption, yAxisOption, parentSelectionsPlot};
                        } else {
                            yAxisOption = <GroupBySetting>DefaultAxisService.initialY(tabId, yAxisOptions);

                            this._store.dispatch(
                                new ChangeGroupBySetting({
                                    tabId,
                                    groupBy: 'Y_AXIS',
                                    groupBySetting: yAxisOption
                                })
                            );
                        }
                        if (updateFilters && currentYAxisOption && !isYAxisPresent && !parentSelectionsPlot) {
                            this.filtersUtils.getFilterModelById(tabId).makeFilterRequest(false);
                        }

                        return {tabId, xAxisOption, yAxisOption, parentSelectionsPlot};
                    });
            })
            .withLatestFrom(this._store.select(getTrellisingRequired))
            .mergeMap(([{tabId, xAxisOption, yAxisOption, parentSelectionsPlot}, trellisingRequired]: any) => {
                // TODO check if trellising required and add MANDATORY_TRELLIS
                if (trellisingRequired) {
                    return this.dataService.getTrellisOptions(tabId, fromJS(yAxisOption))
                        .map((trellisOptions: TrellisOptions<any>[]) => {
                            return {
                                tabId, xAxisOption, yAxisOption,
                                trellisOptions: trellisOptions.map(option => {
                                    (<any>option).category = 'MANDATORY_TRELLIS';
                                    return option;
                                }),
                                parentSelectionsPlot
                            };
                        });
                } else {
                    return Observable.of({tabId, xAxisOption, yAxisOption, trellisOptions: [], parentSelectionsPlot});
                }
            })
            .withLatestFrom(
                this._store.select(getColorByRequired),
                this._store.select(getCurrentPlotSettings)
            )
            .mergeMap(([{tabId, xAxisOption, yAxisOption, trellisOptions, parentSelectionsPlot},
                           colorByRequired, currentPlotSettings]: any) => {
                //avoid second condition
                //without second condition it will not work when we need color by in sub plot
                if (colorByRequired && !parentSelectionsPlot) {

                    return this.dataService.getColorByOptions(tabId, yAxisOption, currentPlotSettings)
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            return colorByOptions.map(option => {
                                // TODO it's a frontend-filled field, so it's absent in the backend-based type.
                                // TODO some better solution is needed here, thouch.
                                (<any>option).category = 'NON_MANDATORY_SERIES';
                                return option;
                            });
                        })
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            this.updateTrellisOptions(colorByOptions.concat(trellisOptions), '', tabId);
                            return {tabId, xAxisOption, yAxisOption, trellisOptions, parentSelectionsPlot};
                        });

                } else {
                    this.updateTrellisOptions(trellisOptions, '');
                    return Observable.of({tabId, xAxisOption, yAxisOption, trellisOptions, parentSelectionsPlot});
                }
            })
            .withLatestFrom(
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentNewColorBy),
                this._store.select(getCurrentPlotSettings),
                this._store.select(getFilterTrellisByOptions)
            )
            .mergeMap(([{
                tabId, xAxisOption, yAxisOption, trellisOptions, parentSelectionsPlot
            }, limit, offset, colorByOptions, plotSettings, filterTrellisByOptions]: any) => {
                const settings = constructDataRequestPayload(
                    xAxisOption,
                    yAxisOption,
                    trellisOptions,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions,
                    parentSelectionsPlot
                );
                return this.dataService.getPlotData(tabId, yAxisOption, settings)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection(tabId);
                        this.updatePlotOptions(payload, tabId, true);
                        this._store.dispatch(TrellisingActionCreator.makeLocalInitialisingAction(true, tabId));
                        return this.trellisingActionCreator.makeLoadingAction(false, tabId);
                    });
            });
    }

    // OLD approach
    private refreshAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_REFRESH_PLOTS && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentTrellising)
            )
            .mergeMap(([[action, tabId], xAxisOption, yAxisOption, limit, offset, trellising]) => {
                return this.fetchChartData(tabId, xAxisOption, yAxisOption, offset, limit, trellising)
                    .map((payload: List<IPlot>) => {
                        this.updatePlotOptions(payload);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // NEW approach
    private trellisRefreshAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_REFRESH_PLOTS && NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions),
                this._store.select(getParentSelections)
            )
            .mergeMap(([[action, tabId], {
                xAxisOption, yAxisOption, limit, offset, trellising, colorByOptions,
                plotSettings, filterTrellisByOptions
            }, parentSelectionsPlot]) => {
                const settings = constructDataRequestPayload(
                    xAxisOption.toJS(),
                    yAxisOption.toJS(),
                    trellising,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions,
                    parentSelectionsPlot
                );
                return this.dataService.getPlotData(tabId, yAxisOption.toJS(), settings)
                    .map((payload: List<IPlot>) => {
                        this.updatePlotOptions(payload, tabId);
                        return this.trellisingActionCreator.makeLoadingAction(false, tabId);
                    });
            });
    }

    // OLD approach
    private updateTrellisAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => {
                return (action.type === TRELLIS_CHANGE_TRELLISING || action.type === TRELLIS_OPTION_CHANGE_TRELLISING)
                    && !NEW_APPROACH_TAB_LIST.contains(tabId);
            })
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
                this.trellisingDispatcher.updatePageNumber(1);
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset)
            )
            .mergeMap(([[action, tabId], xAxisOption, yAxisOption, limit, offset]) => {
                return this.fetchChartData(tabId, xAxisOption, yAxisOption, offset, limit, action.payload.trellising)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearColorMap();
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // NEW approach
    private trellisUpdateTrellisAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => {
                return (action.type === TRELLIS_CHANGE_TRELLISING || action.type === TRELLIS_OPTION_CHANGE_TRELLISING)
                    && NEW_APPROACH_TAB_LIST.contains(tabId);
            })
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions),
                this._store.select(getFiltersUpdateOnYAxisChangeRequired)
            )
            .mergeMap(([[action, tabId], {
                xAxisOption, yAxisOption, limit, offset, trellising, colorByOptions,
                plotSettings, filterTrellisByOptions
            }, filterUpdatingRequired]) => {

                const settings = constructDataRequestPayload(
                    xAxisOption.toJS(),
                    yAxisOption.toJS(),
                    trellising,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions
                );
                if (filterUpdatingRequired) {
                    this.filtersUtils.getFilterModelById(tabId).makeFilterRequest(false);
                }
                return this.dataService.getPlotData(tabId, yAxisOption.toJS(), settings)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearColorMap();
                        this.trellisingDispatcher.clearSelection(tabId);
                        this.updatePlotOptions(payload, tabId, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    private updatePageAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_CHANGE_PAGE && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentTrellising)
            )
            .mergeMap(([[action, tabId], xAxisOption, yAxisOption, limit, offset, trellising]) => {
                return this.fetchChartData(tabId, xAxisOption, yAxisOption, offset, limit, trellising)
                    .map((payload: List<IPlot>) => {
                        this.updatePlotOptions(payload);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // NEW approach
    private trellisUpdatePageAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_CHANGE_PAGE && NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions)
            )
            .mergeMap(([[action, tabId], {
                xAxisOption,
                yAxisOption,
                limit,
                offset,
                trellising,
                colorByOptions,
                plotSettings,
                filterTrellisByOptions
            }]) => {
                const settings = constructDataRequestPayload(
                    xAxisOption.toJS(),
                    yAxisOption.toJS(),
                    trellising,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions
                );
                return this.dataService.getPlotData(tabId, yAxisOption.toJS(), settings)
                    .map((payload: List<IPlot>) => {
                        this.updatePlotOptions(payload);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // OLD approach
    private updateYAxisOptionAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_CHANGE_AXIS && action.payload.yAxis
                && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
            })
            .withLatestFrom(this._store.select(getCurrentYAxisOption))
            .mergeMap(([[action, tabId], yAxisOption]) => {
                return this.dataService.getTrellisOptions(tabId, yAxisOption)
                    .map((payload: TrellisOptions<any>[]) => {
                        this.updateTrellisOptions(payload, action.type);
                        return {action, tabId};
                    });
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentTrellising)
            )
            .mergeMap(([{action, tabId}, xAxisOption, yAxisOption, limit, offset, trellising]) => {
                return this.fetchChartData(tabId, xAxisOption, yAxisOption, offset, limit, trellising)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // NEW approach
    private trellisUpdateYAxisOptionAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId),
                this._store.select(getCurrentNewYAxisOption),
                this._store.select(getCurrentNewColorBy))
            .filter(([action, tabId]) => {

                return action.type === TRELLIS_CHANGE_AXIS && action.payload.yAxis
                    && NEW_APPROACH_TAB_LIST.contains(tabId);
            })
            .do(([action, tabId, currentYAxisOption, currentColorBy]) => {
                this.trellisingDispatcher.cacheColorByForYAxis(currentYAxisOption, currentColorBy, tabId);
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true, action.payload.tabId));
                this._store.dispatch(
                    new ChangeGroupBySetting({
                        tabId,
                        groupBy: 'Y_AXIS',
                        groupBySetting: action.payload.option
                    })
                );

            })
            .withLatestFrom(
                this._store.select(getTrellisingRequired),
                this._store.select(getCurrentNewYAxisOption)
            )
            .mergeMap(([[action, tabId], trellisingRequired, yAxisOption]) => {
                if (trellisingRequired) {
                    return this.dataService.getTrellisOptions(tabId, yAxisOption)
                        .map((trellisOptions: TrellisOptions<any>[]) => {
                            return {
                                tabId, yAxisOption,
                                trellisOptions: trellisOptions.map(option => {
                                    (<any>option).category = 'MANDATORY_TRELLIS';
                                    return option;
                                })
                            };
                        });
                } else {
                    return Observable.of({tabId, yAxisOption, trellisOptions: []});
                }
            })
            .withLatestFrom(this._store.select(getColorByRequired), this._store.select(getFiltersUpdateOnYAxisChangeRequired))
            .mergeMap(([{tabId, yAxisOption, trellisOptions}, colorByRequired, updateFilters]) => {
                if (updateFilters) {
                    this.filtersUtils.getFilterModelById(tabId).makeFilterRequest(false);
                }
                if (colorByRequired) {
                    return this.dataService.getColorByOptions(tabId, yAxisOption)
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            return colorByOptions.map(option => {
                                (<any>option).category = 'NON_MANDATORY_SERIES';
                                return option;
                            });
                        })
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            this.updateTrellisOptions(colorByOptions.concat(trellisOptions), '', tabId);
                            return tabId;
                        });
                } else {
                    this.updateTrellisOptions(trellisOptions, '');
                    return Observable.of(tabId);
                }
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions)
            )
            .mergeMap(([tabId, {
                xAxisOption, yAxisOption, limit, offset, trellising, colorByOptions,
                plotSettings, filterTrellisByOptions
            }]) => {
                const settings = constructDataRequestPayload(xAxisOption.toJS(),
                    yAxisOption.toJS(),
                    trellising,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions);
                return this.dataService.getPlotData(tabId, yAxisOption.toJS(), settings)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection(tabId);
                        this.updatePlotOptions(payload, tabId, true);
                        return this.trellisingActionCreator.makeLoadingAction(false, tabId);
                    });
            });
    }

    // OLD approach
    private updateXAxisOptionAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_CHANGE_AXIS && action.payload.xAxis
                && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentTrellising)
            )
            .mergeMap(([[action, tabId], xAxisOption, yAxisOption, limit, offset, trellising]) => {
                return this.fetchChartData(this.tabId, xAxisOption, yAxisOption, offset, limit, trellising)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // NEW approach
    private trellisUpdateXAxisOptionAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_CHANGE_AXIS && action.payload.xAxis
                && NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(new ChangeGroupBySetting({
                    tabId: action.payload.tabId,
                    groupBy: 'X_AXIS',
                    groupBySetting: action.payload.option
                }));
                this.trellisingDispatcher.generateEmptyPlots();
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions),
                this._store.select(getSubPlotTabId),
                this._store.select(getSubPlotInitialised),
                this._store.select(getMainPlotSelection),
            )
            .mergeMap(([[action, tabId], {
                xAxisOption, yAxisOption, limit, offset, trellising, colorByOptions,
                plotSettings, filterTrellisByOptions
            }, subPlotTabId, subPlotInitialised, {
                subXAxisOption,
                subYAxisOption, subLimit, subOffset, subTrellising, subColorByOptions, subPlotSettings, subFilterTrellisByOptions,
                subMainPlotSelection
            }]) => {
                if (subPlotTabId && subPlotInitialised && action.payload.tabId === subPlotTabId) {
                    const subSettings = constructDataRequestPayload(
                        subXAxisOption.toJS(),
                        subYAxisOption.toJS(),
                        subTrellising,
                        subLimit,
                        subOffset,
                        subColorByOptions,
                        subPlotSettings,
                        subFilterTrellisByOptions,
                        subMainPlotSelection
                    );
                    return this.dataService.getPlotData(subPlotTabId, yAxisOption, subSettings)
                        .map((payload: List<IPlot>) => {
                            this.updatePlotOptions(payload, subPlotTabId, true);
                            return this.trellisingActionCreator.makeLoadingAction(false);
                        });
                }
                const settings = constructDataRequestPayload(xAxisOption.toJS(),
                    yAxisOption.toJS(),
                    trellising,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions);
                return this.dataService.getPlotData(tabId, yAxisOption.toJS(), settings)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    private updateZoomAction(): Observable<Action> {
        return this.actions$
            .filter(action => action.type === TRELLIS_UPDATE_ZOOM)
            .debounceTime(200)
            .do(action => this._store.dispatch(action));
    }

    private updateNoDataAction(): Observable<Action> {
        return this.actions$
            .filter(action => action.type === TRELLIS_NO_DATA)
            .do(action => this._store.dispatch(action));
    }

    private selectionAction(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => action.type === TRELLIS_UPDATE_SELECTION && !NEW_APPROACH_TAB_LIST.contains(tabId))
            .do(([action, tabId]) => {
                // before setting selection for new tabId
                // we need to clear all the related tabs of selected data

                // Get all related tabs without the current
                const relatedTabs: Set<TabId> = Set(TabStoreUtils.relatedSubTabs(this.tabId)).subtract([this.tabId]);
                // Dispatch clearing action for all related tabs
                relatedTabs.forEach((tab) => this.trellisingDispatcher.clearSelection(tab));
                this._store.dispatch(action);
                // return action;
            })
            .withLatestFrom(
                this._store.select(getCurrentXAxisOption),
                this._store.select(getCurrentYAxisOption),
                this._store.select(getMultiEventDetailsOnDemandRequired)
            )
            .mergeMap(([[action, tabId], xAxisOption, yAxisOption, multiDetailsOnDemandRequired]) => {
                const selections: IChartSelection[] = action.payload.selection;
                const selectionRequests = [];

                selections.forEach((selection) => {
                    const trellising: List<ITrellis> = selection.get('trellising');
                    let series: List<ITrellises> = selection.get('series');
                    if (series) {
                        series = <any>series.filter((trellised: ITrellises) => {
                            return trellised.get('trellisedBy') !== 'All';
                        });
                    }
                    const selectedBars = selection.get('bars');
                    const selectionBox: IContinuousSelection[] = selection.get('range').toJS();
                    selectionBox.map(box => {
                        selectionRequests.push(this.dataService.getSelectionDetail(
                            this.tabId, xAxisOption, yAxisOption, series, trellising, box, selectedBars));
                    });
                });
                return forkJoin(selectionRequests);
            }, (action, payload) => {
                // checking for multiDetailsOnDemandRequired
                if (action[action.length - 1]) {
                    return this.trellisingActionCreator.makeUpdateMultiSelectionDetailAction(
                        MarkingUtils.mergeMultiple(<IMultiSelectionDetail[]>payload));
                } else {
                    return this.trellisingActionCreator.makeUpdateSelectionDetailAction(
                        MarkingUtils.merge(<ISelectionDetail[]>payload));
                }
            });
    }

    // NEW approach
    private trellisSelectionAction$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => {
                return action.type === TRELLIS_UPDATE_SELECTION && NEW_APPROACH_TAB_LIST.contains(tabId);
            })
            .do(([action, tabId]) => {
                // before setting selection for new tabId
                // we need to clear all the related tabs of selected data

                // Get all related tabs without the current
                const relatedTabs: Set<TabId> = Set(TabStoreUtils.relatedSubTabs(tabId)).subtract([tabId]);
                // Dispatch clearing action for all related tabs
                relatedTabs.map((id: TabId) => this.trellisingDispatcher.clearSelection(id));
                this._store.dispatch(action);
            })
            .withLatestFrom(
                this._store.select(getPlotDataRequestOptions),
                this._store.select(getMultiEventDetailsOnDemandRequired),
                this._store.select(getSubPlotTabId),
                this._store.select(getSubPlotInitialised),
                this._store.select(getMainPlotSelection),
                this._store.select(getParentSelectionPlot)
            )
            .mergeMap(([[action, tabId], {
                xAxisOption,
                yAxisOption,
                limit,
                offset,
                trellising,
                colorByOptions,
                plotSettings,
                filterTrellisByOptions,
            }, multiEventDetailsOnDemandRequired, subPlotTabId, subPlotInitialised,
                           {
                               subXAxisOption, subYAxisOption, subLimit, subOffset, subTrellising, subColorByOptions,
                               subPlotSettings, subFilterTrellisByOptions
                           }, parentSelectionPlot]) => {
                if (parentSelectionPlot) {
                    return new EmptyObservable();
                }
                if (subPlotTabId && subPlotInitialised) {
                    if (action.payload.selection.size === 1) {
                        const settings = constructDataRequestPayload(
                            subXAxisOption.toJS(),
                            subYAxisOption.toJS(),
                            subTrellising,
                            subLimit,
                            subOffset,
                            subColorByOptions,
                            subPlotSettings,
                            subFilterTrellisByOptions,
                            action.payload.selection
                        );
                        this.dataService.getPlotData(subPlotTabId, yAxisOption, settings).subscribe(data => {
                            this.updatePlotOptions(data, subPlotTabId);
                        });
                    }
                }
                return this.dataService.getSelection(
                    tabId,
                    formatSelection(action.payload.selection, yAxisOption.toJS()),
                    constructDataRequestPayload(
                        xAxisOption.toJS(),
                        yAxisOption.toJS(),
                        trellising, limit,
                        offset,
                        colorByOptions,
                        plotSettings,
                        filterTrellisByOptions
                    ),
                    yAxisOption.toJS()
                ).map((payload) => {
                    return !multiEventDetailsOnDemandRequired
                        ? this.trellisingActionCreator.makeUpdateSelectionDetailAction(payload, tabId)
                        : this.trellisingActionCreator.makeUpdateMultiSelectionDetailAction(payload, tabId);
                });
            });
    }

    // NEW approach
    private trellisChangeSettings$(): Observable<Action> {
        return this.actions$
            .withLatestFrom(this._store.select(getTabId))
            .filter(([action, tabId]) => {
                return action.type === TRELLIS_PLOT_SETTINGS_CHANGE && NEW_APPROACH_TAB_LIST.contains(tabId);
            })
            .do(([action, tabId]) => {
                this._store.dispatch(this.trellisingActionCreator.makeLoadingAction(true));
                this._store.dispatch(action);
            })
            .withLatestFrom(
                this._store.select(getCurrentNewYAxisOption),
                this._store.select(getTrellisingRequired)
            )
            .mergeMap(([[action, tabId], yAxisOption, trellisingRequired]: any) => {
                if (trellisingRequired) {
                    return this.dataService.getTrellisOptions(tabId, fromJS(yAxisOption))
                        .map((trellisOptions: TrellisOptions<any>[]) => {
                            return {
                                action, tabId, yAxisOption,
                                trellisOptions: trellisOptions.map(option => {
                                    (<any>option).category = 'MANDATORY_TRELLIS';
                                    return option;
                                })
                            };
                        });
                } else {
                    return Observable.of({action, tabId, yAxisOption, trellisOptions: []});
                }
            })
            .withLatestFrom(
                this._store.select(getColorByRequired),
                this._store.select(getCurrentNewXAxisOption),
                this._store.select(getCurrentPlotSettings)
            )
            .mergeMap(([{action, tabId, yAxisOption, trellisOptions}, colorByRequired, xAxisOption, currentPlotSettings]: any) => {
                if (colorByRequired) {
                    if (currentPlotSettings) {
                        return this.dataService.getColorByOptions(tabId, yAxisOption, currentPlotSettings)
                            .map((colorByOptions: TrellisOptions<any>[]) => {
                                return colorByOptions.map(option => {
                                    (<any>option).category = TrellisCategory.NON_MANDATORY_SERIES;
                                    return option;
                                });
                            })
                            .map((colorByOptions: TrellisOptions<any>[]) => {
                                this.updateTrellisOptions(colorByOptions.concat(trellisOptions), '');
                                return {tabId, xAxisOption, yAxisOption, trellisOptions};
                            });
                    }
                    return this.dataService.getColorByOptions(tabId, yAxisOption)
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            return colorByOptions.map(option => {
                                (<any>option).category = TrellisCategory.NON_MANDATORY_SERIES;
                                return option;
                            });
                        })
                        .map((colorByOptions: TrellisOptions<any>[]) => {
                            this.updateTrellisOptions(colorByOptions.concat(trellisOptions), '');
                            return {tabId, xAxisOption, yAxisOption, trellisOptions};
                        });
                } else {
                    this.updateTrellisOptions(trellisOptions, '');
                    return Observable.of({tabId, xAxisOption, yAxisOption, trellisOptions});
                }
            })
            .withLatestFrom(
                this._store.select(getCurrentLimit),
                this._store.select(getCurrentOffset),
                this._store.select(getCurrentNewColorBy),
                this._store.select(getCurrentPlotSettings),
                this._store.select(getFilterTrellisByOptions)
            )
            .mergeMap(([{tabId, xAxisOption, yAxisOption, trellisOptions},
                           limit,
                           offset,
                           colorByOptions,
                           plotSettings,
                           filterTrellisByOptions
                       ]) => {
                const settings = constructDataRequestPayload(
                    xAxisOption,
                    yAxisOption,
                    trellisOptions,
                    limit,
                    offset,
                    colorByOptions,
                    plotSettings,
                    filterTrellisByOptions
                );

                return this.dataService.getPlotData(tabId, yAxisOption, settings)
                    .map((payload: List<IPlot>) => {
                        this.trellisingDispatcher.clearSelection();
                        this.updatePlotOptions(payload, undefined, true);
                        return this.trellisingActionCreator.makeLoadingAction(false);
                    });
            });
    }

    // OLD approach
    private fetchChartData(tabId,
                           xAxisOption: DynamicAxis,
                           yAxisOption: string,
                           offset: number,
                           limit: number,
                           allTrellising: List<ITrellises>): Observable<List<IPlot>> {
        const trellising: List<ITrellises> = PaginatedTrellisService.paginatedTrellis(limit, offset, allTrellising);
        return this.dataService.getData(tabId, xAxisOption, yAxisOption, trellising);
    }

    private updatePlotOptions(payload: List<IPlot>, tabId?: TabId, resetToDefault?: boolean): void {
        this.trellisingDispatcher.updateNoData(payload);
        this.trellisingDispatcher.updatePlotData(payload, tabId);
        this.trellisingDispatcher.updateZoom(tabId, resetToDefault);
    }

    private updateTrellisOptions(payload: TrellisOptions<any>[], actionType: string, chartId?: TabId): void {
        this.trellisingDispatcher.updateTrellis(payload, chartId);
        if (actionType !== TRELLIS_CHANGE_AXIS) {
            this.trellisingDispatcher.updatePageNumber(1);
        }
        this.trellisingDispatcher.generateEmptyPlots();
    }

    private isOngoingDataset(): boolean {
        return this.session.currentSelectedDatasets[0].type === 'com.acuity.va.security.acl.domain.AcuityDataset';
    }

    private sortYAxisOptionsByDefaultOption(payload: string[]): string[] {
        const defaultYAxisOption = DefaultAxisService.initialY(this.tabId, payload);
        return union(
            filter(payload, (option: string) => option === defaultYAxisOption),
            filter(payload, (option: string) => option !== defaultYAxisOption)
        );
    }

    private sortXAxisOptionsByDefaultOption(payload: DynamicAxis[]): DynamicAxis[] {
        const defaultXAxisOption = DefaultAxisService.initialX(this.tabId, this.isOngoingDataset(), payload);
        return union(
            filter(payload, (option: DynamicAxis) => option === defaultXAxisOption),
            filter(payload, (option: DynamicAxis) => option !== defaultXAxisOption)
        );
    }
}
