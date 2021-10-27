import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterModule} from '@angular/router';
import {ConmedsComponent} from './ConmedsComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [ConmedsComponent],
    exports: [ConmedsComponent],
    providers: [
    ]
})

export class ConmedsComponentModule {

}
