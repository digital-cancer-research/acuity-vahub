import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DisplayLimitationMessageComponent } from './DisplayLimitationMessageComponent';
import {ModalMessageComponentModule} from '../modalMessage/ModalMessageComponent.module';

@NgModule({
    imports: [CommonModule, FormsModule, ModalMessageComponentModule],
    exports: [DisplayLimitationMessageComponent],
    declarations: [DisplayLimitationMessageComponent],
    providers: [],
})
export class DisplayLimitationMessageComponentModule { }
