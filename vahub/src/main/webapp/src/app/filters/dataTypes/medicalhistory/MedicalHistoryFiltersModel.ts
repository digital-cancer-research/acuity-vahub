import {Injectable} from '@angular/core';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel, CheckListFilterItemModel, RangeDateFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class MedicalHistoryFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('category', 'Medical history category'));
        this.itemsModels.push(new ListFilterItemModel('term', 'Medical history term'));
        this.itemsModels.push(new CheckListFilterItemModel('conditionStatus', 'Condition status'));
        this.itemsModels.push(new CheckListFilterItemModel('currentMedication', 'Current medication'));
        this.itemsModels.push(new RangeDateFilterItemModel('start', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('end', 'End date'));
        this.itemsModels.push(new ListFilterItemModel('preferredTerm', 'PT name'));
        this.itemsModels.push(new ListFilterItemModel('hlt', 'HLT name'));
        this.itemsModels.push(new ListFilterItemModel('soc', 'SOC name'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setMedicalHistoryFilter(serverModel);
    }

    getName(): string {
        return 'medicalHistory';
    }

    getDisplayName(): string {
        return 'Medical History';
    }

    getModulePath(): string {
        return 'medicalhistory';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasMedicalHistoryData() : false;
    }
}
