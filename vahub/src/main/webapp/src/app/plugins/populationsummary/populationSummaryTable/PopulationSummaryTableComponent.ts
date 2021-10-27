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

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {zip} from 'rxjs/observable/zip';
import {List} from 'immutable';

import {FilterEventService} from '../../../filters/module';
import {PopulationSummaryTableService} from './PopulationSummaryTableService';
import {DynamicAxis, FilterId, IPlot, TabId} from '../../../common/trellising/store/index';
import {Trellising} from '../../../common/trellising/store/Trellising';
import {TrellisingObservables} from '../../../common/trellising/store/observable/TrellisingObservables';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {GroupBySetting} from '../../../common/trellising/store/actions/TrellisingActionCreator';

@Component({
    selector: 'population-table',
    templateUrl: 'PopulationSummaryTableComponent.html',
    styleUrls: ['PopulationSummaryTableComponent.css']
})
export class PopulationSummaryTableComponent implements OnInit, OnDestroy {

    rowSettingsTooltip = 'Row settings';
    columnSettingsTooltip = 'Column settings';
    valueSettingsTooltip = 'Value display';
    tabId = TabId.POPULATION_TABLE;
    barchartTabId = TabId.POPULATION_BARCHART;

    private lastFilter: FilterId;
    private shouldBeSubscribed = true;

    constructor(protected filterEventService: FilterEventService,
                public dashboardTableModel: PopulationSummaryTableService,
                public trellising: Trellising,
                public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher,
                public timelineDispatcher: TimelineDispatcher) {
    }

    ngOnInit(): void {
        this.trellising.setTabId(this.barchartTabId);
        this.trellisingDispatcher.setTabId(this.barchartTabId);
        this.trellisingObservables.setTabId(this.barchartTabId);

        this.listenToDataUpdate();
        this.listenToEventFilterChanges();
        this.listenToEventCountChange();
    }

    ngOnDestroy(): void {
        this.shouldBeSubscribed = false;
    }

    listenToEventCountChange(): void {
        const populationCountSubscription = this.filterEventService.populationFilterSubjectCount
            .takeWhile(() => this.shouldBeSubscribed)
            .subscribe((populationCount: number) => {
                if (populationCount === 0) {
                    this.trellising.setNoData(this.lastFilter);
                }
            });
        this.filterEventService.addSubscription(this.barchartTabId.toString() + 'populationCounts', populationCountSubscription);
    }

    listenToEventFilterChanges(): void {
        const populationFilterSubscription = this.filterEventService.populationFilter
            .takeWhile(() => this.shouldBeSubscribed)
            .subscribe((changed) => {
                this.onFilter(FilterId.POPULATION, changed);
                this.timelineDispatcher.globalResetNotification();
            });
        this.addSubscription(FilterId.POPULATION, populationFilterSubscription);
    }

    listenToDataUpdate(): void {
        zip(
            this.trellisingObservables.plots,
            this.trellisingObservables.newYAxisOption,
            (plots: List<IPlot>, yOption: GroupBySetting) => ({plots, yOption})
        )
            .takeWhile(() => this.shouldBeSubscribed)
            .subscribe((res: any) => {
                const data = res.plots ? res.plots.toJS() : [];

                if (data.length > 0 && data[0].data && data[0].data.length > 0) {
                    this.dashboardTableModel.processData(data[0].data, res.yOption.get('groupByOption'));
                }
            });
    }

    selectColumnOptions(newLabel: string): void {
        this.trellising.updateYAxisOption(newLabel);
    }

    selectData(event: MouseEvent, selection: any, type: string, rowName: string): any {
        if (selection) {
            //Uncomment if timeout on click action is required
            // if (this.timeout) {
            //     clearTimeout(this.timeout);
            //     this.timeout = null;
            // }
            // this.dashboardTableModel.selectData(event, selection, type, rowName);
            //Uncomment if timeout on click action is required
            // this.timeout = setTimeout(() => {
            //     let data = {
            //         columnGrouping: this.dashboardTableModel.columnGrouping,
            //         rowGrouping: this.dashboardTableModel.rowGrouping,
            //         counts: this.dashboardTableModel.dashboard.selectedData
            //     };
            // }, 1000);
        }
    }

    changeColumnGrouping(newLabel: DynamicAxis): void {
        this.trellising.updateXAxisOption(newLabel);
    }

    cellSelected(cell: string): boolean {
        // return this.dashboardTableModel.dashboard.highlightedCells.indexOf(cell) !== -1;
        return false;
    }

    private addSubscription(filterId: FilterId, subscription: Subscription): void {
        const key: string = this.barchartTabId.toString() + filterId.toString();
        this.filterEventService.addSubscription(key, subscription);
    }

    private onFilter(filterId: String, changed: any): void {
        if (changed) {
            this.lastFilter = filterId;
            this.trellisingDispatcher.localResetNotification(filterId);
            this.trellising.reset();
        }
    }
}
