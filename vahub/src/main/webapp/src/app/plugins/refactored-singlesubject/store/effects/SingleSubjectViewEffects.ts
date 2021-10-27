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
import {Actions, Effect} from '@ngrx/effects';
import {List, Map} from 'immutable';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {combineLatest} from 'rxjs/observable/combineLatest';
import {includes} from 'lodash';


import {
    ActionTypes,
    ClearSubjectSelection,
    LoadSingleSubjectTableData,
    LoadSingleSubjectTableDataSuccess,
    UpdateOpenedTabData,
    UpdateSelectedSubject,
    UpdateSummaryTabData,
    UpdateSummaryTablesData,
    UpdateSummaryTablesHeaderData,
    UpdateSummaryTabMetadata
} from '../actions/SingleSubjectViewActions';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {getTabId} from '../../../../common/store/reducers/SharedStateReducer';
import {SingleSubjectViewServiceFactory} from '../../http/SingleSubjectViewServiceFactory';
import {
    getSelectedSubjectId,
    getSingleSubjectViewHasTable,
    isSelectedTabPartOfSSV,
    SingleSubjectSummaryTablesUrls
} from '../reducers/SingleSubjectViewReducer';
import {TabId} from '../../../../common/trellising/store';
import * as SharedActions from '../../../../common/store/actions/SharedStateActions';
import {SingleSubjectViewSummaryHttpService} from '../../http/SingleSubjectViewSummaryHttpService';
import {downloadDoc, parseNumericalFields} from '../../../../common/utils/Utils';

const TABS_WITH_NEW_DOD_RESPONSE: TabId[] = [

    TabId.SINGLE_SUBJECT_ALCOHOL_TAB,
    TabId.SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB,
    TabId.SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB,
    TabId.SINGLE_SUBJECT_NICOTINE_TAB,
    TabId.SINGLE_SUBJECT_MEDICAL_HISTORY_TAB,
    TabId.SINGLE_SUBJECT_DEATH_TAB,
    TabId.SINGLE_SUBJECT_DOSE_TAB,
    TabId.SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB,
    TabId.SINGLE_SUBJECT_SAE_TAB,
    TabId.SINGLE_SUBJECT_SURGICAL_HISTORY_TAB,
    TabId.SINGLE_SUBJECT_LUNG_LINEPLOT,
    TabId.SINGLE_SUBJECT_CONMEDS_TAB,
    TabId.SINGLE_SUBJECT_SUMMARY_TAB,
    TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT,
    TabId.SINGLE_SUBJECT_RENAL_LINEPLOT,
    TabId.SINGLE_SUBJECT_VITALS_LINEPLOT,
    TabId.SINGLE_SUBJECT_EXACERBATIONS_TAB
];

@Injectable()
export class SingleSubjectViewEffects {

    /**
     * triggered after {@link UpdateSelectedSubject} action was dispatched
     * this effect does http request for single subject table data,
     * dispatches action {@link UpdateOpenedTabData} which updates table data for opened tab
     *
     */
    @Effect()
    getTabData: Observable<any> = this.actions$
        .ofType(ActionTypes.UPDATE_SELECTED_SUBJECT, SharedActions.ActionTypes.NEW_EVENT_FILTERS_WERE_APPLIED)
        .withLatestFrom(
            this._store.select(getSelectedSubjectId),
            this._store.select(getTabId),
            this._store.select(getSingleSubjectViewHasTable),
            this._store.select(isSelectedTabPartOfSSV)
        )
        .filter(([action, selectedSubjectId, tabId, hasTable, isPartOfSSV]) => {
            return hasTable && hasTable && selectedSubjectId && isPartOfSSV;
        })
        .switchMap(([action, selectedSubjectId, tabId, hasTable, isPartOfSSV]) => {
            this._store.dispatch(new LoadSingleSubjectTableData());
            return this.serviceFactory.getSingleSubjectViewHttpService(tabId).getTableData(selectedSubjectId, tabId)
                .map(response => {
                    this._store.dispatch(new LoadSingleSubjectTableDataSuccess());
                    const tableData = List(parseNumericalFields(includes(TABS_WITH_NEW_DOD_RESPONSE, tabId)
                        ? response['dodData']
                        : response));
                    return new UpdateOpenedTabData({
                        tableData: tableData,
                        tabId: tabId
                    });
                });
        });

    /**
     *
     * triggered after {@link UpdateAvailableSubjects} to check whether selected subject exist in updated
     * subjects {@link SharedState.availableSubjects} list.
     */
    @Effect()
    afterAvailableSubjectsUpdated: Observable<any> = this.actions$
        .ofType(SharedActions.ActionTypes.UPDATE_AVAILABLE_SUBJECTS)
        .map((action: any) => action.payload)
        .withLatestFrom(this._store.select(getSelectedSubjectId))
        .filter(([subjects, selectedSubjectId]) => {
            if (selectedSubjectId === '') {
                return false;
            }
            return !subjects.find(subjectId => subjectId === selectedSubjectId);
        })
        .map(() => new ClearSubjectSelection());

