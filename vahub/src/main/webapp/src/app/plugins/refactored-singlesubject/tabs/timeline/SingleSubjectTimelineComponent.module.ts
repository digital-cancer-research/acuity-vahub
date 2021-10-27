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
