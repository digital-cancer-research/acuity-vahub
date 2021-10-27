/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
