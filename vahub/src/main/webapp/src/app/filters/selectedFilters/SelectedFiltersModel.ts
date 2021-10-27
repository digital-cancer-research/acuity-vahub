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
import {ListFilterItemModel} from '../components/list/ListFilterItemModel';
import {FilterId, TabName} from '../../common/trellising/store/ITrellising';
import {TrackName} from '../../plugins/timeline/store/ITimeline';
import {StudySpecificFilter} from '../components/studySpecific/StudySpecificFilterModel';
import * as  _ from 'lodash';
import {getPluginSummary, getServerPath} from '../../common/utils/Utils';
import {FiltersUtils} from '../utils/FiltersUtils';
import {AbstractFiltersModel} from '../dataTypes/AbstractFiltersModel';
import {BaseFilterItemModel} from '../components/BaseFilterItemModel';
import {FILTER_TYPE} from '../components/dtos';
import {PopulationFiltersModel} from '../dataTypes/population/PopulationFiltersModel';
import {EMPTY} from '../../common/trellising/store';

@Injectable()
export class SelectedFiltersModel {
    selectedPopulationFilters: any = [];
    selectedEventFilters: any = [];
    numberOfSubjects: string;
    numberOfEvents: string;
    totalNumberOfSubjects: number;
    totalNumberOfEvents: number;
    eventWidgetName: string;
    consideredFilter: any;
    timelineSelectedTracks: TrackName[];

    constructor(private filtersUtils: FiltersUtils) {
        this.numberOfSubjects = '(All)';
        this.numberOfEvents = '(All)';
        this.consideredFilter = null;
        this.timelineSelectedTracks = [TrackName.SUMMARY];
    }

    clearSelectedFilters(): void {
        this.selectedPopulationFilters = [];
        this.selectedEventFilters = [];
    }

    transformFilters(filterId: FilterId): void {
        const currentState: string = this.getCurrentPageName();
        let selectedFilters = [];
        if (filterId !== FilterId.POPULATION) {
            if (currentState.indexOf('Timeline') !== -1) {
                _.forEach(this.timelineSelectedTracks, (track: TrackName) => {
                    selectedFilters = _.union(selectedFilters, this.getSelectedFilters(currentState, track));
                });
                this.selectedEventFilters = selectedFilters;
            } else {
                this.selectedEventFilters = this.getSelectedFilters(currentState, filterId);
            }
        }

        // Always update population filters in case a cohort name is changed
        this.selectedPopulationFilters = this.getSelectedFilters(TabName.POPULATION_SUMMARY_PLOT, FilterId.POPULATION);
    }

    updateEventWidgetName(): void {
        const [page, tab] = window.location.hash.slice(10).split('/');
        const options = getPluginSummary(page, tab);
        this.eventWidgetName = options.eventWidgetName;
        this.transformFilters(options.filterId);
    }

