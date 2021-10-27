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

@Injectable()
export class VitalsFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new CheckListFilterItemModel('vitalsMeasurements', 'Measurement name'));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementDate', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysSinceFirstDose', 'Days on study', 1));
        this.itemsModels.push(new RangeFilterItemModel('analysisVisit', 'Analysis visit', 1));
        this.itemsModels.push(new CheckListFilterItemModel('plannedTimePoints', 'Planned time point'));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 0.001));
        this.itemsModels.push(new ListFilterItemModel('scheduleTimepoints', 'Protocol schedule timepoint'));
        this.itemsModels.push(new CheckListFilterItemModel('studyPeriods', 'Study period'));
        this.itemsModels.push(new RangeFilterItemModel('resultValue', 'Result value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('units', 'Result unit'));
        this.itemsModels.push(new RangeFilterItemModel('baseline', 'Baseline value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('changeFromBaseline', 'Change from baseline', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('percentageChangeFromBaseline', 'Percent change from baseline', 0.01));
        this.itemsModels.push(new CheckListFilterItemModel('baselineFlags', 'Baseline flag'));
        this.itemsModels.push(new RangeDateFilterItemModel('lastDoseDate', 'Date of last drug dose'));
        this.itemsModels.push(new ListFilterItemModel('lastDoseAmounts', 'Last drug dose amount'));
        this.itemsModels.push(new ListFilterItemModel('anatomicalLocations', 'Anatomical location'));
        this.itemsModels.push(new ListFilterItemModel('sidesOfInterest', 'Anatomical side of interest'));
        this.itemsModels.push(new ListFilterItemModel('physicalPositions', 'Physical position'));
        this.itemsModels.push(new CheckListFilterItemModel('clinicallySignificant', 'Clinically significant'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setVitalsFilter(serverModel);
    }

    getName(): string {
        return 'vitals';
    }

    getDisplayName(): string {
        return 'Vitals';
    }

    getModulePath(): string {
        return 'vitals';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasVitalsData() : false;
    }
}
