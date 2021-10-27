import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';

@Injectable()
export class SurgicalHistoryFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('surgicalProcedure', 'Medical history term'));
        this.itemsModels.push(new CheckListFilterItemModel('currentMedication', 'Current medication'));
        this.itemsModels.push(new RangeDateFilterItemModel('start', 'Start date'));
        this.itemsModels.push(new ListFilterItemModel('preferredTerm', 'PT name'));
        this.itemsModels.push(new ListFilterItemModel('hlt', 'HLT name'));
        this.itemsModels.push(new ListFilterItemModel('soc', 'SOC name'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setSurgicalHistoryFilter(serverModel);
    }

    getName(): string {
        return 'surgicalHistory';
    }

    getDisplayName(): string {
        return 'SurgicalHistory';
    }

    getModulePath(): string {
        return 'surgical-history';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasSurgicalHistoryData() : false;
    }
}
