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
