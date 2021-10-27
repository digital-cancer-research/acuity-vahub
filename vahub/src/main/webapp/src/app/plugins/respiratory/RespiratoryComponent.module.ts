import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RespiratoryComponent} from './RespiratoryComponent';
import {RouterModule} from '@angular/router';
import {CanActivateRespiratorySpotfire} from './spotfire/CanActivateRespiratorySpotfire';
import {CanActivateDetectRespiratorySpotfire} from './spotfire/CanActivateDetectRespiratorySpotfire';
import {CanActivateLungFunctionBoxPlot} from './lungFunctionBoxPlot/CanActivateLungFunctionBoxPlot';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [RespiratoryComponent],
    exports: [RespiratoryComponent],
    providers: [CanActivateRespiratorySpotfire, CanActivateDetectRespiratorySpotfire, CanActivateLungFunctionBoxPlot]
})
export class RespiratoryComponentModule { }
