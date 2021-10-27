import {Injectable} from '@angular/core';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {ListFilterItemModel} from '../../components/list/ListFilterItemModel';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {CheckListFilterItemModel} from '../../components/checklist/CheckListFilterItemModel';

@Injectable()
export class DeathFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('deathCause', 'Cause of death'));
        this.itemsModels.push(new RangeFilterItemModel('daysFromFirstDoseToDeath', 'Days from first dose to death', 1));
        this.itemsModels.push(new CheckListFilterItemModel('autopsyPerformed', 'Autopsy performed'));
        this.itemsModels.push(new CheckListFilterItemModel('deathRelatedToDisease', 'Death related to disease under investigation'));
        this.itemsModels.push(new CheckListFilterItemModel('designation', 'Designation'));
        this.itemsModels.push(new ListFilterItemModel('hlt', 'MedDRA HLT'));
        this.itemsModels.push(new ListFilterItemModel('llt', 'MedDRA LLT'));
        this.itemsModels.push(new ListFilterItemModel('pt', 'MedDRA PT'));
        this.itemsModels.push(new ListFilterItemModel('soc', 'MedDRA SOC'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setDeathFilter(serverModel);
    }

    getName(): string {
        return 'death';
    }

    getDisplayName(): string {
        return 'Death';
    }

    getModulePath(): string {
        return 'death';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasDeathData() : false;
    }
}
