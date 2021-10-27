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

import {Action} from '@ngrx/store';
import {fromJS} from 'immutable';
import 'rxjs/add/operator/take';
import {
    CHANGE_PAGE, LOADING, UPDATE_INITIAL_OPENING_STATE, UPDATE_INITIALIZED,
    UPDATE_PLOT_BANDS, APPLY_INITIAL_OPENING_STATE, CHANGE_DAY_ZERO_OPTIONS, UPDATE_DATA, UPDATE_POSSIBLE_SUBJECTS,
    POSSIBLE_TRACKS, CHANGE_PERFORMED_JUMP_FLAG
} from '../../../../plugins/timeline/store/TimelineAction';
import {
    ITimelinePage, TimelineId, DayZero,
    ISubject, ITrack, InitializedTimeline, PossibleSubjects, PossibleTracks
} from '../../../../plugins/timeline/store/ITimeline';
import {AppStoreUtils} from '../AppStoreUtils';
import {ActionWithPayload} from '../../../../common/trellising/store/actions/TrellisingActionCreator';

export class TimelineActionCreator {

    static makeInitializationAction(isInitialized: boolean): ActionWithPayload<{isInitialized: boolean}> {
        return {
            type: UPDATE_INITIALIZED,
            payload: {
                isInitialized
            }
        };
    }

    static makeLocalInitializationAction(isInitialized: boolean, timelineId: TimelineId): ActionWithPayload<InitializedTimeline> {
        return {
            type: UPDATE_INITIALIZED,
            payload: {
                timelineId,
                isInitialized
            }
        };
    }

    constructor(private timelineId: TimelineId) {
    }

    makeLoadingAction(loading: boolean): ActionWithPayload<{timelineId: TimelineId, loading: boolean}> {
        return {
            type: LOADING,
            payload: {
                timelineId: this.timelineId,
                loading
            }
        };
    }

    makeChangePageAction(page: ITimelinePage): ActionWithPayload<{timelineId: TimelineId, page: ITimelinePage}> {
        return {
            type: CHANGE_PAGE,
            payload: {
                timelineId: this.timelineId,
                page
            }
        };
    }

    makeUpdatePlotBandsAction(eventData: any): ActionWithPayload<{timelineId: TimelineId, eventData: any}> {
        return {
            type: UPDATE_PLOT_BANDS,
            payload: {
                timelineId: this.timelineId,
                eventData
            }
        };
    }

    makeApplyInitialOpeningStateAction(): ActionWithPayload<{timelineId: TimelineId}> {
        return {
            type: APPLY_INITIAL_OPENING_STATE,
            payload: {
                timelineId: this.timelineId
            }
        };
    }

    makeUpdateInitializedOtherAction(isInitialized: boolean): ActionWithPayload<InitializedTimeline> {
        return {
            type: UPDATE_INITIALIZED,
            payload: {
                timelineId: AppStoreUtils.getOtherTimelineId(this.timelineId),
                isInitialized
            }
        };
    }

    makeUpdateInitializedAction(isInitialized: boolean): ActionWithPayload<InitializedTimeline> {
        return {
            type: UPDATE_INITIALIZED,
            payload: {
                timelineId: this.timelineId,
                isInitialized
            }
        };
    }

    makeUpdateDayZeroAction(dayZeroOptions: DayZero[]): ActionWithPayload<{timelineId: TimelineId, dayZeroOptions: DayZero[]}> {
        return {
            type: CHANGE_DAY_ZERO_OPTIONS,
            payload: {timelineId: this.timelineId, dayZeroOptions: fromJS(dayZeroOptions)}
        };
    }

    makeUpdateSelectedSubjectsAction(selectedSubjects: ISubject[]): ActionWithPayload<{timelineId: TimelineId, subjects: ISubject[]}> {
        return {
            type: UPDATE_DATA,
            payload: {timelineId: this.timelineId, subjects: selectedSubjects}
        };
    }

    makeUpdatePossibleSubjectsAction(possibleSubjects: string[]): ActionWithPayload<PossibleSubjects> {
        return {
            type: UPDATE_POSSIBLE_SUBJECTS,
            payload: {timelineId: this.timelineId, subjects: possibleSubjects}
        };
    }

    makeUpdatePossibleTracksAction(possibleTracks: ITrack[]): ActionWithPayload<PossibleTracks> {
        return {
            type: POSSIBLE_TRACKS, payload: {timelineId: this.timelineId, tracks: possibleTracks}
        };
    }

    makeUpdatePerformedJumpToTimelineAction(performedJumpToTimeline: boolean)
    : ActionWithPayload<{timelineId: TimelineId, performedJumpToTimeline: boolean}> {
        return {
            type: CHANGE_PERFORMED_JUMP_FLAG,
            payload: {
                timelineId: this.timelineId,
                performedJumpToTimeline
            }
        };
    }

}
