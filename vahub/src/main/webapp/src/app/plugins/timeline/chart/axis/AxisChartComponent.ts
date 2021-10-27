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
    ChangeDetectionStrategy,
    Input,
    OnChanges,
    SimpleChange,
    OnDestroy,
    ElementRef,
    NgZone,
} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {IZoom} from '../../store/ITimeline';
import {XAxisCoordinateService} from './XAxisCoordinateService';
import {AxisChartPlotConfigService} from './AxisChartPlotConfigService';
import {chart} from '../../../../../vahub-charts';
import {CustomPlotConfig} from '../../../../../vahub-charts/types/interfaces';

@Component({
    selector: 'timeline-xaxis',
    template: '<div></div>',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [AxisChartPlotConfigService]
})
export class AxisChartComponent implements OnChanges, OnDestroy {

    private static X_POSITION_MARKER_SERIES = 'x-position-marker-series';

    // passed in to add additional configurations
    @Input() zoom: IZoom;

    // The x value to plot the croshair on. Uses 2-way biding to update when the
    // mouse is hovered over a chart
    // @Input() cursorXCoordinate: number;

    protected chart;
    // configurations used for generating the chart
    private plotConfig: CustomPlotConfig;
    private xPositionSubscription: Subscription;

    constructor(private elementRef: ElementRef,
                private axisChartPlotconfigService: AxisChartPlotConfigService,
                protected ngZone: NgZone,
                protected xAxisCoordinateService: XAxisCoordinateService) {
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        if (changes['zoom']) {
            if (!this.zoom && this.chart) {
                this.chart.destroy();
            }

            if (this.zoom) {
                if (!this.chart) {
                    this.createChart();
                }

                this.updateChart();
            }
        }
    }

    ngOnDestroy(): void {
        if (this.chart) {
            this.chart.destroy();
        }
        if (this.xPositionSubscription) {
            this.xPositionSubscription.unsubscribe();
        }
    }

    private removeXPositionMarker(): void {
      this.chart.removeMarker(AxisChartComponent.X_POSITION_MARKER_SERIES);
    }

    private addXPositionMarker(axisX: number): void {
        this.chart.addSeries([{
            id: AxisChartComponent.X_POSITION_MARKER_SERIES,
            color: '#7cb5ec',
            marker: {
                radius: 10,
                symbol: 'triangle'
            },
            transform: 'rotate(180)',
            value: axisX
        }]);
    }

    private listenToXPositionMarkerChanges(): void {
        this.xPositionSubscription = this.xAxisCoordinateService.cursorXCoordinate.subscribe((axisX: number | null) => {
            this.removeXPositionMarker();

            if (axisX !== null) {
                this.addXPositionMarker(axisX);
            }
        });
    }

    private createChart(): void {
        this.plotConfig = this.axisChartPlotconfigService.createPlotConfig(this.zoom);

        this.plotConfig.chart.renderTo = this.elementRef.nativeElement.children[0];
        this.chart = chart(this.plotConfig);

        this.listenToXPositionMarkerChanges();
    }

    private updateChart(): void {
        const { zoomMin, zoomMax, absMax, absMin } = this.zoom;
        this.chart.onZoomX([zoomMin < absMin ? absMin : zoomMin, zoomMax > absMax ? absMax : zoomMax]);
    }
}
