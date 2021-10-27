import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {PatientDataTrackComponent} from './PatientDataTrackComponent';
import {PatientDataSummaryTrackModel} from './PatientDataSummaryTrackModel';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {PatientDataTrackUtils} from './PatientDataTrackUtils';
import {PatientDataDetailTrackModel} from './PatientDataDetailTrackModel';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [PatientDataTrackComponent],
    declarations: [PatientDataTrackComponent],
    providers: [
        PatientDataSummaryTrackModel,
        PatientDataDetailTrackModel,
        BarChartEventService,
        LineChartEventService,
        PatientDataTrackUtils],
})
export class PatientDataTrackComponentModule { }
