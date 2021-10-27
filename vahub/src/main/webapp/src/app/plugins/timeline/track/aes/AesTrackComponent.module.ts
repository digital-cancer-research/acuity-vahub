import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { AesTrackComponent } from './AesTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {AesSummaryTrackModel} from './AesSummaryTrackModel';
import {AesDetailTrackModel} from './AesDetailTrackModel';
import {AesTrackUtils} from './AesTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [AesTrackComponent],
    declarations: [AesTrackComponent],
    providers: [AesSummaryTrackModel, AesDetailTrackModel, BarChartEventService, AesTrackUtils],
})
export class AesTrackComponentModule { }
