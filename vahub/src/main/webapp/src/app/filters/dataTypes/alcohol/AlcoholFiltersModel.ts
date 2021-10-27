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
