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

