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
