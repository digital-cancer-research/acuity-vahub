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
import {
    CheckListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class ExacerbationsFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new CheckListFilterItemModel('exacerbationClassification', 'Exacerbation classification'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtStart', 'Days on study at start', 1));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtEnd', 'Days on study at end', 1));
        this.itemsModels.push(new RangeFilterItemModel('duration', 'Duration (Days)', 1));
        this.itemsModels.push(new CheckListFilterItemModel('startPriorToRandomisation', 'Start prior to randomisation'));
        this.itemsModels.push(new CheckListFilterItemModel('endPriorToRandomisation', 'End prior to randomisation'));
        this.itemsModels.push(new CheckListFilterItemModel('hospitalisation', 'Hospitalisation'));
        this.itemsModels.push(new CheckListFilterItemModel('emergencyRoomVisit', 'Emergency room visit'));
        this.itemsModels.push(new CheckListFilterItemModel('antibioticsTreatment', 'Antibiotics treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('depotCorticosteroidTreatment', 'Depot corticosteroid treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('systemicCorticosteroidTreatment', 'Systemic corticosteroid treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('increasedInhaledCorticosteroidTreatment', 'Increased inhaled corticosteroid treatment'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setExacerbationsFilter(serverModel);
    }

    getName(): string {
        return 'exacerbation';
    }

    getDisplayName(): string {
        return 'Exacerbations';
    }

    getModulePath(): string {
        return 'respiratory';
    }

    protected getFilterPath(): string {
        return 'exacerbation/filters';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasExacerbationsData() : false;
    }
}
