import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {DatasetViews} from '../../../../security/DatasetViews';
import {DownloadTables} from '../../store/actions/SingleSubjectViewActions';
import {Store} from '@ngrx/store';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';

@Component({
    templateUrl: 'SubjectSummaryComponent.html',
    styleUrls: ['./SubjectSummaryComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'subject-summary-component'
})
export class SubjectSummaryComponent {
    @Input() loading: boolean;
    @Input() sections: any;
    @Input() columnDefs: any;
    @Input() selectedSubject: string;
    @Input() header: any;

    constructor(public _store: Store<ApplicationState>, private datasetViews: DatasetViews) {
    }

    onDownloadTables(): void {
        this._store.dispatch(new DownloadTables({}));
    }
}
