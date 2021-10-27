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
import {Subject} from 'rxjs/Subject';
import * as  _ from 'lodash';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {AbstractFiltersModel} from '../../../filters/dataTypes/AbstractFiltersModel';
import {BaseFilterItemModel} from '../../../filters/components/BaseFilterItemModel';
import SavedFilterInstance = Request.SavedFilterInstance;

/**
 * Service for reloading saved filters into the local (i.e. the non-global) filter model on the Cohort Editor page
 */
@Injectable()
export class FilterReloadService {

    private updatingSelectedValues: boolean;
    private savedFilter: SavedFilterInstance;

    constructor(private filterEventService: FilterEventService) {
    }

    resetFilters(filterModel: AbstractFiltersModel): void {
        filterModel.reset();
        filterModel.getFilters();
    }

    reloadSavedFilter(savedFilter: SavedFilterInstance, filterModel: AbstractFiltersModel): Subject<boolean> {
        this.resetFilters(filterModel);

        this.savedFilter = savedFilter;
        this.updatingSelectedValues = false;

        const hasFinishedLoading = new Subject<boolean>();
        let subscription: Subject<any>;
        let reload = true;

        if (filterModel.isPopulationFilter) {
            subscription = this.filterEventService.populationFilterSubjectCount;
        } else {
            subscription = this.filterEventService.eventFilterEventCount;
        }

        subscription.subscribe(() => {
            if (reload) { // Only reload when the reloadSavedFilter method is called, not when the event emits every time
                if (!this.updatingSelectedValues) {
                    this.applySelectedValues(filterModel);
                } else {
                    this.setSelectedValues(filterModel);
                    hasFinishedLoading.next(true);
                    reload = false;
                }
            }
        });

        return hasFinishedLoading;
    }

    private applySelectedValues(filterModel: AbstractFiltersModel): void {
        if (!_.isEmpty(this.savedFilter)) {
            this.setSelectedValues(filterModel);

            filterModel.getFilters(false, true);
            this.updatingSelectedValues = true;
        }
    }

    private setSelectedValues(filterModel: AbstractFiltersModel): void {
        if (!_.isEmpty(this.savedFilter) && !_.isEmpty(this.savedFilter.json)) {
            const savedFilterObj = JSON.parse(this.savedFilter.json);
            const filterKeys = _.keys(savedFilterObj);

            filterModel.itemsModels.forEach((filter: BaseFilterItemModel) => {
                if (filterKeys.indexOf(filter.key) > -1) {
                    filter.setSelectedValues(savedFilterObj[filter.key]);
                    filter.updateNumberOfSelectedFilters();
                }
            });
        }
    }
}
