import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineTrackSelectionComponent} from './TimelineTrackSelectionComponent';
import {TimelineTrackService} from './TimelineTrackService';
import {TrackSelectionOrderPipe} from './TrackSelectionOrderPipe';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TimelineTrackSelectionComponent],
    declarations: [TimelineTrackSelectionComponent, TrackSelectionOrderPipe],
    providers: [TimelineTrackService],
})
export class TimelineTrackSelectionComponentModule {
}
