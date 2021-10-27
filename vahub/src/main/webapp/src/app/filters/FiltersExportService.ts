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
import {map, isNull} from 'lodash';

import {AbstractFiltersModel} from './dataTypes/AbstractFiltersModel';
import {StudySpecificFilter} from './components/studySpecific/StudySpecificFilterModel';
import {ListFilterItemModel} from './components/list/ListFilterItemModel';
import {RangeDateFilterItemModel} from './components/rangedate/RangeDateFilterItemModel';
import {RangeFilterItemModel} from './components/range/RangeFilterItemModel';
import {FiltersUtils} from './utils/FiltersUtils';
import {PopulationFiltersModel} from './dataTypes/population/PopulationFiltersModel';
import {FILTER_TYPE} from './components/dtos';
import {EMPTY} from '../common/trellising/store';

@Injectable()
export class FiltersExportService {

    constructor(public populationFiltersModel: PopulationFiltersModel,
                private filtersUtils: FiltersUtils) {
    }

    exportFilters(event: MouseEvent): void {
        console.log('onExportFilters');
        const file = new Blob([this.transformFiltersToText()], {type: 'text/plain'});
        (<any>event.currentTarget).href = window.URL.createObjectURL(file);
        (<any>event.currentTarget).download = 'Filters.txt';
    }

    transformFiltersToText(): string {
        let filters = '';

        const filterModels = this.filtersUtils.getAvailableEventFilterModels();

        filterModels.unshift(this.populationFiltersModel);
        filterModels.forEach((filterModel: AbstractFiltersModel) => {
            filters += this.transformFilterToText(filterModel);
        });
        return filters;
    }

    transformFilterToText(filterModel: AbstractFiltersModel): string {
        let filterText = '';
        const itemsCount = filterModel.matchedItemsCount || 'N/A';

        filterText += filterModel.getDisplayName() + '\r\n';
        const filterValues = this.getSelectedFilters(filterModel);
        if (filterModel.isPopulationFilter) {
            filterText += '\t' + 'Matched subjects count: ' + itemsCount + '\r\n';
        } else {
            filterText += '\t' + 'Matched items count: ' + itemsCount + '\r\n';
        }
        filterValues.forEach((filter: string) => {
            if (filter) {
                filterText += '\t' + filter + '\r\n';
            }
        });
        filterText += '\r\n';

        return filterText;
    }

    private getSelectedFilters(filtersModel: AbstractFiltersModel): any {
        return filtersModel.itemsModels.map((filter: any) => {
            if (filter.filterIsVisible) {
                let value, selectedValues = null;
                switch (filter.type) {
                    case FILTER_TYPE.LIST:
                    case FILTER_TYPE.CHECK_LIST:
                    case FILTER_TYPE.TOGGLE_LIST:
                    case FILTER_TYPE.UNSELECTED_CHECK_LIST:
                        value = this.transformListFilter(filter);
                        break;
                    case FILTER_TYPE.RANGE:
                        value = this.transformRangeFilter(filter);
                        break;
                    case FILTER_TYPE.RANGE_DATE:
                        value = this.transformRangeDateFilter(filter);
                        break;
                    case FILTER_TYPE.STUDY_SPECIFIC_FILTERS:
                        value = filter.filters.map((studySpecFilter: StudySpecificFilter) => {
                            selectedValues = studySpecFilter.appliedSelectedValues.slice(0);
                            const indexOfNull = selectedValues.indexOf(null);
                            if (indexOfNull !== -1) {
                                selectedValues[indexOfNull] = EMPTY;
                            }
                            value = studySpecFilter.name + ': ' + (studySpecFilter.appliedSelectedValues.length > 0
                            && studySpecFilter.appliedSelectedValues.length !== studySpecFilter.initialValues.length
                                ? selectedValues.join(', ')
                                : '<no filter set>');
                            return value;
                        });
                        value = filter.displayName + ':' + (value.length > 0 ? '\r\n\t\t' + value.join('\r\n\t\t') : '<no filter set>');
                        break;
                    case FILTER_TYPE.MAP_LIST:
                    case FILTER_TYPE.MAP_CHECK_LIST:
                        if (!filter.filters.length) {
                            break;
                        }
                        value = map(filter.filters, this.transformListFilter.bind(this));
                        value = filter.displayName + ':' + (value.length > 0 ? '\r\n\t\t' + value.join('\r\n\t\t') : '<no filter set>');
                        break;
                    case FILTER_TYPE.MAP_RANGE:
                        if (!filter.filters.length) {
                            break;
                        }
                        value = map(filter.filters, this.transformRangeFilter.bind(this));
                        value = filter.displayName + ':' + (value.length > 0 ? '\r\n\t\t' + value.join('\r\n\t\t') : '<no filter set>');
                        break;
                    case FILTER_TYPE.MAP_RANGE_DATE:
                        if (!filter.filters.length) {
                            break;
                        }
                        value = map(filter.filters, this.transformRangeDateFilter.bind(this));
                        value = filter.displayName + ':' + (value.length > 0 ? '\r\n\t\t' + value.join('\r\n\t\t') : '<no filter set>');
                        break;
                    case FILTER_TYPE.COHORT_EDITOR:
                        value = filter.displayName + ': ' + (filter.selectedValues.length ? filter.selectedValues.join(', ') : '<no filter set>');
                        break;
                    default:
                        value = filter.displayName + ': <no filter set>';
                        break;
                }
                return value;
            } else {
                return '';
            }
        });
    }

