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
import {GENE_PERCENTAGE} from './BiomarkersFiltersConstants';

@Injectable()
export class BiomarkersFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('gene', 'Gene'));
        this.itemsModels.push(new CheckListFilterItemModel('mutation', 'Alteration Type'));
        this.itemsModels.push(new RangeFilterItemModel(GENE_PERCENTAGE, 'Gene Percentage', 1));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setBiomarkersFilter(serverModel);
    }

    getName(): string {
        return 'biomarker';
    }

    getDisplayName(): string {
        return 'Genomic Profile Filters';
    }

    getModulePath(): string {
        return 'biomarker';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasOncoBiomarkers() : false;
    }
}
