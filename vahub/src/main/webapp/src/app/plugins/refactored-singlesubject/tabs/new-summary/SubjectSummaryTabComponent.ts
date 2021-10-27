import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {TabId} from '../../../../common/trellising/store/ITrellising';
import {UpdateActiveTabId} from '../../../../common/store/actions/SharedStateActions';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {
    getIsSingleSubjectViewDataLoading, getSelectedSubjectId, getSummaryTabData,
    getSummaryTablesData, getSummaryTablesHeader
} from '../../store/reducers/SingleSubjectViewReducer';

@Component({
    template: `
        <subject-summary-component [selectedSubject]="selectedSubjectId$ | async"
                                   [sections]="sections$ | async"
                                   [loading]="isLoading$ | async"
                                   [header]="header$ | async">
        </subject-summary-component>`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubjectSummaryTabComponent implements OnInit {

    isLoading$: Observable<boolean>;
    selectedSubjectId$: Observable<string>;
    sections$: Observable<any>;
    header$: Observable<any>;

    constructor(public _store: Store<ApplicationState>) {
        this.sections$ = this._store.select(getSummaryTablesData);
        this.isLoading$ = this._store.select(getIsSingleSubjectViewDataLoading);
        this.selectedSubjectId$ = this._store.select(getSelectedSubjectId);
        this.header$ = this._store.select(getSummaryTablesHeader);
    }

    ngOnInit(): void {
        this._store.dispatch(new UpdateActiveTabId(TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB));
    }
}
