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

import {Injectable} from '@angular/core';
import {map, isEmpty, remove, includes, difference} from 'lodash';

import {AbstractFiltersModel} from '../AbstractFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CohortEditorService} from '../../../plugins/cohorteditor/services/CohortEditorService';
import SavedFilterVO = Request.SavedFilterVO;

@Injectable()
export class CohortFiltersModel extends AbstractFiltersModel {

    // bug where PopulationFiltersModel is generated after study selection event.
    hasInitData = false;

    private myAvailableCohorts: SavedFilterVO[];

    constructor(private cohortEditorService: CohortEditorService,
                private populationFiltersModel: PopulationFiltersModel) {
        super();
        this.itemsModels.push(new ListFilterItemModel('cohorts', 'My Cohorts'));
        this.isPopulationFilter = false;
    }

    emitEvent(serverModel: any): void {
        return;
    }

    getName(): string {
        return 'cohort';
    }

    getDisplayName(): string {
        return 'Cohort';
    }

    getModulePath(): string {
        return 'cohort';
    }

    isVisible(): boolean {
        return true;
    }

    setItems(allCohorts: SavedFilterVO[]): void {
        this.myAvailableCohorts = allCohorts;
        const model = {
            values: map(allCohorts, cohort => cohort.savedFilter.name)
        };
        this.itemsModels[0].fromServerObject(model);
        this.updateNumberOfSelectedItems();
    }

    addSelectedValue(cohortName: string): void {
        const itemsModels: ListFilterItemModel = <ListFilterItemModel> this.itemsModels[0];
        const cohortAlreadySelected = includes(itemsModels.selectedValues, cohortName);
        if (!isEmpty(cohortName) && !cohortAlreadySelected) {
            itemsModels.selectedValues.push(cohortName);
        }
        this.updateNumberOfSelectedItems();
    }

    updateNumberOfSelectedItems(): void {
        const itemsModels: ListFilterItemModel = <ListFilterItemModel> this.itemsModels[0];
        itemsModels.numberOfSelectedFilters = this.populationFiltersModel.getCohortEditorFilters().length;
    }

    removeCohort(cohortName: string): void {
        remove((<ListFilterItemModel> this.itemsModels[0]).selectedValues, value => value === cohortName);
        this.removeCohortsFromPopulationFilter([cohortName]);
        (<ListFilterItemModel> this.itemsModels[0]).updateNumberOfSelectedFilters();
    }

    reset(): void {
        const cohortsToRemove = <string[]> map(this.populationFiltersModel.getCohortEditorFilters(), filter => filter.displayName);
        this.removeCohortsFromPopulationFilter(cohortsToRemove);
        this.populationFiltersModel.toggleSubjectIdFilterVisibility(true);
    }

    protected _getFiltersImpl(manuallyApplied = false, reseted = false, triggeredByPopulation = false): void {

        this.loading = true;

        if (this.pendingRequest) {
            this.pendingRequest.unsubscribe();
        }

        if (this.isInitialising()) {
            this.initialise();
        } else if (this.isResetting()) {
            this.reset();
        } else {
            this.apply();
        }
    }

    private isInitialising(): boolean {
        return !this.hasInitData;
    }

    private isResetting(): boolean {
        return (<ListFilterItemModel> this.itemsModels[0]).selectedValues.length === 0;
    }

    private initialise(): void {
        this.cohortEditorService.getCohorts().subscribe((cohorts: SavedFilterVO[]) => {
            this.myAvailableCohorts = cohorts;
            this.itemsModels[0].reset();
            this.itemsModels[0].updateNumberOfSelectedFilters();
            this.setItems(cohorts);
            this.loading = false;
            this.hasInitData = true;
        });
    }

    private apply(): void {
        const unappliedCohorts = this.myAvailableCohorts.filter((cohort) => {
            return includes(this.getUnAppliedCohortNames(), cohort.savedFilter.name);
        });
        const deselectedCohorts = this.getDeselectedAppliedCohortNames();
        this.removeCohortsFromPopulationFilter(deselectedCohorts);
        if (!isEmpty(unappliedCohorts)) {
            unappliedCohorts.forEach((cohort) =>
                this.cohortEditorService.applyCohort(cohort.savedFilter.id, cohort.savedFilter.name).subscribe()
            );
        }
    }

    private getUnAppliedCohortNames(): string[] {
        const appliedCohorts = map(this.populationFiltersModel.getCohortEditorFilters(), filter => filter.displayName);
        const selectedCohorts = (<ListFilterItemModel> this.itemsModels[0]).selectedValues;
        return difference(selectedCohorts, appliedCohorts);
    }

    private getDeselectedAppliedCohortNames(): string[] {
        const appliedCohorts = map(this.populationFiltersModel.getCohortEditorFilters(), filter => filter.displayName);
        const selectedCohorts = (<ListFilterItemModel> this.itemsModels[0]).selectedValues;
        return difference(appliedCohorts, selectedCohorts);
    }

    private removeCohortsFromPopulationFilter(cohortNames: string[]): void {
        (<PopulationFiltersModel> this.populationFiltersModel).removeCohortFilter(cohortNames, true);
        this.populationFiltersModel.getFilters();
    }
}
