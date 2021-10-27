import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel, CheckListFilterItemModel, RangeFilterItemModel, RangeDateFilterItemModel,
    MapListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CvotFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Event Start Date'));
        this.itemsModels.push(new ListFilterItemModel('term', 'Event Term'));
        this.itemsModels.push(new ListFilterItemModel('aeNumber', 'Associated AE No.'));
        this.itemsModels.push(new ListFilterItemModel('category1', 'Event Category 1'));
        this.itemsModels.push(new ListFilterItemModel('category2', 'Event Category 2'));
        this.itemsModels.push(new ListFilterItemModel('category3', 'Event Category 3'));
        this.itemsModels.push(new ListFilterItemModel('description1', 'Event Description 1'));
        this.itemsModels.push(new ListFilterItemModel('description2', 'Event Description 2'));
        this.itemsModels.push(new ListFilterItemModel('description3', 'Event Description 3'));
        // add all the basefiltermodel implementations
         }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCvotFilter(serverModel);
    }

    getName(): string {
        return 'cvotEndpoint';
    }

    getDisplayName(): string {
        return 'Additional CVOT Suspected Endpoint Filters';
    }

    getModulePath(): string {
        return 'cvotendpoint';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCvotData() : false;
    }

    protected getFilterPath(): string {
        return 'filters';
    }
}
