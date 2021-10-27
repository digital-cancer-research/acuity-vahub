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
import {List, fromJS, is} from 'immutable';
import {isEqual, unionWith, isNull} from 'lodash';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {BarLineService} from './BarLineService';
import {BarLinePlotConfigService} from './BarLinePlotConfigService';
import {SessionEventService} from '../../../../../../session/module';
import {IZoom, TabId, ITrellis, ITrellises, IChartSelection, ScaleTypes, IContinuousSelection} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {SelectionService} from '../../services/SelectionService';
import OutputOvertimeData = InMemory.OutputOvertimeData;
import {BarLineChart, MeasureNumberOptions} from '../../../../../../../vahub-charts/barchart/BarLineChart';
import {ChartEvents, ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'barlineplot',
    template: '<div></div>',
    providers: [BarLineService, BarLinePlotConfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BarLinePlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {

    @Input() plotData: List<OutputOvertimeData>;
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
    @Input() scaleType: ScaleTypes;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();
    chart: BarLineChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: BarLinePlotConfigService,
                sessionEventService: SessionEventService,
                private barLineService: BarLineService,
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
            this.chart.onZoomY([Math.round(this.zoomY.zoomMin), Math.round(this.zoomY.zoomMax)]);
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
            const rawData = <OutputOvertimeData>this.plotData.toJS();
            const data = this.barLineService.splitServerData(rawData);
            this.chart.xAxis[0].setCategories(data.categories);

            data.series.forEach((series) => {
                const seriesOptions = {...series};
                seriesOptions.events = {
                    click: function (event: ChartMouseEvent): boolean {
                        return that.clickWithDoubleClickEvent(event);
                    }
                };
                this.chart.addSeries(seriesOptions);
            });

            data.lines.forEach((series) => {
                this.chart.addSeries(series);
            });

            this.chart.redraw();
        }
    }

    protected chartEvents(): ChartEvents {
        const that = this;
        return {
            selection: function (event: ChartMouseEvent): boolean {
                const selectedPoints = [];
                const selectionRectangle = {
                    xMin: event.xAxis[0].min,
                    xMax: event.xAxis[0].max,
                    yMin: event.yAxis[0].min,
                    yMax: event.yAxis[0].max
                };

                const chartSelection: IChartSelection = that.selection ? that.selection.find((subSelection: IChartSelection) => {
                    const trellising = subSelection.get('trellising', null);
                    return trellising && isEqual(trellising.toJS(), that.trellising);
                }) : null;

                const jEvent: ChartMouseEvent = event;

                let newRanges: IContinuousSelection[];
                let selection: IChartSelection[];

                this.series.forEach((series) => {
                    if (series.name !== MeasureNumberOptions) {
                        series.data.forEach((point) => {
                            if (that.pointInSelectionRectangle(point, selectionRectangle)) {
                                selectedPoints.push({
                                    category: point.category,
                                    series: point.series.name === 'All' ? null : point.series.name,
                                    yAxis: that.tabId === TabId.ANALYTE_CONCENTRATION ? point.y0 : undefined
                                });
                            }
                        });
                    }
                });
                // Appending selection
                if (event.originalEvent.ctrlKey) {
                    newRanges = chartSelection && chartSelection.get('range') ? chartSelection.get('range').toJS() : [];
                    const previousPoints = chartSelection && chartSelection.get('bars') ? chartSelection.get('bars').toJS() : [];
                    newRanges.push(selectionRectangle);

                    const selectionInPlot = fromJS({
                        series: that.series,
                        trellising: that.trellising,
                        range: newRanges,
                        bars: unionWith(previousPoints, selectedPoints, isEqual)
                    });
                    if (that.selection && that.selection.size > 0) {
                        selection = [];
                        that.selection
                            .filter((subSelection: IChartSelection) => {
                                const trellising = subSelection.get('trellising', null);
                                return !(trellising && isEqual(trellising.toJS(), that.trellising));
                            })
                            .forEach(subSelection => selection.push(subSelection));
                        selection.push(selectionInPlot);
                    } else {
                        selection = [selectionInPlot];
                    }
                } else {
                    newRanges = [selectionRectangle];
                    selection = [fromJS({
                        series: that.series,
                        trellising: that.trellising,
                        range: newRanges,
                        bars: selectedPoints
                    })];
                }
                if (selection.filter((subSelection) => subSelection.get('bars').size > 0).length > 0) {
                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
                    if (jEvent.target.mouseDownX - jEvent.originalEvent.offsetX < 0) {
                        that.displayMarkingDialogue.emit({
                            x: jEvent.chartX,
                            y: jEvent.chartY
                        });
                    } else {
                        that.displayMarkingDialogue.emit({x: jEvent.chartX, y: jEvent.chartY});
                    }
                }

                return false;
            },
            click: function (event: ChartMouseEvent): boolean {
                if (that.eventInSelection(event)) {
                    that.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
                } else {
                    that.trellisingDispatcher.clearSelections();
                }
                return false;
            },
            removeSelection: () => {
                this.trellisingDispatcher.clearSelections();
                this.displayMarkingDialogue.emit(null);
            }
        };
    }

    protected applySelection(): void {
        if (this.chart) {
            const chartSelection = this.selection.find((selection: IChartSelection) => {
                const trellising = selection.get('trellising', null);
                return trellising && isEqual(trellising.toJS(), this.trellising);
            });
            //Prevent double rendering selection
            if (isEqual(chartSelection, this.currentSelection) || !chartSelection) {
                return;
            }
            const that = this;
            that.removeSelection();
            chartSelection.get('bars').forEach(selectedBar => {
                const {category, series: seriesName} = selectedBar.toJS();
                this.chart.series.forEach((series) => {
                    if (series.name !== MeasureNumberOptions) {
                        series.data.forEach((point) => {
                            if (point.category === category
                                && (point.series.name === seriesName || isNull(seriesName) && point.series.name === 'All')) {
                                point.select(true, true);
                            }
                        });
                    }
                });
            });
            this.currentSelection = chartSelection;
        }
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                if (series.name !== MeasureNumberOptions) {
                    series.data.forEach((point) => {
                        point.select(false, true);
                    });
                }
            });
            this.chart.redraw();
        }
    }

    protected eventInSelection(event: ChartMouseEvent): boolean {
         return super.eventInSelection(event);
    }

    protected pointInSelectionRectangle(point: any, selectionRectangle: IContinuousSelection): boolean {
        const rectYMin = point.y0;
        const rectYMax = point[1] + point.y0;

        return point[0] >= selectionRectangle.xMin &&
            point[0] <= selectionRectangle.xMax &&
            (
                rectYMin >= selectionRectangle.yMin &&
                rectYMax <= selectionRectangle.yMax ||

                rectYMin <= selectionRectangle.yMin &&
                rectYMax > selectionRectangle.yMin ||

                rectYMin < selectionRectangle.yMax &&
                rectYMax >= selectionRectangle.yMax ||

                rectYMin < selectionRectangle.yMin &&
                rectYMax >= selectionRectangle.yMax
            );
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {
        const { series, xAxis, yAxis } = this.chart;
        const newSelection = SelectionService.getAllSelection(series, false, xAxis[0].isCategorical, yAxis[0].isCategorical);
        const newRange = newSelection.range;
        if (isEqual(this.currentSelection, [newRange])) {
            this.trellisingDispatcher.clearSelections();
        } else {
            const alteredXSelection = SelectionService.alterCategoricalSelectionX(newRange.xMin, newRange.xMax);
            newRange.xMin = alteredXSelection.xMin;
            newRange.xMax = alteredXSelection.xMax;

            const selectionInPlot = fromJS({
                series: this.series,
                trellising: this.trellising,
                range: [newRange],
                bars: newSelection.selectedBars
            });

            let selection: IChartSelection[];
            if (event.ctrlKey && this.selection && this.selection.size > 0) {
                selection = [];
                this.selection.forEach(subSelection => {
                    const trellising = subSelection.get('trellising') ? subSelection.get('trellising').toJS() : null;
                    if (!isEqual(trellising, this.trellising)) {
                        selection.push(subSelection);
                    }
                });
                selection.push(selectionInPlot);
            } else {
                selection = [selectionInPlot];
            }
            this.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
            this.displayMarkingDialogue.emit({x: event.layerX, y: event.layerY});
            return false;
        }
        return;
    }
}
