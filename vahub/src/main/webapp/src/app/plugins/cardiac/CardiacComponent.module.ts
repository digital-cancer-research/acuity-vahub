import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {CardiacComponent} from './CardiacComponent';
import {RouterModule} from '@angular/router';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    exports: [CardiacComponent],
    declarations: [CardiacComponent]
})
export class CardiacComponentModule { }
