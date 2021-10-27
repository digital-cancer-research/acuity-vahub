import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';

import {MachineInsightsComponent} from './machineInsightsComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [
        MachineInsightsComponent
    ],
    exports: [
        MachineInsightsComponent
    ],
    providers: []
})
export class MachineInsightsModule {
}
