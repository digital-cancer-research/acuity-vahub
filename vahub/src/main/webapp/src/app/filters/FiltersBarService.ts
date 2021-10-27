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

import {FiltersUtils} from './utils/FiltersUtils';
import {getPluginSummary} from '../common/utils/Utils';
import {PopulationFiltersModel} from './dataTypes/population/PopulationFiltersModel';
import {FilterId} from '../common/trellising/store/ITrellising';
import {AbstractEventFiltersModel} from './dataTypes/AbstractEventFiltersModel';

export interface FilterOptions {
    showPopulationFilter: boolean;
    showTimelineFilter: boolean;
    showRecistFilter: boolean;
    showEventFilter: boolean;
    showSettings: boolean;
    settingsName: string;
    eventFiltersName: string;
    eventFiltersModel: AbstractEventFiltersModel;
    filterId: FilterId;
}

@Injectable()
export class FiltersBarService {

    constructor(public populationFiltersModel: PopulationFiltersModel,
                private filtersUtils: FiltersUtils,
                ) {
    }

    getFilterOptions(plugin: string, pluginSubType?: string): FilterOptions {
        const pluginSummary = getPluginSummary(plugin, pluginSubType);
        return <FilterOptions>{
            showPopulationFilter: pluginSummary.showPopulationFilter,
            showEventFilter: pluginSummary.showEventFilter,
            eventFiltersName: pluginSummary.eventFiltersName,
            showTimelineFilter: pluginSummary.showTimelineFilter,
            showSettings: pluginSummary.showSettings,
            settingsName: pluginSummary.settingsName,
            filterId: pluginSummary.filterId,
            eventFiltersModel: this.filtersUtils.getFilterModelById(pluginSummary.filterId)
        };
    }
}
