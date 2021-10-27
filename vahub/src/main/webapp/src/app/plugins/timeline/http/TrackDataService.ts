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
import {Subject} from 'rxjs/Subject';
import {DayZero, ISubject, ITrack, SubjectRecord, TrackName} from '../store/ITimeline';
import {TrackData, TrackRequest} from './IDataService';
import {StatusTrackDataService} from './status/StatusTrackDataService';
import {AesTrackDataService} from './aes/AesTrackDataService';
import {DoseTrackDataService} from './dose/DoseTrackDataService';
import {ConmedsTrackDataService} from './conmeds/ConmedsTrackDataService';
import {ExacerbationsTrackDataService} from './exacerbations/ExacerbationsTrackDataService';
import {LabsTrackDataService} from './labs/LabsTrackDataService';
import {EcgTrackDataService} from './ecg/EcgTrackDataService';
import {VitalsTrackDataService} from './vitals/VitalsTrackDataService';
import {PatientDataTrackDataService} from './patientdata/PatientDataTrackDataService';
import {SpirometryTrackDataService} from './spirometry/SpirometryTrackDataService';
import {TrackUtilDataService} from './TrackUtilDataService';
import {List, Set} from 'immutable';
import * as  _ from 'lodash';
import {DatasetViews} from '../../../security/DatasetViews';
import {DynamicAxis} from '../../../common/trellising/store/ITrellising';
import {forkJoin} from "rxjs/observable/forkJoin";

@Injectable()
export class TrackDataService {

    constructor(protected trackUtilDataService: TrackUtilDataService,
                protected statusTrackDataService: StatusTrackDataService,
                protected aesTrackDataService: AesTrackDataService,
                protected doseTrackDataService: DoseTrackDataService,
                protected conmedsTrackDataService: ConmedsTrackDataService,
                protected exacerbationsTrackDataService: ExacerbationsTrackDataService,
                protected labsTrackDataService: LabsTrackDataService,
                protected ecgTrackDataService: EcgTrackDataService,
                protected vitalsTrackDataService: VitalsTrackDataService,
                protected patientDataTrackDataService: PatientDataTrackDataService,
                protected spirometryTrackDataService: SpirometryTrackDataService,
                protected datasetViews: DatasetViews) {
    }

    /**
     * Get possible tracks
     * @returns {Observable<ITrack[]>} - observable of array of available tracks
     */
    getPossibleTracks(): Observable<ITrack[]> {
        return this.trackUtilDataService.fetchPossibleTracks();
    }

    /**
     * Get possible subjects that have data for the selected tracks
     * @param dayZero - selected timeline x axis value
     * @param {ITrack[]} tracks - array of selected tracks
     * @returns {Observable<string[]>} returns observable of array containing all available subjects
     */
    getPossibleSubjects(dayZero: any, tracks?: ITrack[]): Observable<string[]> {
        return this.trackUtilDataService.fetchSelectedSubjects(dayZero, tracks);
    }

    /**
     * Get track data
     * Goes through tracks and splits out into multiple api calls and merges back
     * @param {ISubject[]} subjects - array of subjects for that track data is requested.
     * Every subject contain selected tracks for particular subjects with expansion level.
     * @param {DayZero} dayZero - selected timeline x axis option
     * @returns {Observable<ISubject[]>} - observable of initial subjects array with updated track data
     */
    getTrackData(subjects: ISubject[], dayZero: DayZero): Observable<ISubject[]> {
        // create a copy of the subjects
        let resultSubjects: ISubject[] = this.copySubjects(subjects);
        if (!resultSubjects[0] || !resultSubjects[0].tracks || resultSubjects[0].tracks.size === 0) {
            return Observable.of(resultSubjects);
        }

        // format track requests
        const trackRequest: TrackRequest[] = this.getTrackRequests(resultSubjects, dayZero);

        // query for track requests
        const source: Observable<TrackData[][]> = forkJoin(this.queryTrackRequests(trackRequest));

        // format and return track request results
        const result: Subject<ISubject[]> = new Subject<ISubject[]>();
        source.subscribe((trackConfigs: any[]) => {
                trackConfigs.forEach((subjectTrackConfig: TrackData[]) => {
                    subjectTrackConfig.forEach((trackConfig: TrackData) => {
                        resultSubjects = resultSubjects.map((subject: ISubject) => {
                            if (subject.id === trackConfig.subjectId) {
                                return <ISubject> new SubjectRecord({
                                    id: subject.id,
                                    subjectId: subject.subjectId,
                                    tracks: subject.tracks.map((track: ITrack) => {
                                        if (track.name === trackConfig.request.name
                                            && track.expansionLevel === trackConfig.request.expansionLevel) {
                                            return track.set('data', trackConfig.data);
                                        } else {
                                            return track.set('changed', true);
                                        }
                                    })
                                });
                            } else {
                                return subject;
                            }
                        });
                    });
                });
            },
            (error) => {
                console.warn('Error in loading track data');
                console.warn(error._body);
            },
            () => {
                result.next(resultSubjects);
            }
        );
        return result;
    }

