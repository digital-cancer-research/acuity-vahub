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

import {ElementRef, EventEmitter, Input, NgZone, OnChanges, OnDestroy, Output, SimpleChange} from '@angular/core';
import {fromJS, List} from 'immutable';
import * as  _ from 'lodash';
import {isEqual, unionWith} from 'lodash';

import {SessionEventService} from '../../../../../session/event/SessionEventService';
import {IContinuousSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../index';
import {DialogueLocation} from '../DialogueLocation';
import {SelectionService} from '../services/SelectionService';
import {IPlotConfigService} from '../IPlotConfigService';
import {TrellisingDispatcher} from '../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../store/Trellising';
import {IChartSelection, NEW_APPROACH_TAB_LIST} from '../../../store';
import {chart, VahubChart} from '../../../../../../vahub-charts';
import {ScatterChart} from '../../../../../../vahub-charts/scatterchart/ScatterChart';
import {ShiftChart} from '../../../../../../vahub-charts/shiftchart/ShiftChart';
import {BoxPlot} from '../../../../../../vahub-charts/boxplot/BoxPlot';
import {ChartEvents, ChartMouseEvent, UserOptions} from '../../../../../../vahub-charts/types/interfaces';

export abstract class AbstractPlotComponent implements OnChanges, OnDestroy {

    @Input() plotData: any; // probably should be something more concrete... some superclass for all data classes from backend, maybe
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

    chart: VahubChart;
    protected plotConfig: UserOptions;
    protected currentSelection: any;

    // If two clicks been made in period of 400ms then it's doubleClick event
    protected doubleClicker = {
        clickedOnce: false,
        timer: null,
        timeBetweenClicks: 400,
    };

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: IPlotConfigService,
                protected sessionEventService: SessionEventService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        this.chartPlotconfigService.setCurrentDatasets(sessionEventService.currentSelectedDatasets[0]);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (!this.chart) {
            return;
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
        if (changes['selection']) {
            this.updateSelectionRendering();
        }

    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
    }

    protected abstract updateZoomX(): void;

    protected abstract updateZoomY(): void;

    protected updateSelectionRendering(): void {
        this.removeSelection();
        if (!this.selection) {
            this.displayMarkingDialogue.emit(null);
        } else {
            if (this.isSelectionTrellisMatch()) {
                this.applySelection();
            } else {
                this.displayMarkingDialogue.emit(null);
                if (this.chart instanceof ScatterChart
                    || this.chart instanceof ShiftChart
                    || this.chart instanceof BoxPlot) {
                    this.chart.setSelectedZone(null);
                }
            }
        }
    }

    protected isSelectionTrellisMatch(): boolean {
        return this.selection ? this.selection.some((selection) => {
            const trellising = selection.get('trellising', null);
            return isEqual(trellising.toJS(), this.trellising);
        }) : false;
    }

    protected applySelection(): void {
        if (this.chart) {
            let selection: IContinuousSelection[];
            this.selection.forEach((selections) => {
                if (!selections) {
                    return;
                }
                const trellising = selections.get('trellising', null);
                if (trellising && isEqual(trellising.toJS(), this.trellising)) {
                    selection = selections.get('range') ? selections.get('range').toJS() : [];
                }
            });
            this.currentSelection = selection;
        }
    }

    protected removeSelection(): void {
        this.currentSelection = undefined;
        if (this.chart) {
            while (this.chart.series.map(series => series.name).indexOf('selection') > -1) {
                this.chart.series.forEach((series) => {
                    if (series.name.indexOf('selection') > -1) {
                        series.remove(false);
                    }
                });
            }
            this.chart.redraw();
        }
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

                const jEvent = event;

                let xCategories: string[];
                let newRanges: any;
                let selection: any[];

                if (that.isCategorical
                    || that.tabId === TabId.LAB_BOXPLOT
                    || that.tabId === TabId.LAB_SHIFTPLOT
                    || that.tabId === TabId.CARDIAC_BOXPLOT
                    || that.tabId === TabId.PK_RESULT_OVERALL_RESPONSE
                    || that.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT
                    || that.tabId === TabId.RENAL_LABS_BOXPLOT
                    || that.tabId === TabId.VITALS_BOXPLOT
                    || that.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                    || that.tabId === TabId.EXACERBATIONS_COUNTS) {
                    if (that.tabId === TabId.RENAL_CKD_BARCHART
                        || that.tabId === TabId.LAB_BOXPLOT
                        || that.tabId === TabId.LAB_SHIFTPLOT
                        || that.tabId === TabId.CARDIAC_BOXPLOT
                        || that.tabId === TabId.PK_RESULT_OVERALL_RESPONSE
                        || that.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT) {
                        xCategories = that.getCategories(event);
                    } else {
                        const alteredXSelection = SelectionService.alterCategoricalSelectionX(
                            selectionRectangle.xMin,
                            selectionRectangle.xMax
                        );
                        selectionRectangle.xMin = alteredXSelection.xMin;
                        selectionRectangle.xMax = alteredXSelection.xMax;
                    }
                }
                if (that.tabId === TabId.RENAL_LABS_BOXPLOT
                    || that.tabId === TabId.VITALS_BOXPLOT
                    || that.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                    || that.tabId === TabId.EXACERBATIONS_COUNTS) {
                    xCategories = that.getCategories({
                        xAxis: [
                            {
                                axis: event.xAxis[0].axis,
                                min: selectionRectangle.xMin,
                                max: selectionRectangle.xMax
                            }
                        ]
                    });
                }

                // noinspection JSPotentiallyInvalidUsageOfClassThis
                this.series.forEach((series) => {
                   series.points.forEach((point) => {
                       if (that.pointInSelectionRectangle(point, selectionRectangle)) {
                           selectedPoints.push({
                               category: point.category,
                               series: point.series.name === 'All' ? null : point.series.name
                           });
                       }
                   });
                });

                // Appending selection
                if (event.originalEvent.ctrlKey) {
                    newRanges = chartSelection && chartSelection.get('range') ? chartSelection.get('range').toJS() : [];
                    const previousPoints = chartSelection && chartSelection.get('bars') ? chartSelection.get('bars').toJS() : [];
                    if (that.tabId === TabId.RENAL_CKD_BARCHART
                        || that.tabId === TabId.LAB_BOXPLOT
                        || that.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT
                        || that.tabId === TabId.LAB_SHIFTPLOT
                        || that.tabId === TabId.LAB_LINEPLOT
                        || that.tabId === TabId.PK_RESULT_OVERALL_RESPONSE
                        || that.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT
                        || that.tabId === TabId.RENAL_LABS_BOXPLOT
                        || that.tabId === TabId.VITALS_BOXPLOT
                        || that.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                        || that.tabId === TabId.CARDIAC_BOXPLOT
                        || that.tabId === TabId.EXACERBATIONS_COUNTS) {

                        let categories;
                        if (that.tabId === TabId.LAB_LINEPLOT || that.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT) {
                            categories = selectedPoints;
                        } else {
                            categories = xCategories;
                        }

                        newRanges.push({
                            categories,
                            xMin: selectionRectangle.xMin,
                            xMax: selectionRectangle.xMax,
                            yMin: selectionRectangle.yMin,
                            yMax: selectionRectangle.yMax
                        });
                    } else {
                        newRanges.push(selectionRectangle);
                    }
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
                    if (that.tabId === TabId.RENAL_CKD_BARCHART
                        || that.tabId === TabId.LAB_BOXPLOT
                        || that.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT
                        || that.tabId === TabId.LAB_SHIFTPLOT
                        || that.tabId === TabId.LAB_LINEPLOT
                        || that.tabId === TabId.PK_RESULT_OVERALL_RESPONSE
                        || that.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT
                        || that.tabId === TabId.RENAL_LABS_BOXPLOT
                        || that.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                        || that.tabId === TabId.CARDIAC_BOXPLOT
                        || that.tabId === TabId.EXACERBATIONS_COUNTS
                        || that.tabId === TabId.SINGLE_SUBJECT_RENAL_LINEPLOT
                        || that.tabId === TabId.VITALS_BOXPLOT) {

                        let categories;

                        if (that.tabId === TabId.LAB_LINEPLOT || that.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT) {
                            categories = selectedPoints;
                        } else {
                            categories = xCategories;
                        }

                        newRanges = [{
                            categories,
                            xMin: selectionRectangle.xMin,
                            xMax: selectionRectangle.xMax,
                            yMin: selectionRectangle.yMin,
                            yMax: selectionRectangle.yMax
                        }];
                    } else {
                        newRanges = [selectionRectangle];
                    }
                    selection = [fromJS({
                        series: that.series,
                        trellising: that.trellising,
                        range: newRanges,
                        bars: selectedPoints
                    })];
                }
                if (!NEW_APPROACH_TAB_LIST.contains(that.tabId) ||
                    that.hasSelection(selection)) {
                    that.trellisingMiddleware.updateSelection(List<IChartSelection>(selection));
                    if (!jEvent.target.mouseDownX || !jEvent.target.mouseDownY) {
                        that.displayMarkingDialogue.emit({
                            x: jEvent.originalEvent.offsetX,
                            y: jEvent.originalEvent.offsetY
                        });
                    } else if (jEvent.target.mouseDownX - jEvent.originalEvent.offsetX < 0) {
                        that.displayMarkingDialogue.emit({
                            x: jEvent.originalEvent.offsetX,
                            y: jEvent.target.mouseDownY
                        });
                    } else {
                        that.displayMarkingDialogue.emit({x: jEvent.target.mouseDownX, y: jEvent.target.mouseDownY});
                    }
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
            displayMarkingDialogue: (event) => {
                that.displayMarkingDialogue.emit({x: event.offsetX, y: event.offsetY});
            },
        };
    }

    protected hasSelection(selection: IChartSelection[]): boolean {
        return selection.filter(item => {
            return item.get('bars') && item.get('bars').size > 0
                || item.get('range').filter(range => range.get('categories')).size > 0;
        }).length > 0;
    }

    protected getCategories(event: ChartMouseEvent): string[] {
        return SelectionService.listCategories(event.xAxis[0]);
    }

    protected createChart(forceRenderChartWithEmptyData: boolean = false): void {
        if (!this.plotData || !this.elementRef.nativeElement.children[0]) {
            return;
        }
        if (this.chart) {
            this.chart.destroy();
        }
        this.plotConfig = this.createPlotConfig();
        this.plotConfig.chart.renderTo = this.elementRef.nativeElement.children[0];
        if (!this.plotConfig.chart.renderTo.offsetWidth) {
            return;
        }
        if (this.tabId !== TabId.TL_DIAMETERS_PER_SUBJECT_PLOT) {
            this.plotConfig.chart.events = this.chartEvents();
        }
        const that = this;
        this.chart = chart(this.plotConfig);
        this.chart.container.addEventListener('dblclick', function (event: MouseEvent): boolean {
            return that.handleDblclickEvent(event);
        });
    }

    protected pointInSelectionRectangle(point: any, selectionRectangle: any): boolean {
        return false;
    }

    protected createPlotConfig(): UserOptions {
        return this.chartPlotconfigService.createPlotConfig(
            this.title,
            this.xAxisLabel,
            this.globalXAxisLabel,
            this.yAxisLabel,
            this.globalYAxisLabel,
            this.height,
            this.tabId
        );
    }

    protected updateChartConfig(): void {
        const options = this.createPlotConfig();
        // @ts-ignore
        this.chart.update(options, false); //:TODO check if we need to add this method
    }


    protected eventInSelection(event: ChartMouseEvent): boolean {
        const anyMatches: boolean = this.selection ? this.selection.some((selection) => {
            const trellising = selection.get('trellising', null);
            return isEqual(trellising.toJS(), this.trellising);
        }) : false;
        if (anyMatches && event.x && event.y && this.selection) {
            let inSelection = false;
            this.selection.forEach((selection) => {
                const trellising = selection.get('trellising', false) ? selection.get('trellising').toJS() : null;
                if (isEqual(trellising, this.trellising)) {
                    const ranges: IContinuousSelection[] = selection.get('range', false) ? selection.get('range').toJS() : [];
                    inSelection = ranges.some(range => {
                        return (range.xMin <= event.xAxis[0].value && event.xAxis[0].value <= range.xMax)
                            && (range.yMin <= event.yAxis[0].value && event.yAxis[0].value <= range.yMax);
                    });
                }
            });
            return inSelection;
        } else {
            return false;
        }
    }

    protected updateHeight(): void {
        if (this.chart) {
            const currentWidth = this.chart.container.clientWidth;
            this.chart.setSize(currentWidth, this.height);
            this.chart.resize();
        }
    }

    protected hasEvents(): boolean {
        return !(this.tabId === TabId.SINGLE_SUBJECT_LIVER_HYSLAW
            || this.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT
            || this.tabId === TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT
            || this.tabId === TabId.SINGLE_SUBJECT_VITALS_LINEPLOT
            || this.tabId === TabId.SINGLE_SUBJECT_RENAL_LINEPLOT
            || this.tabId === TabId.SINGLE_SUBJECT_LUNG_LINEPLOT);
    }

    protected handleDblclickEvent(event: ChartMouseEvent): boolean {
        const { series, xAxis, yAxis } = this.chart;
        const newSelection = SelectionService.getAllSelection(series, false, xAxis[0].isCategorical, yAxis[0].isCategorical);
        const newRange = newSelection.range;

        if (this.chart instanceof ScatterChart) {
           this.chart.setSelectedZone(newRange);
        }
        if (this.chart instanceof ShiftChart || this.chart instanceof BoxPlot) {
            const rangeForColoringSelectedZone = newRange;

            if  (newRange.xMin === newRange.xMax) {
                rangeForColoringSelectedZone.xMin = rangeForColoringSelectedZone.xMin - 1;
                rangeForColoringSelectedZone.xMax = rangeForColoringSelectedZone.xMax + 1;
            }
            this.chart.setSelectedZone(rangeForColoringSelectedZone);
        }

        let newSelectedBars = newSelection.selectedBars;

        if (isEqual(this.currentSelection, [newRange])) {
            this.trellisingDispatcher.clearSelections();
        } else {
            if (this.isCategorical
                || this.tabId === TabId.RENAL_CKD_BARCHART
                || this.tabId === TabId.SINGLE_SUBJECT_LAB_LINEPLOT
                || this.tabId === TabId.LAB_BOXPLOT
                || this.tabId === TabId.LAB_LINEPLOT
                || this.tabId === TabId.LAB_SHIFTPLOT
                || this.tabId === TabId.SINGLE_SUBJECT_VITALS_LINEPLOT
                || this.tabId === TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT
                || this.tabId === TabId.VITALS_BOXPLOT
                || this.tabId === TabId.CARDIAC_BOXPLOT
                || this.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT
                || this.tabId === TabId.RENAL_LABS_BOXPLOT
                || this.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                || this.tabId === TabId.EXACERBATIONS_COUNTS
                || this.tabId === TabId.SINGLE_SUBJECT_LUNG_LINEPLOT
                || this.tabId === TabId.SINGLE_SUBJECT_RENAL_LINEPLOT) {
                if (this.tabId === TabId.RENAL_CKD_BARCHART) {
                    newRange.categories = _.chain(this.chart.series)
                        .flatMap('chart')
                        .flatMap('xAxis')
                        .flatMap('categories')
                        .uniq()
                        .value();
                } else if (this.tabId === TabId.LAB_BOXPLOT
                    || this.tabId === TabId.LAB_SHIFTPLOT
                    || this.tabId === TabId.SINGLE_SUBJECT_VITALS_LINEPLOT
                    || this.tabId === TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT
                    || this.tabId === TabId.VITALS_BOXPLOT
                    || this.tabId === TabId.CARDIAC_BOXPLOT
                    || this.tabId === TabId.PK_RESULT_OVERALL_RESPONSE
                    || this.tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT
                    || this.tabId === TabId.RENAL_LABS_BOXPLOT
                    || this.tabId === TabId.LUNG_FUNCTION_BOXPLOT
                    || this.tabId === TabId.EXACERBATIONS_COUNTS
                    || this.tabId === TabId.SINGLE_SUBJECT_LUNG_LINEPLOT
                    || this.tabId === TabId.SINGLE_SUBJECT_RENAL_LINEPLOT) {
                    // None of the tabs should have bars in selection
                    newRange.categories = newSelection.selectedBars.map(bar => bar.category);
                    newSelectedBars = [];
                } else {
                    const alteredXSelection = SelectionService.alterCategoricalSelectionX(newRange.xMin, newRange.xMax);
                    newRange.xMin = alteredXSelection.xMin;
                    newRange.xMax = alteredXSelection.xMax;
                }
            }

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

    protected handleClickEvent(event: ChartMouseEvent): boolean {
        return false;
    }

    protected clickWithDoubleClickEvent(event: ChartMouseEvent): boolean {
        if (this.doubleClicker.clickedOnce === true && this.doubleClicker.timer) {
            this.resetDoubleClickTimer();
            return this.handleDblclickEvent(event);
        } else {
            this.doubleClicker.clickedOnce = true;
            this.doubleClicker.timer = setTimeout(() => {
                this.resetDoubleClickTimer();
                return this.handleClickEvent(event);
            }, this.doubleClicker.timeBetweenClicks);
            return false;
        }
    }

    protected resetDoubleClickTimer(): void {
        clearTimeout(this.doubleClicker.timer);
        this.doubleClicker.timer = null;
        this.doubleClicker.clickedOnce = false;
    }

    protected abstract updateChart(): void;
}
