import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel, CheckListFilterItemModel, RangeFilterItemModel, RangeDateFilterItemModel,
    MapListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';
import {GENE_PERCENTAGE} from './BiomarkersFiltersConstants';

@Injectable()
export class BiomarkersFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('gene', 'Gene'));
        this.itemsModels.push(new CheckListFilterItemModel('mutation', 'Alteration Type'));
        this.itemsModels.push(new RangeFilterItemModel(GENE_PERCENTAGE, 'Gene Percentage', 1));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setBiomarkersFilter(serverModel);
    }

    getName(): string {
        return 'biomarker';
    }

    getDisplayName(): string {
        return 'Genomic Profile Filters';
    }

    getModulePath(): string {
        return 'biomarker';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasOncoBiomarkers() : false;
    }
}
