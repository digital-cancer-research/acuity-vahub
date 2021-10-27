import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { DoseTrackComponent } from './DoseTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {DoseSummaryTrackModel} from './DoseSummaryTrackModel';
import {DoseDrugSummaryTrackModel} from './DoseDrugSummaryTrackModel';
import {DoseDrugDetailTrackModel} from './DoseDrugDetailTrackModel';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {DoseTrackSteppedLineChartEventService} from './DoseTrackSteppedLineChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [DoseTrackComponent],
    declarations: [DoseTrackComponent],
    providers: [DoseSummaryTrackModel, DoseDrugSummaryTrackModel, DoseDrugDetailTrackModel, DoseTrackSteppedLineChartEventService, BarChartEventService, DoseTrackUtils],
})
export class DoseTrackComponentModule { }
