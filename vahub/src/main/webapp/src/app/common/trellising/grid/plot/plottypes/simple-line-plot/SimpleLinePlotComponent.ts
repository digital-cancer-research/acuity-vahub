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
import * as _ from 'lodash';
import {isEmpty, isEqual, merge, unionWith} from 'lodash';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {IChartSelection, IContinuousSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {fromJS, List} from 'immutable';
import {SimpleLinePlotConfigService} from './SimpleLinePlotConfigService';
import {PlotSettings, ScaleTypes} from '../../../../store';
import {SelectionService} from '../../services/SelectionService';
import {SimpleLinePlotService} from './SimpleLinePlotService';
import {SimpleLineChart} from '../../../../../../../vahub-charts/linechart/SimpleLineChart';
import {ChartEvents, ChartMouseEvent, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'simple-lineplot',
    templateUrl: 'SimpleLinePlotComponent.html',
    providers: [
        SimpleLinePlotConfigService, SimpleLinePlotService
    ],
    styleUrls: ['SimpleLinePlotComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class SimpleLinePlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {

    @Input() plotData: List<any>;
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
    @Input() aggregationSetting: List<ITrellis>;
    @Input() plotSettings: PlotSettings;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();
    chart: SimpleLineChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: SimpleLinePlotConfigService,
                sessionEventService: SessionEventService,
                private simpleLinePlotService: SimpleLinePlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (!this.chart) {
            return;
        }
        if (changes['scaleType']) {
            const shouldScaleBeUpdated = this.updateYAxisScale(this.scaleType);
            if (shouldScaleBeUpdated) {
                this.updateZoomY();
                this.chart.redraw(false);
            }
        }
        if (changes['height'] && this.height) {
            this.updateHeight();
        }
        if (changes['selection']) {
            this.updateSelectionRendering();
            this.chart.update();
        }
        if (changes['zoomX'] && this.zoomX) {
            this.updateZoomX();
        }
        if (changes['zoomY'] && this.zoomY) {
            this.updateZoomY();
        }
        if (changes['title'] && this.chart && this.tabId !== TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
            this.chart.setTitle(this.title);
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    public updateYAxisScale(scaleType: ScaleTypes): boolean {
        if (scaleType === this.chart.yAxis[0].type) {
            return false;
        }
        switch (scaleType) {
            case ScaleTypes.LOGARITHMIC_SCALE:
                this.chart.yAxis[0].setType(ScaleTypes.LOGARITHMIC_SCALE);
                break;
            case ScaleTypes.LINEAR_SCALE:
                this.chart.yAxis[0].setType(ScaleTypes.LINEAR_SCALE);
                break;
            default:
                return false;
        }
        return true;
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
            const allCategories = SimpleLinePlotService.getStringCategories(rawData, this.isCategorical);
            if (this.tabId === TabId.TL_DIAMETERS_PLOT || this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
                const data = this.simpleLinePlotService.splitServerDataToZones(rawData, allCategories);
                data.forEach((item) => {
                    this.chart.addSeries({
                        type: 'simple-line',
                        name: item.name,
                        color: item.color,
                        zoneAxis: 'x',
                        zones: item.zones,
                        data: item.series,
                        events: {
                            click: (event) => {
                                this.handleClickEvent(event);
                            }
                        }
                    });
                });
            } else {
                rawData.forEach(datum => {
                    this.chart.addSeries({
                        type: 'simple-line',
                        name: datum.seriesBy,
                        color: datum.series[0].color,
                        data: datum.series.map(serie => {
                            return {
                                name: serie.name,
                                color: serie.color,
                                y: serie.y,
                                x: this.isCategorical ? allCategories.indexOf(serie.x) : serie.x,
                                category: serie.x,
                                standardDeviation: serie.standardDeviation
                            };
                        }),
                        events: {
                            click: (event) => {
                                this.handleClickEvent(event);
                            }
                        }
                    });
                    if (SimpleLinePlotService.shouldErrorBarsBeShown(this.plotSettings)) {
                        this.chart.addSeries({
                            type: 'errorbar',
                            data: datum.series.map(serie => {
                                return {
                                    x: this.isCategorical ? allCategories.indexOf(serie.x) : serie.x,
                                    low: serie.y - serie.standardDeviation,
                                    high: serie.y + serie.standardDeviation,
                                    color: serie.color
                                };
                            }),
                        });
                    }
                });
            }
            if (this.isCategorical) {
                this.chart.xAxis[0].setType(ScaleTypes.CATEGORY_SCALE);
                this.chart.xAxis[0].setCategories(allCategories);
            }
            if (this.scaleType === ScaleTypes.LOGARITHMIC_SCALE) {
                this.chart.yAxis[0].setType(ScaleTypes.LOGARITHMIC_SCALE);
            }
            this.chart.onZoomX([this.zoomX.zoomMin, this.zoomX.zoomMax]);
            // This is made to avoid this issue on CTDNA and analyte concentration plots
            // This happened only for some specific screen sizes. Here we add a bit op space to the bottom and to the top of the plot.
            if (this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
                window.scrollTo(0, document.body.scrollHeight);
                this.chart.setTitle(`${this.plotData.first().get('seriesBy').split(' ')[0]}: ${this.title}`);
                this.chart.redrawTitle();
            }
            this.chart.redraw();
        }
    }

    protected pointInSelectionRectangle(point, {xMin, xMax, yMin, yMax}): boolean {
        return point.x >= xMin && point.x <= xMax &&
            point.y >= yMin && point.y <= yMax;
    }

    protected applySelection(): void {
        if (this.chart) {
            const selection: List<IChartSelection> = this.selection;

            if (isEqual(selection, this.currentSelection)) {
                return;
            }

            this.removeSelection();

            selection.forEach((selectionItem: IChartSelection) => {
                const selectionItemJs = selectionItem.toJS();
                const trellisOption: string = selectionItemJs.trellising.length > 0
                    ? selectionItemJs.trellising[0].trellisOption
                    : undefined;

                selectionItemJs.range.forEach(selectionRectangle => {
                    this.chart.series
                        .filter(s => !trellisOption || s.name.indexOf(trellisOption) >= 0)
                        .forEach((series) => {
                        series.data.forEach((point) => {
                            if (this.pointInSelectionRectangle(point, selectionRectangle)) {
                                point.select(true, true);
                            }
                        });
                    });
                });
            });
        }
    }

    protected createPlotConfig(): UserOptions {
        const selectedSubject = this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT ? this.plotData.first().get('seriesBy') : null;
        const customConfig = this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.height,
            selectedSubject
        );
        return merge(customConfig, this.chartPlotconfigService.additionalOptions(this.tabId, this.globalYAxisLabel,
            this.globalXAxisLabel, this.plotSettings, this.scaleType));
    }

    protected updateZoomX(): void {
        if (this.chart) {
            if (this.isCategorical) {
                this.chart.xAxis[0].setType(ScaleTypes.CATEGORY_SCALE);
                this.chart.onZoomX([this.zoomX.zoomMin, this.zoomX.zoomMax]);
            } else {
                const zoomMax = Math.min(this.zoomX.zoomMax, this.zoomX.absMax);
                this.chart.xAxis[0].setType(ScaleTypes.LINEAR_SCALE);
                this.chart.onZoomX([this.zoomX.zoomMin, zoomMax]);
            }
        }
    }

    protected updateZoomY(): void {
        const shiftedYRange = SimpleLinePlotService.getShiftedYAxis(this.zoomY, this.canHaveLogScale(),
            this.tabId, this.globalYAxisLabel);
        this.chart.onZoomY([shiftedYRange.min, shiftedYRange.max]);
    }

    protected chartEvents(): ChartEvents {
        const that = this;
        return {
            selection: function (event): boolean {
                if (that.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
                    return false;
                }
                const selectedPoints = [];
                const selectionRectangle = {
                    xMin: event.xAxis[0].min || 0,
                    xMax: event.xAxis[0].max || 0,
                    yMin: event.yAxis[0].min || 0,
                    yMax: event.yAxis[0].max || 0
                };
                const chartSelection: IChartSelection = that.selection ? that.selection.find((subSelection: IChartSelection) => {
                    const trellising = subSelection.get('trellising', null);
                    return trellising && isEqual(trellising.toJS(), that.trellising);
                }) : null;

                const jEvent: ChartMouseEvent = event;

                let newRanges: IContinuousSelection[];
                let selection: any[];

                if (that.isCategorical) {
                    const alteredXSelection = SelectionService.alterCategoricalSelectionX(
                        selectionRectangle.xMin,
                        selectionRectangle.xMax
                    );
                    selectionRectangle.xMin = alteredXSelection.xMin;
                    selectionRectangle.xMax = alteredXSelection.xMax;

                }
                this.series.forEach((series) => {
                    series.points.forEach((point) => {
                        if (that.pointInSelectionRectangle(point, selectionRectangle)) {
                            selectedPoints.push({
                                category: point.category,
                                series: point.series.name === 'All' ? null : point.series.name,
                                yAxis: point.y
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
                    return (selectionItem.get('bars') && selectionItem.get('bars').size > 0)
                        || selectionItem.get('range')
                            .filter(range => range.get('categories')).size > 0;
                }).length > 0) {
                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
                    that.displayMarkingDialogue.emit({
                        x: jEvent.originalEvent.layerX || jEvent.originalEvent.sourceEvent.layerX,
                        y: jEvent.originalEvent.layerY || jEvent.originalEvent.sourceEvent.layerY
                    });
                }
                return false;
            },
            removeSelection: () => {
                this.trellisingDispatcher.clearSelections();
                this.displayMarkingDialogue.emit(null);
            },
            click: function (event): boolean {
                if (that.eventInSelection(event)) {
                    that.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
                } else {
                    that.trellisingDispatcher.clearSelections();
                }
                return false;
            }
        };
    }

    protected handleClickEvent(event) {
        if (this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
            return false;
        }
        if (event.point.selected) {
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        } else {
            const {
                point: {
                    category,
                    series: {
                        name
                    },
                    x, y
                },
                chartX, chartY,
                ctrlKey
            } = event;

            const currentSelection: IChartSelection[] = this.selection.toJS();

            let selection;

            if (ctrlKey && !isEmpty(currentSelection)) {
                let currentPlotSelection: IChartSelection;

                // from previous selections only the one on the current plot should be found and added
                // (it's important in case when user selects markers on different plots)
                currentPlotSelection = currentSelection.find(
                    cs => {
                        const correspondingTrellisOption: string = _.flatten(cs.trellising)
                            .map(t => t.trellisOption)
                            .find(to => to === this.trellising[0].trellisOption);
                        return !!correspondingTrellisOption;
                    });

                const bars = [...(currentPlotSelection ? currentPlotSelection.bars : []), {
                    category,
                    series: name,
                    yAxis: y
                }];

                const range = [...(currentPlotSelection ? currentPlotSelection.range : []), {
                    xMin: x,
                    xMax: x,
                    yMin: y,
                    yMax: y
                }];

                selection = [...currentSelection.filter(cs => cs !== currentPlotSelection), {
                    bars,
                    range,
                    trellising: this.trellising,
                    series: this.series
                }];
            } else {
                const bars = [{
                    category,
                    series: name,
                    yAxis: y
                }];

                const range = [{
                    xMin: x,
                    xMax: x,
                    yMin: y,
                    yMax: y
                }];

                selection = [{
                    bars,
                    range,
                    trellising: this.trellising,
                    series: this.series
                }];
            }

            this.trellisingMiddleware.updateSelection(fromJS(selection));
            this.displayMarkingDialogue.emit({x: chartX, y: chartY});
        }

        return false;
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {
        if (this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
            return false;
        }
        const { series, xAxis, yAxis } = this.chart;
        const newSelection = SelectionService.getAllSelection(series, true, xAxis[0].isCategorical, yAxis[0].isCategorical);
        const newRange = newSelection.range;
        const newSelectedBars = newSelection.selectedBars;

        if (isEqual(this.currentSelection, [newRange])) {
            this.trellisingDispatcher.clearSelections();
        } else {
            const selectionInPlot = fromJS({
                series: this.series,
                trellising: this.trellising,
                range: [newRange],
                bars: newSelectedBars
            });

            let selection: any[];
            if (event.ctrlKey && this.selection && this.selection.size > 0) {
                selection = [];
                this.selection.forEach(subSelection => {
                    const trellising = subSelection.get('trellising') ? subSelection.get('trellising').toJS() : null;
                    if (!isEqual(trellising, this.trellising)) {
                        selection.push(subSelection.toMap());
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

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    if (typeof point.select === 'function') {
                        point.select(false, true);
                    }
                });
            });
        }
    }

    private canHaveLogScale(): boolean {
        return this.tabId === TabId.ANALYTE_CONCENTRATION;
    }
}
