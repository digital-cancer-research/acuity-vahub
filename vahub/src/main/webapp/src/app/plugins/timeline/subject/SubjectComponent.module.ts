import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { SubjectComponent } from './SubjectComponent';
import {TimelineTrackComponentModule} from '../track/TimelineTrackComponent.module';
import {OrderTrackPipe} from './OrderTrackPipe';

@NgModule({
    imports: [TimelineTrackComponentModule, CommonModule, FormsModule],
    exports: [SubjectComponent],
    declarations: [SubjectComponent, OrderTrackPipe],
    providers: [],
})
export class SubjectComponentModule { }
