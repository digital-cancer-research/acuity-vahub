import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SingleSubjectViewTableComponent} from './SingleSubjectViewTableComponent';
import {ProgressComponentModule} from '../../../common/loading/ProgressComponent.module';
import {CommonPipesModule} from '../../../common/pipes/CommonPipes.module';
import {AgGridModule} from 'ag-grid-angular/main';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ProgressComponentModule,
        CommonPipesModule,
        AgGridModule.withComponents([
            SingleSubjectViewTableComponent
        ])
    ],
    declarations: [
        SingleSubjectViewTableComponent
    ],
    exports: [SingleSubjectViewTableComponent],
    providers: [

    ]
})
export class SingleSubjectViewTableComponentModule {

}
