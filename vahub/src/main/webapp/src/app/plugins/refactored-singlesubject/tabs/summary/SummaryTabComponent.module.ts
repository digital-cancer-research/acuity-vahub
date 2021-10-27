import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SummaryTabComponent} from './SummaryTabComponent';
import {SummaryComponent} from './SummaryComponent';
import {ProgressComponentModule} from '../../../../common/loading/ProgressComponent.module';
import {CommonPipesModule} from '../../../../common/pipes/CommonPipes.module';
import {RouterModule} from '@angular/router';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ProgressComponentModule,
        CommonPipesModule,
        RouterModule
    ],
    declarations: [
        SummaryTabComponent,
        SummaryComponent
    ],
    exports: [SummaryTabComponent],
    providers: []
})
export class SummaryTabComponentModule {

}
