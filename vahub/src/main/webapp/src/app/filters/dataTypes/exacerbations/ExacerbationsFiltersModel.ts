import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {
    CheckListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class ExacerbationsFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new CheckListFilterItemModel('exacerbationClassification', 'Exacerbation classification'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtStart', 'Days on study at start', 1));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudyAtEnd', 'Days on study at end', 1));
        this.itemsModels.push(new RangeFilterItemModel('duration', 'Duration (Days)', 1));
        this.itemsModels.push(new CheckListFilterItemModel('startPriorToRandomisation', 'Start prior to randomisation'));
        this.itemsModels.push(new CheckListFilterItemModel('endPriorToRandomisation', 'End prior to randomisation'));
        this.itemsModels.push(new CheckListFilterItemModel('hospitalisation', 'Hospitalisation'));
        this.itemsModels.push(new CheckListFilterItemModel('emergencyRoomVisit', 'Emergency room visit'));
        this.itemsModels.push(new CheckListFilterItemModel('antibioticsTreatment', 'Antibiotics treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('depotCorticosteroidTreatment', 'Depot corticosteroid treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('systemicCorticosteroidTreatment', 'Systemic corticosteroid treatment'));
        this.itemsModels.push(new CheckListFilterItemModel('increasedInhaledCorticosteroidTreatment', 'Increased inhaled corticosteroid treatment'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setExacerbationsFilter(serverModel);
    }

    getName(): string {
        return 'exacerbation';
    }

    getDisplayName(): string {
        return 'Exacerbations';
    }

    getModulePath(): string {
        return 'respiratory';
    }

    protected getFilterPath(): string {
        return 'exacerbation/filters';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasExacerbationsData() : false;
    }
}
