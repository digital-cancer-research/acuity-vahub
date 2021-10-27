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
import {
    ListFilterItemModel, CheckListFilterItemModel, RangeDateFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CerebrovascularFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new CheckListFilterItemModel('eventType', 'Type of Events'));
        this.itemsModels.push(new ListFilterItemModel('aeNumber', 'Associated AE No.'));
        this.itemsModels.push(new RangeDateFilterItemModel('eventStartDate', 'Event Start Date'));
        this.itemsModels.push(new ListFilterItemModel('eventTerm', 'Event Term'));
        this.itemsModels.push(new CheckListFilterItemModel('primaryIschemicStroke', 'If Primary Ischemic Stroke'));
        this.itemsModels.push(new CheckListFilterItemModel('traumatic', 'If Traumatic'));
        this.itemsModels.push(new CheckListFilterItemModel('intraHemorrhageLoc', 'Loc. of Primary Intracranial Hemorrhage'));
        this.itemsModels.push(new ListFilterItemModel('intraHemorrhageOtherLoc', 'Primary Intra. Hemorrhage Other, Specify'));
        this.itemsModels.push(new CheckListFilterItemModel('symptomsDuration', 'Duration of Symptoms'));
        this.itemsModels.push(new CheckListFilterItemModel('mrsPriorStroke', 'MRS Prior to Stroke'));
        this.itemsModels.push(new CheckListFilterItemModel('mrsDuringStrokeHosp', 'MRS During Stroke Hospitalisation'));
        this.itemsModels.push(new CheckListFilterItemModel('mrsCurrVisitOr90DAfterStroke', 'MRS at Current Visit or 90D After Stroke'));
        this.itemsModels.push(new ListFilterItemModel('comment', 'Comment'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCerebrovascularFilter(serverModel);
    }

    getName(): string {
        return 'cerebrovascular';
    }

    getDisplayName(): string {
        return 'Cerebrovascular';
    }

    getModulePath(): string {
        return 'cerebrovascular';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCerebrovascularData() : false;
    }
}
