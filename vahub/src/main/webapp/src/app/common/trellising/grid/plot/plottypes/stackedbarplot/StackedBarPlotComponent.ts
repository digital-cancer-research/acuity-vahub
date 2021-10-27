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
    Input, Output, EventEmitter
} from '@angular/core';
import {is, fromJS, List} from 'immutable';
import {isEqual, cloneDeep} from 'lodash';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/module';
import {StackedBarPlotConfigService} from './StackedBarPlotConfigService';
import {StackedBarPlotService} from './StackedBarPlotService';
import {IZoom, TabId, ITrellis, ITrellises, IChartSelection, IContinuousSelection} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import OutputBarChartData = Request.OutputBarChartData;
import TrellisedBarChart = Request.TrellisedBarChart;
import {StackedBarChart} from '../../../../../../../vahub-charts/barchart/StackedBarChart';
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'stackedbarplot',
    template: '<div></div>',
    providers: [StackedBarPlotConfigService, StackedBarPlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StackedBarPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: List<TrellisedBarChart<any, any>>;
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
    chart: StackedBarChart;

    readonly BAR_WIDTH_HALF = 0.5;
    readonly DELTA = 0.01;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: StackedBarPlotConfigService,
                sessionEventService: SessionEventService,
                private barPlotservice: StackedBarPlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
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
        if (changes['selection'] && !is(changes['selection'].previousValue, changes['selection'].currentValue)) {
            this.updateSelectionRendering();
            this.chart.update();
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
            if (this.zoomX.absMax < zoomMax) {
                zoomMax = this.zoomX.absMax;
            }
            this.chart.onZoomX([this.zoomX.zoomMin, zoomMax]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            let min = this.zoomY.zoomMin,
                max = this.zoomY.zoomMax;
            if (this.zoomY.zoomMin === this.zoomY.zoomMax) {
                const padding = (this.zoomY.absMax - this.zoomY.absMin) / 100;
                min = min > padding ? min - padding : 0;
                max = (max + padding < this.zoomY.absMax) ? max + padding : this.zoomY.absMax;
            }
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
            if (this.chart.series.length > 0) {
                this.chart.clearSeries();
            }
            const that = this;
            const rawData: OutputBarChartData[] = this.plotData.toJS();
            const barChartData = this.barPlotservice.splitServerData(rawData);
            this.chart.xAxis[0].setCategories(barChartData.categories);
            const sortedSeries = barChartData.series.reverse();
            sortedSeries.forEach((series) => {
                const seriesOptions: any = series;
                seriesOptions.events = {
                    click: function (event: ChartMouseEvent): boolean {
                        return that.clickWithDoubleClickEvent(event);
                    }
                };
                this.chart.addSeries(seriesOptions);
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
            // const selection: IContinuousSelection[] = currentPlotSelection.get('range') ? currentPlotSelection.get('range').toJS() : [];

            //Prevent double rendering selection
            if (isEqual(currentPlotSelection, this.currentSelection)) {
                return;
            }
            const that = this;
            that.removeSelection();
            currentPlotSelection.get('range').map(subSelection => subSelection.toJS()).forEach(subSelection => {
                const xMin: number = subSelection.xMin;
                const xMax: number = subSelection.xMax;
                const yMin: number = subSelection.yMin;
                const yMax: number = subSelection.yMax;
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        const aPoint = <any>point;
                        //In category
                        const lowerX = point.x - this.BAR_WIDTH_HALF;
                        const upperX = point.x + this.BAR_WIDTH_HALF;
                        if (Math.max(lowerX, xMin) <= Math.min(upperX, xMax)) {
                            //In column segment
                            const lowerY = aPoint.y0;
                            const upperY = aPoint.y + aPoint.y0;
                            if (Math.max(lowerY, yMin) < Math.min(upperY, yMax)) {
                                point.select(true, true);
                            }
                        }
                    });
                });
            });
            this.currentSelection = currentPlotSelection;
        }
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    point.select(false, true);
                });
            });
        }
    }

    protected eventInSelection(event: ChartMouseEvent): boolean {
        return super.eventInSelection(event);
    }

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        if (event.point.selected) {
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        } else {
            const selectedRange: IContinuousSelection = {
                xMin: event.point.x - this.BAR_WIDTH_HALF + this.DELTA,
                xMax: event.point.x + this.BAR_WIDTH_HALF - this.DELTA,
                yMin: event.point.stackY - event.point.y + this.DELTA,
                yMax: event.point.stackY - this.DELTA
            };

            let newRanges: IContinuousSelection[];
            const rangeToAdd: IContinuousSelection = selectedRange;
            let newBars: any[];
            let selection: List<IChartSelection>;

            const selectedBar = {
                category: event.point.category,
                series: event.point.series.name === 'All' ? null : event.point.series.name
            };

            //Appending selection
            if (event.ctrlKey) {
                newRanges = this.currentSelection && this.currentSelection.get('range')
                    ? cloneDeep(this.currentSelection.get('range'))
                    : [];
                newBars = this.currentSelection && this.currentSelection.get('bars')
                    ? cloneDeep(this.currentSelection.get('bars'))
                    : [];
                newBars.push(selectedBar);
                newRanges.push(rangeToAdd);
                const selectionInPlot = fromJS({
                    series: this.series,
                    trellising: this.trellising,
                    range: newRanges,
                    bars: newBars
                });

                if (this.selection && this.selection.size > 0) {
                    const sameChartSelectionIndex = this.selection.findIndex((subSelection: IChartSelection) => {
                        const trellising = subSelection.get('trellising', null);
                        return trellising && isEqual(trellising.toJS(), this.trellising);
                    });
                    if (sameChartSelectionIndex !== -1) {
                        selection = this.selection.updateIn([sameChartSelectionIndex, 'range'], range => range.push(fromJS(rangeToAdd)));
                        selection = selection.updateIn([sameChartSelectionIndex, 'bars'], bars => bars.push(fromJS(selectedBar)));
                    } else {
                        selection = this.selection.push(selectionInPlot);
                    }
                } else {
                    selection = List.of(selectionInPlot);
                }
            } else {
                newRanges = [selectedRange];
                selection = List.of(fromJS({
                    series: this.series,
                    trellising: this.trellising,
                    range: newRanges,
                    bars: [selectedBar]
                }));
            }
            this.trellisingMiddleware.updateSelection(selection);
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        }
        return false;
    }

    protected pointInSelectionRectangle(point: any, selectionRectangle: IContinuousSelection): boolean {
        const rectYMin = point.y0;
        const rectYMax = point.y + point.y0;
        return point.x >= selectionRectangle.xMin &&
            point.x <= selectionRectangle.xMax &&
            (rectYMin >= selectionRectangle.yMin &&
                rectYMax <= selectionRectangle.yMax ||
                rectYMin < selectionRectangle.yMin &&
                rectYMax > selectionRectangle.yMin ||
                rectYMin < selectionRectangle.yMax &&
                rectYMax > selectionRectangle.yMax ||
                rectYMin < selectionRectangle.yMin &&
                rectYMax > selectionRectangle.yMax);
    }
}
