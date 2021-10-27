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
} from '@angular/core';
import {isEqual, unionWith} from 'lodash';
import {fromJS, List} from 'immutable';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/module';
import {LinePlotConfigService} from './LinePlotConfigService';
import {LinePlotService} from './LinePlotService';
import {IChartSelection, IContinuousSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;
import {LineChart} from '../../../../../../../vahub-charts/linechart/LineChart';
import {ChartEvents} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'lineplot',
    template: '<div></div>',
    providers: [LinePlotConfigService, LinePlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LinePlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    @Input() plotData: List<OutputOvertimeLineChartData>;
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
    chart: LineChart;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: LinePlotConfigService,
                sessionEventService: SessionEventService,
                private linePlotService: LinePlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
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
            const rawData: OutputOvertimeLineChartData[] = this.plotData.toJS();

            const linePlotData = this.linePlotService.splitServerData(rawData);

            this.chart.xAxis[0].setCategories(linePlotData.categories.map(String));
            linePlotData.data.forEach((series) => {
                this.chart.addSeries({
                    name: series.name,
                    color: series.color,
                    type: 'line',
                    data: series.data
                });
            });
        }
        this.chart.redraw();
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

    protected applySelection(): void {
        if (this.chart) {
            let currentPlotSelection: IChartSelection;
            currentPlotSelection = this.selection.find((selections) => {
                const trellising = selections.get('trellising', null);
                return trellising && isEqual(trellising.toJS(), this.trellising);
            });
            const selection: IContinuousSelection[] = currentPlotSelection.get('range') ? currentPlotSelection.get('range').toJS() : [];

            //Prevent double rendering selection
            if (isEqual(selection, this.currentSelection)) {
                return;
            }
            const that = this;
            that.removeSelection();
            selection.forEach(subSelection => {
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
            this.currentSelection = selection;
        }
        this.chart.update();
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    point.select(false);
                });
            });
        }
        this.chart.update();
    }

    protected pointInSelectionRectangle(point, {xMin, xMax, yMin, yMax}): boolean {
        return point.x >= xMin && point.x <= xMax &&
            point.y >= yMin && point.y <= yMax;
    }

    protected chartEvents(): ChartEvents {
        const that = this;
        return {
            selection: function (event): boolean {
                const selected = {
                    points: [],
                    rectangle: {
                        xMin: event.xAxis[0].min || 0,
                        xMax: event.xAxis[0].max || 0,
                        yMin: event.yAxis[0].min || 0,
                        yMax: event.yAxis[0].max || 0
                    }
                };

                const currentSelection = {
                    series: that.series,
                    trellising: that.trellising,
                    range: null,
                    bars: null
                };

                const chartSelection: IChartSelection = that.selection ? that.selection.find((selection: IChartSelection) => {
                    const trellising = selection.get('trellising', null);
                    return trellising && isEqual(trellising.toJS(), that.trellising);
                }) : null;

                // noinspection JSPotentiallyInvalidUsageOfClassThis
                this.series.forEach((series) => {
                    series.points.forEach((point) => {
                        if (that.pointInSelectionRectangle(point, selected.rectangle)) {
                            selected.points.push({
                                category: point.category,
                                series: point.series.name === 'All' ? null : point.series.name,
                            });
                        }
                    });
                });

                currentSelection.range = [selected.rectangle];
                currentSelection.bars = selected.points;

                const shouldAppendWithPreviousSelection = event.originalEvent.ctrlKey;

                const newSelection: IChartSelection[] = shouldAppendWithPreviousSelection
                    ? that.getAppendedSelection(chartSelection, currentSelection, selected)
                    : [fromJS(currentSelection)];

                if (that.hasSelection(newSelection)) {
                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(newSelection));
                    that.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
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

    private getAppendedSelection(chartSelection: IChartSelection, currentSelection, selected): IChartSelection[] {
        let appendedSelection: IChartSelection[] = [];
        const previousPoints = chartSelection && chartSelection.get('bars') ? chartSelection.get('bars').toJS() : [];

        currentSelection.range = chartSelection && chartSelection.get('range') ? chartSelection.get('range').toJS() : [];
        currentSelection.range.push(selected.rectangle);
        currentSelection.bars = unionWith(previousPoints, selected.points, isEqual);

        appendedSelection = [fromJS(currentSelection)];

        if (this.selection && this.selection.size > 0) {
            appendedSelection = [];
            this.selection
                .filter((selectionItem) => {
                    const trellising = selectionItem.get('trellising', null);
                    return !(trellising && isEqual(trellising.toJS(), this.trellising));
                })
                .forEach(subSelection => appendedSelection.push(<IChartSelection>subSelection.toMap()));

            appendedSelection.push(fromJS(currentSelection));
        }

        return appendedSelection;
    }
}
