import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class ExposureFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('analyte', 'Analyte'));
        this.itemsModels.push(new ListFilterItemModel('treatmentCycle', 'Cycle'));
        this.itemsModels.push(new ListFilterItemModel('treatment', 'Nominal dose'));
        this.itemsModels.push(new ListFilterItemModel('day', 'Day'));
        this.itemsModels.push(new ListFilterItemModel('visit', 'Visit'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setExposureFilter(serverModel);
    }

    getName(): string {
        return 'exposure';
    }

    getDisplayName(): string {
        return 'Analyte Concentration';
    }

    getModulePath(): string {
        return 'exposure';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasExposureData() : false;
    }
}
