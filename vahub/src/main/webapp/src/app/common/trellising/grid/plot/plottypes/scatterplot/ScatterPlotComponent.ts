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
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    NgZone,
    OnChanges,
    OnDestroy,
    Output,
    SimpleChange
} from '@angular/core';
import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/module';
import {ScatterPlotConfigService} from './ScatterPlotConfigService';
import {IChartSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {is, List, Map} from 'immutable';
import {ScatterData, ScatterPlotService} from './ScatterPlotService';
import {RANGE_DELTA, MINIMUM_ZOOM_PERCENTAGE} from '../../../../store';
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import {isEqual} from 'lodash';
import {ScatterChart} from '../../../../../../../vahub-charts/scatterchart/ScatterChart';
import {ChartMouseEvent, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'scatterplot',
    template: `<div></div>`,
    providers: [ScatterPlotConfigService, ScatterPlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ScatterPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: Map<string, TrellisedScatterPlot<string, string>>;
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
    chart: ScatterChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: ScatterPlotConfigService,
                sessionEventService: SessionEventService,
                private scatterPlotService: ScatterPlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (this.chart) {
            if (changes['selection'] && !is(changes['selection'].previousValue, changes['selection'].currentValue)) {
                this.updateSelectionRendering();
                this.chart.update();
            }
        }
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (changes['zoomX'] && this.zoomX) {
            this.updateZoomX();
        }
        if (changes['zoomY'] && this.zoomY && this.yRange) {
            this.updateZoomY();
        }
        if (changes['title'] && this.chart) {
            this.chart.setTitle(this.title);
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

    protected updateZoomX(): void {
        if (this.chart) {
            let zoomMax = this.zoomX.zoomMax;
            let zoomMin = this.zoomX.zoomMin;
            if (zoomMax === zoomMin) {
                zoomMin -= this.xRange.max * MINIMUM_ZOOM_PERCENTAGE;
                zoomMax += this.xRange.max * MINIMUM_ZOOM_PERCENTAGE;
            } else {
                zoomMax *= RANGE_DELTA;
            }
            this.chart.onZoomX([zoomMin, zoomMax]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            let zoomMax = this.zoomY.zoomMax;
            let zoomMin = this.zoomY.zoomMin;
            if (this.tabId !== TabId.LIVER_HYSLAW && this.tabId !== TabId.SINGLE_SUBJECT_LIVER_HYSLAW) {
                zoomMin = this.yRange.min + (this.yRange.max - this.yRange.min) * zoomMin / 100.0;
                zoomMax = this.yRange.min + (this.yRange.max - this.yRange.min) * zoomMax / 100.0;
            }
            if (zoomMax === zoomMin) {
                zoomMin -= this.yRange.max * MINIMUM_ZOOM_PERCENTAGE;
                zoomMax += this.yRange.max * MINIMUM_ZOOM_PERCENTAGE;
            } else {
                zoomMax *= RANGE_DELTA;
            }
            this.chart.onZoomY([zoomMin, zoomMax]);
        }
    }

    protected createPlotConfig(): UserOptions {
        const { xaxisLabel, yaxisLabel } = this.plotData.toJS();
        return this.chartPlotconfigService.createPlotConfig(
            this.title,
            xaxisLabel,
            this.globalXAxisLabel,
            yaxisLabel,
            this.globalYAxisLabel,
            this.height
        );
    }

    protected updateChart(): void {
        if (!this.chart) {
            this.createChart();
        } else {
            this.updateChartConfig();
        }
        if (this.chart) {
            if (!this.plotData) {
                return;
            }
            if (this.chart.series.length > 0) {
                this.chart.clearSeries();
            }
            const that = this;
            const rawData: TrellisedScatterPlot<string, string> = this.plotData.toJS();
            this.chart.xAxis[0].setTitle({text: rawData.xaxisLabel});
            this.chart.yAxis[0].setTitle({text: rawData.yaxisLabel});
            const scatterSeries: ScatterData[] = this.scatterPlotService.reformatServerData(rawData.data);
            scatterSeries.forEach(data => {
                this.chart.addSeries({
                    type: 'point',
                    data: data.data,
                    color: data.color,
                    events: {
                        click: function (event: ChartMouseEvent): boolean {
                            return that.clickWithDoubleClickEvent(event);
                        }
                    }
                });
            });

            this.chart.redraw(false);
        }
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    point.select(false);
                });
            });
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

            //Prevent double rendering selection
            if (isEqual(currentPlotSelection, this.currentSelection)) {
                return;
            }
            this.removeSelection();
            currentPlotSelection.get('range').map(subSelection => subSelection.toJS()).forEach(subSelection => {
                const xMin: number = subSelection.xMin;
                const xMax: number = subSelection.xMax;
                const yMin: number = subSelection.yMin;
                const yMax: number = subSelection.yMax;
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        if (point.x >= xMin && point.x <= xMax) {
                            if (point.y >= yMin && point.y <= yMax) {
                                point.select(true);
                            }
                        }
                    });
                });
            });
            this.currentSelection = currentPlotSelection;
        }
        this.chart.redraw();
    }
}
