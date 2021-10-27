import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {LabsComponent} from './LabsComponent';
import {RouterModule} from '@angular/router';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [LabsComponent],
    exports: [LabsComponent],
    providers: [
    ]
})
export class LabsComponentModule { }
