import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { ConmedsTrackComponent } from './ConmedsTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {ConmedsSummaryTrackModel} from './ConmedsSummaryTrackModel';
import {ConmedsClassTrackModel} from './ConmedsClassTrackModel';
import {ConmedsLevelTrackModel} from './ConmedsLevelTrackModel';
import {ConmedsTrackUtils} from './ConmedsTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [ConmedsTrackComponent],
    declarations: [ConmedsTrackComponent],
    providers: [ConmedsSummaryTrackModel, ConmedsClassTrackModel, ConmedsLevelTrackModel, BarChartEventService, ConmedsTrackUtils],
})
export class ConmedsTrackComponentModule { }
