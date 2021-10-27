import {Injectable} from '@angular/core';

import {DatasetViews} from '../../../security/DatasetViews';
import {UserPermissions} from '../../../security/UserPermissions';
import {ListFilterItemModel} from '../../components/module';
import {FilterEventService} from '../../event/FilterEventService';
import {FilterHttpService} from '../../http/FilterHttpService';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';

@Injectable()
export class PkOverallResponseFiltersModel extends AbstractEventFiltersModel {

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews,
                protected userPermissions: UserPermissions) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this.itemsModels.push(new ListFilterItemModel('analyte', 'Analyte'));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setPkResultOverallResponseFilter(serverModel);
    }

    getName(): string {
        return 'pkResultWithResponse';
    }

    getDisplayName(): string {
        return 'PK Response';
    }

    getModulePath(): string {
        return 'pkresultwithresponse';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasPkResultWithResponseData()
            && this.userPermissions.hasViewOncologyPackagePermission() : false;
    }
}
