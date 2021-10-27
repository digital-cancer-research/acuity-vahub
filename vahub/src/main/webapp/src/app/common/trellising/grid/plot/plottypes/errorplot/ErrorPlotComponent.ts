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

import {
    Component,
    ElementRef,
    ChangeDetectionStrategy,
    NgZone,
    OnChanges,
    SimpleChange,
    OnDestroy,
    Input,
    Output,
    EventEmitter
} from '@angular/core';
import {Map, List, is} from 'immutable';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {ErrorPlotConfigService} from './ErrorPlotConfigService';
import {TabId} from '../../../../store';
import {IZoom, ITrellis, ITrellises, IChartSelection} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import ShiftPlotData = InMemory.ShiftPlotData;
import {isEqual} from 'lodash';
import {ShiftChart} from '../../../../../../../vahub-charts/shiftchart/ShiftChart';
import {PLOT_COLORS} from '../../../../../../../vahub-charts/utils/utils';
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'errorplot',
    template: '<div></div>',
    providers: [ErrorPlotConfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: Map<string, string>;
    @Input() xAxisLabel: string;
    @Input() globalXAxisLabel: string;
    @Input() yAxisLabel: string;
    @Input() globalYAxisLabel: string;
    @Input() xRange: { max: number, min: number };
    @Input() yRange: { max: number, min: number };
    @Input() title: string;
    @Input() zoomX: IZoom;
    @Input() zoomY: IZoom;
    @Input() isCategorical: boolean;
    @Input() trellising: List<ITrellis>;
    @Input() series: List<ITrellises>;
    @Input() height: number;
    @Input() tabId: TabId;
    @Input() selection: List<IChartSelection>;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();
    chart: ShiftChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: ErrorPlotConfigService,
                sessionEventService: SessionEventService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (changes['zoomX'] && this.zoomX && this.xRange) {
            this.updateZoomX();
        }
        if (changes['zoomY'] && this.zoomY && this.yRange) {
            this.updateZoomY();
        }
        if (changes['title'] && this.chart) {
            this.chart.setTitle(this.title);
        }
        if (changes['selection'] && !is(changes['selection'].previousValue, changes['selection'].currentValue)) {
            this.updateSelectionRendering();
        }
        if (changes['height'] && this.height) {
            this.updateHeight();
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected getCategories(event: ChartMouseEvent) {
        const {min, max} = event.xAxis[0];
        return event.xAxis[0].axis.categories.filter(x => x >= min && x <= max);
    }

    protected updateZoomX(): void {
        if (this.chart) {
            let zoomMax = this.zoomX.zoomMax;
            let zoomMin = this.zoomX.zoomMin;
            zoomMin = this.xRange.min + (this.xRange.max - this.xRange.min) * zoomMin / 100.0;
            zoomMax = this.xRange.min + (this.xRange.max - this.xRange.min) * zoomMax / 100.0;
            let range = zoomMax === zoomMin ? zoomMin : zoomMax - zoomMin;
            if (range === 0) {
                range = this.chart.series[0]['categories'][1];
            }
            const min = zoomMin - range * 0.05;
            const max = zoomMax + range * 0.05;
            this.chart.onZoomX([min, max]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            let zoomMax = this.zoomY.zoomMax;
            let zoomMin = this.zoomY.zoomMin;
            zoomMin = this.yRange.min + (this.yRange.max - this.yRange.min) * zoomMin / 100.0;
            zoomMax = this.yRange.min + (this.yRange.max - this.yRange.min) * zoomMax / 100.0;
            const range = zoomMax === zoomMin ? zoomMin : zoomMax - zoomMin;
            const min = zoomMin - range * 0.05;
            const max = zoomMax + range * 0.05;
            this.chart.onZoomY([min, max]);
        }
    }

    protected updateChart(): void {
        if (!this.chart) {
            this.createChart();
        } else {
            this.updateChartConfig();
        }
        if (this.chart) {
            if (this.plotData.isEmpty()) {
                return;
            }
            const rawData: ShiftPlotData = this.plotData.toJS();
            this.chart.xAxis[0].setTitle({text: this.globalXAxisLabel});
            this.chart.yAxis[0].setTitle({text: this.yAxisLabel});

            this.chart.addSeries({data: rawData.data});

            this.chart.addPlotLine({
                color: PLOT_COLORS.baseLine,
                x1: this.xRange.min,
                x2: this.xRange.max,
                y1: this.xRange.min,
                y2: this.xRange.max,
                width: 1,
                styles: {
                    fill: 'none',
                    'stroke-dasharray': '4,3'
                }
            });

            this.chart.redraw();
        }
    }

    protected removeSelection(): void {
        this.currentSelection = null;
        if (this.chart) {
            this.chart.redraw();
        }
    }
    protected applySelection(): void {
        if (this.chart) {
            let currentPlotSelection: IChartSelection;
            currentPlotSelection = this.selection.find((selections) => {
                const trellising = selections.get('trellising', null);
                return trellising && isEqual(trellising.toJS(), this.trellising);
            });
            if (isEqual(currentPlotSelection, this.currentSelection)) {
                return;
            }
            this.removeSelection();
            this.currentSelection = currentPlotSelection;
        }
    }
}
