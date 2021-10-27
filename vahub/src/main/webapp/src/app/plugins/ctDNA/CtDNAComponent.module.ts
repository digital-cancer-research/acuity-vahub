import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterModule} from '@angular/router';
import {CtDNAComponent} from './CtDNAComponent';
import {TrellisingComponentModule} from '../../common/trellising/trellising/TrellisingComponent.module';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {CtDNAPlotComponent} from './ctDNAPlot/CtDNAPlotComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        TrellisingComponentModule,
        CommonDirectivesModule
    ],
    declarations: [CtDNAPlotComponent, CtDNAComponent],
    exports: [CtDNAComponent],
    providers: []
})

export class CtDNAComponentModule {

}
