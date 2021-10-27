import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

import {CommonPipesModule} from '../../pipes';
import {ColorByService} from './ColorByService';

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        CommonPipesModule
    ],
    exports: [
        // LegendControlComponent,
        // TableAxisLabelComponent
    ],
    declarations: [
        // LegendControlComponent,
        // TableAxisLabelComponent
    ],
    providers: [
        ColorByService
    ],
})
export class ColorByModule {
}
