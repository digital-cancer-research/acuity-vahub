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
import {is, List} from 'immutable';
import {chain, isEmpty, map, isEqual} from 'lodash';
import {TextUtils} from '../../../../../utils/TextUtils';


import {AbstractPlotComponent} from '../AbstractPlotComponent';
import {SessionEventService} from '../../../../../../session/event/SessionEventService';
import {BoxPlotConfigService} from './BoxPlotConfigService';
import {BoxPlotData, BoxPlotSeries, BoxPlotService} from './BoxPlotService';
import {ICategoricalSelection, IChartSelection, ITrellis, ITrellises, IZoom, TabId} from '../../../../index';
import {DialogueLocation} from '../../DialogueLocation';
import {TrellisingDispatcher} from '../../../../store/dispatcher/TrellisingDispatcher';
import {Trellising} from '../../../../store/Trellising';
import {ScaleTypes} from '../../../../store';
import OutputBoxplotEntry = Request.OutputBoxplotEntry;
import {BoxPlot} from '../../../../../../../vahub-charts/boxplot/BoxPlot';
import {ChartMouseEvent} from '../../../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'boxplot',
    template: '<div></div>',
    providers: [BoxPlotConfigService, BoxPlotService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BoxPlotComponent extends AbstractPlotComponent implements OnChanges, OnDestroy {
    private lastPlotData: BoxPlotData;
    private wasCategorical: boolean;

    @Input() plotData: List<OutputBoxplotEntry>;
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
    chart: BoxPlot;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: BoxPlotConfigService,
                sessionEventService: SessionEventService,
                private boxPlotService: BoxPlotService,
                protected ngZone: NgZone,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
        super(elementRef, chartPlotconfigService, sessionEventService, ngZone, trellisingDispatcher, trellisingMiddleware);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['plotData'] && this.plotData) {
            this.updateChart();
        }
        if (changes['scaleType']) {
            this.updateYAxisScale(this.scaleType);
        }
        if (changes['zoomX'] && this.zoomX && this.xRange) {
            this.updateZoomX();
        }
        if (changes['zoomY'] && this.zoomY && this.yRange) {
            this.updateZoomY();
        }
        if (changes['title'] && this.chart) {
            const formattedTitle = TextUtils.changeWeekToAssessmentWeek(this.title, this.tabId);
            this.chart.setTitle(formattedTitle);
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

    public updateYAxisScale(scaleType: ScaleTypes): void {
        if (scaleType === this.chart.yAxis[0].type) {
            return;
        }
        switch (scaleType) {
            case ScaleTypes.LOGARITHMIC_SCALE:
                this.chart.yAxis[0].setType(<string>scaleType);
                break;
            case ScaleTypes.LINEAR_SCALE:
                this.chart.yAxis[0].setType(<string>scaleType);
                break;
            default:
                return;
        }
        this.chart.redraw();
    }

    protected getCategories(event: ChartMouseEvent): string[] {
        let categories: string[];
        if (this.chart.xAxis[0].categories.length > 0) {
            categories = <string[]> map(this.plotData.toJS().slice(event.xAxis[0].min + 0.5, event.xAxis[0].max + 1), 'x');
        } else {
            categories = <string[]> chain(this.plotData.toJS())
                .map('x')
                .filter(x => event.xAxis[0].min <= Number(x) && event.xAxis[0].max >= Number(x))
                .value();
        }
        return categories;
    }

    protected updateZoomX(): void {
        if (this.chart) {
            if (this.isCategorical) {
                let min = this.xRange.min + (this.xRange.max - this.xRange.min) * this.zoomX.zoomMin / 100.0;
                let max = this.xRange.min + (this.xRange.max - this.xRange.min) * this.zoomX.zoomMax / 100.0;
                max = max < 0.5 ? 0.5 : max;
                min = max - min <= 1 ? max - 1 : min;
                this.chart.onZoomX([min, max]);
            } else {
                this.chart.onZoomX([this.zoomX.zoomMin, this.zoomX.zoomMax]);
            }
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
        if (this.chart && this.plotData) {
            if (this.plotData.isEmpty()) {
                return;
            }
            const rawData: OutputBoxplotEntry[] = this.plotData.toJS();
            const boxPlotData: BoxPlotData = this.boxPlotService.splitServerData(rawData, this.isCategorical);
            const hasNoData = isEmpty(boxPlotData.boxes) && isEmpty(boxPlotData.outliers) && isEmpty(boxPlotData.eventCounts.filter(e => e));
            if (hasNoData) {
                this.chart.setNoDataMessage('No data to display');
                return;
            }
            //Don't do anything if the data is unchanged
            if (this.lastPlotData && boxPlotData === this.lastPlotData) {
                return;
            } else {
                this.lastPlotData = boxPlotData;
                if (boxPlotData.categories) {
                    this.chart.xAxis[0].setType(ScaleTypes.CATEGORY_SCALE);
                    this.chart.xAxis[0].setCategories(boxPlotData.categories);
                } else {
                    this.chart.xAxis[0].setType(ScaleTypes.LINEAR_SCALE);
                }
                this.chart.yAxis[0].setTitle({text: this.yAxisLabel});

                // Can only over write data if the same x axis type
                if (isEmpty(this.chart.series) || this.wasCategorical !== this.isCategorical) {
                    if (this.chart.series.length > 0) {
                        this.chart.clearSeries();
                    }
                    const series: BoxPlotSeries = {
                        name: 'Observations',
                        color: '#2c3e50',
                        type: 'boxplot',
                        data: boxPlotData.boxes,
                        eventCounts: boxPlotData.eventCounts,
                        tooltip: {
                            hideDelay: 50,
                            delayForDisplay: 500,
                            headerFormat: '<em>Visit No: {point.key}</em><br/>'
                        }
                    };
                    this.chart.addSeries(series);
                    this.chart.addSeries({
                        name: 'Outlier',
                        zIndex: 1,
                        color: '#2c3e50',
                        allowPointSelect: false,
                        type: 'scatter',
                        data: boxPlotData.outliers,
                        tooltip: {
                            hideDelay: 50,
                            delayForDisplay: 500,
                            pointFormat: 'Observation: {point.y}'
                        },
                        marker: {
                            symbol: 'circle',
                            radius: 2
                        },
                        stickyTracking: false
                    });
                } else {
                    this.chart.series[0].setData(boxPlotData.boxes, false, true);
                    this.chart.series[1].setData(boxPlotData.outliers, false, true);
                }

                //remember previously if it was categorical
                this.wasCategorical = this.isCategorical;
            }
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
            const selection: ICategoricalSelection[] = currentPlotSelection.get('range') ? currentPlotSelection.get('range').toJS() : [];

            // Prevent double rendering selection
            if (isEqual(selection, this.currentSelection)) {
                return;
            }
            this.removeSelection();

            this.currentSelection = selection;
            this.chart.update();
        }
    }

}
