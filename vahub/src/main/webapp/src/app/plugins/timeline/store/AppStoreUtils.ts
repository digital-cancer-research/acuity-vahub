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

import {
    AppStore,
    TrackName,
    TrackRecord,
    InitialOpeningStateRecord,
    TimelineRecord,
    ITrack,
    ISubject, EcgWarnings, ECG_WARNING_OPTIONS, ITrackDataPoint, EcgWarningKeys, TimelineId
} from './ITimeline';
import {fromJS, List, Map} from 'immutable';
import * as  _ from 'lodash';
import {FilterId} from '../../../common/trellising/store/ITrellising';

export class AppStoreUtils {

    static buildInitialStore(): AppStore {
        return <AppStore>fromJS({
            initialOpeningState: {
                compareSubjects: new InitialOpeningStateRecord({
                    tracks: List(
                        [new TrackRecord({
                            name: TrackName.SUMMARY,
                            selected: true,
                            order: 1,
                            expansionLevel: 1
                        })]
                    )
                }),
                subjectProfile: new InitialOpeningStateRecord({
                    tracks: List([
                        new TrackRecord({
                            name: TrackName.SUMMARY,
                            selected: true,
                            order: 1,
                            expansionLevel: 1
                        }),
                        new TrackRecord({
                            name: TrackName.DOSE,
                            selected: true,
                            order: 2,
                            expansionLevel: 2
                        }),
                        new TrackRecord({
                            name: TrackName.AES,
                            selected: true,
                            order: 3,
                            expansionLevel: 2
                        }),
                        new TrackRecord({
                            name: TrackName.HEALTHCARE_ENCOUNTERS,
                            selected: true,
                            order: 4,
                            expansionLevel: 3
                        }),
                        new TrackRecord({
                            name: TrackName.LABS,
                            selected: true,
                            order: 5,
                            expansionLevel: 3
                        }),
                        new TrackRecord({
                            name: TrackName.SPIROMETRY,
                            selected: true,
                            order: 6,
                            expansionLevel: 2
                        }),
                        new TrackRecord({
                            name: TrackName.CONMEDS,
                            selected: true,
                            order: 7,
                            expansionLevel: 3
                        }),
                        new TrackRecord({
                            name: TrackName.EXACERBATION,
                            selected: true,
                            order: 8,
                            expansionLevel: 1
                        }),
                        new TrackRecord({
                            name: TrackName.VITALS,
                            selected: true,
                            order: 9,
                            expansionLevel: 2
                        }),
                        new TrackRecord({
                            name: TrackName.ECG,
                            selected: true,
                            order: 10,
                            expansionLevel: 2
                        }),
                        new TrackRecord({
                            name: TrackName.PRD,
                            selected: true,
                            order: 11,
                            expansionLevel: 1
                        })
                    ])
                }),
            },
            timelines: {
                compareSubjects: new TimelineRecord({
                    id: 'compareSubjects'
                }),
                subjectProfile: new TimelineRecord({
                    id: 'subjectProfile',
                    showPagination: false
                })
            }
        });
    }

    static isTimelineRelatedFilter(filterId: FilterId): boolean {
        return [FilterId.POPULATION,
                FilterId.AES,
                FilterId.LAB,
                FilterId.CARDIAC,
                FilterId.CONMEDS,
                FilterId.EXACERBATIONS,
                FilterId.DOSE,
                FilterId.VITALS,
                FilterId.LUNG_FUNCTION
            ].indexOf(filterId.toString()) !== -1;
    }

    static getOtherTimelineId(timelineId: TimelineId): TimelineId {
        if (timelineId === TimelineId.COMPARE_SUBJECTS) {
            return TimelineId.SUBJECT_PROFILE;
        } else {
            return TimelineId.COMPARE_SUBJECTS;
        }
    }

    static getAvailableEcgWarnings(payload: ISubject[], currentWarnings: EcgWarnings): EcgWarnings {
        _.forEach(ECG_WARNING_OPTIONS, (option) => {
            currentWarnings[option.key].available = false;
        });
        payload.forEach((subject: ISubject) => {
            subject.tracks
                .filter((track: ITrack) => {
                    return track.name === TrackName.ECG;
                })
                .forEach((track: ITrack) => {
                    if (track.data) {
                        switch (track.expansionLevel) {
                            case 1:
                                track.data.forEach((point: ITrackDataPoint) => {
                                    currentWarnings = this.updateWarnings(point.metadata, point.metadata.qtcfValue, point.metadata.qtcfChange, currentWarnings);
                                });
                                break;
                            case 2:
                                track.data.forEach((point: ITrackDataPoint) => {
                                    if (point.metadata.testName.toUpperCase().indexOf('QTCF') !== -1) {
                                        currentWarnings = this.updateWarnings(point.metadata, point.metadata.valueRaw, point.metadata.valueChangeFromBaseline, currentWarnings);
                                    }
                                });
                                break;
                            case 3:
                                track.data.forEach((point: ITrackDataPoint) => {
                                    if (point.metadata.testName.toUpperCase().indexOf('QTCF') !== -1) {
                                        currentWarnings = this.updateWarnings(point.metadata, point.metadata.valueRaw, point.metadata.valueChangeFromBaseline, currentWarnings);
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
        });
        return currentWarnings;
    }

    private static updateWarnings(metadata: any, qtcfValue: number, qtcfChange: number, ecgWarnings: EcgWarnings): EcgWarnings {
        const sex = metadata.sex ? metadata.sex.toUpperCase() : null;
        ecgWarnings[EcgWarningKeys.ABNORMAL].available = ecgWarnings[EcgWarningKeys.ABNORMAL].available || (metadata.abnormality && metadata.abnormality.toUpperCase() !== 'NO' && metadata.abnormality.toUpperCase() !== 'N');
        ecgWarnings[EcgWarningKeys.SIGNIFICANT].available = ecgWarnings[EcgWarningKeys.SIGNIFICANT].available || (metadata.significant && metadata.significant.toUpperCase() !== 'NO' && metadata.significant.toUpperCase() !== 'N');
        ecgWarnings[EcgWarningKeys.QTCF_MALE].available = ecgWarnings[EcgWarningKeys.QTCF_MALE].available || (qtcfValue >= 450 && (sex === 'MALE' || sex === 'M'));
        ecgWarnings[EcgWarningKeys.QTCF_FEMALE].available = ecgWarnings[EcgWarningKeys.QTCF_FEMALE].available || (qtcfValue >= 480 && (sex === 'FEMALE' || sex === 'F'));
        ecgWarnings[EcgWarningKeys.QTCF_CHANGE].available = ecgWarnings[EcgWarningKeys.QTCF_CHANGE].available || (qtcfChange >= 60);
        return ecgWarnings;
    }
}
