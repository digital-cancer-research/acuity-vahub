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
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SelectedFiltersComponent} from './SelectedFiltersComponent';
import {ModalMessageComponentModule} from '../../common/modals/modalMessage/ModalMessageComponent.module';
import {SelectedFiltersModel} from './SelectedFiltersModel';
import {BrowserModule} from '@angular/platform-browser';
import {FilterPipesModule} from '../pipes/FilterPipes.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ModalMessageComponentModule,
        BrowserModule,
        FilterPipesModule
    ],
    exports: [SelectedFiltersComponent],
    declarations: [SelectedFiltersComponent],
    providers: [SelectedFiltersModel],
})
export class SelectedFiltersComponentModule {
}
