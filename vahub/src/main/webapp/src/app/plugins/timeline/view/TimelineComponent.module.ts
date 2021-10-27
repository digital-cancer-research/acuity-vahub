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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineComponent} from './TimelineComponent';
import {    TrackDataService
} from '../http/TrackDataService';

import {TimelineZoomComponentModule} from '../zoom/TimelineZoomComponent.module';
import {TimelineLegendComponentModule} from '../legend/TimelineLegendComponent.module';
import {TimelineViewComponentModule} from './TimelineViewComponent.module';
import {TimelinePaginationComponentModule} from '../pagination/index';
import {TimelineAxisLabelComponentModule} from '../axislabel/TimelineAxisLabelComponent.module';
import {ModalMessageComponentModule} from '../../../common/modals/modalMessage/ModalMessageComponent.module';
import {ProgressComponentModule} from '../../../common/loading/ProgressComponent.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ProgressComponentModule,
        TimelineZoomComponentModule,
        TimelinePaginationComponentModule,
        TimelineLegendComponentModule,
        TimelineViewComponentModule,
        ModalMessageComponentModule,
        TimelineAxisLabelComponentModule
    ],
    exports: [TimelineComponent],
    declarations: [
        TimelineComponent
    ],
    providers: [
        TrackDataService

    ],
})
export class TimelineComponentModule {
}
