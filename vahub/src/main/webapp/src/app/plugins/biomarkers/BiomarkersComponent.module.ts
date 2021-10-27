import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterModule} from '@angular/router';
import {BiomarkersComponent} from './BiomarkersComponent';
import {BiomarkersPlotComponent} from './biomarkersPlot/BiomarkersPlotComponent';
import {TrellisingComponentModule} from '../../common/trellising/trellising/TrellisingComponent.module';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {CanActivateBiomarkers} from './CanActivateBiomarkers';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        TrellisingComponentModule,
        CommonDirectivesModule
    ],
    declarations: [BiomarkersComponent, BiomarkersPlotComponent],
    exports: [BiomarkersComponent],
    providers: [CanActivateBiomarkers]
})

export class BiomarkersComponentModule {

}
