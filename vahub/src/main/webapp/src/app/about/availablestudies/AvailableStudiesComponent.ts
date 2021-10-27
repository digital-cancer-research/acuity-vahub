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
import {ApplicationState} from '../../common/store/models/ApplicationState';
import {Observable} from 'rxjs/Observable';
import * as fromAvailableStudies from './store/reducers/AvailableStudiesReducer';
import {
    UpdateAvailableStudiesAction,
    UpdateLoadingStateAction,
    UpdateSearchStringAction,
    UpdateSelectedDatasetsAction
} from './store/actions/AvailableStudiesActions';
import {StudyService} from '../../common/StudyService';
import {Store} from '@ngrx/store';
import Dataset = Request.Dataset;
import {ConfigurationService} from '../../configuration/ConfigurationService';

@Component({
    selector: 'available-studies',
    templateUrl: 'AvailableStudiesComponent.html',
    styleUrls: ['../../studyselection/StudySelectionComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class AvailableStudiesComponent implements OnInit, OnDestroy {
    acls: Observable<any>;

    loading: Observable<boolean>;
    totalNumberOfSubjects: Observable<number>;
    combinedStudyInfo: Observable<any>;
    availableDatasets: Observable<any>;
    selectedDatasets: Observable<any>;
    studyWarnings: Observable<any>;
    datasetIdAndNumSubjectsMap: Observable<any>;
    selectedDrugProgrammeName: Observable<string>;
    searchString: Observable<string>;
    counts: Observable<any>;
    isMultipleCollapseAvailable = true;

    datasetsInfoModal: any;

    requestAccessModal: any;

    constructor(private _store: Store<ApplicationState>,
                private studyService: StudyService,
                private configurationService: ConfigurationService) {
        this.selectedDatasets = this._store.select(fromAvailableStudies.getSelectedDatasets);

        this.availableDatasets = this._store.select(fromAvailableStudies.getDatasets);

        this.loading = this._store.select(fromAvailableStudies.getLoadingState);

        this.totalNumberOfSubjects = this._store.select(fromAvailableStudies.getNumberOfSubjects);

        this.studyWarnings = this._store.select(fromAvailableStudies.getStudyWarnings);

        this.datasetIdAndNumSubjectsMap = this._store.select(fromAvailableStudies.getStudySelectionMap);

        this.acls = this._store.select(fromAvailableStudies.getRois);

        this.searchString = this._store.select(fromAvailableStudies.getSearchString);

        this.counts = this._store.select(fromAvailableStudies.getCounts);

        this.datasetsInfoModal = {
            title: `Datasets loaded in ACUITY currently`,
            body: `This page shows all datasets that are currently loaded in ACUITY,
        organised into Drug Programme and Study groups. One study may be represented by more than one dataset, such
        as 1 ongoing raw dataset plus one Blind Data Review reporting dataset.`,
            buttonText: 'Ok',
            isVisible: true
        };

        this.requestAccessModal = {
            title: `Dataset access request`,
            body: `Thank you for your interest in using ACUITY!

        An email has been sent to you and the ACUITY Support team with the details of your request.
        The ACUITY Support team will be contact you soon to process your request.`,
            buttonText: 'Ok',
            isVisible: false
        };
    }

    ngOnInit(): void {
        this._store.dispatch(new UpdateLoadingStateAction(true));

        this.studyService.getAllStudyInfo()
            .subscribe((res) => {
                this._store.dispatch(new UpdateAvailableStudiesAction(res));
            });

        this.datasetsInfoModal.isVisible = true;
    }

    ngOnDestroy(): void {
        this._store.dispatch(new UpdateLoadingStateAction(false));
    }

    openDataset(): void {
        this._store.dispatch(new UpdateLoadingStateAction(true));

        setTimeout(() => {
            this._store.dispatch(new UpdateLoadingStateAction(false));
        }, 1000);

        this.requestAccessModal.isVisible = true;
    }

    onSelectDataset(selectedDatasets: Dataset[]): void {
        this._store.dispatch(new UpdateSelectedDatasetsAction(selectedDatasets));
    }

    handleUpdateQuery(searchString: string): void {
        this._store.dispatch(new UpdateSearchStringAction(searchString));
    }

    dismissDatasetInfoModal(): void {
        this.datasetsInfoModal.isVisible = false;
    }

    dismissRequestAccessModal(): void {
        this.requestAccessModal.isVisible = false;
    }
}
