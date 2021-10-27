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
import {fromJS, is, List} from 'immutable';
import * as  _ from 'lodash';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/module';
import {RangePlotConfigService} from './RangePlotConfigService';
import {RangePlotService} from './RangePlotService';
import {ICategoricalSelection, IChartSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import RangeChartSeries = Request.RangeChartSeries;
import {RangeChart} from '../../../../../../../vahub-charts/linechart/RangeChart';
import {ScaleTypes} from '../../../../store';
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'rangeplot',
    template: '<div></div>',
    providers: [RangePlotConfigService, RangePlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RangePlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: List<RangeChartSeries<any, any>>;
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
    chart: RangeChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: RangePlotConfigService,
                sessionEventService: SessionEventService,
                private rangePlotService: RangePlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (this.chart) {
            if (changes['zoomX'] && this.zoomX && this.xRange) {
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

    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected getCategories(event: ChartMouseEvent): string[] {
        let categories;
        if (event.xAxis[0].axis.categories) {
            categories = _.chain(this.plotData.toJS())
                .flatMap('data')
                .map('x')
                .uniq()
                .value()
                .slice(Math.ceil(event.xAxis[0].min), Math.ceil(event.xAxis[0].max));
        } else {
            categories = _.chain(this.plotData.toJS())
                .flatMap('data')
                .map('x')
                .filter(x => event.xAxis[0].min <= Math.ceil(x) && event.xAxis[0].max >= Math.ceil(x))
                .uniq()
                .value()
                .map(_.toNumber);
        }
        return categories;
    }

    protected updateZoomX(): void {
        if (this.isCategorical) {
            let min = this.xRange.min + (this.xRange.max - this.xRange.min) * this.zoomX.zoomMin / 100.0;
            let max = this.xRange.min + (this.xRange.max - this.xRange.min) * this.zoomX.zoomMax / 100.0;
            max = max < 1 ? 1 : max;
            min = max - min <= 1 ? max - 1 : min;
            this.chart.xAxis[0].setType(ScaleTypes.CATEGORY_SCALE);
            this.chart.onZoomX([min, max]);
        } else {
            this.chart.xAxis[0].setType(ScaleTypes.LINEAR_SCALE);
            this.chart.onZoomX([this.zoomX.zoomMin, this.zoomX.zoomMax]);
        }
    }

    protected updateZoomY(): void {
        const min = this.yRange.min + (this.yRange.max - this.yRange.min) * this.zoomY.zoomMin / 100.0;
        const max = this.yRange.min + (this.yRange.max - this.yRange.min) * this.zoomY.zoomMax / 100.0;
        if (this.chart) {
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
            const rawData = this.plotData.toJS();

            const rangePlotData = this.rangePlotService.splitServerData(rawData, this.isCategorical);
            const that = this;
            if (rangePlotData.categories) {
                this.chart.xAxis[0].setCategories(rangePlotData.categories);
                this.chart.xAxis[0].setType(ScaleTypes.CATEGORY_SCALE);
            }
            this.chart.yAxis[0].setTitle({text: this.yAxisLabel});
            rangePlotData.data.forEach(subPlotData => {
                const color = subPlotData.color || RangePlotService.colourPalette(subPlotData.name);

                this.chart.addSeries({
                    name: subPlotData.name,
                    color,
                    zIndex: 2,
                    type: 'line',
                    marker: {
                        symbol: 'circle',
                        fillColor: 'white',
                        lineWidth: 2,
                        lineColor: color,
                        enabled: true,
                        states: {
                            select: {
                                lineColor: 'black',
                                fillColor: 'white'
                            }
                        }
                    },
                    events: {
                        click: function (event: ChartMouseEvent): boolean {
                            return that.clickWithDoubleClickEvent(event);
                        }
                    },
                    data: subPlotData.averages,
                    showInLegend: false
                });

                this.chart.addSeries({
                    name: subPlotData.name + '- Range',
                    color: color,
                    zIndex: 0,
                    type: 'arearange',
                    fillOpacity: 0.3,
                    lineWidth: 0,
                    data: subPlotData.ranges,
                    showInLegend: true,
                    marker: {
                        enabled: false,
                        states: {
                            hover: {
                                enabled: false
                            },
                            select: {
                                enabled: false
                            }
                        }
                    }
                });
            });

            this.chart.redraw();
        }
    }

    protected applySelection(): void {
        if (this.chart) {
            let currentPlotSelection: IChartSelection;
            currentPlotSelection = this.selection.find((selections) => {
                return this.isTrellisMatch(selections);
            });
            const selection: ICategoricalSelection[] = currentPlotSelection.get('range') ? currentPlotSelection.get('range').toJS() : [];

            // Prevent double rendering selection
            if (_.isEqual(selection, this.currentSelection)) {
                return;
            }
            this.removeSelection();


            selection.forEach(subSelection => {
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        if (this.pointInSelectionRectangle(point, subSelection)) {
                            point.select(true);
                        }
                    });
                });
            });

            this.currentSelection = selection;
            this.chart.update();
        }
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    if (typeof point.select === 'function') {
                        point.select(false);
                    }
                });
            });
        }
    }

    protected pointInSelectionRectangle(point, selectionRectangle): boolean {
        const { yMin, yMax, xMin, xMax } = selectionRectangle;
        return point.x >= xMin && point.x <= xMax
            && point.y >= yMin && point.y <= yMax;
    }

    protected isSelectionTrellisMatch(): boolean {
        const that = this;
        return this.selection ? this.selection.some((selection) => {
            return that.isTrellisMatch(selection);
        }) : false;
    }

    private isTrellisMatch(selection): boolean {
        const trellising = _.filter(selection.get('trellising', null).toJS(), {category: 'MANDATORY_TRELLIS'});
        return _.isEqual(trellising, this.trellising);
    }

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        if (event.point.selected) {
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        } else {
            const x = event.point.category;
            const yMin = event.point.y;
            const yMax = event.point.y;
            let newRanges = [];
            let selection: List<IChartSelection>;
            let trellising;
            if (event.point.series.name === 'All'
                || this.tabId === TabId.LAB_LINEPLOT
                || this.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT) {
                trellising = this.trellising;
            } else {
                trellising = this.trellising.concat([{
                    trellisedBy: this.series[0].trellisedBy,
                    trellisOption: event.point.series.name,
                    category: this.series[0].category
                }]);
            }

            // Appending selection
            if (event.ctrlKey) {
                newRanges = _.cloneDeep(this.currentSelection);
                newRanges = newRanges ? newRanges : [];

                const trellisOption: string = trellising[0].trellisOption;
                const categories = _.flatten(this.selection
                    .filter(i =>
                        !trellisOption || i.toJS().trellising[0].trellisOption === trellisOption)
                    .map(i => i.get('bars'))
                    .toJS());

                let selectionInPlot;

                if (this.tabId === TabId.LAB_LINEPLOT || this.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT) {
                    const newCategory = {
                        category: x,
                        series: event.point.series.name
                    };
                    categories.push(newCategory);

                    newRanges.push({
                        categories: [newCategory],
                        yMin: yMin,
                        yMax: yMax
                    });

                    selectionInPlot = fromJS({
                        series: this.series,
                        trellising: trellising,
                        bars: categories,
                        range: newRanges
                    });
                } else {
                    newRanges.push({
                        categories: [x],
                        yMin: yMin,
                        yMax: yMax
                    });

                    selectionInPlot = fromJS({
                        series: this.series,
                        trellising: trellising,
                        range: newRanges
                    });
                }
                if (this.selection && this.selection.size > 0) {
                    const currentSelection = [];
                    this.selection
                        .filter((selectionItem) => {
                            const currentTrellising = selectionItem.get('trellising', null);
                            return !(currentTrellising && _.isEqual(currentTrellising.toJS(), this.trellising));
                        })
                        .forEach(subSelection => currentSelection.push(subSelection));
                    currentSelection.push(selectionInPlot);
                    selection = List(currentSelection);
                } else {
                    selection = List.of(selectionInPlot);
                }
            } else {
                let categories = [x];
                if (this.tabId === TabId.LAB_LINEPLOT || this.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT) {
                    categories = [{
                        category: x,
                        series: event.point.series.name
                    }];
                    newRanges.push({
                        categories,
                        yMin: yMin,
                        yMax: yMax
                    });

                    selection = List.of(fromJS({
                        bars: categories,
                        series: this.series,
                        trellising: trellising,
                        range: newRanges
                    }));
                } else {
                    newRanges.push({
                        categories,
                        yMin: yMin,
                        yMax: yMax
                    });

                    selection = List.of(fromJS({
                        series: this.series,
                        trellising: trellising,
                        range: newRanges
                    }));
                }
            }

            this.trellisingMiddleware.updateSelection(selection);
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        }
        return false;
    }

}
