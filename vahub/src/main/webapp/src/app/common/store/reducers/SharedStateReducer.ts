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

import {ApplicationState} from '../models/ApplicationState';
import {TabId} from '../../trellising/store/ITrellising';
import {Map, List} from 'immutable';
import {createSelector} from 'reselect';
import {ActionTypes, UpdateActiveTabId, UpdateAvailableSubjects} from '../actions/SharedStateActions';
import {ActionWithPayload} from '../../trellising/store/actions/TrellisingActionCreator';

export interface SharedState extends Map<string, any> {
    activeTabId: TabId;
    //subjects from population filters model
    availableSubjects: List<string>;
}

const initialState = <SharedState>Map({
    activeTabId: null,
    availableSubjects: List<string>()
});

export function sharedStateReducer(state: SharedState = initialState, action: ActionWithPayload<any>): Map<string, any> {
    switch (action.type) {
        case ActionTypes.UPDATE_ACTIVE_TAB_ID:
            return updateActiveTabId(state, action);
        case ActionTypes.UPDATE_AVAILABLE_SUBJECTS:
            return updateAvailableSubjects(state, action);
        default:
            return state;
    }


    function updateActiveTabId(state1: SharedState, action1: UpdateActiveTabId): Map<string, any> {
        return state1.set('activeTabId', action1.payload);
    }

    function updateAvailableSubjects(state1: SharedState, action1: UpdateAvailableSubjects): Map<string, any> {
        return state1.set('availableSubjects', action1.payload);
    }
}

export const getSharedState = (state: ApplicationState): SharedState => {
    return state.sharedStateReducer;
};

export const getTabId = createSelector(getSharedState, (sharedState: SharedState) => sharedState.get('activeTabId'));

export const getAvailableSubjects = createSelector(getSharedState, (sharedState: SharedState)
    : List<string> => sharedState.get('availableSubjects'));
