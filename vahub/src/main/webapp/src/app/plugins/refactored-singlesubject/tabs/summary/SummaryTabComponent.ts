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

import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {TabId} from '../../../../common/trellising/store/ITrellising';
import {UpdateActiveTabId} from '../../../../common/store/actions/SharedStateActions';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {
    getIsSingleSubjectViewDataLoading, getSelectedSubjectId, getSummaryTabData,
    getSummaryTabMetadata
} from '../../store/reducers/SingleSubjectViewReducer';

@Component({
    template: `
        <summary-component [subjectDetailMetadata]="metadata$ | async"
                           [subjectDetail]="details$ | async"
                           [selectedSubject]="selectedSubjectId$ | async"
                           [loading]="isLoading$ | async">
        </summary-component>`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SummaryTabComponent implements OnInit {

    metadata$: Observable<any>;
    details$: Observable<any>;
    isLoading$: Observable<boolean>;
    selectedSubjectId$: Observable<string>;

    constructor(public _store: Store<ApplicationState>) {
        this.metadata$ = this._store.select(getSummaryTabMetadata);
        this.details$ = this._store.select(getSummaryTabData);
        this.isLoading$ = this._store.select(getIsSingleSubjectViewDataLoading);
        this.selectedSubjectId$ = this._store.select(getSelectedSubjectId);
    }

    ngOnInit(): void {
        this._store.dispatch(new UpdateActiveTabId(TabId.SINGLE_SUBJECT_SUMMARY_TAB));
    }
}
