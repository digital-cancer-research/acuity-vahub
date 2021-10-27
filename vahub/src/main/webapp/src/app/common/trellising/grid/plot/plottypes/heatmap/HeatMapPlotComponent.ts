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
import {cloneDeep, isEmpty, merge} from 'lodash';
import {DialogueLocation} from '../../DialogueLocation';
import {
    BIOMARKERS_X_MAX,
    BIOMARKERS_Y_MAX,
    IChartSelection,
    ITrellis,
    ITrellises,
    IZoom,
    TabId,
    IModalMessage,
    IContinuousSelection,
    IHeatMap
} from '../../../../store';
import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {HeatMapPlotConfigService} from './HeatMapPlotConfigService';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {HeatMapChart} from '../../../../../../../vahub-charts/heatmap/HeatMapChart';
import {
    ChartEvents,
    ChartMouseEvent,
    SeriesOptions,
    UserOptions
} from '../../../../../../../vahub-charts/types/interfaces';
import OutputHeatMapEntry = InMemory.OutputHeatMapEntry;

export interface AxisValue {
    X_AXIS: string;
    Y_AXIS: string;
}
export interface SelectionObject {
    selectionItems: Array<AxisValue>;
    selectedTrellises: Array<ITrellis>;
    rangeRectangle: Array<IContinuousSelection>;
}

@Component({
    selector: 'heat-map',
    templateUrl: 'HeatMapPlotComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [HeatMapPlotConfigService]
})
export class HeatMapPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {

    @Input() plotData: IHeatMap;
    @Input() title: string;
    @Input() trellising: List<ITrellis>;
    @Input() height: number;
    @Input() tabId: TabId;
    @Input() selection: List<IChartSelection>;
    @Input() series: List<ITrellises>;
    @Input() zoomY: IZoom;
    @Input() zoomX: IZoom;
    @Input() yRange: { max: number, min: number };
    @Input() xRange: { max: number, min: number };
    @Output() displayMarkingDialogue: EventEmitter<DialogueLocation> = new EventEmitter<DialogueLocation>();

    chart: HeatMapChart;
    selectionRectangle: IContinuousSelection;

    modal: IModalMessage = {
        msg: `Due to display limitations and to provide better user experience it is possible to only view ${BIOMARKERS_X_MAX}
        subjects and ${BIOMARKERS_Y_MAX} genes at a time. <br/>
        To see the remaining subjects please use the zoombar at the bottom, to see remaining genes please use the
        zoombar on the left. <br/>
        To alter the number of subjects / genes in zoom view please use the Genomic Profile Setting panel
        to adjust the number of subjects or the number of genes in the genomic profile view.`,
        sessionStorageVarName: 'displayLimitationBiomarkersAccepted',
        isVisible: true
    };

    constructor(protected elementRef: ElementRef,
        protected chartPlotconfigService: HeatMapPlotConfigService,
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
        if (changes['zoomY'] && this.zoomY && this.yRange) {
            this.updateZoomY();
        }
        if (changes['zoomX'] && this.zoomX && this.xRange) {
            this.updateZoomX();
        }
        if (changes['selection'] && !is(changes['selection'].previousValue, changes['selection'].currentValue)) {
            this.updateSelectionRendering();
        }
        if (changes['height'] && this.height) {
            this.updateHeight();
        }
    }

