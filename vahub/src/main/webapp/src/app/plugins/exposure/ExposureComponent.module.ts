import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ExposureComponent} from './ExposureComponent';
import {RouterModule} from '@angular/router';
import {AnalyteConcentrationComponent} from './analyte-concentration/AnalyteConcentrationComponent';
import {TrellisingComponentModule} from '../../common/trellising/trellising/TrellisingComponent.module';
import {CanActivateExposureSpotfire} from './spotfire/CanActivateExposureSpotfire';
import {CommonDirectivesModule} from '../../common/directives/directives.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        TrellisingComponentModule,
        CommonDirectivesModule
    ],
    declarations: [ExposureComponent, AnalyteConcentrationComponent],
    exports: [ExposureComponent],
    providers: [CanActivateExposureSpotfire]
})
export class ExposureComponentModule { }
