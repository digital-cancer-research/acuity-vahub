import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {StatusTrackModel} from './StatusTrackModel';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {StatusTrackComponent} from './StatusTrackComponent';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [StatusTrackComponent],
    declarations: [StatusTrackComponent],
    providers: [StatusTrackModel, BarChartEventService]
})
export class StatusTrackComponentModule { }
