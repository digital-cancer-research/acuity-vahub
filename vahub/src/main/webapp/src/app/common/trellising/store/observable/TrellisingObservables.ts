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
import {Store} from '@ngrx/store';
import {List, Map} from 'immutable';

import {
    DynamicAxis,
    IChartSelection,
    IDetailsOnDemand,
    ILegend,
    IMultiSelectionDetail,
    IPlot,
    ISelectionDetail,
    ITrellises,
    IZoom,
    NEW_APPROACH_TAB_LIST,
    PlotSettings,
    ScaleTypes,
    TabId,
    TabStore,
    TrellisDesign,
    XAxis,
    YAxis
} from '../ITrellising';
import 'rxjs/add/operator/take';
import {DisplayedGroupBySetting, GroupBySetting} from '../actions/TrellisingActionCreator';
import * as fromTrellising from '../reducer/TrellisingReducer';
import {ApplicationState} from '../../../store/models/ApplicationState';

@Injectable()
export class TrellisingObservables {
    private tabId: TabId;

    loading: Observable<boolean>;
    noData: Observable<boolean>;
    xAxis: Observable<XAxis>;
    xAxisOption: Observable<DynamicAxis>;
    xAxisOptions: Observable<DynamicAxis[]>;

    newXAxisOption: Observable<GroupBySetting>;
    newXAxisOptions: Observable<DisplayedGroupBySetting>;
    newYAxisOption: Observable<GroupBySetting>;
    newYAxisOptions: Observable<DisplayedGroupBySetting>;

    xAxisZoom: Observable<IZoom>;
    xAxisTextZoomRequired: Observable<boolean>;
    legendSortingRequired: Observable<boolean>;
    yAxis: Observable<YAxis>;
    yAxisOption: Observable<string>;
    yAxisOptions: Observable<string[]>;
    yAxisZoom: Observable<IZoom>;
    advancedMeasuredControlRequired: Observable<boolean>;
    page: Observable<number>;
    pages: Observable<number[]>;
    columns: Observable<number>;
    actualLimit: Observable<number>;
    plots: Observable<List<IPlot>>;
    limit: Observable<number>;
    offset: Observable<number>;
    trellisDesign: Observable<TrellisDesign>;
    columnLimit: Observable<number>;
    height: Observable<number>;
    legend: Observable<ILegend[]>;
    legendDisabled: Observable<boolean>;
    zoomDisabled: Observable<boolean>;
    zoomIsHidden: Observable<boolean>;
    detailsOnDemandDisabled: Observable<boolean>;
    multiDetailsOnDemandRequired: Observable<boolean>;
    eventDetailsOnDemandDisabled: Observable<boolean>;
    selection: Observable<List<IChartSelection>>;
    selectionDetail: Observable<ISelectionDetail>;
    multiSelectionDetail: Observable<IMultiSelectionDetail>;
    trellising: Observable<List<ITrellises>>;
    baseTrellising: Observable<List<ITrellises>>;
    eventDetailsOnDemand: Observable<IDetailsOnDemand>;
    multiEventDetailsOnDemand: Observable<Map<any, IDetailsOnDemand>>;
    subjectDetailsOnDemand: Observable<IDetailsOnDemand>;
    logarithmicScaleRequired: Observable<boolean>;
    settingLabelRequired: Observable<boolean>;
    availableScaleTypes: Observable<ScaleTypes[]>;
    scaleType: Observable<ScaleTypes>;
    isAllColoringOptionAvailable: Observable<boolean>;
    hasEventFilters: Observable<boolean>;
    jumpToAesFromAeNumberLocation: Observable<string>;
    plotSettingsRequired: Observable<boolean>;
    plotSettings: Observable<PlotSettings>;
    customControlLabels: Observable<string[]>; // if some explanation to what the colorBy control is doing is needed, null if not needed
    subPlotTabId: Observable<TabId>;

    advancedAxisControlRequired$: Observable<boolean>;
    preserveTitle$: Observable<boolean>;
    parentSelectionPlot$: Observable<TabId>;
    zoomRangeRequired$: Observable<boolean>;
    cBioJumpRequired$: Observable<boolean>;
    zoomMargin: Observable<number>;

    constructor(private _store: Store<ApplicationState>) {
    }

