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

import {Component, OnDestroy, ViewChild} from '@angular/core';
import * as  _ from 'lodash';

import {AvailableCohortsComponent} from './AvailableCohortsComponent';
import {FilterListComponent} from './FilterListComponent';
import {AbstractPluginComponent} from '../../AbstractPluginComponent';
import {CohortEditorService} from '../services/CohortEditorService';
import {
    PopulationFiltersModel,
    AesFiltersModel,
    CohortFiltersModel,
    AbstractFiltersModel,
    SelectedFiltersModel
} from '../../../filters/module';
import {FiltersUtils} from '../../../filters/utils/FiltersUtils';
import {FilterReloadService} from '../services/FilterReloadService';
import {FiltersExportService} from '../../../filters/FiltersExportService';
import {SaveCohortDto} from '../dto/SaveCohortDto';
import UserVO = Request.UserVO;
import SavedFilterVO = Request.SavedFilterVO;
import SavedFilterInstance = Request.SavedFilterInstance;

@Component({
    templateUrl: 'CohortEditorComponent.html',
    styleUrls: ['CohortEditorComponent.css', '../../../filters/filters.css'],
    providers: [FiltersUtils, PopulationFiltersModel, AesFiltersModel, SelectedFiltersModel, FiltersExportService]
})
export class CohortEditorComponent extends AbstractPluginComponent implements OnDestroy {

    addFilterOpen: boolean;

    showPopulationFilters: boolean;
    showAeFilters: boolean;
    loading = true;

    sharedWith: UserVO[] = [];

    @ViewChild(AvailableCohortsComponent)
    availableCohortsComponent: AvailableCohortsComponent;

    @ViewChild(FilterListComponent)
    filterListComponent: FilterListComponent;

    private originalPopulationFilterModel: PopulationFiltersModel;
    private originalAesFilterModel: AesFiltersModel;

    constructor(private cohortEditorService: CohortEditorService,
                private localPopulationFiltersModel: PopulationFiltersModel,
                private cohortFiltersModel: CohortFiltersModel,
                private localAeFiltersModel: AesFiltersModel,
                private filterReloadService: FilterReloadService,
                private filtersExportService: FiltersExportService) {
        super();
        filtersExportService.populationFiltersModel = this.localPopulationFiltersModel;
    }

    ngOnDestroy(): void {
        this.availableCohortsComponent.removeUnsavedChanges();
    }

    canSave(): boolean {
        const hasPopulationFiltersSet = !_.isEmpty(this.localPopulationFiltersModel.transformFiltersToServer());
        const hasAeFiltersSet = !_.isEmpty(this.localAeFiltersModel.transformFiltersToServer());
        return hasPopulationFiltersSet || hasAeFiltersSet;
    }

    saveCohort(): void {
        this.loading = true;

        const saveCohortDto = this.beforeSave();
        this.cohortEditorService.saveCohort(saveCohortDto)
            .subscribe((savedFilterVo: SavedFilterVO[]) => {
                this.afterSave(savedFilterVo);
            });
    }

    saveAndApplyCohort(): void {
        this.loading = true;
        const selectedCohort = this.availableCohortsComponent.getSelectedCohort();

        const saveCohortDto = this.beforeSave();
        this.cohortEditorService.saveCohort(saveCohortDto)
            .subscribe((savedFilterVo: SavedFilterVO[]) => {
                const id = _.find(savedFilterVo, (f) => f.savedFilter.name === saveCohortDto.cohortName).savedFilter.id;
                this.reloadCohortFilters();
                this.cohortEditorService.applyCohort(id, saveCohortDto.cohortName)
                    .subscribe(() => {
                        this.afterSave(savedFilterVo);
                        this.cohortFiltersModel.addSelectedValue(selectedCohort.savedFilter.name);
                    });
            });
    }

    applyCohort(): void {
        this.loading = true;
        const selectedCohort = this.availableCohortsComponent.getSelectedCohort();
        this.reloadCohortFilters();
        this.cohortEditorService
            .applyCohort(this.availableCohortsComponent.selectedCohortId, selectedCohort.savedFilter.name)
            .subscribe(() => {
                this.cohortFiltersModel.addSelectedValue(selectedCohort.savedFilter.name);
                this.loading = false;
            });
    }

    private reloadCohortFilters() {
        this.cohortFiltersModel.reset();
        this.cohortFiltersModel.clearNotAppliedSelectedValues();
    }

    onClearAll(): void {
        if (this.showPopulationFilters) {
            this.cohortEditorService.clearAllFilters(this.localPopulationFiltersModel);
            this.filterReloadService.resetFilters(this.localPopulationFiltersModel);
        } else if (this.showAeFilters) {
            this.filterReloadService.resetFilters(this.localAeFiltersModel);
        }
    }

