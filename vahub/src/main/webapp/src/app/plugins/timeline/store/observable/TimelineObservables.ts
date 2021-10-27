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
import {Store} from '@ngrx/store';
import {List} from 'immutable';

import 'rxjs/add/operator/take';
import {
    ISubject, TimelineId, IZoom, ITimelinePage, ITrack, LabsYAxisValue, SpirometryYAxisValue,
    EcgYAxisValue, EcgWarnings, VitalsYAxisValue, AppStore, DayZero
} from '../ITimeline';
import {DynamicAxis} from '../../../../common/trellising/store';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';

@Injectable()
export class TimelineObservables {
    private timelineId: TimelineId;

    loading: Observable<boolean>;
    showPagination: Observable<boolean>;
    subjects: Observable<List<ISubject>>;
    dayZero: Observable<DynamicAxis>;
    dayZeroOptions: Observable<DynamicAxis[]>;
    zoom: Observable<IZoom>;
    page: Observable<ITimelinePage>;
    totalNumberOfSubjects: Observable<number>;
    tracks: Observable<List<ITrack>>;
    labsYAxisValue: Observable<LabsYAxisValue>;
    spirometryYAxisValue: Observable<SpirometryYAxisValue>;
    ecgYAxisValue: Observable<EcgYAxisValue>;
    ecgWarnings: Observable<EcgWarnings>;
    vitalsYAxisValue: Observable<VitalsYAxisValue>;
    plotBands: Observable<any[]>;

    constructor(private _store: Store<ApplicationState>) {
    }

    setTimelineId(timelineId: TimelineId): void {
        this.timelineId = timelineId;
        this.subscribeToStore();
    }

    getState(store: Store<ApplicationState>): AppStore {
        let state: any;
        store.take(1).subscribe(s => state = s);
        return state.timelineReducer;
    }

    getCurrentDisplayedSubjects(): ISubject[] {
        const state = this.getState(this._store);
        const displayedSubjects: List<string>
            = state.getIn(['timelines', this.timelineId, 'displayedSubjects']);
        const subjects: Map<string, ISubject>
            = state.getIn(['timelines', this.timelineId, 'subjects']);
        return displayedSubjects.reduce((selected, subjectId) => {
            selected.push(subjects.get(subjectId));
            return selected;
        }, []);
    }

    getCurrentDisplayedTracks(): ITrack[] {
        const state = this.getState(this._store);
        const tracks: List<ITrack>
            = state.getIn(['timelines', this.timelineId, 'tracks']);
        return tracks.reduce((selected, track) => {
            if (track.get('selected')) {
                selected.push(track);
            }
            return selected;
        }, []);
    }

    getCurrentPage(): ITimelinePage {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'page']);
    }

    getSubjectById(subjectId: string): ISubject {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'subjects', subjectId]);
    }

    getCurrentDayZero(): DayZero {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'dayZero']);
    }

    getCurrentEcgWarnings(): EcgWarnings {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'ecgWarnings']);
    }

    getPerformedJumpToTimelineFlag(): EcgWarnings {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'performedJumpToTimeline']);
    }

    getIsInitialized(): boolean {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'isInitialized']);
    }

    /**
     * Get DayZero options from store
     * @returns {any[]}
     */
    getDayZeroOptions(): any[] {
        const state = this.getState(this._store);
        return state.getIn(['timelines', this.timelineId, 'dayZeroOptions']);
    }

    private subscribeToStore(): void {
        const timelineStore: Observable<AppStore> = this._store.select('timelineReducer');
        this.loading = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'loading']));
        this.showPagination = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'showPagination']));
        this.dayZero = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'dayZero']));
        this.dayZeroOptions = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'dayZeroOptions']));
        this.subjects = timelineStore.map(data => {
            return data.getIn(['timelines', this.timelineId, 'displayedSubjects'])
                .reduce((currentSubjects, subjectId) => {
                    currentSubjects = currentSubjects.push(data.getIn(['timelines', this.timelineId, 'subjects', subjectId]));
                    return currentSubjects;
                }, List<ISubject>());
        });
        this.zoom = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'zoom']));
        this.page = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'page']));
        this.totalNumberOfSubjects = timelineStore.map(data => {
            return data.getIn(['timelines', this.timelineId, 'subjects']).size;
        });
        this.tracks = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'tracks']));
        this.labsYAxisValue = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'labsYAxisValue']));
        this.spirometryYAxisValue = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'spirometryYAxisValue']));
        this.ecgYAxisValue = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'ecgYAxisValue']));
        this.ecgWarnings = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'ecgWarnings']));
        this.vitalsYAxisValue = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'vitalsYAxisValue']));
        this.plotBands = timelineStore.map(data => data.getIn(['timelines', this.timelineId, 'plotBands']));
    }
}
