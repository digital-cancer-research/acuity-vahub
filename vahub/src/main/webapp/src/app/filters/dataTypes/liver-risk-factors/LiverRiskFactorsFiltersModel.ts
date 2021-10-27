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
export class LiverRiskFactorsFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new RangeFilterItemModel('potentialHysLawCaseNum', 'Potential Hy\'s law case number'));
        this.itemsModels.push(new CheckListFilterItemModel('value', 'Liver risk factor'));
        this.itemsModels.push(new CheckListFilterItemModel('occurrence', 'Liver risk factor occurrence'));
        this.itemsModels.push(new CheckListFilterItemModel('referencePeriod', 'Liver risk factor reference period'));
        this.itemsModels.push(new ListFilterItemModel('details', 'Liver risk factor details'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('stopDate', 'Stop date'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtStart', 'Study day at liver risk factor start'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtStop', 'Study day at liver risk factor stop'));
        this.itemsModels.push(new CheckListFilterItemModel('comment', 'Liver risk factor comment'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setLiverRiskFactorsFilter(serverModel);
    }

    getName(): string {
        return 'liverRisk';
    }

    getDisplayName(): string {
        return 'Liver Risk Factors';
    }

    getModulePath(): string {
        return 'liver-risk';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasLiverRiskFactorsData() : false;
    }
}
