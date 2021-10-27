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
export class ConmedsFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('medicationName', 'Medication name', false, 4));
        this.itemsModels.push(new ListFilterItemModel('atcCode', 'ATC code'));
        this.itemsModels.push(new RangeFilterItemModel('dose', 'Dose', 0.01));
        this.itemsModels.push(new ListFilterItemModel('doseUnits', 'Dose unit'));
        this.itemsModels.push(new ListFilterItemModel('doseFrequency', 'Dose frequency'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('duration', 'Duration (days)', 1));
        this.itemsModels.push(new ListFilterItemModel('ongoing', 'Conmed treatment ongoing'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtConmedStart', 'Study day at conmed start', 1));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtConmedEnd', 'Study day at conmed end', 1));
        this.itemsModels.push(new ListFilterItemModel('startPriorToRandomisation', 'Start prior to randomisation'));
        this.itemsModels.push(new ListFilterItemModel('endPriorToRandomisation', 'End prior to randomisation'));
        this.itemsModels.push(new ListFilterItemModel('treatmentReason', 'Treatment reason'));
        this.itemsModels.push(new ListFilterItemModel('atcText', 'ATC Text', false, 4));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setConmedsFilter(serverModel);
    }

    getName(): string {
        return 'conmeds';
    }

    getDisplayName(): string {
        return 'Conmeds';
    }

    getModulePath(): string {
        return 'conmeds';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasConmedsData() : false;
    }
}
