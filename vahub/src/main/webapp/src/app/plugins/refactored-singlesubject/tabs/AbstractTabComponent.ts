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

import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {List} from 'immutable';
import {OnInit} from '@angular/core';

import {
    getFilteredDetailsOnDemandColumnsForTab,
    getIsSingleSubjectViewDataLoading, getSelectedSubjectId, getSingleSubjectTableConfig, getSingleSubjectViewHasTable,
    getSingleSubjectViewHasTrellising, getSingleSubjectViewTabData
} from '../store/reducers/SingleSubjectViewReducer';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {TabId} from '../../../common/trellising';
import {getTabId} from '../../../common/store/reducers/SharedStateReducer';


export const TAB_COMPONENT_TEMPLATE = `
        <simple-loading [loading] = "isLoading$ | async"></simple-loading>
        <div class="notifications" *ngIf="!(selectedSubjectId$ | async)">
            No subject is currently selected
        </div>
        <trellising-component *ngIf="(hasTrellising$ | async) && (selectedSubjectId$ | async) && !(isLoading$ | async)"
                                [tabId]="tabId$ | async"
        ></trellising-component>
        <div *ngIf="(hasTable$ | async) && (selectedSubjectId$ | async) && !(isLoading$ | async)" style="display: block">
            <single-subject-view-table [tabTableData]="tabData$ | async"
                                       [columnDefs]="columnDefs$ | async"
                                       [tableConfig]="tableConfig$ | async"
                                       [tabId]="tabId$ | async"
            >
            </single-subject-view-table>
        </div>`;

export abstract class AbstractTabComponent implements OnInit {
    tabData$: Observable<List<any>>;
    columnDefs$: Observable<List<any>>;
    isLoading$: Observable<boolean>;
    selectedSubjectId$: Observable<string>;
    hasTrellising$: Observable<boolean>;
    hasTable$: Observable<boolean>;
    tabId$: Observable<TabId>;
    tableConfig$: Observable<any>;

    constructor(public _store: Store<ApplicationState>) {
        this.tabData$ = this._store.select(getSingleSubjectViewTabData);
        this.columnDefs$ = this._store.select(getFilteredDetailsOnDemandColumnsForTab);
        this.isLoading$ = this._store.select(getIsSingleSubjectViewDataLoading);
        this.selectedSubjectId$ = this._store.select(getSelectedSubjectId);
        this.hasTrellising$ = this._store.select(getSingleSubjectViewHasTrellising);
        this.hasTable$ = this._store.select(getSingleSubjectViewHasTable);
        this.tabId$ = this._store.select(getTabId);
        this.tableConfig$ = this._store.select(getSingleSubjectTableConfig);
    }

    abstract ngOnInit(): void;
}
