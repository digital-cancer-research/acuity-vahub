import {Injectable} from '@angular/core';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {
    ListFilterItemModel, CheckListFilterItemModel, RangeDateFilterItemModel
} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CIEventsFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        // add all the basefiltermodel implementations
        this.itemsModels.push(new CheckListFilterItemModel('finalDiagnosis', 'Final diagnosis'));
        this.itemsModels.push(new ListFilterItemModel('otherDiagnosis', 'Specify Other Diagnosis'));
        this.itemsModels.push(new RangeDateFilterItemModel('eventStartDate', 'Event Start Date'));
        this.itemsModels.push(new ListFilterItemModel('eventTerm', 'Event Term'));
        this.itemsModels.push(new ListFilterItemModel('aeNumber', 'Associated AE No.'));
        this.itemsModels.push(new CheckListFilterItemModel('ischemicSymptoms', 'Ischemic Symptoms'));
        this.itemsModels.push(new CheckListFilterItemModel('cieSymptomsDuration', 'Duration of CIE symptoms'));
        this.itemsModels.push(new CheckListFilterItemModel('sympPromtUnsHosp', 'Did the Symptoms Prompt an Uns. Hosp.'));
        this.itemsModels.push(new CheckListFilterItemModel('eventSuspToBeDueToStentTromb', 'Event Susp. to be Due to Stent Thromb.'));
        this.itemsModels.push(new CheckListFilterItemModel('previousEcgAvailable', 'Previous ECG Before Event Available'));
        this.itemsModels.push(new RangeDateFilterItemModel('previousEcgDate', 'Date of Previous ECG'));
        this.itemsModels.push(new CheckListFilterItemModel('ecgAtTheEventTime', 'ECG at the Time of the Event'));
        this.itemsModels.push(new ListFilterItemModel('noEcgAtTheEventTime', 'If no ECG at the Time of Event, Specify'));
        this.itemsModels.push(new CheckListFilterItemModel('localCardiacBiomarkersDrawn', 'Were Local Cardiac Biomarkers Drawn'));
        this.itemsModels.push(new CheckListFilterItemModel('coronaryAngiography', 'Coronary Angiography Performed'));
        this.itemsModels.push(new RangeDateFilterItemModel('angiographyDate', 'Date of Angiography'));
        this.itemsModels.push(new ListFilterItemModel('description1', 'Event Description 1'));
        this.itemsModels.push(new ListFilterItemModel('description2', 'Event Description 2'));
        this.itemsModels.push(new ListFilterItemModel('description3', 'Event Description 3'));
        this.itemsModels.push(new ListFilterItemModel('description4', 'Event Description 4'));
        this.itemsModels.push(new ListFilterItemModel('description5', 'Event Description 5'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setCIEventsFilter(serverModel);
    }

    getName(): string {
        return 'cievents';
    }

    getDisplayName(): string {
        return 'CI Events';
    }

    getModulePath(): string {
        return 'cievents';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasCIEventsData() : false;
    }
}
