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
import {SingleSubjectTimelineComponent} from './SingleSubjectTimelineComponent';

import {TimelineZoomComponentModule} from '../../../timeline/zoom/TimelineZoomComponent.module';
import {TimelineLegendComponentModule} from '../../../timeline/legend/TimelineLegendComponent.module';
import {TimelineViewComponentModule} from '../../../timeline/view/TimelineViewComponent.module';
import {TimelinePaginationComponentModule} from '../../../timeline/pagination/index';
import {TimelineAxisLabelComponentModule} from '../../../timeline/axislabel/TimelineAxisLabelComponent.module';
import {ModalMessageComponentModule} from '../../../../common/modals/modalMessage/ModalMessageComponent.module';
import {ProgressComponentModule} from '../../../../common/loading/ProgressComponent.module';
import {SingleSubjectTimelineTrackDataService} from './SingleSubjectTimelineTrackDataService';


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
    exports: [SingleSubjectTimelineComponent],
    declarations: [
        SingleSubjectTimelineComponent
    ],
    providers: [
        SingleSubjectTimelineTrackDataService
    ],
})
export class SingleSubjectTimelineComponentModule {
}
