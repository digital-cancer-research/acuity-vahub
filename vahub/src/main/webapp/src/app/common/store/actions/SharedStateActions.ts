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
import {TabId} from '../../trellising/store/ITrellising';
import {List} from 'immutable';


export const ActionTypes = {
    UPDATE_ACTIVE_TAB_ID: 'UPDATE_ACTIVE_TAB_ID',
    UPDATE_AVAILABLE_SUBJECTS: 'UPDATE_AVAILABLE_SUBJECTS',
    NEW_EVENT_FILTERS_WERE_APPLIED: 'NEW_EVENT_FILTERS_WERE_APPLIED'
};

export class UpdateActiveTabId implements Action {
    readonly type = ActionTypes.UPDATE_ACTIVE_TAB_ID;
    constructor(public payload: TabId) {}
}

export class UpdateAvailableSubjects implements Action {
    readonly type = ActionTypes.UPDATE_AVAILABLE_SUBJECTS;
    constructor(public payload: List<string>) {}
}

export class NewEventFiltersWereApplied implements Action {
    readonly type = ActionTypes.NEW_EVENT_FILTERS_WERE_APPLIED;
    constructor() {}
}
