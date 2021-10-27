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

import {ElementRef, Input, NgZone, OnChanges, OnDestroy, SimpleChange} from '@angular/core';

import {IChartPlotconfigService} from './IChartPlotconfigService';
import {XAxisCoordinateService} from './axis/XAxisCoordinateService';
import {IHighlightedPlotArea, IZoom} from '../store/ITimeline';
import {TimelineUtils} from './TimelineUtils';
import {Subscription} from 'rxjs/Subscription';

import * as _ from 'lodash';
import {List} from 'immutable';
import {chart} from '../../../../vahub-charts';

export abstract class AbstractChartComponent implements OnChanges, OnDestroy {

    // passed in to add additional configurations
    @Input() customPlotConfig: any;
    // data to be displayed in the chart, need to be formatted to data serises before used
    @Input() plotData: any[];
    // the zoom of the chart
    @Input() zoom: IZoom;
    // whether the plot is highlighted or not
    @Input() highlighted: boolean;

    @Input() plotBands: List<IHighlightedPlotArea>;

    protected chart;
    protected plotBandIds: string[] = [];
    protected VERTICAL_PLOT_LINE_TEMPLATE = {
        dashStyle: 'Dot',
        color: '#7cb5ec',
        width: 5,
        id: 'ref-x',
    };

    // configurations used for generating the chart
    private plotConfig: any;

    private xAxisCoordinateServiceSubscription: Subscription;

    constructor(protected elementRef: ElementRef,
                protected chartPlotconfigService: IChartPlotconfigService,
                protected ngZone: NgZone,
                protected xAxisCoordinateService: XAxisCoordinateService) {
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        this.updateChange(changes);

        // Show x-axis vertical plot line if there is a plot data
        // and after axis coordinates was updated (i.e. after redraw)
        if (changes['plotData']) {
            this.updateXAxisCoordinateServiceSubscription();
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
        this.xAxisCoordinateServiceSubscription.unsubscribe();
    }

    protected updateChange(changes): void {
        if (changes['customPlotConfig']) {
            this.createChart();
        }

        if (changes['plotData']) {
            this.updateChart();
            this.chart.redraw();
        }

        if (changes['zoom'] && this.zoom
            && (!changes['zoom'].previousValue && changes['zoom'].currentValue
                || changes['zoom'].previousValue && !changes['zoom'].currentValue
                || changes['zoom'].previousValue.absMin !== changes['zoom'].currentValue.absMin
                || changes['zoom'].previousValue.absMax !== changes['zoom'].currentValue.absMax
                || changes['zoom'].previousValue.zoomMin !== changes['zoom'].currentValue.zoomMin
                || changes['zoom'].previousValue.zoomMax !== changes['zoom'].currentValue.zoomMax
                || changes['zoom'].previousValue.zoomed !== changes['zoom'].currentValue.zoomed)) {
            this.updateZoom();
        }

        if (changes['highlighted']) {
            this.updateHighlight();
        }

        if (changes['plotBands']) {
            this.updateHighlightedAreas(changes['plotBands'].currentValue.equals(changes['plotBands'].previousValue));
        }
    }

    protected createChart(): void {
        if (this.chart) {
            this.chart.destroy();
        }
        this.plotConfig = this.chartPlotconfigService.createPlotConfig({
            ...this.customPlotConfig,
            handleDblclick: this.handleDblClick,
        });

        // add highlighting
        this.plotConfig.chart.plotBackgroundColor = this.highlighted
            ? TimelineUtils.CHART_HIGHLIGHT_BACKGROUND : TimelineUtils.CHART_NONE_HIGHLIGHT_BACKGROUND;

        this.plotConfig.chart.renderTo = this.elementRef.nativeElement.children[0];
        this.chart = chart(this.plotConfig);
    }

    protected updateHighlight(): void {
        const options: any = {
                plotBackgroundColor: this.highlighted ? TimelineUtils.CHART_HIGHLIGHT_BACKGROUND
                    : TimelineUtils.CHART_NONE_HIGHLIGHT_BACKGROUND
        };
        this.chart.highlightBackground(options);
    }

    protected handleDblClick = (axisX): void => {
            // hide vertical line on double click on it
            if (this.isPointOnVerticalLine(axisX)) {
                axisX = null;
            }

            this.xAxisCoordinateService.cursorXCoordinate.next(axisX);
    }

    protected updateChart(): void {
        if (this.chart) {
            this.chart.removeSeries();

            // add each of the new series from the updated plot object
            _.forEach(this.plotData, (newSeries: any) => {
                this.chart.addSeries(this.getExtendedChartSeries(newSeries));
            });

            // the reflow method is overridden by the height option, so update that as well.
           // this.chart.options.chart.height = this.plotConfig.chart.height;
        }
    }

    protected updateZoom(): void {
        if (this.chart) {
            let { zoomMin, zoomMax } = this.zoom;
            const { absMax, absMin } = this.zoom;
            zoomMin = zoomMin < absMin ? absMin : zoomMin;
            zoomMax = zoomMax > absMax ? absMax : zoomMax;
            if (this.zoom.zoomMin === this.zoom.zoomMax) {
                const padding = 0.01 * Math.abs((absMax - absMin));
                zoomMin = zoomMin - padding < absMin ? absMin : zoomMin - padding;
                zoomMax = zoomMax + padding > absMax ? absMax : zoomMax + padding;
            }
            this.chart.onZoomX([zoomMin, zoomMax]);
        }
    }

    protected updateHighlightedAreas(isEqual: boolean): void {
        if (this.chart) {
            if (this.plotBandIds.length) {
                this.plotBandIds.forEach(id => this.chart.removePlotBand(id));
            }
            this.plotBands
                .toArray()
                .forEach((area: any) => {
                    const from = area.get('from');
                    const to = area.get('to');
                    const id = `id-${from}${to}`.replace(/[.]/gi, '');
                    this.chart.addPlotBand({
                        color: '#fcffc5',
                        from, to, id
                    });
                    this.plotBandIds.push(id);
                });
        }
    }

    /**
     *
     * @param newSeries {any}
     * @param options {any}
     * @returns {any}
     */
    protected getExtendedChartSeries(newSeries: any): any {
        return _.extend({}, newSeries, {});
    }

    protected updateXAxisCoordinateServiceSubscription(): void {
        if (this.xAxisCoordinateServiceSubscription) {
            this.xAxisCoordinateServiceSubscription.unsubscribe();
            this.removeVerticalLine();
        }

        this.xAxisCoordinateServiceSubscription =
            this.xAxisCoordinateService.cursorXCoordinate.subscribe(axisX => this.updateVerticalPlotLine(axisX));
    }

    protected updateVerticalPlotLine(axisX: number | null): void {
        this.removeVerticalLine();

        if (axisX !== null) {
            this.addVerticalLine(axisX);
        }
    }

    protected isPointOnVerticalLine(chartX: number): boolean {
        const verticalLineChartX: number | null = this.chart.getPlotLine();

        if (!verticalLineChartX) {
            return false;
        }

        return Math.abs(verticalLineChartX - chartX) <= this.VERTICAL_PLOT_LINE_TEMPLATE.width;
    }

    protected removeVerticalLine(): void {
        this.chart.removePlotLine(this.VERTICAL_PLOT_LINE_TEMPLATE.id);
    }

    protected addVerticalLine(axisX: number): void {
        this.chart.addPlotLines([{...this.VERTICAL_PLOT_LINE_TEMPLATE, value: axisX}]);
    }
}
