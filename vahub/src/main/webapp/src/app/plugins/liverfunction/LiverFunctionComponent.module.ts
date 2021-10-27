import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {LiverFunctionComponent} from './LiverFunctionComponent';
import {RouterModule} from '@angular/router';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [LiverFunctionComponent],
    exports: [LiverFunctionComponent]
})
export class LiverFunctionComponentModule { }
