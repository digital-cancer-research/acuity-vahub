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

import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {CommonPipesModule} from '../../../common/pipes';
import { FilterCollectionComponent } from './FilterCollectionComponent';
import {
    ListFilterComponent,
    CheckListFilterComponent,
    RangeFilterComponentModule,
    RangeDateFilterComponent,
    MapListFilterComponent,
    MapRangeDateFilterComponent,
    MapRangeFilterComponent,
    StudySpecificFilterComponent,
    UnselectedCheckListFilterComponent
} from '../../components/module';

import {FilterPipesModule} from '../../pipes/FilterPipes.module';

@NgModule({
    imports: [CommonModule, CommonPipesModule, FormsModule, RangeFilterComponentModule, FilterPipesModule],
    exports: [FilterCollectionComponent],
    declarations: [
        FilterCollectionComponent,
        ListFilterComponent,
        CheckListFilterComponent,
        RangeDateFilterComponent,
        MapListFilterComponent,
        MapRangeDateFilterComponent,
        MapRangeFilterComponent,
        StudySpecificFilterComponent,
        UnselectedCheckListFilterComponent
    ],
    providers: [],
})
export class FilterCollectionComponentModule { }
