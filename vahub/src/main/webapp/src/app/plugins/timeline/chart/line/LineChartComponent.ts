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
    ElementRef,
    SimpleChange,
    Input,
    NgZone,
    OnDestroy
} from '@angular/core';
import {XAxisCoordinateService} from '../axis/XAxisCoordinateService';
import {AbstractChartComponent} from '../AbstractChartComponent';
import {LineChartPlotconfigService} from './LineChartPlotconfigService';
import {IHighlightedPlotArea, IZoom} from '../../store/ITimeline';
import {PlotLine, PlotExtreme} from '../IChartEvent';
import {List} from 'immutable';

@Component({
    selector: 'timeline-linechart',
    template: '<div class="row"></div>',
    providers: [LineChartPlotconfigService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class LineChartComponent extends AbstractChartComponent implements OnDestroy {

    // passed in to add additional configurations
    @Input() customPlotConfig: any;
    // data to be displayed in the chart, need to be formatted to data serises before used
    @Input() plotData: any[];
    // the zoom of the chart
    @Input() zoom: IZoom;
    // this set the extremes of the chart
    @Input() extremes: PlotExtreme;
    // additional lines to be drawn on yAxis
    @Input() yAxisPlotLines: PlotLine[];
    // whether the plot is highlighted or not
    @Input() highlighted: boolean;

    @Input() plotBands: List<IHighlightedPlotArea>;

    constructor(protected elementRef: ElementRef,
                protected lineChartPlotconfigService: LineChartPlotconfigService,
                protected ngZone: NgZone,
                protected xAxisCoordinateService: XAxisCoordinateService) {
        super(elementRef, lineChartPlotconfigService, ngZone, xAxisCoordinateService);
    }

    protected updateChange(changes: { [propertyName: string]: SimpleChange }) {
        super.updateChange(changes);

        if (changes['extremes'] && this.extremes) {
            this.updateYExtremes();
        }

        if (changes['yAxisPlotLines'] && this.yAxisPlotLines) {
            this.updateYAxisPlotLines();
        }
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }

    private updateYExtremes(): void {
        if (this.chart) {
            if (this.extremes.min === this.extremes.max) {
                this.chart.onZoomY([this.extremes.min - 1, this.extremes.max + 1]);
            } else {
                this.chart.onZoomY([this.extremes.min, this.extremes.max]);
            }
        }
    }

    private updateYAxisPlotLines(): void {
        if (this.chart) {
            this.yAxisPlotLines.forEach(yAxisPlotLine => {
                this.chart.addPlotLine(this.createPlotLine(yAxisPlotLine));
            });
        }
    }

    private createPlotLine(plotLine: PlotLine): any {
        return {
            zIndex: plotLine.zIndex || 2,
            width: plotLine.width || 1,
            dashStyle: plotLine.dashStyle || 'ShortDash',
            value: plotLine.value,
            color: plotLine.color
        };
    }
}
