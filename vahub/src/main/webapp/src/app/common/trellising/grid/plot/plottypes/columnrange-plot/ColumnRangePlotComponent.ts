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
    OnInit,
    Output,
    SimpleChange
} from '@angular/core';
import {fromJS, List, Map} from 'immutable';
import {cloneDeep, flatten, isEmpty, isEqual, merge, unionWith} from 'lodash';

import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {
    IChartSelection,
    ITrellis,
    ITrellises,
    IZoom,
    MAX_BARS_ALL_PRIOR_THERAPIES,
    PlotSettings,
    TabId,
    TherapiesType,
    IModalMessage, IContinuousSelection, ICategoricalSelection
} from '../../../../store';
import {DialogueLocation} from '../../DialogueLocation';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {ColumnRangePlotConfigService} from './ColumnRangePlotConfigService';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {SelectionService} from '../../services/SelectionService';
import {LEFT_ARROW_BASE64} from '../../../../../CommonChartUtils';
import {ColumnRangeChart} from '../../../../../../../vahub-charts/barchart/ColumnRangeChart';
import {ChartEvents, ChartMouseEvent, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';
import OutputColumnRangeChartEntry = InMemory.OutputColumnRangeChartEntry;
import OutputMarkEntry = InMemory.OutputMarkEntry;

@Component({
    selector: 'columnrange-plot',
    templateUrl: 'ColumnRangePlotComponent.html',
    providers: [ColumnRangePlotConfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class ColumnRangePlotComponent extends AbstractPlotComponent implements OnChanges, OnInit, OnDestroy {

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
    @Input() selection: List<IChartSelection>;
    @Input() plotSettings: PlotSettings;
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();

    therapiesSettings: TherapiesType;
    chart: ColumnRangeChart;

    private readonly DELTA = 0.1;
    private readonly SINGLE_POINT_DELTA = 0.5;
    private readonly SELECTION_DELTA = 0.3;

    modal: IModalMessage = {
        msg: `To provide better user experience the default view shows only ${MAX_BARS_ALL_PRIOR_THERAPIES} therapies.
        To see the remaining subjects please use zoombar on the left.`,
        sessionStorageVarName: 'displayLimitationAllTherapiesAccepted',
        isVisible: true
    };

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: ColumnRangePlotConfigService,
                sessionEventService: SessionEventService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnInit(): void {
        this.therapiesSettings = this.plotSettings.get('trellisedBy');
        if (this.therapiesSettings !== TherapiesType.ALL_PRIOR_THERAPIES) {
            this.modal.isVisible = false;
        }
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }

        if (changes['zoomX']) {
            this.updateZoomX();
        }
        if (changes['zoomY']) {
            this.updateZoomY();
        }

        if (changes['height'] && this.height) {
            this.updateHeight();
        }

        if (changes['selection']) {
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
            let min = this.zoomX.zoomMin !== this.zoomX.absMin ? this.zoomX.zoomMin
                : this.zoomX.zoomMin - Math.abs(this.zoomX.zoomMin) * this.DELTA;
            let max = this.zoomX.zoomMax !== this.zoomX.absMax ? this.zoomX.zoomMax
                : this.zoomX.zoomMax + Math.abs(this.zoomX.zoomMax) * this.DELTA;
            if (min === max) {
                min = min - this.SINGLE_POINT_DELTA;
                max = max + this.SINGLE_POINT_DELTA;
            }
            this.chart.onZoomY([min, max]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            this.chart.onZoomX([this.zoomY.absMax - this.zoomY.zoomMax, this.zoomY.absMax - this.zoomY.zoomMin]);
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
            const entries: OutputColumnRangeChartEntry[] = this.plotData.get('data').toJS();

            if (this.plotSettings.get('trellisedBy') === TherapiesType.ALL_PRIOR_THERAPIES) {
                this.chart.xAxis[0].categories.forEach((category, index, allCategories) => {
                    if (index !== 0 && category !== allCategories[index - 1]) {
                        this.chart.addPlotLine({
                            color: 'grey',
                            axis: 'y',
                            value: index - 0.5,
                            width: 1,
                            styles: {
                                fill: 'none',
                                'stroke-dasharray': '4,3'
                            }
                        });
                    }
                });
            }
            // start date markers series is added as first to prevent overlap column bars
            // as markers are twice bigger size and positioned from center of image
            this.addMarkersForEventsWithoutStartDate(entries);
            this.chart.addSeries({
                data: entries,
                events: {
                    click: (event) => {
                        this.handleClickEvent(event);
                    }
                }
            });
            this.addDiagnosisDateScatterMarkers();
            this.addProgressionDatesScatterMarkers();
            this.chart.redraw();
        }
    }

    private addProgressionDatesScatterMarkers() {
        const progressionDates: OutputMarkEntry[] = this.plotData.get('progressionDates').toJS()
            .map(date => {
                date.marker = {
                    symbol: 'triangle',
                    fillColor: 'red',
                    name: 'progression',
                };
                return date;
            });
        const progressionDatesScatter = {
            name: 'Progression date',
            type: 'scatter',
            color: 'red',
            data: progressionDates
        };
        this.chart.addSeries(progressionDatesScatter);
    }

    private addDiagnosisDateScatterMarkers() {

        const diagnosisDates: OutputMarkEntry[] = this.plotData.get('diagnosisDates').toJS()
            .map(date => {
                date.marker = {
                    symbol: 'diamond',
                    fillColor: '#000000',
                    name: 'diagnosis',
                };
                return date;
            });

        const diagnosisDatesScatter = {
            name: 'Analisys date',
            type: 'scatter',
            color: 'black',
            data: diagnosisDates
        };
        this.chart.addSeries(diagnosisDatesScatter);
    }

    /**
     * If there is at least one event with no start date then a series of markers would be added for notifying user about such data
     * @param entries
     */
    private addMarkersForEventsWithoutStartDate(entries) {
        const itemsForEventsWithNoStartDate = entries.filter(item => item.noStartDate).map(item => {
            return  {
                x: item.x,
                y: item.low,
                marker: {
                    symbol: LEFT_ARROW_BASE64,
                    transform: 'rotate(90)',
                }
            };
        });
        if (!isEmpty(itemsForEventsWithNoStartDate)) {
            const noStartDateEventMarkersScatter = {
                type: 'scatter',
                data: itemsForEventsWithNoStartDate,
                name: 'startdate',
            };
            this.chart.addSeries(noStartDateEventMarkersScatter);
        }
    }

    protected createPlotConfig(): UserOptions {
        const customConfig = this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.height,
            this.plotData.get('categories')
        );
        return merge(customConfig, this.chartPlotconfigService.additionalOptions(this.tabId,
            this.plotSettings.get('trellisedBy')));
    }

    protected applySelection(): void {
        if (this.chart) {
            const selection = this.selection;

            if (isEqual(selection, this.currentSelection)) {
                return;
            }

            this.removeSelection();

            selection.forEach((selectionItem) => {
                const columns = selectionItem.get('columns');
                this.chart.series.forEach((series) => {
                    series.data.forEach((point: any) => {
                        // if columns have same x and same end this means that they have same start
                        // two columns in one row can not have same end
                        const selectedColumn = columns
                            .find(column => column.get('x') === point.x
                                && column.get('end') === point.high
                                && this.isNotSummary(point));
                        if (selectedColumn) {
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

    protected chartEvents(): ChartEvents {
        const that = this;
        return {
            removeSelection: () => {
                this.trellisingDispatcher.clearSelections();
                this.displayMarkingDialogue.emit(null);
            },
            selection: function (event): boolean {
                const selectedPoints = [];
                const selectionRectangle = {
                    yMin: event.yAxis[0].min || 0,
                    yMax: event.yAxis[0].max || 0,
                    xMin: event.xAxis[0].min || 0,
                    xMax: event.xAxis[0].max || 0
                };
                const chartSelection: IChartSelection = that.selection ? that.selection.find((subSelection: IChartSelection) => {
                    const trellising = subSelection.get('trellising', null);
                    return trellising && isEqual(trellising.toJS(), that.trellising);
                }) : null;

                const jEvent: ChartMouseEvent = event;

                let newRanges: IContinuousSelection[];
                let selection: IChartSelection[];

                this.series.forEach((series) => {
                    series.points.forEach((point) => {
                        if (that.pointInSelectionRectangle(point, selectionRectangle) && that.isNotSummary(point)) {
                            selectedPoints.push({
                                category: point.category,
                                series: that.therapiesSettings,
                                start: point.low,
                                end: point.high,
                                x: point.x
                            });
                        }
                    });
                });

                // Appending selection
                if (event.originalEvent.ctrlKey) {
                    newRanges = chartSelection && chartSelection.get('range') ? chartSelection.get('range').toJS() : [];
                    const previousPoints = chartSelection && chartSelection.get('columns') ? chartSelection.get('columns').toJS() : [];
                    newRanges.push(selectionRectangle);
                    const selectionInPlot = fromJS({
                        series: that.therapiesSettings,
                        trellising: that.trellising,
                        range: newRanges,
                        columns: unionWith(previousPoints, selectedPoints, isEqual)
                    });
                    if (that.selection && that.selection.size > 0) {
                        selection = [];
                        that.selection
                            .filter((selectionItem) => {
                                const trellising = selectionItem.get('trellising', null);
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
                        series: that.therapiesSettings,
                        trellising: that.trellising,
                        range: newRanges,
                        columns: selectedPoints
                    })];
                }
                if (selection.filter((selectionItem) => {
                    return selectionItem.get('columns').size > 0
                        || selectionItem.get('range')
                            .filter(range => range.get('categories')).size > 0;
                }).length > 0) {
                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
                    that.displayMarkingDialogue.emit({x: jEvent.originalEvent.offsetX, y: jEvent.originalEvent.offsetY});
                }

                return false;
            }
        };
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {
        const { series, xAxis, yAxis } = this.chart;
        const newSelection = SelectionService.getAllSelection(series, false, xAxis[0].isCategorical, yAxis[0].isCategorical);
        const newRange = newSelection.range;
        const newSelectedColumns = flatten(this.chart.series.map((serie: any) => {
            return serie.data.map((point: any) => {
                return {
                    category: point.category,
                    series: this.therapiesSettings,
                    start: point.low,
                    end: point.high,
                    x: point.x
                };
            }).filter(point => this.isNotSummary(point));
        }));

        if (isEqual(this.currentSelection, [newRange])) {
            this.trellisingDispatcher.clearSelections();
        } else {
            const selectionInPlot = fromJS({
                series: this.series,
                trellising: this.trellising,
                range: [newRange],
                columns: newSelectedColumns
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

    protected pointInSelectionRectangle(point, {yMin, yMax, xMin, xMax}): boolean {
        return !(point.x - this.SELECTION_DELTA > xMax || point.x + this.SELECTION_DELTA < xMin || point.low > yMax || point.high < yMin);
    }

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        if (event.point.selected) {
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        } else if (this.isNotSummary(event.point)) {
            let newRanges: ICategoricalSelection[];
            let newColumns: any[];
            let selection: List<IChartSelection>;

            const selectedColumn = {
                category: event.point.category,
                series: this.therapiesSettings,
                start: event.point.low,
                end: event.point.high,
                x: event.point.x
            };
            const selectionRectangle = {
                yMin: event.point.low || 0,
                yMax: event.point.high || 0,
                categories: event.point.category
            };
            //Appending selection
            if (event.ctrlKey) {
                newRanges = this.currentSelection && this.currentSelection.get('range')
                    ? cloneDeep(this.currentSelection.get('range'))
                    : [];
                newColumns = this.currentSelection && this.currentSelection.get('columns')
                    ? cloneDeep(this.currentSelection.get('columns'))
                    : [];
                newColumns.push(selectedColumn);
                newRanges.push(selectionRectangle);

                const selectionInPlot = fromJS({
                    series: this.series,
                    trellising: this.trellising,
                    range: newRanges,
                    columns: newColumns
                });

                if (this.selection && this.selection.size > 0) {
                    const sameChartSelectionIndex = this.selection.findIndex((subSelection: IChartSelection) => {
                        const trellising = subSelection.get('trellising', null);
                        return trellising && isEqual(trellising.toJS(), this.trellising);
                    });
                    if (sameChartSelectionIndex !== -1) {
                        selection = this.selection.updateIn([sameChartSelectionIndex, 'range'],
                            range => range.push(fromJS(selectionRectangle)));
                        selection = selection.updateIn([sameChartSelectionIndex, 'columns'],
                            columns => columns.push(fromJS(selectedColumn)));
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
                    range: [selectionRectangle],
                    columns: [selectedColumn]
                }));
            }
            this.trellisingMiddleware.updateSelection(selection);
            this.displayMarkingDialogue.emit({x: event.chartX, y: event.chartY});
        }
        return false;
    }

    private isNotSummary(point: any): boolean {
        return point.name !== 'Summary';
    }
}
