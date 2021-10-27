import {Injectable} from '@angular/core';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {ListFilterItemModel, CheckListFilterItemModel, RangeDateFilterItemModel, RangeFilterItemModel} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class LabsFiltersModel extends AbstractEventFiltersModel {
    static LABCODE_KEY = 'labcode';

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new CheckListFilterItemModel('labCategory', 'Measurement category'));
        this.itemsModels.push(new ListFilterItemModel('labcode', 'Measurement name', false, 4));
        this.itemsModels.push(new RangeDateFilterItemModel('measurementTimePoint', 'Measurement time point'));
        this.itemsModels.push(new RangeFilterItemModel('daysOnStudy', 'Days on study', 1));
        this.itemsModels.push(new RangeFilterItemModel('analysisVisit', 'Analysis visit', 1));
        this.itemsModels.push(new RangeFilterItemModel('visitNumber', 'Visit number', 0.001));
        this.itemsModels.push(new ListFilterItemModel('protocolScheduleTimepoint', 'Protocol Schedule Timepoint'));
        this.itemsModels.push(new CheckListFilterItemModel('studyPeriods', 'Study period'));
        this.itemsModels.push(new RangeFilterItemModel('labValue', 'Result value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('labUnit', 'Result unit', false, 4));
        this.itemsModels.push(new ListFilterItemModel('valueDipstick', 'Laboratory value dipstick'));
        this.itemsModels.push(new RangeFilterItemModel('baselineValue', 'Baseline value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('changeFromBaselineValue', 'Change from baseline', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('percentageChangeFromBaselineValue', 'Percent change from baseline', 0.01));
        this.itemsModels.push(new CheckListFilterItemModel('baselineFlag', 'Baseline flag'));
        this.itemsModels.push(new CheckListFilterItemModel('outOfRefRange', 'Out of ref. range'));
        this.itemsModels.push(new RangeFilterItemModel('refRangeNormValue', 'Ref. range norm. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('labValueOverLowerRefValue', 'Times lower ref. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('labValueOverUpperRefValue', 'Times upper ref. value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('lowerRefValue', 'Lower ref. range value', 0.01));
        this.itemsModels.push(new RangeFilterItemModel('upperRefValue', 'Upper ref. range value', 0.01));
        this.itemsModels.push(new ListFilterItemModel('sourceType', 'Source type', false, 2));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setLabsFilter(serverModel);
    }

    getName(): string {
        return 'labs';
    }

    getDisplayName(): string {
        return 'Lab';
    }

    getModulePath(): string {
        return 'labs';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasLabsData() : false;
    }
}
