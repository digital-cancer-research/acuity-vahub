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

import {Component, Input, Output, EventEmitter, Inject} from '@angular/core';
import {DOCUMENT} from '@angular/platform-browser';
import * as  _ from 'lodash';

import {BaseFilterItemModel} from '../../components/BaseFilterItemModel';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';

@Component({
    selector: 'filter-collection',
    templateUrl: 'FilterCollectionComponent.html',
    styleUrls: ['../../filters.css']
})
export class FilterCollectionComponent {

    @Input() filtersModel: AbstractEventFiltersModel;
    @Input() isCohortFilter: boolean;
    @Output() clearAll = new EventEmitter<void>();
    @Output() exportFilters = new EventEmitter<MouseEvent>();

    openedFilterModel: BaseFilterItemModel;

    constructor(@Inject(DOCUMENT) private document: any) {
    }

    openContent($event, model: BaseFilterItemModel): void {
        const filterTitleParent = $($event.target).closest('.filter-item');
        const isOpen = filterTitleParent.hasClass('active');

        // offset of filter title
        const dynamicOffset: number = this.isCohortFilter ? 163 : 53;

        $('.filter-item').removeClass('active');
        $('.filter-collection').hide();

        if (isOpen) {
            this.openedFilterModel = null;
            filterTitleParent.removeClass('active');
            filterTitleParent.children('.filter-collection').hide();
            $($event.target).parents().children('.apply-clear-buttons').hide();
            if (!this.isCohortTab()) {
                $($event.target).parents().children('.export-clear-buttons').show();
            }
        } else {
            this.openedFilterModel = model;
            filterTitleParent.addClass('active');
            filterTitleParent.children('.filter-collection').show();
            $($event.target).parents().children('.apply-clear-buttons').show();
            $($event.target).parents().children('.export-clear-buttons').hide();
            const filterList = $event.target.closest('.filter-list');
            const scrollOffset: number = filterList.scrollTop;

            $(filterList).animate({
                scrollTop: $event.target.closest('.filter-list__title').offsetTop - dynamicOffset
            }, 'fast');

            if (!this.isCohortTab()) {
                $($event.target).parents().children('.export-clear-buttons').hide();
            }
        }
    }

    onApply(): void {
        const activeFilterItem = this.document.querySelector('.filter-item.active');

        if (!_.isNull(activeFilterItem)) {
            activeFilterItem.scrollIntoView();
        }

        this.filtersModel.itemsModels.forEach((filter: BaseFilterItemModel) => {
            filter.updateNumberOfSelectedFilters();
        });
        this.filtersModel.getFilters(false, true);

    }

    onClear(): void {
        if (this.openedFilterModel) {
            this.openedFilterModel.clear();
            this.openedFilterModel.resetNumberOfSelectedFilters();
        }
        this.filtersModel.getFilters(false, false, true);
    }

    onClearAll(): void {
        console.log(this.filtersModel.getName(), ' attempted to clear all');
        this.clearAll.emit();
    }

    onExportFilters(event: MouseEvent): void {
        console.log(this.filtersModel.getName(), ' attempted to export');
        this.exportFilters.emit(event);
    }

    getFilterList(): BaseFilterItemModel[] | BaseFilterItemModel[][] {
        const filters = _.filter(this.filtersModel.itemsModels,
            (item) => item.key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) === -1);

        if (this.isCohortFilter) {
            const half = Math.floor(filters.length / 2);

            /* return array split in a half to display filters in 2 columns
               and have ability to scroll inside both the columns */
            return [
                filters.slice(0, half),
                filters.slice(half, filters.length)
            ];
        }

        return filters;
    }

    private isCohortTab(): boolean {
        return $('.tab-pane.cohort').hasClass('active');
    }
}
