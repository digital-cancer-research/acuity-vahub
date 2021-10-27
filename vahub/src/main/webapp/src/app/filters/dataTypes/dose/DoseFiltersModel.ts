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
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';

@Injectable()
export class DoseFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('studyDrug', 'Drugs'));
        this.itemsModels.push(new CheckListFilterItemModel('studyDrugCategory', 'Study drug category'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('dosePerAdmin', 'Dose per administration'));
        this.itemsModels.push(new CheckListFilterItemModel('doseUnit', 'Dose unit'));
        this.itemsModels.push(new CheckListFilterItemModel('doseFreq', 'Dose frequency'));
        this.itemsModels.push(new RangeFilterItemModel('totalDailyDose', 'Total daily dose'));
        this.itemsModels.push(new RangeFilterItemModel('plannedDose', 'Planned dose'));
        this.itemsModels.push(new CheckListFilterItemModel('plannedDoseUnits', 'Planned dose units'));
        this.itemsModels.push(new RangeFilterItemModel('plannedNoDaysTreatment', 'Planned No. of days treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('formulation', 'Formulation'));
        this.itemsModels.push(new CheckListFilterItemModel('route', 'Route'));
        this.itemsModels.push(new CheckListFilterItemModel('actionTaken', 'Action taken'));
        this.itemsModels.push(new CheckListFilterItemModel('mainReasonForActionTaken', 'Main reason for action taken'));
        this.itemsModels.push(new CheckListFilterItemModel('mainReasonForActionTakenSpec', 'Main reason for action taken, Specification'));
        this.itemsModels.push(new RangeFilterItemModel('aeNumCausedActionTaken', 'AE number caused action taken'));
        this.itemsModels.push(new CheckListFilterItemModel('aePtCausedActionTaken', 'AE PT caused action taken'));
        this.itemsModels.push(new CheckListFilterItemModel('reasonForTherapy', 'Reason for therapy'));
        this.itemsModels.push(new CheckListFilterItemModel('treatmentCycleDelayed', 'Treatment cycle delayed'));
        this.itemsModels.push(new CheckListFilterItemModel('reasonTreatmentCycleDelayed', 'Reason treatment cycle delayed'));
        this.itemsModels.push(new CheckListFilterItemModel('reasonTreatmentCycleDelayedOther', 'Reason treatment cycle delayed, Other'));
        this.itemsModels.push(new RangeFilterItemModel('aeNumCausedTreatmentCycleDelayed', 'AE number caused treatment cycle delayed'));
        this.itemsModels.push(new CheckListFilterItemModel('aePtCausedTreatmentCycleDelayed', 'AE PT caused treatment cycle delayed'));
        this.itemsModels.push(new CheckListFilterItemModel('medicationCode', 'Medication code'));
        this.itemsModels.push(new CheckListFilterItemModel('medicationDictionaryText', 'Medication dictionary text'));
        this.itemsModels.push(new CheckListFilterItemModel('atcCode', 'ATC code'));
        this.itemsModels.push(new CheckListFilterItemModel('atcDictionaryText', 'ATC dictionary text'));
        this.itemsModels.push(new CheckListFilterItemModel('medicationPt', 'Medication PT'));
        this.itemsModels.push(new CheckListFilterItemModel('medicationGroupingName', 'Medication grouping name'));
        this.itemsModels.push(new CheckListFilterItemModel('activeIngredients', 'Active ingredients'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setDoseFilter(serverModel);
    }

    getDisplayName(): string {
        return 'Dose';
    }

    getName(): string {
        return 'dose';
    }

    getModulePath(): string {
        return 'dose';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasDoseData() : false;
    }
}
