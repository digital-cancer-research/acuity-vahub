import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ExacerbationsComponent} from './ExacerbationsComponent';
import {RouterModule} from '@angular/router';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule
    ],
    declarations: [ExacerbationsComponent],
    exports: [ExacerbationsComponent],
    providers: [
    ]
})
export class ExacerbationsComponentModule { }
