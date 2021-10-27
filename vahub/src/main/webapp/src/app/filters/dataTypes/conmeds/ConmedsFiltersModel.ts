import {Injectable} from '@angular/core';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel, CheckListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {DatasetViews} from '../../../security/DatasetViews';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {RangeDateFilterItemModel} from '../../components/rangedate/RangeDateFilterItemModel';

@Injectable()
export class ConmedsFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new ListFilterItemModel('medicationName', 'Medication name', false, 4));
        this.itemsModels.push(new ListFilterItemModel('atcCode', 'ATC code'));
        this.itemsModels.push(new RangeFilterItemModel('dose', 'Dose', 0.01));
        this.itemsModels.push(new ListFilterItemModel('doseUnits', 'Dose unit'));
        this.itemsModels.push(new ListFilterItemModel('doseFrequency', 'Dose frequency'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'Start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'End date'));
        this.itemsModels.push(new RangeFilterItemModel('duration', 'Duration (days)', 1));
        this.itemsModels.push(new ListFilterItemModel('ongoing', 'Conmed treatment ongoing'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtConmedStart', 'Study day at conmed start', 1));
        this.itemsModels.push(new RangeFilterItemModel('studyDayAtConmedEnd', 'Study day at conmed end', 1));
        this.itemsModels.push(new ListFilterItemModel('startPriorToRandomisation', 'Start prior to randomisation'));
        this.itemsModels.push(new ListFilterItemModel('endPriorToRandomisation', 'End prior to randomisation'));
        this.itemsModels.push(new ListFilterItemModel('treatmentReason', 'Treatment reason'));
        this.itemsModels.push(new ListFilterItemModel('atcText', 'ATC Text', false, 4));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setConmedsFilter(serverModel);
    }

    getName(): string {
        return 'conmeds';
    }

    getDisplayName(): string {
        return 'Conmeds';
    }

    getModulePath(): string {
        return 'conmeds';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasConmedsData() : false;
    }
}
