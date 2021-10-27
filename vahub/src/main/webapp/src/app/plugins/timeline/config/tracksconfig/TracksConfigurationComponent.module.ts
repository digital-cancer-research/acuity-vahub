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
import {LabsConfigurationComponent} from './labs/LabsConfigurationComponent';
import {EcgYAxisConfigurationComponent} from './ecg/yAxisValue/EcgYAxisConfigurationComponent';
import {EcgWarningsConfigurationComponent} from './ecg/warnings/EcgWarningsConfigurationComponent';
import {SpirometryConfigurationComponent} from './spirometry/SpirometryConfigurationComponent';
import {VitalsConfigurationComponent} from './vitals/VitalsConfigurationComponent';
import {TracksConfigurationComponent} from './TracksConfigurationComponent';
import {TimelineConfigurationService} from './TracksConfigurationService';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TracksConfigurationComponent],
    declarations: [TracksConfigurationComponent,
        LabsConfigurationComponent,
        EcgYAxisConfigurationComponent,
        EcgWarningsConfigurationComponent,
        VitalsConfigurationComponent,
        SpirometryConfigurationComponent],
    providers: [TimelineConfigurationService]
})
export class TracksConfigurationComponentModule {
}
