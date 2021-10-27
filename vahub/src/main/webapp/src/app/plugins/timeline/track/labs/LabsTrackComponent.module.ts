import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { LabsTrackComponent } from './LabsTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {LabsTrackUtils} from './LabsTrackUtils';
import {LabsSummaryTrackModel} from './LabsSummaryTrackModel';
import {LabsCategoryTrackModel} from './LabsCategoryTrackModel';
import {LabsDetailTrackModel} from './LabsDetailTrackModel';
import {LabsDetailWithThresholdTrackModel} from './LabsDetailWithThresholdTrackModel';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [LabsTrackComponent],
    declarations: [LabsTrackComponent],
        providers: [
        LabsSummaryTrackModel,
        LabsCategoryTrackModel,
        LabsDetailTrackModel,
        LabsDetailWithThresholdTrackModel,
        BarChartEventService,
        LineChartEventService,
        LabsTrackUtils
    ],
})
export class LabsTrackComponentModule { }
