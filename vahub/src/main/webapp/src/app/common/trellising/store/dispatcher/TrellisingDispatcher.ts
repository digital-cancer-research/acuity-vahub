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
import {Store} from '@ngrx/store';
import {fromJS, List} from 'immutable';

import {DynamicAxis, FilterId, IPlot, ITrellises, PlotSettings, ScaleTypes, TabId, TabStore} from '../ITrellising';
import {TabStoreUtils} from '../utils/TabStoreUtils';
import 'rxjs/add/operator/take';
import {TimelineConfigService} from '../services/TimelineConfigService';
import {SessionEventService} from '../../../../session/module';
import {TrellisingActionCreator} from '../actions/TrellisingActionCreator';
import {TimelineId} from '../../../../plugins/timeline/store/ITimeline';
import {ActionTypes} from '../actions/TrellisingActions';
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class TrellisingDispatcher {
    private trellisingActionCreator: TrellisingActionCreator;
    private tabId: TabId;

    constructor(private _store: Store<TabStore>,
                private timelineConfigService: TimelineConfigService,
                private session: SessionEventService) {
    }

    public setTabId(tabId: TabId): void {
        this.tabId = tabId;
        this.trellisingActionCreator = new TrellisingActionCreator(tabId);
    }

    public updateHeight(height: number): void {
        this._store.dispatch(TrellisingActionCreator.makeUpdateHeightAction(height));
    }

    public localResetNotification(filterId: FilterId): void {
        TabStoreUtils.tabsRelatedToFilter(filterId).forEach((tabId) => {
            this._store.dispatch(TrellisingActionCreator.makeLocalInitialisingAction(false, tabId));
        });
    }

    public globalResetNotification(): void {
        this._store.dispatch(TrellisingActionCreator.makeGlobalInitialisingAction(false));
    }

    public generateEmptyPlots(): void {
        this._store.dispatch(this.trellisingActionCreator.makeGenerateEmptyPlotsAction());
    }

    public updatePlotData(data: List<IPlot>, tabId?: TabId): void {
        this._store.dispatch(this.trellisingActionCreator.makeUpdatePlotDataAction(data, tabId || this.tabId));
    }

    public updateNoData(data: List<IPlot>): void {
        const noData = data.size === 0;
        this._store.dispatch(this.trellisingActionCreator.makeNoDataAction(noData));
    }

    public updateEventDetailsOnDemand(tableData: any): void {
        this._store.dispatch(this.trellisingActionCreator.makeUpdateEventDetailsOnDemandAction(tableData));
    }

    public updateSubjectDetailsOnDemand(tableData: any, tabId: TabId): void {
        this._store.dispatch(this.trellisingActionCreator.makeUpdateSubjectDetailsOnDemandAction(tableData, tabId));
    }

    public updateMultiEventDetailsOnDemand(tableData: any, tabId: TabId): void {
        this._store.dispatch(this.trellisingActionCreator.makeUpdateMultiEventDetailsOnDemandAction(tableData, tabId));
    }


    public clearSelections(): void {
        this._store.dispatch(this.trellisingActionCreator.makeClearSelectionsAction());
    }

    public clearSelection(tabId?: TabId): void {
        this._store.dispatch(this.trellisingActionCreator.makeClearSelectionAction(tabId));
    }

    public clearSubPlot(tabId: TabId): void {
        this._store.dispatch(TrellisingActionCreator.makeClearSubPlotAction(tabId));
    }

    public clearColorMap(): void {
        this._store.dispatch(this.trellisingActionCreator.makeClearColorMapAction());
    }

    public updateTrellis(trellising: TrellisOptions<any>[], chartId?: TabId): void {
        const iTrellising: List<ITrellises> = fromJS(trellising);
        this._store.dispatch(this.trellisingActionCreator.makeChangeTrellisingAction(iTrellising, this.isOngoingDataset(), chartId));
    }

    public updateTrellisColorBy(trellising: TrellisOptions<any>[]): void {
        this._store.dispatch(this.trellisingActionCreator.makeChangeTrellisingColorByAction(fromJS(trellising)));
    }

    public updatePlotSettings(plotSetting: PlotSettings): void {
        this._store.dispatch(this.trellisingActionCreator.makeChangePlotSettingsAction(plotSetting));
    }

    public updateXAxisOption(option: DynamicAxis): void {
        this._store.dispatch(this.trellisingActionCreator.makeUpdateXAxisOption(option));
    }

    public updateXAxisOptions(options: DynamicAxis[]): void {
        const xAxisOptions: List<DynamicAxis> = fromJS(options);
        this._store.dispatch(this.trellisingActionCreator.makeChangeXAxisOptionsAction(xAxisOptions));
    }

    public updateYAxisOptions(options: string[]): void {
        const yAxisOptions: List<string> = fromJS(options);
        this._store.dispatch(this.trellisingActionCreator.makeChangeYAxisOptionsAction(yAxisOptions));
    }

    public updateZoom(tabId?: TabId, resetToDefault?: boolean): void {
        this._store.dispatch(this.trellisingActionCreator.makeAutoUpdateZoomAction(tabId, resetToDefault));
    }

    public setInitialStateOfTimeline(): void {
        this._store.dispatch(TrellisingActionCreator.makeInitialStateOfTimelineAction(
            this.timelineConfigService.getInitialState(this.tabId), TimelineId.COMPARE_SUBJECTS));
    }

    public updatePageNumber(pageNumber: number): void {
        this._store.dispatch(this.trellisingActionCreator.makePageNumberAction(pageNumber));
    }

    private isOngoingDataset(): boolean {
        return this.session.currentSelectedDatasets[0].type === 'com.acuity.va.security.acl.domain.AcuityDataset';
    }

    public updateScale(scaleType: ScaleTypes): void {
        this._store.dispatch(this.trellisingActionCreator.makeChangeScaleTrellisingAction(scaleType));
    }

    public updateLegendDisabled(legendDisabled: boolean): void {
        this._store.dispatch(this.trellisingActionCreator.updateLegendDisabled(legendDisabled));
    }

    public cacheColorByForYAxis(currentYAxisOption: string, currentColorBy: string, chartId: TabId): void {
        this._store.dispatch(this.trellisingActionCreator.cacheColorByForAxis(currentYAxisOption, currentColorBy, chartId));
    }

    public updateZoomRange(axisType: 'xAxis' | 'yAxis', rangeValue): void {
        console.log(axisType, rangeValue, this.tabId);
        this._store.dispatch({
            type: ActionTypes.TRELLIS_CHANGE_ZOOM_RANGE,
            payload: {
                axisType,
                range: rangeValue,
                tabId: this.tabId
            }
        });
    }
}
