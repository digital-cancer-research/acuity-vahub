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