    private getSelectedFilters(currentState: string, filterId: any): any {
        const filtersModel: AbstractFiltersModel = this.filtersUtils.getFilterModelById(filterId);
        if (filtersModel) {
            const filters: any = filtersModel.transformFiltersToServer();
            if (filtersModel.getName() === 'aes') {
                delete filters.aeDetailLevel;
            }
            if (this.areSubjectIdsFromCohortFilters(filterId, filters)) {
                delete filters.subjectId;
            }
            return _.flatten(_.map(filters, (value: any, key: string) => {
                const filter: any = _.find(filtersModel.itemsModels, (filter: BaseFilterItemModel) => {
                    return filter.key === key;
                });
                if (filter) {
                    switch (filter.type) {
                        case FILTER_TYPE.LIST:
                        case FILTER_TYPE.CHECK_LIST:
                        case FILTER_TYPE.UNSELECTED_CHECK_LIST:
                        case FILTER_TYPE.RANGE:
                        case FILTER_TYPE.RANGE_DATE:
                        case FILTER_TYPE.TOGGLE_LIST:
                        case FILTER_TYPE.COHORT_EDITOR:
                            return this.processFilterItem(filter, filter.type, key, currentState, filterId);
                        case FILTER_TYPE.STUDY_SPECIFIC_FILTERS:
                            return _.map(_.filter(filter.filters, (studySpecFilter: StudySpecificFilter) => {
                                return _.some(value.values, (value: string) => {
                                    return value.indexOf(studySpecFilter.name) !== -1;
                                });
                            }), (studySpecFilter: StudySpecificFilter) => {
                                return this.processFilterItem(studySpecFilter, FILTER_TYPE.STUDY_SPECIFIC_FILTERS, key, currentState, filterId);
                            });
                        case FILTER_TYPE.MAP_LIST:
                            return filter.filters.filter((filter: ListFilterItemModel) => {
                                return filter.appliedSelectedValues.length > 0;
                            }).map((filter: BaseFilterItemModel) => {
                                return this.processFilterItem(filter, filter.type, key, currentState, filterId);
                            });
                        case FILTER_TYPE.MAP_RANGE:
                        case FILTER_TYPE.MAP_RANGE_DATE:
                            return _.map(filter.filters, (filter: BaseFilterItemModel) => {
                                return this.processFilterItem(filter, filter.type, key, currentState, filterId);
                            });
                        default:
                            return {};
                    }
                }
                return {};
            }));
        } else {
            return [];
        }
    }

    private areSubjectIdsFromCohortFilters(filterId, filters: any): boolean {
        if (_.isNull(filters.subjectId)) {
            return true;
        }

        if (!_.isEmpty(filters.subjectId) && filters.subjectId[0] === PopulationFiltersModel.NO_INTERSECT_OF_SUBJECTS) {
            return true;
        }

        return this.hasCohortFilters(filters);
    }

    private hasCohortFilters(filters: any): boolean {
        const cohortFilters = _.chain(filters)
            .keys()
            .filter((key) => key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1)
            .value();
        return !_.isEmpty(cohortFilters);
    }

    private processFilterItem(filter: any, type: FILTER_TYPE, key: string, currentState: string, filterId: any): any {
        let filterValues;
        let joinedFilterValues = null;
        const selectedValues = _.clone(filter.selectedValues);
        switch (type) {
            case FILTER_TYPE.LIST:
            case FILTER_TYPE.CHECK_LIST:
            case FILTER_TYPE.UNSELECTED_CHECK_LIST:
            case FILTER_TYPE.TOGGLE_LIST:
            case FILTER_TYPE.STUDY_SPECIFIC_FILTERS:
            case FILTER_TYPE.COHORT_EDITOR:
                const indexOfNull = selectedValues.indexOf(null) & selectedValues.indexOf('null');
                if (indexOfNull !== -1) {
                    selectedValues[indexOfNull] = EMPTY;
                }
                filterValues = selectedValues;
                const name = filter.displayName ? filter.displayName : filter.name;
                joinedFilterValues = (name + selectedValues.join()).length < 40 ? selectedValues.join(' ,') : null;
                break;
            case FILTER_TYPE.RANGE:
            case FILTER_TYPE.RANGE_DATE:
                filterValues = {
                    from: filter.haveMadeChange && !_.isNull(selectedValues.from) ? selectedValues.from : null,
                    to: filter.haveMadeChange && !_.isNull(selectedValues.to) ? selectedValues.to : null,
                    includeEmptyValues: selectedValues.includeEmptyValues
                };
                break;
            default:
                filterValues = selectedValues;
                break;
        }
        return {
            key,
            type,
            displayValues: filterValues,
            values: selectedValues,
            displayName: filter.displayName ? filter.displayName : filter.name,
            joinedFilterValues,
            currentState,
            filterId
        };
    }

    private getCurrentPageName(): string {
        const [page, tab] = window.location.hash.slice(10).split('/');
        return getPluginSummary(page, tab).pageName;
    }
}
