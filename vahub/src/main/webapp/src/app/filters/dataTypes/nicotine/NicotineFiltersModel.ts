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

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel, CheckListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';

@Injectable()
export class NicotineFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('category', 'Substance Category'));
        this.itemsModels.push(new CheckListFilterItemModel('useOccurrence', 'Substance Use Occurrence'));
        this.itemsModels.push(new ListFilterItemModel('type', 'Substance Type'));
        this.itemsModels.push(new ListFilterItemModel('otherTypeSpec', 'Other Substance Type Specification'));
        this.itemsModels.push(new ListFilterItemModel('subTypeUseOccurrence', 'Substance Type Use Occurrence'));
        this.itemsModels.push(new ListFilterItemModel('currentUseSpec', 'Current Substance Use Specification'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Substance Use Start Date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'Substance Use End Date'));
        this.itemsModels.push(new RangeFilterItemModel('consumption', 'Substance Consumption'));
        this.itemsModels.push(new ListFilterItemModel('frequencyInterval', 'Substance Use Frequency Interval'));
        this.itemsModels.push(new RangeFilterItemModel('numberPackYears', 'Number of Pack Years'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setNicotineFilter(serverModel);
    }

    getName(): string {
        return 'nicotine';
    }

    getDisplayName(): string {
        return 'Nicotine';
    }

    getModulePath(): string {
        return 'nicotine';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasNicotineData() : false;
    }
}
