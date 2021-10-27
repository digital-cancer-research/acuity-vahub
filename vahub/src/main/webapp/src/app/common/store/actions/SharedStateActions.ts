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
