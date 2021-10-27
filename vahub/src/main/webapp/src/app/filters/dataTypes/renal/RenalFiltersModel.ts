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
    ListFilterItemModel,
    RangeDateFilterItemModel,
    CheckListFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class RenalFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('measurementName', 'Measurement name', false, 4));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementTimePoint', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudy', 'Days on study', 1));
        this.itemsModels.push(new RangeFilterItemModel('analysisVisit', 'Analysis visit', 1));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 1));
        this.itemsModels.push(new CheckListFilterItemModel('studyPeriods', 'Study period'));
        this.itemsModels.push(new RangeFilterItemModel('labValue', 'Result value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('labUnit', 'Result unit', false, 4));
        this.itemsModels.push(new RangeFilterItemModel('labValueOverUpperRefValue', 'Times upper ref. range value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('upperRefValue', 'Upper ref. range value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('ckdStage', 'CKD stage', false, 4));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setRenalFilter(serverModel);
    }

    getName(): string {
        return 'renal';
    }

    getDisplayName(): string {
        return 'Renal';
    }

    getModulePath(): string {
        return 'renal';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasRenalData() : false;
    }
}
