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

import {BiomarkerSettingsComponent} from './dataTypes/biomarker/BiomarkerSettingsComponent';
import {DoseProportionalitySettingsComponent} from './dataTypes/dose-proportionality/DoseProportionalitySettingsComponent';
import {ExposureSettingsComponent} from './dataTypes/exposure/ExposureSettingsComponent';
import {PriorTherapySettingsComponent} from './dataTypes/prior-therapy/PriorTherapySettingsComponent';
import {ListSelectionComponent} from './list-selection/ListSelectionComponent';
import {RadioButtonsSettingsComponent} from './radioButtons/RadioButtonsSettingsComponent';
import {PkOverallResponseSettingsComponent} from './dataTypes/pk-overall-response-settings/PkOverallResponseSettingsComponent';
import {AesChordSettingsComponent} from './dataTypes/aes/AesChordSettingsComponent';
import {RangeSettingsComponent} from './range/RangeSettingsComponent';
import {CtDNASettingsComponent} from './dataTypes/ctdna-settings/CtDNASettingsComponent';

export const settingsComponents = [
    ExposureSettingsComponent,
    DoseProportionalitySettingsComponent,
    PkOverallResponseSettingsComponent,
    CtDNASettingsComponent,
    PriorTherapySettingsComponent,
    BiomarkerSettingsComponent,
    AesChordSettingsComponent,
    RadioButtonsSettingsComponent,
    ListSelectionComponent,
    RangeSettingsComponent
];
