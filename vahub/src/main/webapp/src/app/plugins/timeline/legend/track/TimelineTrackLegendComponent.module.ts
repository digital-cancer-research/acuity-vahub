import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { TimelineTrackLegendComponent } from './TimelineTrackLegendComponent';
import {TimelineTrackLegendItemComponent} from './TimelineTrackLegendItemComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TimelineTrackLegendComponent],
    declarations: [TimelineTrackLegendComponent, TimelineTrackLegendItemComponent],
    providers: [],
})
export class TimelineTrackLegendComponentModule { }
