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
import {isEqual, cloneDeep, isNull} from 'lodash';
import {fromJS, List, is} from 'immutable';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/module';
import {GroupedBarPlotConfigService} from './GroupedBarPlotConfigService';
import {BarChartPlotData, BarChartSeriesData, GroupedBarPlotService} from './GroupedBarPlotService';
import {IContinuousSelection, IZoom, TabId, ITrellis, ITrellises, IChartSelection} from '../../../../index';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {DialogueLocation} from '../../DialogueLocation';
import OutputBarChartData = Request.OutputBarChartData;
import {GroupedBarChart} from '../../../../../../../vahub-charts/barchart/GroupedBarChart';
import {ChartEvents, ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'groupedbarplot',
    template: '<div></div>',
    providers: [GroupedBarPlotConfigService, GroupedBarPlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class GroupedBarPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: List<OutputBarChartData>;
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

    chart: GroupedBarChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: GroupedBarPlotConfigService,
                sessionEventService: SessionEventService,
                private groupedBarPlotService: GroupedBarPlotService,
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

    /**
     * The product of the the series trellis options in the plot
     */
    private barsInGroup(): number {
        const series: { trellisOptions: string[] }[] = <any> this.series;
        if (series.length === 0) {
            return 1;
        }
        return series
            .map(s => s.trellisOptions.length)
            .reduce((acc, cur) => acc * cur, 1);
    }

    /**
     * Currently this assumes only one trellisBy in series
     */
    private categoryIndex(category: string | number): number {
        if ((<any>this.series).length === 0) {
            return 0;
        } else {
            return this.series[0].trellisOptions.sort().reverse().indexOf(<string>category);
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
            this.chart.onZoomY([this.zoomY.zoomMin, this.zoomY.zoomMax]);
        }
    }

    protected chartEvents(): ChartEvents {
        return {
            selection: function(event): boolean {
                return this.handleClickEvent(event);
            },
            removeSelection: () => {
                this.trellisingDispatcher.clearSelections();
                this.displayMarkingDialogue.emit(null);
            },
        };
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
            const barChartData: BarChartPlotData = this.groupedBarPlotService.splitServerData(rawData);
            this.chart.xAxis[0].setCategories(barChartData.categories);
            barChartData.series.forEach((series) => {
                const seriesOptions: BarChartSeriesData = series;
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
            const chartSelection: IChartSelection = this.selection.find((selection: IChartSelection) => {
                const trellising = selection.get('trellising', null);
                return trellising && isEqual(trellising.toJS(), this.trellising);
            });

            //Prevent double rendering selection
            if (isEqual(chartSelection, this.currentSelection) || !chartSelection) {
                return;
            }
            this.removeSelection();
            chartSelection.get('bars').forEach(selectedBar => {
                const {category, series: seriesName} = selectedBar.toJS();
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        if (point.category === category
                            && (point.series.name === seriesName || isNull(seriesName) && point.series.name === 'All')) {
                            point.select(true, true);
                        }
                    });
                });
            });
            this.currentSelection = chartSelection;
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
            const barWidth = 1.0 - GroupedBarPlotConfigService.groupPadding * 2.0;
            const selectedRange = {
                xMin: 0.01 + event.point.x - 0.5 + GroupedBarPlotConfigService.groupPadding
                + barWidth * this.categoryIndex(event.point.series.name) / this.barsInGroup(),
                xMax: -0.01 + event.point.x - 0.5 + GroupedBarPlotConfigService.groupPadding
                + barWidth * (this.categoryIndex(event.point.series.name) + 1) / this.barsInGroup(),
                yMin: 0.0,
                yMax: event.point.y
            };
            let newRanges: IContinuousSelection[];
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
                newRanges.push(selectedRange);
                newBars = this.currentSelection && this.currentSelection.get('bars')
                    ? cloneDeep(this.currentSelection.get('bars'))
                    : [];
                newBars.push(selectedBar);
                const selectionInPlot = fromJS({
                    series: this.series,
                    trellising: this.trellising,
                    range: newRanges,
                    bars: newBars
                });
                if (this.selection && this.selection.size > 0) {
                    const sameChartSelectionIndex = this.selection.findIndex((chartSelection: IChartSelection) => {
                        const trellising = chartSelection.get('trellising', null);
                        return trellising && isEqual(trellising.toJS(), this.trellising);
                    });
                    if (sameChartSelectionIndex !== -1) {
                        selection = this.selection.updateIn([sameChartSelectionIndex, 'range'], range => range.push(fromJS(selectedRange)));
                        selection = selection.updateIn([sameChartSelectionIndex, 'bars'], bars => bars.push(fromJS(selectedBar)));
                    } else {
                        selection = this.selection.push(selectionInPlot);
                    }
                } else {
                    selection = List.of(selectionInPlot);
                }
            } else {
                selection = List.of(fromJS({
                    series: this.series,
                    trellising: this.trellising,
                    range: [selectedRange],
                    bars: [selectedBar]
                }));
            }
            this.trellisingMiddleware.updateSelection(selection);
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        }
        return false;
    }
}
