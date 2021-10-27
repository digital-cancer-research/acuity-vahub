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
