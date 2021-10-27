import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {TrellisingLegendComponent} from './TrellisingLegendComponent';
import {ControlComponentModule} from './control/ControlComponent.module';
import {CommonPipesModule} from '../../pipes/index';
@NgModule({
    imports: [FormsModule, CommonModule, CommonPipesModule, ControlComponentModule],
    exports: [TrellisingLegendComponent],
    declarations: [TrellisingLegendComponent],
    providers: [],
})
export class TrellisingLegendComponentModule {
}
