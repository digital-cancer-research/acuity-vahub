import {Action} from '@ngrx/store';

export const ActionTypes = {
    GET_COMBINED_STUDY_INFO: 'GET_COMBINED_STUDY_INFO',
    DOWNLOAD_DETAILS_ON_DEMAND: 'DOWNLOAD_DETAILS_ON_DEMAND',
    UPDATE_COMBINED_STUDY_INFO: 'UPDATE_COMBINED_STUDY_INFO',
    UPDATE_SELECTED_DATASETS: 'UPDATE_SELECTED_DATASETS',
    UPDATE_LOADING_STATE: 'UPDATE_LOADING_STATE',
    UPDATE_SEARCH_STRING: 'UPDATE_SEARCH_STRING'
};

export class GetCombinedStudyInfoAction implements Action {
    readonly type = ActionTypes.GET_COMBINED_STUDY_INFO;
    constructor() {}
}

export class UpdateCombinedStudyInfoAction implements Action {
    readonly type = ActionTypes.UPDATE_COMBINED_STUDY_INFO;
    constructor(public payload: any) {}
}

export class UpdateSelectedDatasetsAction implements Action {
    readonly type = ActionTypes.UPDATE_SELECTED_DATASETS;
    constructor(public payload: any) {}
}

export class UpdateLoadingStateAction implements Action {
    readonly type = ActionTypes.UPDATE_LOADING_STATE;
    constructor(public payload: any) {}
}

export class UpdateSearchStringAction implements Action {
    readonly type = ActionTypes.UPDATE_SEARCH_STRING;
    constructor(public payload: any) {}
}

export class DownloadDetailsOnDemandAction implements Action {
    readonly type = ActionTypes.DOWNLOAD_DETAILS_ON_DEMAND;
    constructor(public payload: any) {}
}
