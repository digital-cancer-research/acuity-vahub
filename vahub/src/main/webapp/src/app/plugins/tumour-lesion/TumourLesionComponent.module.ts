import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {RouterModule} from '@angular/router';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {TumourLesionComponent} from './TumourLesionComponent';
import {CanActivateTumourResponse} from '../tumour-response/CanActivateTumourResponse';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        CommonDirectivesModule
    ],
    declarations: [TumourLesionComponent],
    exports: [TumourLesionComponent],
    providers: [CanActivateTumourResponse]
})
export class TumourLesionComponentModule { }
