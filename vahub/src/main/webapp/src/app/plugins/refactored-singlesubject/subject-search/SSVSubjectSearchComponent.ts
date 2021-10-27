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

import {Component} from '@angular/core';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {
    getAvailableSubjectsBySearchString, getSelectedSubjectId, getSingleSubjectViewTabData,
    getSubjectSearchString
} from '../store/reducers/SingleSubjectViewReducer';
import {Observable} from 'rxjs/Observable';
import {List} from 'immutable';
import {
    ClearSubjectSelection, UpdateSelectedSubject,
    UpdateSubjectSearchString
} from '../store/actions/SingleSubjectViewActions';

@Component({
    selector: 'ssv-subject-search',
    templateUrl: 'SSVSubjectSearchComponent.html'
})
export class SSVSubjectSearchComponent {
    selectedSubjectId$: Observable<string>;
    availableSubjects$: Observable<List<string>>;
    subjectSearchString$: Observable<string>;

    constructor(public _store: Store<ApplicationState>) {
        this.selectedSubjectId$ = this._store.select(getSelectedSubjectId);
        this.availableSubjects$ = this._store.select(getAvailableSubjectsBySearchString);
        this.subjectSearchString$ = this._store.select(getSubjectSearchString);
    }

    updateSubjectSearchString(newSearchString: string): void {
        this._store.dispatch(new UpdateSubjectSearchString(newSearchString));
    }

    clearSubjectSelection(): void {
        this._store.dispatch(new ClearSubjectSelection());
    }

    updateSelectedSubject(subject: string): void {
        this._store.dispatch(new UpdateSelectedSubject(subject));
    }
}
