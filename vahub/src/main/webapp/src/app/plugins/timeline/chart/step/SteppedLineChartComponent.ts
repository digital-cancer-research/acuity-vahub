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
    SimpleChange
} from '@angular/core';
import {AbstractChartComponent} from '../AbstractChartComponent';
import {XAxisCoordinateService} from '../axis/XAxisCoordinateService';
import {SteppedLineChartPlotconfigService} from './SteppedLineChartPlotconfigService';
import {IHighlightedPlotArea, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'timeline-steppedlinechart',
    template: '<div class="row"></div>',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SteppedLineChartPlotconfigService]
})
export class SteppedLineChartComponent extends AbstractChartComponent implements OnChanges, OnDestroy {
    // passed in to add additional configurations
    @Input() customPlotConfig: any;
    // data to be displayed in the chart, need to be formatted to data serises before used
    @Input() plotData: any[];
    // the zoom of the chart
    @Input() zoom: IZoom;
    // whether the plot is highlighted or not
    @Input() highlighted: boolean;

    @Input() plotBands: List<IHighlightedPlotArea>;

    constructor(protected elementRef: ElementRef,
                protected steppedLineChartPlotconfigService: SteppedLineChartPlotconfigService,
                protected ngZone: NgZone,
                protected xAxisCoordinateService: XAxisCoordinateService) {
        super(elementRef, steppedLineChartPlotconfigService, ngZone, xAxisCoordinateService);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }
}
