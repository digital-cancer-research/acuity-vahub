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
