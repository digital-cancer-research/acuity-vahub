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
import {Store} from '@ngrx/store';
import {TimelineActionCreator} from '../actions/TimelineActionCreator';
import 'rxjs/add/operator/take';
import {AppStore, DayZero, ISubject, ITrack, TimelineId} from '../ITimeline';

@Injectable()
export class TimelineDispatcher {
    private timelineId: TimelineId;
    private timelineActionCreator: TimelineActionCreator;

    constructor(private _store: Store<AppStore>) {

    }

    setTimelineId(timelineId: TimelineId): void {
        this.timelineId = timelineId;
        this.timelineActionCreator = new TimelineActionCreator(timelineId);
    }

    globalResetNotification(): void {
        this._store.dispatch(TimelineActionCreator.makeInitializationAction(false));
    }

    localResetNotification(timelineId: TimelineId): void {
        this._store.dispatch(TimelineActionCreator.makeLocalInitializationAction(false, timelineId));
    }

    updateLoadingState(isLoading: boolean): void {
        this._store.dispatch(this.timelineActionCreator.makeLoadingAction(isLoading));
    }

    updatePlotBands(point: any, ctrlKey: boolean): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdatePlotBandsAction({point, ctrlKey})); // TODO: Re-factor
    }

    applyInitialOpeningState(): void {
        this._store.dispatch(this.timelineActionCreator.makeApplyInitialOpeningStateAction());
    }

    updateOthersIsInitialized(isInitialized: boolean): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdateInitializedOtherAction(isInitialized));
    }

    updateLocalIsInitialized(isInitialized: boolean): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdateInitializedAction(isInitialized));
    }

    setDayZeroOptions(dayZeroOptions: DayZero[]): any {
        this._store.dispatch(this.timelineActionCreator.makeUpdateDayZeroAction(dayZeroOptions));
    }

    updateSelectedSubjects(selectedSubjects: ISubject[]): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdateSelectedSubjectsAction(selectedSubjects));
    }

    updatePossibleSubjects(possibleSubjects: string[]): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdatePossibleSubjectsAction(possibleSubjects));
    }

    updatePossibleTracks(possibleTracks: ITrack[]): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdatePossibleTracksAction(possibleTracks));
    }

    updatePerformedJumpToTimelineFlag(value: boolean): void {
        this._store.dispatch(this.timelineActionCreator.makeUpdatePerformedJumpToTimelineAction(value));
    }
}