    /**
     * Create a new copy of an array of subjects, clear previously stored data
     * @param {ISubject[]} subjects - array of subjects for that track data is requested
     * @returns {ISubject[]} returns array of subjects
     * with empty data in tracks for those tracks that have been changed
     * and ids instead of ecodes
     */
    copySubjects(subjects: ISubject[]): ISubject[] {
        return <Array<ISubject>>subjects.map((subject) => {
            // create new subjects with empty tracks data and subject id instead of ecode
            return <ISubject>new SubjectRecord({
                id: this.datasetViews.getSubjectIdByEcode(subject.subjectId),
                subjectId: subject.subjectId,
                tracks: subject.tracks.map((track: ITrack) => {
                    return track.get('changed') ? track.set('data', null) : track;
                })
            });
        });
    }

    /**
     * Create array of queries for all requested tracks
     * @param {TrackRequest[]} trackRequests - array of requested tracks
     * containing track name, expansion level and rest required info
     * @returns {Observable<TrackData[]>[]} - returns array of observables of data arrays
     */
    private queryTrackRequests(trackRequests: TrackRequest[]): Observable<TrackData[]>[] {
        return trackRequests.reduce((trackDataRequests: Observable<TrackData[]>[], request) => {
            switch (request.name) {
                case TrackName.DOSE:
                    trackDataRequests.push(this.doseTrackDataService.fetchData(request));
                    break;

                case TrackName.AES:
                    trackDataRequests.push(this.aesTrackDataService.fetchData(request));
                    break;

                case TrackName.SUMMARY:
                    trackDataRequests.push(this.statusTrackDataService.fetchData(request));
                    break;

                case TrackName.CONMEDS:
                    trackDataRequests.push(this.conmedsTrackDataService.fetchData(request));
                    break;

                case TrackName.EXACERBATION:
                    trackDataRequests.push(this.exacerbationsTrackDataService.fetchData(request));
                    break;

                case TrackName.LABS:
                    trackDataRequests.push(this.labsTrackDataService.fetchData(request));
                    break;

                case TrackName.ECG:
                    trackDataRequests.push(this.ecgTrackDataService.fetchData(request));
                    break;

                case TrackName.VITALS:
                    trackDataRequests.push(this.vitalsTrackDataService.fetchData(request));
                    break;

                case TrackName.PRD:
                    trackDataRequests.push(this.patientDataTrackDataService.fetchData(request));
                    break;

                case TrackName.SPIROMETRY:
                    trackDataRequests.push(this.spirometryTrackDataService.fetchData(request));
                    break;

                default:
                    break;
            }
            return trackDataRequests;
        }, []);
    }

    /**
     * Get the unique list of tracks and expansionLevels with the list of subjects
     * that need updated data for the particular track
     * @param {ISubject[]} subjects - list of subjects that are waiting for some track data
     * @param {DayZero} dayZero - currently selected timeline x axis option
     * @returns {TrackRequest[]} - array of tracks with expansion level and subjects
     * that are waiting for data for this track
     */
    getTrackRequests(subjects: ISubject[], dayZero: DayZero): TrackRequest[] {
        const uniqTracks = this.collectSortedUnqiueTracks(subjects);

        const trackRequests: TrackRequest[] = uniqTracks.map((track: ITrack) => {

            const trackSubjects = <string[]>subjects.filter((subject: ISubject) => {
                return subject.tracks.some((subjectTrack: ITrack) => {
                    return (subjectTrack.name === track.name &&
                        subjectTrack.expansionLevel === track.expansionLevel);
                });
            }).map(subject => subject.subjectId);
            const subjectIds = trackSubjects.map(subject => this.datasetViews.getSubjectIdByEcode(subject));

            return <TrackRequest> {
                name: track.name,
                expansionLevel: track.expansionLevel,
                dayZero,
                trackSubjects,
                subjectIds
            };
        });
        return trackRequests;
    }

    /**
     * Collect a array of unique tracks from array of subjects,
     * for that the data is requested and sort them according to
     * their name and expansion level.
     * Only tracks marked as 'changed' are added to the request to minimize data flow
     * @param {ISubject[]} subjects - array of subjects for those data should be requested from server
     * @returns {ITrack[]} - unique array of tracks with additional info extracted from array of subjects
     */
    collectSortedUnqiueTracks(subjects: ISubject[]): ITrack[] {
        const allTracks: Set<ITrack> = subjects.reduce((tracks: Set<ITrack>, subject: ISubject) => {
            tracks = tracks.union(subject.tracks
                .filter((track: ITrack) => {
                return track.get('changed');
            })
            );
            return tracks;
        }, Set<ITrack>());

        return _.sortBy(allTracks.toArray(), ['name', 'expansionLevel']);
    }

    /**
     * Requests from server info if current dataset has a randomisation date
     * @returns {Observable<Boolean>}
     */
    hasRandomisationDates(): Observable<Boolean> {
        return this.trackUtilDataService.hasRandomisationDate();
    }

    /**
     * Requests from server all timeline x axis options for current dataset
     * @returns {Observable<Immutable.List<DynamicAxis>>} - returns list of available timeline x axis options
     */
    fetchDayZeroOptions(): Observable<List<DynamicAxis>> {
        return this.trackUtilDataService.fetchDayZeroOptions();
    }
}
