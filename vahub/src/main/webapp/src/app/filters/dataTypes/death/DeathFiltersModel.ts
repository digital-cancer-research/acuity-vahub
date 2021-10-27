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
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {ListFilterItemModel} from '../../components/list/ListFilterItemModel';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {CheckListFilterItemModel} from '../../components/checklist/CheckListFilterItemModel';

@Injectable()
export class DeathFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('deathCause', 'Cause of death'));
        this.itemsModels.push(new RangeFilterItemModel('daysFromFirstDoseToDeath', 'Days from first dose to death', 1));
        this.itemsModels.push(new CheckListFilterItemModel('autopsyPerformed', 'Autopsy performed'));
        this.itemsModels.push(new CheckListFilterItemModel('deathRelatedToDisease', 'Death related to disease under investigation'));
        this.itemsModels.push(new CheckListFilterItemModel('designation', 'Designation'));
        this.itemsModels.push(new ListFilterItemModel('hlt', 'MedDRA HLT'));
        this.itemsModels.push(new ListFilterItemModel('llt', 'MedDRA LLT'));
        this.itemsModels.push(new ListFilterItemModel('pt', 'MedDRA PT'));
        this.itemsModels.push(new ListFilterItemModel('soc', 'MedDRA SOC'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setDeathFilter(serverModel);
    }

    getName(): string {
        return 'death';
    }

    getDisplayName(): string {
        return 'Death';
    }

    getModulePath(): string {
        return 'death';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasDeathData() : false;
    }
}
