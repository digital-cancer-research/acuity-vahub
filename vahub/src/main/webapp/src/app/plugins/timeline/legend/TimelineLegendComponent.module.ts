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
import {LabsSummaryTrackLegendConfigService} from './labs/LabsSummaryTrackLegendConfigService';
import {LabsDetailTrackLegendConfigService} from './labs/LabsDetailTrackLegendConfigService';
import {ExacerbationsTrackLegendConfigService} from './exacerbations/ExacerbationsTrackLegendConfigService';
import {SpirometrySummaryTrackLegendConfigService} from './spirometry/SpirometrySummaryTrackLegendConfigService';
import {SpirometryDetailTrackLegendConfigService} from './spirometry/SpirometryDetailTrackLegendConfigService';
import {VitalsSummaryTrackLegendConfigService} from './vitals/VitalsSummaryTrackLegendConfigService';
import {VitalsDetailTrackLegendConfigService} from './vitals/VitalsDetailTrackLegendConfigService';
import {PatientDataSummaryTrackLegendConfigService} from './patientdata/PatientDataSummaryTrackLegendConfigService';
import {PatientDataDetailTrackLegendConfigService} from './patientdata/PatientDataDetailTrackLegendConfigService';
import {EcgSummaryTrackLegendConfigService} from './ecg/EcgSummaryTrackLegendConfigService';
import {EcgMeasurementTrackLegendConfigService} from './ecg/EcgMeasurementTrackLegendConfigService';
import {EcgDetailTrackLegendConfigService} from './ecg/EcgDetailTrackLegendConfigService';
import {TimelineLegendComponent} from './TimelineLegendComponent';
import {StatusTrackLegendConfigService} from './status/StatusTrackLegendConfigService';
import {AesTrackLegendConfigService} from './aes/AesTrackLegendConfigService';
import {DoseSummaryTrackLegendConfigService} from './dose/DoseSummaryTrackLegendConfigService';
import {DoseDrugDetailTrackLegendConfigService} from './dose/DoseDrugDetailTrackLegendConfigService';
import {ConmedsTrackLegendConfigService} from './conmeds/ConmedsTrackLegendConfigService';
import {TimelineTrackLegendComponentModule} from './track/TimelineTrackLegendComponent.module';

@NgModule({
    imports: [CommonModule, FormsModule, TimelineTrackLegendComponentModule],
    exports: [TimelineLegendComponent],
    declarations: [TimelineLegendComponent],
    providers: [
        StatusTrackLegendConfigService,
        AesTrackLegendConfigService,
        DoseSummaryTrackLegendConfigService,
        DoseDrugDetailTrackLegendConfigService,
        ConmedsTrackLegendConfigService,
        LabsSummaryTrackLegendConfigService,
        LabsDetailTrackLegendConfigService,
        ExacerbationsTrackLegendConfigService,
        SpirometrySummaryTrackLegendConfigService,
        SpirometryDetailTrackLegendConfigService,
        VitalsSummaryTrackLegendConfigService,
        VitalsDetailTrackLegendConfigService,
        PatientDataSummaryTrackLegendConfigService,
        PatientDataDetailTrackLegendConfigService,
        EcgSummaryTrackLegendConfigService,
        EcgMeasurementTrackLegendConfigService,
        EcgDetailTrackLegendConfigService
    ],
})
export class TimelineLegendComponentModule {
}
