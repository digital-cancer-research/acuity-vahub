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
import {includes, isEmpty, isEqual, merge, unionWith} from 'lodash';
import {fromJS, List, Map} from 'immutable';
import {WaterfallPlotConfigService} from './WaterfallPlotConfigService';
import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {
    IChartSelection,
    IContinuousSelection,
    ITrellis,
    ITrellises,
    IZoom,
    RANGE_DELTA,
    TabId
} from '../../../../store';
import {DialogueLocation} from '../../DialogueLocation';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {StackedBarChart} from '../../../../../../../vahub-charts/barchart/StackedBarChart';
import {ChartEvents, ChartMouseEvent, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'waterfall-plot',
    template: `
        <div></div>
    `,
    providers: [WaterfallPlotConfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WaterfallPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: Map<string, any>;
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
    @Input() colorByValue: string;
    @Input() selection: List<IChartSelection>;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();
    private dataY = [];
    private readonly UPPER_WATERFALL_PLOT_LINE = 20;
    private readonly LOWER_WATERFALL_PLOT_LINE = -30;
    chart: StackedBarChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: WaterfallPlotConfigService,
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

        if (changes['height'] && this.height) {
            this.updateHeight();
        }

        if (changes['selection']) {
            this.updateSelectionRendering();
            this.chart.update();
        }

        if (changes['zoomY'] && this.zoomY) {
            this.updateZoomY();
        }
        if (changes['zoomX'] && this.zoomX) {
            this.updateZoomX();
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected pointInSelectionRectangle(point, {categories, yMax, yMin}): boolean {
        return includes(categories, point.category);
    }

    protected getCategories(event): string[] {
        const xMin = event.xAxis[0].min;
        const xMax = event.xAxis[0].max;
        const yMin = event.yAxis[0].min;
        const yMax = event.yAxis[0].max;
        const indexes = this.plotData.get('entries').toJS()
            .filter(data => data.x >= xMin && data.x <= xMax &&
                (data.y >= yMin && data.y <= yMax || data.y >= yMax && yMax >= 0 || data.y <= yMin && yMin <= 0))
            .map(data => data.x);
        return indexes.map(i => event.xAxis[0].axis.categories[i]);
    }

    protected createChart(forceRenderChartWithEmptyData: boolean = false): void {
        super.createChart(forceRenderChartWithEmptyData);
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
            const entries = this.plotData.get('entries').toJS();
            const xcategories = this.plotData.get('xcategories').toJS();
            this.chart.xAxis[0].setCategories(xcategories);
            this.chart.addSeries({
                data: entries,
                borderWidth: 1,
                borderColor: '#B6B6B6',
                events: {
                    click: (event) => {
                        this.clickWithDoubleClickEvent(event);
                    }
                }
            });
            this.chart.addPlotLine({
                color: '#d10e0e',
                axis: 'y',
                value: this.UPPER_WATERFALL_PLOT_LINE,
                width: 1,
                styles: {
                    fill: 'none',
                    'stroke-dasharray': '4'
                }
            });
            this.chart.addPlotLine({
                color: '#d10e0e',
                axis: 'y',
                value: this.LOWER_WATERFALL_PLOT_LINE,
                width: 1,
                styles: {
                    fill: 'none',
                    'stroke-dasharray': '4'
                }
            });
            this.chart.redraw();
        }
        this.dataY = this.chart.series[0].data.map(point => point.y).slice();
    }

    protected chartEvents(): ChartEvents {
        const that = this;
        return {
            selection: function (event): boolean {
                const selectedPoints = [];
                const selectionRectangle = {
                    xMin: event.xAxis[0].min || 0,
                    xMax: event.xAxis[0].max || 0,
                    yMin: event.yAxis[0].min || 0,
                    yMax: event.yAxis[0].max || 0,
                    categories: undefined
                };
                const chartSelection: IChartSelection = that.selection ? that.selection.find((subSelection: IChartSelection) => {
                    const trellising = subSelection.get('trellising', null);
                    return trellising && isEqual(trellising.toJS(), that.trellising);
                }) : null;

                const jEvent: ChartMouseEvent = event;

                let xCategories: string[];
                let newRanges: IContinuousSelection[];
                let selection: any[];

                xCategories = that.getCategories(event);
                selectionRectangle.categories = xCategories;

                this.series.forEach((series) => {
                    series.points.forEach((point) => {
                        if (that.pointInSelectionRectangle(point, selectionRectangle)) {
                            selectedPoints.push({
                                category: point.category,
                                series: point.series.name === 'All' ? null : point.series.name,
                                yAxis: undefined
                            });
                        }
                    });
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
                            .filter((selectionItem) => {
                                const trellising = selectionItem.get('trellising', null);
                                return !(trellising && isEqual(trellising.toJS(), that.trellising));
                            })
                            .forEach(subSelection => selection.push(subSelection.toMap()));
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
                if (selection.filter((selectionItem) => {
                    return selectionItem.get('bars').size > 0
                        || selectionItem.get('range')
                            .filter(range => range.get('categories')).size > 0;
                }).length > 0) {

                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
                    that.displayMarkingDialogue.emit({
                        x: jEvent.originalEvent.offsetX,
                        y: jEvent.originalEvent.offsetY
                    });
                }

                return false;
            },
            click: function (event): boolean {
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
            },
        };
    }


    protected applySelection(): void {
        if (this.chart) {
            const selection = this.selection;

            if (isEqual(selection, this.currentSelection)) {
                return;
            }

            this.removeSelection();

            selection.forEach((selectionItem) => {
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        if (selectionItem.get('bars')
                            .map(i => i.get('category'))
                            .contains(point.category)) {
                            point.select(true, true);
                        }
                    });
                });
            });
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

    protected createPlotConfig(): UserOptions {
        const customConfig = this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.height
        );
        return merge(customConfig, this.chartPlotconfigService.additionalOptions(this.tabId, this.colorByValue));
    }

    protected updateZoomX(): void {
        if (this.chart) {
            const zoomMax = Math.min(this.zoomX.absMax, this.zoomX.zoomMax);
            this.chart.onZoomX([this.zoomX.zoomMin, zoomMax]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            this.chart.onZoomY([this.zoomY.zoomMin * RANGE_DELTA, this.zoomY.zoomMax * RANGE_DELTA]);
        }
    }

    protected handleClickEvent(event): boolean {
        const {
            point: {
                category
            },
            ctrlKey
        } = event;

        const currentSelection = this.selection.toJS();

        let bars;

        if (ctrlKey && !isEmpty(currentSelection)) {
            bars = [...currentSelection[0].bars, {
                category
            }];
        } else {
            bars = [{
                category,
            }];
        }

        const selection = [{
            bars,
            trellising: this.trellising,
            series: this.series
        }];

        this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        this.trellisingMiddleware.updateSelection(fromJS(selection));

        return false;
    }
}
