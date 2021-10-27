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
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class ExposureFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('analyte', 'Analyte'));
        this.itemsModels.push(new ListFilterItemModel('treatmentCycle', 'Cycle'));
        this.itemsModels.push(new ListFilterItemModel('treatment', 'Nominal dose'));
        this.itemsModels.push(new ListFilterItemModel('day', 'Day'));
        this.itemsModels.push(new ListFilterItemModel('visit', 'Visit'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setExposureFilter(serverModel);
    }

    getName(): string {
        return 'exposure';
    }

    getDisplayName(): string {
        return 'Analyte Concentration';
    }

    getModulePath(): string {
        return 'exposure';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasExposureData() : false;
    }
}
