import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { ExacerbationsTrackComponent } from './ExacerbationsTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {ExacerbationsTrackUtils} from './ExacerbationsTrackUtils';
import {ExacerbationsSummaryTrackModel} from './ExacerbationsSummaryTrackModel';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [ExacerbationsTrackComponent],
    declarations: [ExacerbationsTrackComponent],
    providers: [ExacerbationsSummaryTrackModel, BarChartEventService, ExacerbationsTrackUtils]
})
export class ExacerbationsTrackComponentModule { }