    isNewApproachTab(tabId: TabId): boolean {
        return NEW_APPROACH_TAB_LIST.contains(<string>tabId);
    }

    public setTabId(tabId: TabId): void {
        this.tabId = tabId;
        this.subscribeToStore();
    }

    public getCurrentIsInitialised(): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', this.tabId, 'isInitialised']);
    }

    public getCurrentXAxisOption(): DynamicAxis {
        const state = this.getState(this._store);
        const xAxisOption = state.getIn(['tabs', this.tabId, 'xAxis', 'option']);
        return xAxisOption !== null && xAxisOption ? xAxisOption.toJS() : null;
    }

    public getCurrentYAxisOption(): string {
        const state = this.getState(this._store);
        return state.getIn(['tabs', this.tabId, 'yAxis', 'option']);
    }

    public getHasEventFilters(): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', this.tabId, 'hasEventFilters']);
    }

    public getCurrentTrellising(): List<ITrellises> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', this.tabId, 'trellising']);
    }

    public getAllTrellises(): List<ITrellises> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', this.tabId, 'baseTrellising']);
    }

    public getAllTrellisesByTabId(tabId: TabId): List<ITrellises> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'baseTrellising']);
    }

    public getCurrentTrellisingByTabId(tabId: TabId): List<ITrellises> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'trellising']);
    }
    public getAllColoringOptionAvailable(tabId: TabId): List<ITrellises> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'isAllColoringOptionAvailable']);
    }

    public getSelectionByTabId(tabId: TabId): List<IChartSelection> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'selections']);
    }

    public getSelectionDetailByTabId(tabId: TabId): List<ISelectionDetail> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'detail']);
    }

    getXAxisOptions(tabId: TabId): DynamicAxis[] {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'xAxis', 'options']);
    }

    getXAxisOption(tabId: TabId): DynamicAxis {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'xAxis', 'option']);
    }

    getYAxisOptions(tabId: TabId): DynamicAxis[] {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'yAxis', 'options']);
    }

    getYAxisOption(tabId: TabId): DynamicAxis {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'yAxis', 'option']);
    }

    getNewXAxisOptions(tabId: TabId): DisplayedGroupBySetting {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'groupByOptions', 'X_AXIS']);
    }

    getNewXAxisOption(tabId: TabId): GroupBySetting {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'groupBySettings', 'X_AXIS']);
    }

    getNewYAxisOptions(tabId: TabId): DisplayedGroupBySetting {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'groupByOptions', 'Y_AXIS']);
    }

    getNewYAxisOption(tabId: TabId): GroupBySetting {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'groupBySettings', 'Y_AXIS']);
    }

    getAdvancedMeasuredControlRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'advancedMeasuredControlRequired']);
    }

    getLoading(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'isLoading']);
    }

    getZoomDisabled(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'zoomDisabled']);
    }

    getZoomIsHidden(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'zoomDisabled']);
    }

    getXAxisTextZoomRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'xAxisTextZoomRequired']);
    }

    getXAxisZoom(tabId: TabId): IZoom {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'xAxis', 'zoom']);
    }

    getYAxisZoom(tabId: TabId): IZoom {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'yAxis', 'zoom']);
    }

    getNoData(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'noData']);
    }

    getTrellisDesign(tabId: TabId): TrellisDesign {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'trellisDesign']);
    }

    getLegend(tabId: TabId): ILegend[] {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'legend']);
    }

    getLegendDisabled(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'legendDisabled']);
    }

    getLegendSortingRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'legendSortingRequired']);
    }

    getPage(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'page', 'value']);
    }

    getZoomMargin(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'zoomMargin']);
    }

    getPages(tabId: TabId): number[] {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'pages']);
    }

    getPlots(tabId: TabId): List<IPlot> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'plots']);
    }

    getLimit(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'page', 'limit']);
    }

    getColumnLimit(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'columnLimit']);
    }

    getColumns(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'columns']);
    }

    getActualLimit(tabId: TabId): number {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'actualLimit']);
    }

    getCustomControlLabels(tabId: TabId): string[] {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'customControlLabels']);
    }

    getDetailsOnDemandDisabled(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'detailsOnDemandDisabled']);
    }

    getParentPlotId(tabId: TabId): TabId {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'parentSelectionPlot']);
    }

    getMultiDetailsOnDemandRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'multiDetailsOnDemandRequired']);
    }

    getEventDetailsOnDemandDisabled(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'eventDetailsOnDemandDisabled']);
    }

    getEventDetailsOnDemand(tabId: TabId): IDetailsOnDemand {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'eventDetailsOnDemand']);
    }

    getMultiEventDetailsOnDemand(tabId: TabId): Map<any, IDetailsOnDemand> {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'multiEventDetailsOnDemand']);
    }

    getSubjectDetailsOnDemand(tabId: TabId): IDetailsOnDemand {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'subjectDetailsOnDemand']);
    }

    getSelectionDetail(tabId: TabId): ISelectionDetail {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'selectionDetail']);
    }

    getMultiSelectionDetail(tabId: TabId): IMultiSelectionDetail {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'multiSelectionDetail']);
    }

    getAdvancedAxisControlRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'advancedAxisControlRequired']);
    }

    getPreserveTitle(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'preserveTitle']);
    }

    getPreserveGlobalTitle(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'preserveGlobalTitle']);
    }

    getTrellisingRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'trellisingRequired']);
    }

    getPlotSettings(tabId: TabId): PlotSettings {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'plotSettings']);
    }

    isLogarithmicScaleRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'logarithmicScaleRequired']);
    }

    isSettingLabelRequired(tabId: TabId): boolean {
        const state = this.getState(this._store);
        return state.getIn(['tabs', tabId, 'settingLabelRequired']);
    }

    private getState(store: Store<ApplicationState>): TabStore {
        let state: any;
        store.take(1).subscribe(s => state = s);
        return state.trellisingReducer;
    }


    private subscribeToStore(): void {
        const tabStore: Observable<TabStore> = this._store.select('trellisingReducer');
        this.loading = tabStore.map(data => data.getIn(['tabs', this.tabId, 'isLoading']));
        this.noData = tabStore.map(data => data.getIn(['tabs', this.tabId, 'noData']));
        this.xAxis = tabStore.map(data => data.getIn(['tabs', this.tabId, 'xAxis']));
        this.zoomMargin = tabStore.map(data => data.getIn(['tabs', this.tabId, 'zoomMargin']));
        this.selection = tabStore.map(data => data.getIn(['tabs', this.tabId, 'selections']));
        this.selectionDetail = tabStore.map(data => data.getIn(['tabs', this.tabId, 'detail']));
        this.multiSelectionDetail = tabStore.map(data => data.getIn(['tabs', this.tabId, 'multiSelectionDetail']));
        this.legend = tabStore.map(data => data.getIn(['tabs', this.tabId, 'legend']));
        this.xAxisOption = tabStore.map(data => data.getIn(['tabs', this.tabId, 'xAxis', 'option']));
        this.xAxisOptions = tabStore.map(data => data.getIn(['tabs', this.tabId, 'xAxis', 'options']));

        this.newXAxisOption = tabStore.map(data => data.getIn(['tabs', this.tabId, 'groupBySettings', 'X_AXIS']));
        this.newXAxisOptions = tabStore.map(data => data.getIn(['tabs', this.tabId, 'groupByOptions', 'X_AXIS']));
        this.newYAxisOption = tabStore.map(data => data.getIn(['tabs', this.tabId, 'groupBySettings', 'Y_AXIS']));
        this.newYAxisOptions = tabStore.map(data => data.getIn(['tabs', this.tabId, 'groupByOptions', 'Y_AXIS']));

        this.xAxisZoom = tabStore.map(data => data.getIn(['tabs', this.tabId, 'xAxis', 'zoom']));
        this.xAxisTextZoomRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'xAxisTextZoomRequired']));
        this.legendSortingRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'legendSortingRequired']));
        this.yAxis = tabStore.map(data => data.getIn(['tabs', this.tabId, 'yAxis']));
        this.yAxisOption = tabStore.map(data => data.getIn(['tabs', this.tabId, 'yAxis', 'option']));
        this.yAxisOptions = tabStore.map(data => data.getIn(['tabs', this.tabId, 'yAxis', 'options']));
        this.yAxisZoom = tabStore.map(data => data.getIn(['tabs', this.tabId, 'yAxis', 'zoom']));
        this.advancedMeasuredControlRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'advancedMeasuredControlRequired']));
        this.legendDisabled = tabStore.map(data => data.getIn(['tabs', this.tabId, 'legendDisabled']));
        this.zoomDisabled = tabStore.map(data => data.getIn(['tabs', this.tabId, 'zoomDisabled']));
        this.zoomIsHidden = tabStore.map(data => data.getIn(['tabs', this.tabId, 'zoomIsHidden']));
        this.detailsOnDemandDisabled = tabStore.map(data => data.getIn(['tabs', this.tabId, 'detailsOnDemandDisabled']));
        this.multiDetailsOnDemandRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'multiDetailsOnDemandRequired']));
        this.eventDetailsOnDemandDisabled = tabStore.map(data => data.getIn(['tabs', this.tabId, 'eventDetailsOnDemandDisabled']));
        this.page = tabStore.map(data => data.getIn(['tabs', this.tabId, 'page', 'value']));
        this.limit = tabStore.map(data => data.getIn(['tabs', this.tabId, 'page', 'limit']));
        this.offset = tabStore.map(data => data.getIn(['tabs', this.tabId, 'page', 'offset']));
        this.pages = tabStore.map(data => data.getIn(['tabs', this.tabId, 'pages']));
        this.columnLimit = tabStore.map(data => data.getIn(['tabs', this.tabId, 'columnLimit']));
        this.columns = tabStore.map(data => data.getIn(['tabs', this.tabId, 'columns']));
        this.actualLimit = tabStore.map(data => data.getIn(['tabs', this.tabId, 'actualLimit']));
        this.plots = tabStore.map(data => data.getIn(['tabs', this.tabId, 'plots']));
        this.trellisDesign = tabStore.map(data => data.getIn(['tabs', this.tabId, 'trellisDesign']));
        this.height = tabStore.map(data => data.get('height'));
        this.trellising = tabStore.map(data => data.getIn(['tabs', this.tabId, 'trellising']));
        this.baseTrellising = tabStore.map(data => data.getIn(['tabs', this.tabId, 'baseTrellising']));
        this.eventDetailsOnDemand = tabStore.map(data => data.getIn(['tabs', this.tabId, 'eventDetailsOnDemand']));
        this.subjectDetailsOnDemand = tabStore.map(data => data.getIn(['tabs', this.tabId, 'subjectDetailsOnDemand']));
        this.multiEventDetailsOnDemand = tabStore.map(data => data.getIn(['tabs', this.tabId, 'multiEventDetailsOnDemand']));
        this.logarithmicScaleRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'logarithmicScaleRequired']));
        this.settingLabelRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'settingLabelRequired']));
        this.availableScaleTypes = tabStore.map(data => data.getIn(['tabs', this.tabId, 'availableScaleTypes']));
        this.scaleType = tabStore.map(data => data.getIn(['tabs', this.tabId, 'scaleType']));
        this.isAllColoringOptionAvailable = tabStore.map(data => data.getIn(['tabs', this.tabId, 'isAllColoringOptionAvailable']));
        this.hasEventFilters = tabStore.map(data => data.getIn(['tabs', this.tabId, 'hasEventFilters']));
        this.jumpToAesFromAeNumberLocation = tabStore.map(data => data.getIn(['tabs', this.tabId, 'jumpToAesFromAeNumberLocation']));
        this.plotSettingsRequired = tabStore.map(data => data.getIn(['tabs', this.tabId, 'plotSettingsRequired']));
        this.plotSettings = tabStore.map(data => data.getIn(['tabs', this.tabId, 'plotSettings']));
        this.customControlLabels = tabStore.map(data => data.getIn(['tabs', this.tabId, 'customControlLabels']));

        this.advancedAxisControlRequired$ = this._store.select(fromTrellising.getAdvancedAxisControlRequired);
        this.preserveTitle$ = this._store.select(fromTrellising.getPreserveTitle);
        this.parentSelectionPlot$ = this._store.select(fromTrellising.getParentSelectionPlot);
        this.zoomRangeRequired$ = this._store.select(fromTrellising.getZoomRangeRequired);
        this.cBioJumpRequired$ = this._store.select(fromTrellising.getCBioJumpRequired);
    }
}