    private transformListFilter(filter: ListFilterItemModel): string {
        let value, selectedValues;

        selectedValues = filter.appliedSelectedValues.slice(0);
        const indexOfNull = selectedValues.indexOf(null);
        if (indexOfNull !== -1) {
            selectedValues[indexOfNull] = EMPTY;
        }
        value = filter.displayName + ': ' + (filter.appliedSelectedValues.length ? selectedValues.join(', ') : '<no filter set>');

        return value;
    }

    /**
     * Do stuff
     * @param filter
     * @returns {any}
     */
    private transformRangeFilter(filter: RangeFilterItemModel): string {
        let value, from, to, includeEmptyValues;

        from = !isNull(filter.appliedSelectedValues.from) && filter.appliedSelectedValues.from !== filter.availableValues.min
            ? filter.appliedSelectedValues.from.toString()
            : '<no filter set>';
        to = !isNull(filter.appliedSelectedValues.to) && filter.appliedSelectedValues.to !== filter.availableValues.max
            ? filter.appliedSelectedValues.to.toString()
            : '<no filter set>';
        includeEmptyValues = filter.appliedSelectedValues.includeEmptyValues;
        value = filter.displayName + ': ';
        if (filter.haveMadeChange && (from !== '<no filter set>' || to !== '<no filter set>')) {
            value += 'from ' + from + ', to ' + to + ', include empty values: ' + includeEmptyValues;
        } else {
            value += '<no filter set>';
        }

        return value;
    }

    private transformRangeDateFilter(filter: RangeDateFilterItemModel): string {
        let value, from, to, includeEmptyValues;

        from = this.hasFromFilterChanged(filter) ? filter.appliedSelectedValues.from : '<no filter set>';
        to = this.hasToFilterChanged(filter) ? filter.appliedSelectedValues.to : '<no filter set>';
        includeEmptyValues = filter.appliedSelectedValues.includeEmptyValues;
        value = filter.displayName + ': ';
        if (filter.haveMadeChange && (from !== '<no filter set>' || to !== '<no filter set>')) {
            value += 'from ' + from + ', to ' + to + ', include empty values: ' + includeEmptyValues;
        } else {
            value += '<no filter set>';
        }
        return value;
    }

    private hasFromFilterChanged(filter: RangeDateFilterItemModel): boolean {
        return !isNull(filter.appliedSelectedValues.from) && filter.appliedSelectedValues.from !== filter.availableValues.from;
    }

    private hasToFilterChanged(filter: RangeDateFilterItemModel): boolean {
        return !isNull(filter.appliedSelectedValues.to) && filter.appliedSelectedValues.to !== filter.availableValues.to;
    }
}
