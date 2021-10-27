import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {TumourTherapyComponent} from './TumourTherapyComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        CommonDirectivesModule
    ],
    declarations: [TumourTherapyComponent],
    exports: [TumourTherapyComponent],
    providers: []
})
export class TumourTherapyComponentModule {
}
