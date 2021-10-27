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

import {AesTrackComponentModule} from './aes/AesTrackComponent.module';
import {DoseTrackComponentModule} from './dose/DoseTrackComponent.module';
import {StatusTrackComponentModule} from './status/StatusTrackComponent.module';
import {ConmedsTrackComponentModule} from './conmeds/ConmedsTrackComponent.module';
import {VitalsTrackComponentModule} from './vitals/VitalsTrackComponent.module';
import {PatientDataTrackComponentModule} from './patientdata/PatientDataTrackComponent.module';
import {ExacerbationsTrackComponentModule} from './exacerbations/ExacerbationsTrackComponent.module';
import {EcgTrackComponentModule} from './ecg/EcgTrackComponent.module';
import {LabsTrackComponentModule} from './labs/LabsTrackComponent.module';
import {SpirometryTrackComponentModule} from './spirometry/SpirometryTrackComponent.module';

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineTrackComponent} from './TimelineTrackComponent';

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        AesTrackComponentModule,
        DoseTrackComponentModule,
        StatusTrackComponentModule,
        ConmedsTrackComponentModule,
        VitalsTrackComponentModule,
        PatientDataTrackComponentModule,
        ExacerbationsTrackComponentModule,
        EcgTrackComponentModule,
        LabsTrackComponentModule,
        SpirometryTrackComponentModule
    ],
    exports: [TimelineTrackComponent],
    declarations: [TimelineTrackComponent],
    providers: [],
})
export class TimelineTrackComponentModule {
}
