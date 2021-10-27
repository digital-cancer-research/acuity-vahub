import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExplanationModalComponent } from './ExplanationModalComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [ExplanationModalComponent],
    declarations: [ExplanationModalComponent],
    providers: [],
})
export class ExplanationModalComponentModule { }
