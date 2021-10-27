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

import {Component, Input, ChangeDetectionStrategy, SimpleChanges, OnChanges} from '@angular/core';
import {List} from 'immutable';
import * as  _ from 'lodash';

import {IPlot, TrellisDesign, TabId} from '../index';
import {YAxisRangeService, Range} from './range/yaxis/YAxisRangeService';
import {XAxisRangeService} from './range/xaxis/XAxisRangeService';
import {TrellisingObservables} from '../store/observable/TrellisingObservables';

@Component({
    selector: 'trellis-grid',
    templateUrl: 'GridComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class GridComponent implements OnChanges {
    @Input() limit: number;
    @Input() columnLimit: number;
    @Input() columns: number;
    @Input() actualLimit: number;
    @Input() plots: List<IPlot>;
    @Input() trellisDesign: TrellisDesign;
    @Input() height: number;
    @Input() tabId: TabId;
    @Input() page: number;

    plotHeight: number;
    rowList: number[];
    columnList: number[];

    constructor(private yAxisRangeService: YAxisRangeService,
                private xAxisRangeService: XAxisRangeService,
                public trellisingObservables: TrellisingObservables) {

    }

    public ngOnChanges(changes: SimpleChanges): void {
        this.setPlotHeight();
        this.setRowList();
        this.setColumnList();
    }

    public xRange(row: number, col: number): Range {
        return this.xAxisRangeService.getRange(row, col, this.columnLimit, this.limit, this.plots, this.tabId, this.trellisDesign);
    }

    public yRange(row: number, col: number): Range {
        return this.yAxisRangeService.getRange(row, col, this.columnLimit, this.limit, this.plots, this.tabId);
    }

    public plot(row: number, col: number): IPlot {
        if (!this.columnLimit) {
            return;
        }
        const index = row * this.columns + col;
        return this.plots.get(index, undefined);
    }

    private setPlotHeight(): void {
        if (!this.height || !this.limit || !this.columnLimit) {
            this.plotHeight = 0;
        } else {
            this.plotHeight = this.height / Math.ceil(this.actualLimit / this.columns);
        }
    }

    private setRowList(): void {
        if (!this.limit || !this.columnLimit) {
            this.rowList = [];
        } else {
            this.rowList = _.range(Math.floor(this.actualLimit / this.columns));
        }
    }

    private setColumnList(): void {
        if (!this.columnLimit) {
            this.columnList = [];
        } else {
            this.columnList = _.range(this.columns);
        }
    }
}
