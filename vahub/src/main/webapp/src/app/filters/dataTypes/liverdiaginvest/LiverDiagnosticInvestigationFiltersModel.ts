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
export class LiverDiagnosticInvestigationFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('liverDiagInv', 'Liver diagnostic investigation'));
        this.itemsModels.push(new ListFilterItemModel('liverDiagInvSpec', 'Liver diagnostic investigation specification'));
        this.itemsModels.push(new RangeDateFilterItemModel('liverDiagInvDate', 'Liver diagnostic investigation date'));
        this.itemsModels.push(new RangeFilterItemModel('studyDayLiverDiagInv', 'Study day at liver diagnostic investigation'));
        this.itemsModels.push(new ListFilterItemModel('liverDiagInvResult', 'Liver diagnostic investigation results'));
        this.itemsModels.push(new RangeFilterItemModel('potentialHysLawCaseNum', 'Potential Hy\'s law case number'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setLiverDiagnosticInvestigationFilter(serverModel);
    }

    getName(): string {
        return 'liverDiag';
    }

    getDisplayName(): string {
        return 'LiverDiagnosticInvestigation';
    }

    getModulePath(): string {
        return 'liver-diag';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasLiverDiagnosticInvestigationData() : false;
    }
}
