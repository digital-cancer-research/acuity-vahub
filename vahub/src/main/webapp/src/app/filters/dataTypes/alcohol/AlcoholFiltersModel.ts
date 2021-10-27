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
import {CheckListFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';

@Injectable()
export class AlcoholFiltersModel extends AbstractEventFiltersModel {

  constructor(populationFiltersModel: PopulationFiltersModel,
              filterHttpService: FilterHttpService,
              filterEventService: FilterEventService,
              datasetViews: DatasetViews) {
    super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

    this.itemsModels.push(new ListFilterItemModel('substanceCategory', 'Substance category'));
    this.itemsModels.push(new CheckListFilterItemModel('substanceUseOccurrence', 'Substance use occurrence'));
    this.itemsModels.push(new ListFilterItemModel('substanceType', 'Type of substance'));
    this.itemsModels.push(new ListFilterItemModel('otherSubstanceTypeSpec', 'Other substance type specification'));
    this.itemsModels.push(new CheckListFilterItemModel('substanceTypeUseOccurrence', 'Substance type use occurrence'));
    this.itemsModels.push(new RangeFilterItemModel('substanceConsumption', 'Substance consumption'));
    this.itemsModels.push(new ListFilterItemModel('frequency', 'Frequency'));
    this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
    this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));

  }

  emitEvent(serverModel: any): void {
    this.filterEventService.setAlcoholFilter(serverModel);
  }

  getName(): string {
    return 'alcohol';
  }

  getDisplayName(): string {
    return 'Alcohol';
  }

  getModulePath(): string {
    return 'alcohol';
  }

  isVisible(): boolean {
    return this.datasetViews ? this.datasetViews.hasAlcoholData() : false;
  }
}
