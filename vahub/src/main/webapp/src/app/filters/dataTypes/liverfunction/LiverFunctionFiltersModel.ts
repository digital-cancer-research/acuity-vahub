import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {
    CheckListFilterItemModel,
    ListFilterItemModel,
    RangeDateFilterItemModel,
    RangeFilterItemModel
} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class LiverFunctionFiltersModel extends AbstractEventFiltersModel {

    public liverLabNames: ListFilterItemModel;

    constructor(populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new RangeDateFilterItemModel('measurementTimePoint', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudy', 'Days on study', 1));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 0.001));
        this.itemsModels.push(new RangeFilterItemModel('labValue', 'Result value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('baselineValue', 'Baseline value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('changeFromBaselineValue', 'Change from baseline', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('percentageChangeFromBaselineValue', 'Percent change from baseline', 0.01));
        this.itemsModels.push(new CheckListFilterItemModel('baselineFlag', 'Baseline flag'));
        this.itemsModels.push(new RangeFilterItemModel('refRangeNormValue', 'Ref. range norm. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('labValueOverLowerRefValue', 'Times lower ref. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('labValueOverUpperRefValue', 'Times upper ref. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('lowerRefValue', 'Lower ref. range value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('upperRefValue', 'Upper ref. range value', 0.01));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setLiverFunctionFilter(serverModel);
    }

    getName(): string {
        return 'liver';
    }

    getDisplayName(): string {
        return 'Liver';
    }

    getModulePath(): string {
        return 'liver';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasLiverData() : false;
    }
}
