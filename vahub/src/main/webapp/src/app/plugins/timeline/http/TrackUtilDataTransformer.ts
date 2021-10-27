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

import { Injectable } from '@angular/core';
import {
    TrackName,
    TrackRecord,
    ITrack
} from '../store/ITimeline';
import {TrackDataTransformer} from './TrackDataTransformer';

@Injectable()
export class TrackUtilDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformPossibleTrackData(result: any): ITrack[] {
        const resultTrackNames: TrackName[] = this.mapToTrackName(result);

        const tracks: ITrack[] = [];
        resultTrackNames.forEach((trackName: TrackName, index: number) => {
            tracks.push(<ITrack>new TrackRecord({
                name: trackName,
                order: index + 1,
                expansionLevel: 1,
                selected: false
            }));
        });

        return tracks;
    }

    mapToTrackName(names: string[]): TrackName[] {
        return names.reduce((resultTrackNames, trackName: string) => {
            switch (trackName) {
                case 'STATUS_SUMMARY':
                    resultTrackNames.push(TrackName.SUMMARY);
                    break;
                case 'DOSING':
                    resultTrackNames.push(TrackName.DOSE);
                    break;
                case 'AES':
                    resultTrackNames.push(TrackName.AES);
                    break;
                case 'LABS':
                    resultTrackNames.push(TrackName.LABS);
                    break;
                case 'HEALTHCARE_ENCOUNTERS':
                    resultTrackNames.push(TrackName.HEALTHCARE_ENCOUNTERS);
                    break;
                case 'CONMEDS':
                    resultTrackNames.push(TrackName.CONMEDS);
                    break;
                case 'VITALS':
                    resultTrackNames.push(TrackName.VITALS);
                    break;
                case 'EXACERBATIONS':
                    resultTrackNames.push(TrackName.EXACERBATION);
                    break;
                case 'SPIROMETRY':
                    resultTrackNames.push(TrackName.SPIROMETRY);
                    break;
                case 'ECG':
                    resultTrackNames.push(TrackName.ECG);
                    break;
                case 'PATIENT_DATA':
                    resultTrackNames.push(TrackName.PRD);
                    break;
                default:
                    break;
            }
            return resultTrackNames;
        }, new Array<TrackName>());
    }

    mapToOriginalTrackName(tracks: ITrack[]): string[] {
        return tracks.reduce((resultTrackNames, track: ITrack) => {
            switch (track.name) {
                case TrackName.SUMMARY:
                    resultTrackNames.push('STATUS_SUMMARY');
                    break;
                case TrackName.DOSE:
                    resultTrackNames.push('DOSING');
                    break;
                case TrackName.AES:
                    resultTrackNames.push('AES');
                    break;
                case TrackName.LABS:
                    resultTrackNames.push('LABS');
                    break;
                case TrackName.HEALTHCARE_ENCOUNTERS:
                    resultTrackNames.push('HEALTHCARE_ENCOUNTERS');
                    break;
                case TrackName.CONMEDS:
                    resultTrackNames.push('CONMEDS');
                    break;
                case TrackName.VITALS:
                    resultTrackNames.push('VITALS');
                    break;
                case TrackName.EXACERBATION:
                    resultTrackNames.push('EXACERBATIONS');
                    break;
                case TrackName.SPIROMETRY:
                    resultTrackNames.push('SPIROMETRY');
                    break;
                case TrackName.ECG:
                    resultTrackNames.push('ECG');
                    break;
                case TrackName.PRD:
                    resultTrackNames.push('PATIENT_DATA');
                    break;
                default:
                    break;
            }
            return resultTrackNames;
        }, new Array<string>());
    }

    transformSelectedSubjectIds(result: any): string[] {
        const selectedSubjectIds: string[] = result || [];

        return selectedSubjectIds;
    }
}
