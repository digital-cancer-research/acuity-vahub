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
    Input,
    NgZone,
    OnChanges,
    OnDestroy,
    OnInit
} from '@angular/core';
import {XAxisCoordinateService} from '../axis/XAxisCoordinateService';
import {BarChartPlotconfigService} from './BarChartPlotconfigService';
import {IHighlightedPlotArea, IZoom} from '../../store/ITimeline';
import * as _ from 'lodash';
import {List} from 'immutable';
import {TimelineDispatcher} from '../../store/dispatcher/TimelineDispatcher';
import {AbstractChartComponent} from '../AbstractChartComponent';

@Component({
    selector: 'timeline-barchart',
    template: '<div class="row" id="intro9"></div>',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [BarChartPlotconfigService]
})
export class BarChartComponent extends AbstractChartComponent implements OnInit, OnChanges, OnDestroy {
    // passed in to add additional configurations
    @Input() customPlotConfig: any;
    // data to be displayed in the chart, need to be formatted to data serises before used
    @Input() plotData: any[];
    // the zoom of the chart
    @Input() zoom: IZoom;
    // whether the plot is highlighted or not
    @Input() highlighted: boolean;

    @Input() plotBands: List<IHighlightedPlotArea>;

    protected plotBandIds: string[] = [];
    protected chartContainer: HTMLElement;
    protected chartContainerListener: EventListener;
    private resizeObserver: ResizeObserver;

    constructor(protected elementRef: ElementRef,
                protected barChartPlotconfigService: BarChartPlotconfigService,
                protected ngZone: NgZone,
                protected timelineDispatcher: TimelineDispatcher,
                protected xAxisCoordinateService: XAxisCoordinateService) {
        super(elementRef, barChartPlotconfigService, ngZone, xAxisCoordinateService);
    }

    ngOnInit(): void {
        //  to prevent tooltip freeze bug in a specific case
        this.chartContainer = this.chart.container;
        this.chartContainerListener = () => this.chart.hideTooltip();
        this.chartContainer.addEventListener('wheel', this.chartContainerListener);

        // TODO: figure out the reason for a passing width increase and fix it (for the this.elementRef.nativeElement.children[0].width)
        this.resizeObserver = new ResizeObserver(elem => {
            this.chart.calculateSize(this.chart.height, elem[0].borderBoxSize[0].inlineSize);
            this.chart.resize();
        });
        this.resizeObserver.observe(this.elementRef.nativeElement.children[0]);
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.chartContainer.removeEventListener('wheel', this.chartContainerListener);
        this.resizeObserver.unobserve(this.elementRef.nativeElement.children[0]);
    }

    protected updateHighlightedAreas(isEqual: boolean): void {
        if (this.chart) {
            if (this.plotBandIds.length) { this.plotBandIds.forEach(id => this.chart.removePlotBand(id)); }
            this.plotBands
                .toArray()
                .forEach((area: any) => {
                    const from = area.get('from'),
                        to = area.get('to'),
                        id = `id-${from}${to}`.replace(/[.]/gi, '');
                    this.chart.addPlotBand({
                        color: '#fcffc5',
                        from, to, id
                    }, true);
                    this.plotBandIds.push(id);
                });
        }
    }

    protected getExtendedChartSeries(newSeries: any): any {
        return _.extend({}, newSeries, {
            point: {
                events: {
                    click: (e): void => {
                        const point = e.point;
                        if (!_.isNil(point.high) && !_.isNil(point.low)) {
                            this.timelineDispatcher.updatePlotBands(point, e.ctrlKey);
                        }
                    }
                }
            }
        });
    }

    protected getCursorX(e: any): number {
        // axes are inverse in the bar chart
        return this.chart.yAxis[0].toValue(e.chartX);
    }

    protected addVerticalLine(axisX: number): void {
        this.chart.addPlotLines([{...this.VERTICAL_PLOT_LINE_TEMPLATE, value: axisX}], true);
    }
}