    protected updateSelectionRendering(): void {
        if (this.selection.size === 0) {
            this.removeSelection();
            this.displayMarkingDialogue.emit(null);
        } else {
            if (this.isSelectionTrellisMatch()) {
                this.applySelection();
            } else {
                this.displayMarkingDialogue.emit(null);
            }
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected chartEvents(): ChartEvents {
        return {
            selection: (event): boolean => {
                if (this.chart) {
                    this.selectionRectangle = {
                        xMin: event.xAxis[0].min,
                        xMax: event.xAxis[0].max,
                        yMin: event.yAxis[0].min,
                        yMax: event.yAxis[0].max
                    };

                    let selection: List<IChartSelection> | IChartSelection = this.selection ? this.selection.find((chartSelection: IChartSelection) =>
                        chartSelection.get('rangeRectangle', null)) : null;

                    let selectedMarker: AxisValue;
                    let selectedMarkers: AxisValue[] = [];
                    let selectionObject: SelectionObject | List<SelectionObject>;

                    let oldRange: IContinuousSelection;

                    this.chart.series.forEach((series) => {
                        series.data.forEach((point) => {
                            if (this.isSelected(this.selectionRectangle, point)) {
                                selectedMarker = this.getAxisValue(point);
                                selectedMarkers.push(selectedMarker);
                            }
                        });
                    });

                    if (selectedMarkers.length === 0) {
                        this.removeSelection();
                    }

                    if (event.originalEvent.ctrlKey) {
                        selectedMarkers = cloneDeep(selectedMarkers);
                        oldRange = this.selectionRectangle;
                        selectedMarkers = selectedMarkers ? selectedMarkers : [];
                        selectionObject = {
                            selectionItems: selectedMarkers,
                            selectedTrellises: [],
                            rangeRectangle: [oldRange],
                        };
                        if (selection) {
                            selectionObject.rangeRectangle.push(selection.toJS().rangeRectangle[0]);
                        }
                        this.currentSelection = selectionObject;

                        if (this.selection && this.selection.size > 0) {
                            const newSelection = this.selection.toJS();
                            selectedMarkers.forEach((marker) => {
                                newSelection[0].selectionItems.push(marker);
                            });
                            newSelection[0].rangeRectangle.push(oldRange);
                            this.selection = List.of(fromJS(newSelection[0]));
                            selection = this.selection;
                        } else {
                            selection = List.of(fromJS(selectionObject));
                        }
                        this.trellisingMiddleware.updateSelection(selection);

                    } else {
                        selectionObject = List.of(fromJS({
                            selectionItems: selectedMarkers,
                            selectedTrellises: [],
                            rangeRectangle: [this.selectionRectangle],
                        }));
                        this.currentSelection = selectionObject;
                        this.trellisingMiddleware.updateSelection(this.currentSelection);
                    }

                    this.displayMarkingDialogue.emit({
                        x: event.originalEvent.chartX || event.chartX,
                        y: event.originalEvent.chartY || event.chartY
                    });
                }

                return false;
            },
            click: (event): boolean =>  {
                if (this.eventInSelection(event)) {
                    this.displayMarkingDialogue.emit({ x: event.chartX, y: event.chartY });
                } else {
                    this.trellisingDispatcher.clearSelections();
                }
                return false;
            },
            removeSelection: () => {
                this.trellisingDispatcher.clearSelections();
                this.displayMarkingDialogue.emit(null);
            },
        };
    }

    protected isSelectionTrellisMatch(): boolean {
        return !!this.selection;
    }

    protected updateZoomX(): void {
        if (this.chart) {
            this.chart.onZoomX([this.zoomX.zoomMin - 1, this.zoomX.zoomMax]);
        }
    }

    protected updateZoomY(): void {
        if (this.chart) {
            this.chart.onZoomY([this.zoomY.zoomMin - 1, this.zoomY.zoomMax]);
        }
    }

    protected updateChart(): void {
        const that = this;
        if (!this.chart) {
            this.createChart();
        } else {
            this.updateChartConfig();
        }
        if (this.chart) {
            if (this.plotData.isEmpty()) {
                return;
            }
            this.clearSeries();

            const entries: SeriesOptions<OutputHeatMapEntry> = this.chartPlotconfigService.modifyGreenColoredEntries(
                this.plotData.get('entries').toJS(), this.tabId);
            entries.boostedSeries.concat(entries.svgSeries).forEach((series) => {
                series.events = {
                    click: function (event: ChartMouseEvent): boolean {
                        return that.clickWithDoubleClickEvent(event);
                    }
                };
            });
            // empty series - series of light gray cells
            // single click resets selection
            // double click selects all cells
            const xcategories = this.plotData.get('xcategories').toJS();
            const ycategories = this.plotData.get('ycategories').toJS();
            entries.emptySeries = this.chartPlotconfigService.fillWithNoMutationSeries(xcategories, ycategories);
            if (entries.emptySeries.length > 0) {
                this.chart.addSeries({
                    name: HeatMapPlotConfigService.DEFAULT_FILLING_SERIES,
                    data: entries.emptySeries,
                });
            }
            entries.emptySeries.forEach((series) => {
                series.events = {
                    click: function (event: ChartMouseEvent): boolean {
                        return that.clickWithDoubleClickEvent(event);
                    }
                };
            });
            this.chart.addSeries({
                data: entries.boostedSeries,
            });
            this.chart.addSeries({
                data: entries.svgSeries
            });
            this.chart.redraw();
        }
    }

    protected clearSeries(): void {
        if (this.chart.series.length > 0) {
            this.chart.clearSeries();
        }
    }

    protected createPlotConfig(): UserOptions {
        const xcategories = this.plotData.get('xcategories').toJS();
        const ycategories = this.plotData.get('ycategories').toJS();
        const customConfig = this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.height,
            xcategories,
            ycategories
        );
        return merge(customConfig, this.chartPlotconfigService.additionalOptions(this.tabId));
    }

    protected applySelection(): void {
        if (this.chart) {
            const selection = this.selection;

            this.removeSelection();

            selection.forEach((selectionItem) => {
                const selectionRectangles = selectionItem.get('rangeRectangle').toJS();

                selectionRectangles.forEach(selectionRectangle => {
                    this.chart.series.forEach((series) => {
                        series.data.forEach((point) => {
                            if (this.isSelected(selectionRectangle, point)) {
                                point.select(true, true);
                            }
                        });
                    });
                });
            });
            this.chart.update();
        }
    }

    // Check if point is in selection rectangle
    private isSelected(subSelection: IContinuousSelection, point): boolean {
        const { xMin, xMax, yMin, yMax } = subSelection;

        return point.x >= xMin && point.x <= xMax &&
            point.y >= yMin && point.y <= yMax &&
            point.value !== 'No mutation detected';
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    point.select(false, true);
                });
            });
            this.chart.redraw();
        }
    }

    protected eventInSelection(event: ChartMouseEvent): boolean {
        return true;
    }

    private resetClickEvent(event: ChartMouseEvent): boolean {
        this.removeSelection();
        const selection = List.of(fromJS({
            selectionItems: [],
            selectedTrellises: [],
            rangeRectangle: [],
        }));
        this.trellisingMiddleware.updateSelection(selection);
        this.displayMarkingDialogue.emit({ x: event.chartX, y: event.chartY });
        return false;
    }

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        if (event.point.selected) {
            this.displayMarkingDialogue.emit({ x: event.chartX, y: event.chartY });
        } else {
            this.selectionRectangle = {
                xMin: event.point.x - 0.49,
                xMax: event.point.x + 0.49,
                yMin: event.point.y - 0.01,
                yMax: event.point.y + 0.01
            };
            let selection: List<IChartSelection>;
            let selectedMarker: AxisValue;

            this.chart.series.forEach((series) => {
                series.data.forEach((point) => {
                    if (this.isSelected(this.selectionRectangle, point)) {
                        selectedMarker = this.getAxisValue(point);
                    }
                });
            });
            if (isEmpty(selectedMarker)) {
                this.resetClickEvent(event);
            }
            // Appending selection
            if (event.ctrlKey) {
                let newSelectedMarker: AxisValue;
                this.chart.series.forEach((series) => {
                    series.data.forEach((point) => {
                        if (this.isSelected(this.selectionRectangle, point)) {
                            newSelectedMarker = this.getAxisValue(point);
                        }
                    });
                });
                const selectionInPlot = fromJS({
                    selectionItems: [newSelectedMarker],
                    selectedTrellises: [],
                    rangeRectangle: [this.selectionRectangle],
                });
                if (this.selection && this.selection.size > 0) {
                    selection = this.selection.push(selectionInPlot);
                } else {
                    selection = List.of(selectionInPlot);
                }
            } else {
                selection = List.of(fromJS({
                    selectionItems: [selectedMarker],
                    selectedTrellises: [],
                    rangeRectangle: [this.selectionRectangle],
                }));
            }
            this.trellisingMiddleware.updateSelection(selection);
            this.displayMarkingDialogue.emit({ x: event.chartX, y: event.chartY });
        }
        return false;
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {

        let selectedMarker: AxisValue;
        const selectedMarkers = [];
        this.selectionRectangle = {
            xMin: -1000,
            xMax: 1000,
            yMin: -1000,
            yMax: 1000
        };

        this.chart.series.forEach((series) => {
            series.data.forEach((point: OutputHeatMapEntry) => {
                if (point.value !== 'No mutation detected') {
                    selectedMarker = this.getAxisValue(point);
                    selectedMarkers.push(selectedMarker);
                }
            });
        });


        const selectionObject = List.of(fromJS({
            selectionItems: selectedMarkers,
            selectedTrellises: [],
            rangeRectangle: [this.selectionRectangle],
        }));
        this.currentSelection = selectionObject;
        this.trellisingMiddleware.updateSelection(selectionObject);
        this.displayMarkingDialogue.emit({ x: event.layerX, y: event.layerY });
        return false;
    }

    private getAxisValue(point: OutputHeatMapEntry): AxisValue {
        return {
            X_AXIS: this.chart.xAxis[0].categories[point.x],
            Y_AXIS: this.chart.yAxis[0].categories[point.y]
        };
    }
}
