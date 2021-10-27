import {NgModule} from '@angular/core';
import {SubjectComponentModule} from '../subject/SubjectComponent.module';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineViewComponent} from './TimelineViewComponent';
import {TimelineContextMenuComponentModule} from '../menu/TimelineContextMenuComponent.module';
import {
    trackTransformers,
    trackDataServices
} from '../http/index';
import {FilterHttpService} from '../../../filters/http/FilterHttpService';

import {
    DoseFiltersModel,
    ConmedsFiltersModel
} from '../../../filters/dataTypes/module';
import {TimelineDispatcher} from '../store/dispatcher/TimelineDispatcher';
import {TimelineObservables} from '../store/observable/TimelineObservables';
@NgModule({
    imports: [SubjectComponentModule, CommonModule, FormsModule, TimelineContextMenuComponentModule],
    exports: [TimelineViewComponent],
    declarations: [TimelineViewComponent],
    providers: [
        DoseFiltersModel,
        ConmedsFiltersModel,
        FilterHttpService,
        TimelineDispatcher,
        TimelineObservables,
        ...trackTransformers,
        ...trackDataServices
    ]
})
export class TimelineViewComponentModule {
}
