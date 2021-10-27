import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ModalMessageComponent } from './ModalMessageComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [ModalMessageComponent],
    declarations: [ModalMessageComponent],
    providers: [],
})
export class ModalMessageComponentModule { }
