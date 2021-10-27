import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {
    ListFilterItemModel,
    CheckListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';
import {StudySpecificFilterModel} from '../../components/studySpecific/StudySpecificFilterModel';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class LungFunctionFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('measurementName', 'Measurement name', false, 4));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementTimePoint', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudy', 'Days on study', 1));
        this.itemsModels.push(new ListFilterItemModel('protocolScheduleTimepoint', 'Protocol Schedule Timepoint'));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 0.001));
        this.itemsModels.push(new RangeFilterItemModel('resultValue', 'Result value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('resultUnit', 'Result unit', false, 4));
        this.itemsModels.push(new RangeFilterItemModel('baselineValue', 'Baseline value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('changeFromBaseline', 'Change from baseline', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('percentChangeFromBaseline', 'Percent change from baseline', 0.01));
        this.itemsModels.push(new CheckListFilterItemModel('baselineFlag', 'Baseline flag'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setLungFunctionFilter(serverModel);
    }

    getName(): string {
        return 'lungFunction';
    }

    getDisplayName(): string {
        return 'Respiratory';
    }

    getModulePath(): string {
        return 'respiratory';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasRespiratryData() : false;
    }

    protected getFilterPath(): string {
        return 'lung-function/filters';
    }
}
