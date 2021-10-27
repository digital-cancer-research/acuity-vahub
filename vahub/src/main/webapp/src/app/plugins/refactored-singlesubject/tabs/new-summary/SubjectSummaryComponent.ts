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
