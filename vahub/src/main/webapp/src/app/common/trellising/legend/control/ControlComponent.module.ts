import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {ControlComponent} from './ControlComponent';
import {CommonPipesModule} from '../../../pipes/index';
@NgModule({
    imports: [FormsModule, CommonModule, CommonPipesModule],
    exports: [ControlComponent],
    declarations: [ControlComponent],
    providers: [],
})
export class ControlComponentModule {
}
