import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';

import {FilterPipesModule} from '../../filters/pipes/FilterPipes.module';
import {FiltersBarComponentModule} from '../../filters/module';
import {CohortEditorComponent} from './components/CohortEditorComponent';
import {AvailableCohortsComponent} from './components/AvailableCohortsComponent';
import {FilterListComponent} from './components/FilterListComponent';
import {CohortSharingComponent} from './components/CohortSharingComponent';
import {CohortEditorService} from './services/CohortEditorService';
import {FilterReloadService} from './services/FilterReloadService';
import {CohortFilterPipe} from './pipes/CohortFilterPipe';
import {CohortUserPipe} from './pipes/CohortUserPipe';
import {ModalMessageComponentModule} from '../../common/modals/modalMessage/ModalMessageComponent.module';
import {ProgressComponentModule} from '../../common/module';
import {SelectedFiltersComponentModule} from '../../filters/module';
import {FilterEventService} from '../../filters/event/FilterEventService';

@NgModule({
    imports: [
        CommonModule,
        FilterPipesModule,
        FormsModule,
        FiltersBarComponentModule,
        RouterModule,
        ModalMessageComponentModule,
        ProgressComponentModule,
        SelectedFiltersComponentModule
    ],
    exports: [CohortEditorComponent],
    declarations: [
        CohortEditorComponent,
        CohortFilterPipe,
        CohortUserPipe,
        AvailableCohortsComponent,
        FilterListComponent,
        CohortSharingComponent
    ],
    providers: [
        CohortEditorService,
        FilterReloadService,
        FilterEventService
    ]
})
export class CohortEditorModule {
}
