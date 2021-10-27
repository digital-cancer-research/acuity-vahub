import {Action} from '@ngrx/store';
export const ActionTypes = {
    GET_AVAILABLE_STUDIES_LIST: 'GET_AVAILABLE_STUDIES_LIST',
    UPDATE_AVAILABLE_STUDIES_LIST: 'UPDATE_AVAILABLE_STUDIES_LIST',
    UPDATE_LOADING_STATE: 'UPDATE_LOADING_STATE',
    UPDATE_SELECTED_DATASETS_ACTION: 'UPDATE_SELECTED_DATASETS_ACTION',
    UPDATE_SEARCH_STRING_ACTION: 'UPDATE_SEARCH_STRING_ACTION'
};

export class GetAvailableStudiesAction implements Action {
    readonly type = ActionTypes.GET_AVAILABLE_STUDIES_LIST;
    constructor() {}
}

export class UpdateAvailableStudiesAction implements Action {
    readonly type = ActionTypes.UPDATE_AVAILABLE_STUDIES_LIST;
    constructor(public payload: any) {}
}

export class UpdateLoadingStateAction implements Action {
    readonly type = ActionTypes.UPDATE_LOADING_STATE;
    constructor(public payload: any) {}
}

export class UpdateSelectedDatasetsAction implements Action {
    readonly type = ActionTypes.UPDATE_SELECTED_DATASETS_ACTION;
    constructor(public payload: any) {}
}

export class UpdateSearchStringAction implements Action {
    readonly type = ActionTypes.UPDATE_SEARCH_STRING_ACTION;
    constructor(public payload: any) {}
}