    onExportFilters(event: MouseEvent): void {
        let file;
        if (this.showPopulationFilters) {
            file = new Blob([this.filtersExportService.transformFilterToText(this.localPopulationFiltersModel)], {type: 'text/plain'});
        } else if (this.showAeFilters) {
            file = new Blob([this.filtersExportService.transformFilterToText(this.localAeFiltersModel)], {type: 'text/plain'});
        }
        (<any>event.currentTarget).href = window.URL.createObjectURL(file);
        (<any>event.currentTarget).download = 'Filters.txt';
    }

    hasUnsavedChanges(): boolean {
        return this.areChangesUnsavedForFilter(this.originalPopulationFilterModel, this.localPopulationFiltersModel)
            || this.areChangesUnsavedForFilter(this.originalAesFilterModel, this.localAeFiltersModel)
            || !_.isEmpty(this.availableCohortsComponent.renamedCohortName);
    }

    hidePopulationFilters(): void {
        this.showPopulationFilters = false;
        this.loading = true;
    }

    hideEventFilters(): void {
        this.showAeFilters = false;
        this.loading = true;
    }

    displayPopulationFilters(): void {
        this.showPopulationFilters = true;
        this.loading = false;
    }

    displayEventFilters(): void {
        this.showAeFilters = true;
        this.loading = false;
    }

    onLoading(loading: boolean): void {
        this.loading = loading;
    }

    updateFilters(): void {
        this.hidePopulationFilters();
        this.hideEventFilters();

        const selectedFilterInstance = this.filterListComponent.selectedFilterInstance;
        if (selectedFilterInstance !== '') {
            const cohortFilter = _.find(this.availableCohortsComponent.getSelectedCohort().cohortFilters,
                (filter: SavedFilterInstance) => filter.filterView === selectedFilterInstance);
            const filterModel = selectedFilterInstance === 'POPULATION' ? this.localPopulationFiltersModel : this.localAeFiltersModel;
            if (cohortFilter.id === this.availableCohortsComponent.ID_FOR_NEW_COHORTS) {
                cohortFilter.json = JSON.stringify(filterModel.transformFiltersToServer());
            }
            this.filterReloadService.reloadSavedFilter(cohortFilter, filterModel)
                .subscribe((hasFinishedLoading: boolean) => {
                    if (hasFinishedLoading) {
                        if (selectedFilterInstance === 'POPULATION') {
                            this.displayPopulationFilters();
                        } else {
                            this.displayEventFilters();
                        }
                    }
                });
        }
    }

    cohortSelected(cohortSelected: SavedFilterVO): void {
        this.loadFiltersForSelectedCohort(cohortSelected.cohortFilters);
        this.setSharedWith(cohortSelected);
    }

    resetFilters(): void {
        this.hidePopulationFilters();
        this.hideEventFilters();
        this.filterReloadService.resetFilters(this.localPopulationFiltersModel);
        this.filterReloadService.resetFilters(this.localAeFiltersModel);
        this.loading = false;
    }

    resetSharedWith(): void {
        this.sharedWith = [];
    }

    saveOriginalFilterState(): void {
        this.originalPopulationFilterModel = _.cloneDeep(this.localPopulationFiltersModel);
        this.originalAesFilterModel = _.cloneDeep(this.localAeFiltersModel);
    }

    private beforeSave(): SaveCohortDto {
        this.updateSelectedCohortSharedWith();
        this.cohortEditorService.renameCohortIfApplied(this.availableCohortsComponent.oldCohortName, this.getCohortNameToSave());

        return new SaveCohortDto(
            this.getCohortIdToSave(), this.getCohortNameToSave(), this.availableCohortsComponent.getSelectedCohort().sharedWith,
            this.getCohortFilterIdToSave('POPULATION'), this.getCohortFilterIdToSave('AES'),
            this.localPopulationFiltersModel, this.localAeFiltersModel);
    }

    private afterSave(savedFilterVo: SavedFilterVO[]): void {
        this.availableCohortsComponent.addSavedCohortsToList(savedFilterVo);
        this.availableCohortsComponent.renamedCohortName = '';
        this.cohortFiltersModel.setItems(this.availableCohortsComponent.availableCohorts);
        this.sharedWith = _.find(savedFilterVo, (sf) => {
            return sf.savedFilter.id === this.availableCohortsComponent.getSelectedCohort().savedFilter.id;
        }).sharedWith;
        this.updateSelectedCohortSharedWith();
    }

