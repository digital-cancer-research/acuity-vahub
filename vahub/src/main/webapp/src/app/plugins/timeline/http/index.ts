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

import {TrackUtilDataService} from './TrackUtilDataService';
import {AesTrackDataService} from './aes/AesTrackDataService';
import {StatusTrackDataService} from './status/StatusTrackDataService';
import {DoseTrackDataService} from './dose/DoseTrackDataService';
import {ConmedsTrackDataService} from './conmeds/ConmedsTrackDataService';
import {ExacerbationsTrackDataService} from './exacerbations/ExacerbationsTrackDataService';
import {LabsTrackDataService} from './labs/LabsTrackDataService';
import {EcgTrackDataService} from './ecg/EcgTrackDataService';
import {VitalsTrackDataService} from './vitals/VitalsTrackDataService';
import {PatientDataTrackDataService} from './patientdata/PatientDataTrackDataService';
import {SpirometryTrackDataService} from './spirometry/SpirometryTrackDataService';

import {TrackDataTransformer} from './TrackDataTransformer';
import {TrackUtilDataTransformer} from './TrackUtilDataTransformer';
import {AesTrackDataTransformer} from './aes/AesTrackDataTransformer';
import {StatusTrackDataTransformer} from './status/StatusTrackDataTransformer';
import {DoseTrackDataTransformer} from './dose/DoseTrackDataTransformer';
import {ConmedsTrackDataTransformer} from './conmeds/ConmedsTrackDataTransformer';
import {ExacerbationsTrackDataTransformer} from './exacerbations/ExacerbationsTrackDataTransformer';
import {LabsTrackDataTransformer} from './labs/LabsTrackDataTransformer';
import {EcgTrackDataTransformer} from './ecg/EcgTrackDataTransformer';
import {VitalsTrackDataTransformer} from './vitals/VitalsTrackDataTransformer';
import {PatientDataTrackDataTransformer} from './patientdata/PatientDataTrackDataTransformer';
import {SpirometryTrackDataTransformer} from './spirometry/SpirometryTrackDataTransformer';
import {BaseTrackDataService} from './BaseTrackDataService';

export {TrackDataService} from './TrackDataService';
export const trackTransformers = [
    TrackDataTransformer,
    TrackUtilDataTransformer,
    AesTrackDataTransformer,
    StatusTrackDataTransformer,
    ConmedsTrackDataTransformer,
    ExacerbationsTrackDataTransformer,
    DoseTrackDataTransformer,
    LabsTrackDataTransformer,
    EcgTrackDataTransformer,
    VitalsTrackDataTransformer,
    PatientDataTrackDataTransformer,
    SpirometryTrackDataTransformer
];

export const trackDataServices = [
    TrackUtilDataService,
    StatusTrackDataService,
    AesTrackDataService,
    DoseTrackDataService,
    ConmedsTrackDataService,
    ExacerbationsTrackDataService,
    LabsTrackDataService,
    EcgTrackDataService,
    VitalsTrackDataService,
    PatientDataTrackDataService,
    SpirometryTrackDataService,
    BaseTrackDataService
];

