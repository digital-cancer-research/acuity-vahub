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
import {AgGridModule} from 'ag-grid-angular/main';
import {CommonModule} from '@angular/common';
import {DetailsOnDemandComponent} from './DetailsOnDemandComponent';
import {DetailsOnDemandHeightService} from './services/DetailsOnDemandHeightService';
import {DetailsOnDemandEventTableService} from './services/DetailsOnDemandEventTableService';
import {DetailsOnDemandSubjectTableService} from './services/DetailsOnDemandSubjectTableService';
import {DetailsOnDemandSummaryService} from './services/DetailsOnDemandSummaryService';
import {ModalMessageComponentModule} from '../../modals/modalMessage/ModalMessageComponent.module';
import {FormsModule} from '@angular/forms';
import {ProgressComponentModule} from '../../loading/ProgressComponent.module';
import {AdverseEventsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/adverse-events/AdverseEventsColumnModel';
import {ConmedsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/conmeds/ConmedsColumnModel';
import {ExacerbationsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/exacerbations/ExacerbationsColumnModel';
import {LabsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/labs/LabsColumnModel';
import {RenalColumnsModel} from '../../../plugins/refactored-singlesubject/datatypes/renal/RenalColumnsModel';
import {VitalsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/vitals/VitalsColumnModel';
import {CardiacColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cardiac/CardiacColumnModel';
import {CerebrovascularColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cerebrovascular/CerebrovascularColumnModel';
import {CIEventsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cievents/CIEventsColumnModel';
import {CvotColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cvot/CvotColumnModel';
import {DetailsOnDemandEffects} from './store/effects/DetailsOnDemandEffects';
import {EffectsModule} from '@ngrx/effects';
import {MultiDetailsOnDemandComponent} from './multiDetailsOnDemand/MultiDetailsOnDemandComponent';
import {CommonPipesModule} from '../../pipes';
import {QTProlongationColumnModel} from './services/model/QTProlongationColumnModel';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ModalMessageComponentModule,
        AgGridModule.withComponents([DetailsOnDemandComponent]),
        ProgressComponentModule,
        EffectsModule.forFeature([DetailsOnDemandEffects]),
        CommonPipesModule
    ],
    exports: [DetailsOnDemandComponent, MultiDetailsOnDemandComponent],
    declarations: [DetailsOnDemandComponent, MultiDetailsOnDemandComponent],
    providers: [
        AdverseEventsColumnModel,
        ConmedsColumnModel,
        ExacerbationsColumnModel,
        LabsColumnModel,
        RenalColumnsModel,
        VitalsColumnModel,
        CardiacColumnModel,
        CIEventsColumnModel,
        CerebrovascularColumnModel,
        CvotColumnModel,
        QTProlongationColumnModel,
        DetailsOnDemandSummaryService,
        DetailsOnDemandEventTableService,
        DetailsOnDemandSubjectTableService,
        DetailsOnDemandHeightService]
})
export class DetailsOnDemandComponentModule {
}
