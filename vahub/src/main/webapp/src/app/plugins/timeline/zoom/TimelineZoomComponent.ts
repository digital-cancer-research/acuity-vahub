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

import {Component, ChangeDetectionStrategy, Input, Output, EventEmitter} from '@angular/core';
import {IZoom} from '../store/ITimeline';

@Component({
    selector: 'timeline-zoom',
    templateUrl: 'TimelineZoomComponent.html',
    styleUrls: ['./TimelineZoomComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineZoomComponent {
    @Input() zoom: IZoom;
    @Output() updateZoom = new EventEmitter(false);
}
