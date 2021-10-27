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
