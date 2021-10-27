import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {PopulationSummaryComponent} from './PopulationSummaryComponent';
import {RouterModule} from '@angular/router';
import {PopulationSummaryTableService} from './populationSummaryTable/PopulationSummaryTableService';
import {ApplySafetyFiltersModalComponent, ApplySafetyFiltersModalService} from '../../common/module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
    ],
    declarations: [PopulationSummaryComponent, ApplySafetyFiltersModalComponent],
    exports: [PopulationSummaryComponent],
    providers: [PopulationSummaryTableService, ApplySafetyFiltersModalService]
})
export class PopulationSummaryComponentModule {
}
