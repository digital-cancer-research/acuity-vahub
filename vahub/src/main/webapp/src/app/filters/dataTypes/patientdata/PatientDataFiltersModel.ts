import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {
    ListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel,
    CheckListFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';
//to apply this filters uncomment lines 43-49 in TimelineFilterComponent.html
@Injectable()
export class PatientDataFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('measurementName', 'Measurement name'));
        this.itemsModels.push(new RangeFilterItemModel('value', 'Value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('unit', 'Unit'));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementDate', 'Measurement date'));
        this.itemsModels.push(new RangeDateFilterItemModel('reportDate', 'Report date'));
        this.itemsModels.push(new ListFilterItemModel('sourceType', 'Source type'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setPatientDataFilter(serverModel);
    }

    getName(): string {
        return 'patientData';
    }

    getDisplayName(): string {
        return 'PatientData';
    }

    getModulePath(): string {
        return 'timeline/patientdata';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasPatientData() : false;
    }
}
