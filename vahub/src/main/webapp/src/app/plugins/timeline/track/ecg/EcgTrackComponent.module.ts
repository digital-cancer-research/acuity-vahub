import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {EcgTrackComponent} from './EcgTrackComponent';
import {EcgSummaryTrackModel} from './EcgSummaryTrackModel';
import {EcgMeasurementTrackModel} from './EcgMeasurementTrackModel';
import {EcgDetailTrackModel} from './EcgDetailTrackModel';
import {EcgTrackUtils} from './EcgTrackUtils';
import {LineChartEventService} from '../../chart/line/LineChartEventService';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [EcgTrackComponent],
    declarations: [EcgTrackComponent],
    providers: [
        EcgSummaryTrackModel,
        EcgMeasurementTrackModel,
        EcgDetailTrackModel,
        BarChartEventService,
        LineChartEventService,
        EcgTrackUtils]
})
export class EcgTrackComponentModule { }
