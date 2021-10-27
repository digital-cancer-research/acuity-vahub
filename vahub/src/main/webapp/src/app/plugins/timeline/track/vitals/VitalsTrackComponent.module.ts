import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {VitalsTrackComponent} from './VitalsTrackComponent';
import {VitalsSummaryTrackModel} from './VitalsSummaryTrackModel';
import {VitalsMeasurementTrackModel} from './VitalsMeasurementTrackModel';
import {VitalsDetailTrackModel} from './VitalsDetailTrackModel';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {VitalsTrackUtils} from './VitalsTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [VitalsTrackComponent],
    declarations: [VitalsTrackComponent],
    providers: [
        VitalsSummaryTrackModel,
        VitalsMeasurementTrackModel,
        VitalsDetailTrackModel,
        BarChartEventService,
        LineChartEventService,
        VitalsTrackUtils],
})
export class VitalsTrackComponentModule { }
