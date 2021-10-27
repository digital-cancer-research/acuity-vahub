import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SubjectSummaryTabComponent} from './SubjectSummaryTabComponent';
import {SubjectSummaryComponent} from './SubjectSummaryComponent';
import {SubjectSummarySectionComponent} from './summary-section/SubjectSummarySectionComponent';
import {SubjectInfoComponent} from './summary-section/subject-info/SubjectInfoComponent';
import {ProgressComponentModule} from '../../../../common/loading/ProgressComponent.module';
import {CommonPipesModule} from '../../../../common/pipes/CommonPipes.module';
import {SubjectSummaryTableComponent} from './summary-section/summary-table/SubjectSummaryTableComponent';
import {AgGridModule} from 'ag-grid-angular/main';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ProgressComponentModule,
        CommonPipesModule,
        AgGridModule.withComponents([
            SubjectSummaryTableComponent
        ])
    ],
    declarations: [
        SubjectSummaryTabComponent,
        SubjectSummaryComponent,
        SubjectSummarySectionComponent,
        SubjectInfoComponent,
        SubjectSummaryTableComponent
    ],
    exports: [SubjectSummaryTabComponent],
    providers: [

    ]
})
export class SubjectSummaryTabComponentModule {

}
