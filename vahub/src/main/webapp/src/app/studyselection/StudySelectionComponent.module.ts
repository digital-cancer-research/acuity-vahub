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
import {StudySelectionComponent} from './StudySelectionComponent';
import {StudyListComponent} from '../studylist/StudyListComponent';
import {StudySelectionControlsComponent} from '../studylistcontrols/StudyListControlsComponent';
import {CommonModule} from '@angular/common';
import {ProgressComponentModule} from '../common/loading/ProgressComponent.module';
import {ModalMessageComponentModule} from '../common/modals/modalMessage/ModalMessageComponent.module';
import {CommonPipesModule} from '../common/pipes/CommonPipes.module';
import {FormsModule} from '@angular/forms';

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        CommonPipesModule,
        ProgressComponentModule,
        ModalMessageComponentModule
    ],
    exports: [
        StudySelectionComponent,
        StudyListComponent,
        StudySelectionControlsComponent
    ],
    declarations: [
        StudySelectionComponent,
        StudyListComponent,
        StudySelectionControlsComponent
    ],
    providers: [],
})

export class StudySelectionComponentModule { }
