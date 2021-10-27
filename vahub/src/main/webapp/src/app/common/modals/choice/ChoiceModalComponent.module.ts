import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChoiceModalComponent } from './ChoiceModalComponent';

/**
 * @module for modal window with opportunity to apply previous filters to a new view
 */
@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [ChoiceModalComponent],
    declarations: [ChoiceModalComponent],
    providers: [],
})
export class ChoiceModalComponentModule { }
