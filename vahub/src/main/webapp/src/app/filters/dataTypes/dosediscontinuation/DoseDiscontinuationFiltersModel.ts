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
import {CheckListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';

@Injectable()
export class DoseDiscontinuationFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new CheckListFilterItemModel('studyDrug', 'Study drug'));
        this.itemsModels.push(new RangeDateFilterItemModel('discDate', 'Date of IP discontinuation'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtDisc', 'Study day at IP discontinuation'));
        this.itemsModels.push(new CheckListFilterItemModel('discMainReason', 'Main reason for IP discontinuation'));
        this.itemsModels.push(new CheckListFilterItemModel('discSpec', 'IP discontinuation specification'));
        this.itemsModels.push(new CheckListFilterItemModel('subjectDecisionSpec', 'Subject decision specification'));
        this.itemsModels.push(new CheckListFilterItemModel('subjectDecisionSpecOther', 'Other subject decision specification'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setDoseDiscontinuationFilter(serverModel);
    }

    getName(): string {
        return 'doseDisc';
    }

    getDisplayName(): string {
        return 'Dose Discontinuation';
    }

    getModulePath(): string {
        return 'dose-disc';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasDoseDiscontinuationData() : false;
    }
}