    private updateSelectedCohortSharedWith(): void {
        this.availableCohortsComponent.getSelectedCohort().sharedWith = this.sharedWith;
    }

    private setSharedWith(cohortSelected: SavedFilterVO): void {
        this.sharedWith = cohortSelected.sharedWith;
    }

    private loadFiltersForSelectedCohort(cohortFilters: SavedFilterInstance[]): void {
        this.loading = true;
        const populationFilters = _.filter(cohortFilters, {filterView: 'POPULATION'})[0];
        const aeFilters = _.filter(cohortFilters, {filterView: 'AES'})[0];

        if (!_.isEmpty(populationFilters)) {
            this.loadPopulationFiltersForSelectedCohort(populationFilters, aeFilters);
        } else if (!_.isEmpty(aeFilters)) {
            this.loadAeFiltersForSelectedCohort(aeFilters);
        }
    }

    private areChangesUnsavedForFilter(originalFilter: AbstractFiltersModel, latestFilter: AbstractFiltersModel): boolean {
        if (!originalFilter) {
            return this.availableCohortsComponent.hasCohortThatIsNotSaved();
        }

        const originalSelectedValues = _.chain(originalFilter.itemsModels)
            .filter((r: any) => !_.isBoolean(r.haveMadeChange))
            .flatMap('selectedValues')
            .value();
        const uniqueOriginalSelectedValues = _.uniq(originalSelectedValues);
        const changedSelectedValues = _.chain(latestFilter.itemsModels)
            .filter((r: any) => !_.isBoolean(r.haveMadeChange))
            .flatMap('selectedValues')
            .value();
        const originalRangeValues = _.chain(originalFilter.itemsModels)
            .filter((r: any) => _.isBoolean(r.haveMadeChange))
            .flatMap('haveMadeChange')
            .value();
        const changedRangeValues = _.chain(latestFilter.itemsModels)
            .filter((r: any) => _.isBoolean(r.haveMadeChange))
            .flatMap('haveMadeChange')
            .value();

        if (uniqueOriginalSelectedValues.length === 1 && _.isUndefined(uniqueOriginalSelectedValues[0])) {
            return false;
        }

        return !_.isEqual(originalSelectedValues, changedSelectedValues) || !_.isEqual(originalRangeValues, changedRangeValues);
    }

    private loadPopulationFiltersForSelectedCohort(populationFilters: SavedFilterInstance, aeFilters: SavedFilterInstance): void {
        this.filterReloadService.reloadSavedFilter(populationFilters, this.localPopulationFiltersModel)
            .subscribe((hasFinishedLoading: boolean) => {
                if (hasFinishedLoading) {
                    if (!_.isEmpty(aeFilters)) {
                        this.loadAeFiltersForSelectedCohort(aeFilters);
                    } else {
                        this.filterReloadService.resetFilters(this.localAeFiltersModel);
                        this.displayPopulationFilters();
                        this.saveOriginalFilterState();
                    }
                }
            });
    }

    private loadAeFiltersForSelectedCohort(aeFilters: SavedFilterInstance): void {
        this.filterReloadService.reloadSavedFilter(aeFilters, this.localAeFiltersModel)
            .subscribe((hasFinishedLoading: boolean) => {
                if (hasFinishedLoading) {
                    this.displayEventFilters();
                    this.loading = false;
                    this.addFilterOpen = false;
                    this.saveOriginalFilterState();
                }
            });
    }

    private getCohortNameToSave(): string {
        return _.isEmpty(this.availableCohortsComponent.newCohortName) ? this.availableCohortsComponent.getSelectedCohort().savedFilter.name
            : this.availableCohortsComponent.newCohortName;
    }

    private getCohortIdToSave(): number {
        return this.availableCohortsComponent.selectedCohortId === this.availableCohortsComponent.ID_FOR_NEW_COHORTS ? null
            : this.availableCohortsComponent.selectedCohortId;
    }

    private getCohortFilterIdToSave(filterView: string): number {
        if (this.availableCohortsComponent.selectedCohortId === this.availableCohortsComponent.ID_FOR_NEW_COHORTS
            || _.isUndefined(this.availableCohortsComponent.getSelectedCohort())) {
            return null;
        }

        const cohortFilter: SavedFilterInstance = _.find(this.availableCohortsComponent.getSelectedCohort().cohortFilters,
            {filterView: filterView} as any);

        if (_.isUndefined(cohortFilter) || cohortFilter.id === this.filterListComponent.ID_FOR_NEW_FILTERS) {
            return null;
        }

        return cohortFilter.id;
    }
}
