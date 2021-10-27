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

import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {StudyService} from '../common/module';
import {Store} from '@ngrx/store';
import {
    UpdateCombinedStudyInfoAction,
    UpdateLoadingStateAction,
    UpdateSearchStringAction,
    UpdateSelectedDatasetsAction
} from './store/actions/StudySelectionActions';
import {Observable} from 'rxjs/Observable';
import * as fromStudySelection from './store/reducers/StudySelectionReducer';
import {ApplicationState} from '../common/store/models/ApplicationState';

@Component({
    selector: 'study-selection',
    templateUrl: 'StudySelectionComponent.html',
    styleUrls: ['./StudySelectionComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StudySelectionComponent implements OnInit, OnDestroy {
    acls: Observable<any>;

    loading: Observable<boolean>;
    totalNumberOfSubjects: Observable<any>;
    combinedStudyInfo: Observable<any>;
    availableDatasets: Observable<any>;
    selectedDatasets: Observable<any>;
    studyWarnings: Observable<any>;
    datasetIdAndNumSubjectsMap: Observable<any>;
    selectedDrugProgrammeName: Observable<any>;
    searchString: Observable<string>;

    isMultipleCollapseAvailable = false;

    constructor(private router: Router,
                private _store: Store<ApplicationState>,
                private studyService: StudyService) {
        this.selectedDatasets = this._store.select(fromStudySelection.getSelectedDatasets);
        this.selectedDrugProgrammeName = this._store.select(fromStudySelection.getSelectedDrugProgramme);
        this.availableDatasets = this._store.select(fromStudySelection.getDatasets);

        this.loading = this._store.select(fromStudySelection.getLoadingState);

        this.totalNumberOfSubjects = this._store.select(fromStudySelection.getNumberOfSubjects);

        this.studyWarnings = this._store.select(fromStudySelection.getStudyWarnings);

        this.datasetIdAndNumSubjectsMap = this._store.select(fromStudySelection.getStudySelectionMap);

        this.acls = this._store.select(fromStudySelection.getRois);

        this.searchString = this._store.select(fromStudySelection.getSearchString);
    }

    ngOnInit(): void {
        this._store.dispatch(new UpdateLoadingStateAction(true));
        this.studyService.getCombinedStudyInfo()
            .subscribe((res) => {
                this._store.dispatch(new UpdateCombinedStudyInfoAction(res));
            });
    }

    ngOnDestroy(): void {
        this._store.dispatch(new UpdateLoadingStateAction(false));
    }

    openDataset(): void {
        this._store.dispatch(new UpdateLoadingStateAction(true));
        this.router.navigate(['/plugins/population-summary/']);
    }

    onSelectDataset(selectedDataset: Map<string, any>): void {
        this._store.dispatch(new UpdateSelectedDatasetsAction({dataset: selectedDataset}));
    }

    handleUpdateQuery(searchString: string): void {
        this._store.dispatch(new UpdateSearchStringAction(searchString));
    }
}
