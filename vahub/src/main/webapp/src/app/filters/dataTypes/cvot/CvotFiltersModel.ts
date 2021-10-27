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
import {ListFilterItemModel, CheckListFilterItemModel, RangeFilterItemModel, RangeDateFilterItemModel,
    MapListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CvotFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Event Start Date'));
        this.itemsModels.push(new ListFilterItemModel('term', 'Event Term'));
        this.itemsModels.push(new ListFilterItemModel('aeNumber', 'Associated AE No.'));
        this.itemsModels.push(new ListFilterItemModel('category1', 'Event Category 1'));
        this.itemsModels.push(new ListFilterItemModel('category2', 'Event Category 2'));
        this.itemsModels.push(new ListFilterItemModel('category3', 'Event Category 3'));
        this.itemsModels.push(new ListFilterItemModel('description1', 'Event Description 1'));
        this.itemsModels.push(new ListFilterItemModel('description2', 'Event Description 2'));
        this.itemsModels.push(new ListFilterItemModel('description3', 'Event Description 3'));
        // add all the basefiltermodel implementations
         }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCvotFilter(serverModel);
    }

    getName(): string {
        return 'cvotEndpoint';
    }

    getDisplayName(): string {
        return 'Additional CVOT Suspected Endpoint Filters';
    }

    getModulePath(): string {
        return 'cvotendpoint';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCvotData() : false;
    }

    protected getFilterPath(): string {
        return 'filters';
    }
}
