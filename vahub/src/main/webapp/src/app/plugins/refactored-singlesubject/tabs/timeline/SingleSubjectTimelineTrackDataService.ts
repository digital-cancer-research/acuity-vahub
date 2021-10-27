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

import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ITrack, DayZero} from '../../../timeline/store/ITimeline';
import {StatusTrackDataService} from '../../../timeline/http/status/StatusTrackDataService';
import {AesTrackDataService} from '../../../timeline/http/aes/AesTrackDataService';
import {DoseTrackDataService} from '../../../timeline/http/dose/DoseTrackDataService';
import {ConmedsTrackDataService} from '../../../timeline/http/conmeds/ConmedsTrackDataService';
import {ExacerbationsTrackDataService} from '../../../timeline/http/exacerbations/ExacerbationsTrackDataService';
import {LabsTrackDataService} from '../../../timeline/http/labs/LabsTrackDataService';
import {EcgTrackDataService} from '../../../timeline/http/ecg/EcgTrackDataService';
import {VitalsTrackDataService} from '../../../timeline/http/vitals/VitalsTrackDataService';
import {SpirometryTrackDataService} from '../../../timeline/http/spirometry/SpirometryTrackDataService';
import {TrackUtilDataService} from '../../../timeline/http/TrackUtilDataService';
import {SingleSubjectModel} from '../../../refactored-singlesubject/SingleSubjectModel';
import {DatasetViews} from '../../../../security/DatasetViews';
import {TrackDataService} from '../../../timeline/http/TrackDataService';
import {PatientDataTrackDataService} from '../../../timeline/http/patientdata/PatientDataTrackDataService';

@Injectable()
export class SingleSubjectTimelineTrackDataService extends TrackDataService {

    constructor(trackUtilDataService: TrackUtilDataService,
                statusTrackDataService: StatusTrackDataService,
                aesTrackDataService: AesTrackDataService,
                doseTrackDataService: DoseTrackDataService,
                conmedsTrackDataService: ConmedsTrackDataService,
                exacerbationsTrackDataService: ExacerbationsTrackDataService,
                labsTrackDataService: LabsTrackDataService,
                ecgTrackDataService: EcgTrackDataService,
                vitalsTrackDataService: VitalsTrackDataService,
                patientDataTrackDataService: PatientDataTrackDataService,
                spirometryTrackDataService: SpirometryTrackDataService,
                private singleSubjectModel: SingleSubjectModel,
                datasetViews: DatasetViews) {
        super(trackUtilDataService,
            statusTrackDataService,
            aesTrackDataService,
            doseTrackDataService,
            conmedsTrackDataService,
            exacerbationsTrackDataService,
            labsTrackDataService,
            ecgTrackDataService,
            vitalsTrackDataService,
            patientDataTrackDataService,
            spirometryTrackDataService,
            datasetViews);
    }

    /**
     * Get possible subjects
     */
    getPossibleSubjects(dayZero: any, tracks?: ITrack[]): Observable<string[]> {
        const chosenSubjects: string[] = this.singleSubjectModel.currentChosenSubject ? [this.singleSubjectModel.currentChosenSubject] : [];
        if (chosenSubjects.length !== 0) {
            return this.trackUtilDataService.fetchSelectedSubjects(dayZero, tracks, chosenSubjects);
        }
        return Observable.of(chosenSubjects);
    }
}
