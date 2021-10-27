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

import {Component, Input, ChangeDetectionStrategy} from '@angular/core';
import {TrackLegendConfig} from './ITrackLegend';

@Component({
    selector: 'timeline-track-legend',
    templateUrl: 'TimelineTrackLegendComponent.html',
    styleUrls: ['./TimelineTrackLegendComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineTrackLegendComponent {
    @Input() trackLegendConfig: TrackLegendConfig;
    @Input() yAxisOption: any;
    @Input() ecgWarningsAvailable: boolean;
}