    @Effect()
    getSummaryTabMetadataData: Observable<any> = this.actions$
        .ofType(SharedActions.ActionTypes.UPDATE_ACTIVE_TAB_ID)
        .withLatestFrom(
            this._store.select(getTabId)
        )
        .filter(([action, tabId]) => {
            return tabId === TabId.SINGLE_SUBJECT_SUMMARY_TAB;
        })
        .switchMap(([action, tabId]) => {
            return this.singleSubjectViewSummaryHttpService.getSubjectDetailMetadata()
                .map((response) => {
                    return new UpdateSummaryTabMetadata(Map(response));
                });
        });

    @Effect()
    getSummaryTabData: Observable<any> = this.actions$
        .ofType(ActionTypes.UPDATE_SELECTED_SUBJECT, SharedActions.ActionTypes.UPDATE_ACTIVE_TAB_ID)
        .withLatestFrom(
            this._store.select(getSelectedSubjectId),
            this._store.select(getTabId)
        )
        .filter(([action, selectedSubjectId, tabId]) => {
            return selectedSubjectId && tabId === TabId.SINGLE_SUBJECT_SUMMARY_TAB;
        })
        .switchMap(([action, selectedSubjectId]) => {
            this._store.dispatch(new LoadSingleSubjectTableData());
            return this.singleSubjectViewSummaryHttpService.getSubjectDetail(selectedSubjectId)
                .map((response) => {
                    this._store.dispatch(new LoadSingleSubjectTableDataSuccess());
                    return new UpdateSummaryTabData(Map(response));
                });
        });

    @Effect()
    getSummaryTablesHeaderData: Observable<any> = this.actions$
        .ofType(ActionTypes.UPDATE_SELECTED_SUBJECT, SharedActions.ActionTypes.UPDATE_ACTIVE_TAB_ID)
        .withLatestFrom(
            this._store.select(getSelectedSubjectId),
            this._store.select(getTabId)
        )
        .filter(([action, selectedSubjectId, tabId]) => {
            return selectedSubjectId && tabId === TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB;
        })
        .switchMap(([action, selectedSubjectId]) => {
            this._store.dispatch(new LoadSingleSubjectTableData());
            return this.singleSubjectViewSummaryHttpService.getSummaryTablesHeader(selectedSubjectId)
                .map((response) => {
                    this._store.dispatch(new LoadSingleSubjectTableDataSuccess());
                    return new UpdateSummaryTablesHeaderData(response);
                });
        });

    @Effect()
    getSummaryTablesData: Observable<any> = this.actions$
        .ofType(ActionTypes.UPDATE_SELECTED_SUBJECT, SharedActions.ActionTypes.UPDATE_ACTIVE_TAB_ID)
        .withLatestFrom(
            this._store.select(getSelectedSubjectId),
            this._store.select(getTabId)
        )
        .filter(([action, selectedSubjectId, tabId]) => {
            return selectedSubjectId && tabId === TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB;
        })
        .do(action => this._store.dispatch(new LoadSingleSubjectTableData()))
        .mergeMap(([action, selectedSubjectId]) => {
            return this.singleSubjectViewSummaryHttpService.getSummaryTablesMetadata(selectedSubjectId)
                .map((metadata) => {
                    const tablesData = [];
                    const tablesMetadata = [];
                    const singleSubjectSummaryTablesUrls = new SingleSubjectSummaryTablesUrls();
                    metadata.forEach((tableMetadata) => {
                        // if there is no tumour in table, then this field is null
                        // if there is no access, we don't show this table
                        if (tableMetadata.hasTumourAccess !== false) {
                            const url = singleSubjectSummaryTablesUrls.get(tableMetadata.name);
                            tablesData.push(this.singleSubjectViewSummaryHttpService.getSubjectSummaryTable(selectedSubjectId, url));
                            tablesMetadata.push(tableMetadata);
                        }
                    });
                    return {tablesData, tablesMetadata};
                });

        })
        .mergeMap(({tablesData, tablesMetadata}) => {
            return combineLatest(...tablesData)
                .map((commonResponse) => {
                    let response = Map();
                    commonResponse.forEach((tableResponse, index) => {
                        const tableMetadata = tablesMetadata[index];
                        response = response.setIn([tableMetadata.headerName, tableMetadata.subheaderName, tableMetadata.displayName], {
                            tableData: tableResponse,
                            columnDefs: tableMetadata.columns
                        });
                    });
                    this._store.dispatch(new LoadSingleSubjectTableDataSuccess());
                    return new UpdateSummaryTablesData(response);
                });
        });

    @Effect()
    downloadTables: Observable<any> = this.actions$
        .ofType(ActionTypes.DOWNLOAD_TABLES)
        .withLatestFrom(
            this._store.select(getSelectedSubjectId),
            this._store.select(getTabId)
        )
        .filter(([action, selectedSubjectId, tabId]) => {
            return selectedSubjectId && tabId === TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB;
        })
        .switchMap(([action, selectedSubjectId]) => {
            return this.singleSubjectViewSummaryHttpService.downloadSummaryTablesDoc(selectedSubjectId)
                .map((response) => {
                    downloadDoc(`Subject_${selectedSubjectId}`, response);
                    return response;
                });
        });

    constructor(private actions$: Actions,
                private _store: Store<ApplicationState>,
                private singleSubjectViewSummaryHttpService: SingleSubjectViewSummaryHttpService,
                private serviceFactory: SingleSubjectViewServiceFactory) {
    }
}
