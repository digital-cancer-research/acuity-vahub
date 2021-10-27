import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class DoseProportionalityFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('analyte', 'Analyte'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setDoseProportionalityFilter(serverModel);
    }

    getName(): string {
        return 'pkResult';
    }

    getDisplayName(): string {
        return 'Dose Proportionality';
    }

    getModulePath(): string {
        return 'pkresult';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasPkResultData() : false;
    }
}
