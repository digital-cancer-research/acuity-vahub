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
import {TabId} from '../../../../common/trellising/store';

export const ActionTypes = {
    UPDATE_SELECTED_SUBJECT: 'UPDATE_SELECTED_SUBJECT',
    UPDATE_SUBJECT_SEARCH_STRING: 'UPDATE_SUBJECT_SEARCH_STRING',
    CLEAR_SUBJECT_SELECTION: 'CLEAR_SUBJECT_SELECTION',
    UPDATE_OPENED_TAB_DATA: 'UPDATE_OPENED_TAB_DATA',
    LOAD_DATA: 'LOAD_DATA',
    LOAD_DATA_SUCCESS: 'LOAD_DATA_SUCCESS',
    UPDATE_SUMMARY_TAB_METADATA: 'UPDATE_SUMMARY_TAB_METADATA',
    UPDATE_SUMMARY_TAB_DATA: 'UPDATE_SUMMARY_TAB_DATA',
    UPDATE_SUBJECT_SUMMARY_TABLES_DATA: 'UPDATE_SUBJECT_SUMMARY_TABLES_DATA',
    DOWNLOAD_TABLES: 'DOWNLOAD_TABLES',
    UPDATE_TABLE_CONFIG: 'UPDATE_TABLE_CONFIG',
    UPDATE_TABLES_HEADER: 'UPDATE_TABLES_HEADER'
};

export class UpdateSelectedSubject implements Action {
    readonly type = ActionTypes.UPDATE_SELECTED_SUBJECT;
    constructor(public payload: string) {}
}

export class UpdateSubjectSearchString implements Action {
    readonly type = ActionTypes.UPDATE_SUBJECT_SEARCH_STRING;
    constructor(public payload: string) {}
}


export class ClearSubjectSelection implements Action {
    readonly type = ActionTypes.CLEAR_SUBJECT_SELECTION;
    constructor() {}
}

export class UpdateOpenedTabData implements Action {
    readonly type = ActionTypes.UPDATE_OPENED_TAB_DATA;
    constructor(public payload: {tabId: TabId, tableData: any}) {}
}

export class LoadSingleSubjectTableData implements Action {
    readonly type = ActionTypes.LOAD_DATA;
    constructor() {}
}

export class LoadSingleSubjectTableDataSuccess implements Action {
    readonly type = ActionTypes.LOAD_DATA_SUCCESS;
    constructor() {}
}

export class UpdateSummaryTabMetadata implements Action {
    readonly type = ActionTypes.UPDATE_SUMMARY_TAB_METADATA;
    constructor(public payload: any) {}
}

export class UpdateSummaryTabData implements Action {
    readonly type = ActionTypes.UPDATE_SUMMARY_TAB_DATA;
    constructor(public payload: any) {}
}

export class UpdateSummaryTablesData implements Action {
    readonly type = ActionTypes.UPDATE_SUBJECT_SUMMARY_TABLES_DATA;
    constructor(public payload: any) {}
}

export class DownloadTables implements Action {
    readonly type = ActionTypes.DOWNLOAD_TABLES;
    constructor(public payload: any) {}
}

export class UpdateTableConfig implements Action {
    readonly type = ActionTypes.UPDATE_TABLE_CONFIG;

    constructor(public payload: any) {
    }
}

export class UpdateSummaryTablesHeaderData implements Action {
    readonly type = ActionTypes.UPDATE_TABLES_HEADER;

    constructor(public payload: any) {
    }
}
