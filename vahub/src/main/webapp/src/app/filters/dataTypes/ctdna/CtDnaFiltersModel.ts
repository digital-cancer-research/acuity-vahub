import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {ListFilterItemModel} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';
import {UnselectedCheckListFilterItemModel} from '../../components/unselectedchecklist/UnselectedCheckListFilterItemModel';

@Injectable()
export class CtDnaFiltersModel extends AbstractEventFiltersModel {
    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('gene', 'Gene'));
        this.itemsModels.push(new ListFilterItemModel('mutation', 'Mutation'));
        this.itemsModels.push(new UnselectedCheckListFilterItemModel('trackedMutation', 'Tracked mutations', datasetViews));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCtDnaFilter(serverModel);
    }

    getName(): string {
        return 'ctDna';
    }

    getDisplayName(): string {
        return 'ctDNA Filters';
    }

    getModulePath(): string {
        return 'ctdna';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCtDnaData() : false;
    }
}
