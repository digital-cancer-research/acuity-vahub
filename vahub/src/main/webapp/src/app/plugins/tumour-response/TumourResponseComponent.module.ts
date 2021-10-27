import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {TumourResponseComponent} from './TumourResponseComponent';
import {RouterModule} from '@angular/router';
import {CanActivateTumourResponse} from './CanActivateTumourResponse';
import {CommonDirectivesModule} from '../../common/directives/directives.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        CommonDirectivesModule
    ],
    declarations: [TumourResponseComponent],
    exports: [TumourResponseComponent],
    providers: [CanActivateTumourResponse]
})
export class TumourResponseComponentModule { }
