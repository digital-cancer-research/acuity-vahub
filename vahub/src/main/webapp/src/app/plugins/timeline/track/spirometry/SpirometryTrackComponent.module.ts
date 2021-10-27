import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { SpirometryTrackComponent } from './SpirometryTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {SpirometrySummaryTrackModel} from './SpirometrySummaryTrackModel';
import {SpirometryCategoryTrackModel} from './SpirometryCategoryTrackModel';
import {SpirometryDetailTrackModel} from './SpirometryDetailTrackModel';
import {SpirometryTrackUtils} from './SpirometryTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [SpirometryTrackComponent],
    declarations: [SpirometryTrackComponent],
    providers: [
        SpirometrySummaryTrackModel,
        SpirometryCategoryTrackModel,
        SpirometryDetailTrackModel,
        BarChartEventService,
        LineChartEventService,
        SpirometryTrackUtils
    ]
})
export class SpirometryTrackComponentModule { }
