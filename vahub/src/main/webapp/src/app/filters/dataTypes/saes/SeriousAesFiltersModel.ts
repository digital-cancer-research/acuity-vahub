import {Injectable} from '@angular/core';
import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {
    ListFilterItemModel,
    CheckListFilterItemModel,
    RangeFilterItemModel,
    RangeDateFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class SeriousAesFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews
    ) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new RangeFilterItemModel('aeNumber', 'AE number', 1));
        this.itemsModels.push(new CheckListFilterItemModel('ae', 'Adverse event'));
        this.itemsModels.push(new CheckListFilterItemModel('pt', 'Preferred term'));
        this.itemsModels.push(new RangeDateFilterItemModel('startDate', 'AE start date'));
        this.itemsModels.push(new RangeDateFilterItemModel('endDate', 'AE end date'));
        this.itemsModels.push(new RangeFilterItemModel('daysFromFirstDoseToCriteria', 'Days from first dose to AE met criteria', 1));
        this.itemsModels.push(new RangeDateFilterItemModel('findOutDate', 'Date investigator aware of serious AE'));
        this.itemsModels.push(new CheckListFilterItemModel('hospitalizationRequired', 'Requires or prolongs hospitalization'));
        this.itemsModels.push(new CheckListFilterItemModel('congenitalAnomaly', 'Congenital anomaly or birth defect'));
        this.itemsModels.push(new CheckListFilterItemModel('lifeThreatening', 'Life threatening'));
        this.itemsModels.push(new CheckListFilterItemModel('disability', 'Persist. or sign. disability/incapacity'));
        this.itemsModels.push(new CheckListFilterItemModel('otherSeriousEvent', 'Other medically important serious event'));
        this.itemsModels.push(new RangeDateFilterItemModel('hospitalizationDate', 'Date of hospitalization'));
        this.itemsModels.push(new RangeDateFilterItemModel('becomeSeriousDate', 'Date AE met criteria for serious AE'));
        this.itemsModels.push(new RangeDateFilterItemModel('dischargeDate', 'Date of discharge'));
        this.itemsModels.push(new CheckListFilterItemModel('primaryDeathCause', 'Primary cause of death'));
        this.itemsModels.push(new CheckListFilterItemModel('secondaryDeathCause', 'Secondary cause of death'));
        this.itemsModels.push(new ListFilterItemModel('ad', 'Additional Drug'));
        this.itemsModels.push(new ListFilterItemModel('ad1', 'Additional Drug 1'));
        this.itemsModels.push(new CheckListFilterItemModel('causedByAD', 'AE Caused by Additional Drug'));
        this.itemsModels.push(new CheckListFilterItemModel('causedByAD1', 'AE Caused by Additional Drug 1'));
        this.itemsModels.push(new ListFilterItemModel('ad2', 'Additional Drug 2'));
        this.itemsModels.push(new CheckListFilterItemModel('causedByAD2', 'AE Caused by Additional Drug 2'));
        this.itemsModels.push(new ListFilterItemModel('otherMedication', 'Other medication'));
        this.itemsModels.push(new CheckListFilterItemModel('causedByOtherMedication', 'AE caused by other medication'));
        this.itemsModels.push(new CheckListFilterItemModel('resultInDeath', 'Results in death'));
        this.itemsModels.push(new ListFilterItemModel('studyProcedure', 'Study procedure(s)'));
        this.itemsModels.push(new CheckListFilterItemModel('causedByStudy', 'AE caused by study procedure(s)'));
        this.itemsModels.push(new ListFilterItemModel('description', 'AE description'));

    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setSeriousAesFilter(serverModel);
    }

    getName(): string {
        return 'seriousAe';
    }

    getDisplayName(): string {
        return 'Serious Adverse Event';
    }

    getModulePath(): string {
        return 'sae';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasSeriousAesData() : false;
    }
}
