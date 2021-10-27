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

import {Component, Output, OnInit, EventEmitter, Input} from '@angular/core';
import * as  _ from 'lodash';

import {CohortEditorService} from '../services/CohortEditorService';
import {CohortFiltersModel} from '../../../filters/module';
import SavedFilterVO = Request.SavedFilterVO;

@Component({
    selector: 'available-cohorts',
    templateUrl: 'AvailableCohortsComponent.html',
    styleUrls: ['CohortEditorComponent.css']
})
export class AvailableCohortsComponent implements OnInit {

    @Input()
    loading: boolean;

    @Output()
    hidePopulationFilters = new EventEmitter<void>();

    @Output()
    hideEventFilters = new EventEmitter<void>();

    @Output()
    loading$ = new EventEmitter<boolean>();

    @Output()
    cohortSelected = new EventEmitter<SavedFilterVO>();

    @Output()
    resetFilters = new EventEmitter<void>();

    @Output()
    saveOriginalFilterState = new EventEmitter<void>();

    @Output()
    resetSharedWith = new EventEmitter<void>();

    searchText: SavedFilterVO;
    availableCohorts: SavedFilterVO[];
    selectedCohortId: number;
    newCohortName: string;
    renamedCohortName: string;
    addModalIsVisible: boolean;
    renameModalIsVisible: boolean;
    oldCohortName: string;

    readonly ID_FOR_NEW_COHORTS = -1;

    constructor(
        private cohortEditorService: CohortEditorService,
        private cohortFiltersModel: CohortFiltersModel) {
    }

    ngOnInit(): void {
        this.loading$.emit(true);
        this.cohortEditorService.getCohorts().subscribe((savedFilterVo: SavedFilterVO[]) => {
            this.availableCohorts = _.reject(savedFilterVo, {savedFilter: {name: null }});
            this.loading$.emit(false);
        });
        this.cohortEditorService.updateNumberOfSubjectsInSelectedFilters();
        this.hidePopulationFilters.emit();
    }

    selectCohort(cohortId: number): void {
        if (cohortId === this.ID_FOR_NEW_COHORTS) {
            return;
        }

        if (this.selectedCohortId === this.ID_FOR_NEW_COHORTS) {
            const ok = window.confirm('You will lose unsaved changes. Do you want to continue?');
            if (ok) {
                this.removeUnsavedChanges();
            } else {
                return;
            }
        }

        this.selectedCohortId = cohortId;
        this.hidePopulationFilters.emit();
        this.hideEventFilters.emit();
        this.resetFilters.emit();
        this.cohortSelected.emit(this.getSelectedCohort());
    }

    removeUnsavedChanges(): void {
        _.remove(this.availableCohorts, {savedFilter: {id: this.ID_FOR_NEW_COHORTS}});
        this.newCohortName = '';
    }

    showAddModal(): void {
        this.newCohortName = '';
        this.addModalIsVisible = true;
    }

    showRenameModal(): void {
        this.renamedCohortName = '';
        this.renameModalIsVisible = true;
    }

    addModalSubmitted(submitPressed: boolean): void {
        if (!submitPressed) {
            this.newCohortName = '';
            this.hideModal();
        } else {
            const errorMessage = this.getCohortNameMessage(this.newCohortName);

            if (errorMessage.length) {
                window.alert(errorMessage);
            } else {
                this.availableCohorts.unshift(<SavedFilterVO>{
                    savedFilter: {
                        name: this.newCohortName,
                        id: this.ID_FOR_NEW_COHORTS
                    }
                });
                this.hideModal();
                this.selectedCohortId = this.ID_FOR_NEW_COHORTS;
                this.resetSharedWith.emit();
                this.resetFilters.emit();
            }
        }
    }

    renameModalSubmitted(submitPressed: boolean): void {
        if (submitPressed) {
            const errorMessage = this.getCohortNameMessage(this.renamedCohortName);

            if (errorMessage.length) {
                window.alert(errorMessage);
            } else {
                this.oldCohortName = this.getSelectedCohort().savedFilter.name;
                this.getSelectedCohort().savedFilter.name = this.renamedCohortName;
                if (this.getSelectedCohort().savedFilter.id === this.ID_FOR_NEW_COHORTS) {
                    this.newCohortName = this.renamedCohortName;
                }

                this.renameModalIsVisible = false;
            }
        } else {
            this.hideModal();
        }
    }

    getSelectedCohort(): SavedFilterVO {
        return _.find(this.availableCohorts, (cohort: SavedFilterVO) => cohort.savedFilter.id === this.selectedCohortId);
    }

    deleteCohort(): void {
        if (this.hasCohortThatIsNotSaved()) {
            _.remove(this.availableCohorts, {savedFilter: {name: this.newCohortName}});
            this.newCohortName = '';
            this.selectedCohortId = undefined;
            this.resetFilters.emit();
        } else {
            this.loading$.emit(true);
            this.cohortEditorService.deleteCohort(this.selectedCohortId, this.getSelectedCohort().savedFilter.name)
                .subscribe((savedFilterVo: SavedFilterVO[]) => {
                    this.resetFilters.emit();
                    this.addSavedCohortsToList(savedFilterVo);
                    this.selectedCohortId = undefined;
                    this.hidePopulationFilters.emit();
                    this.hideEventFilters.emit();
                    this.loading$.emit(false);
                });
        }
    }

    hasCohortThatIsNotSaved(): boolean {
        return this.selectedCohortId === this.ID_FOR_NEW_COHORTS;
    }

    addSavedCohortsToList(savedFilters: SavedFilterVO[], appliedCohortName: string = ''): void {
        this.availableCohorts = savedFilters;
        this.loading$.emit(false);
        this.newCohortName = '';
        if (this.selectedCohortId === this.ID_FOR_NEW_COHORTS) {
            this.selectedCohortId = savedFilters[0].savedFilter.id;
        }
        this.cohortFiltersModel.setItems(this.availableCohorts);
        this.saveOriginalFilterState.emit();
    }

    private hideModal(): void {
        this.renameModalIsVisible = false;
        this.addModalIsVisible = false;
    }

    private hasCohortWithSameName(newCohortName?: string): boolean {
        return _.some(this.availableCohorts, (cohort) => cohort.savedFilter.name === newCohortName);
    }

    private getCohortNameMessage(newCohortName?: string): string {
        if (_.isEmpty(newCohortName)) {
            return 'You must give the cohort a name';
        } else if (this.hasCohortWithSameName(newCohortName)) {
            return 'There as already a cohort with this name. Please choose a different name.';
        }
        return '';
    }
}
