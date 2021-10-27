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

import {ChangeDetectionStrategy, Component, EventEmitter, Input, Output} from '@angular/core';
import {
    EcgWarnings,
    EcgYAxisValue,
    IHighlightedPlotArea,
    ITrack,
    IZoom,
    LabsYAxisValue,
    SpirometryYAxisValue,
    TrackName,
    VitalsYAxisValue
} from '../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'timeline-track',
    templateUrl: 'TimelineTrackComponent.html',
    styleUrls: ['./TimelineTrackComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineTrackComponent {
    @Input() track: ITrack;
    @Input() subjectId: string;
    @Input() subjectHighlighted: boolean;
    @Input() zoom: IZoom;
    @Input() labsYAxisValue: LabsYAxisValue;
    @Input() spirometryYAxisValue: SpirometryYAxisValue;
    @Input() ecgYAxisValue: EcgYAxisValue;
    @Input() ecgWarnings: EcgWarnings;
    @Input() vitalsYAxisValue: VitalsYAxisValue;
    @Input() plotBands: List<IHighlightedPlotArea>;
    @Output() expandOrCollapseTrack = new EventEmitter(false);

    trackName = TrackName;
}
