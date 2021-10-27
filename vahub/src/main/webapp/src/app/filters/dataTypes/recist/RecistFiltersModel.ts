import {Injectable} from '@angular/core';
import {Store} from '@ngrx/store';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {DatasetViews} from '../../../security/DatasetViews';
import {FilterEventService} from '../../event/FilterEventService';
import {FilterHttpService} from '../../http/FilterHttpService';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {RangeFilterItemModel} from '../../components/range/RangeFilterItemModel';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {
    getCurrentNewColorBy,
    getCurrentNewYAxisOption
} from '../../../common/trellising/store/reducer/TrellisingReducer';
import {getServerPath} from '../../../common/utils/Utils';
import {handleYAxisOptions} from '../../../common/CommonChartUtils';
import {combineLatest} from 'rxjs/observable/combineLatest';
import {YAxisType} from '../../../common/trellising/store';

@Injectable()
export class RecistFiltersModel extends AbstractEventFiltersModel {

    constructor(
        populationFiltersModel: PopulationFiltersModel,
        filterHttpService: FilterHttpService,
        filterEventService: FilterEventService,
        datasetViews: DatasetViews,
        private _store: Store<ApplicationState>) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);
        this.itemsModels.push(new ListFilterItemModel('bestResponse', 'Best overall response', false, 4));
        this.itemsModels.push(new RangeFilterItemModel('bestPercentageChangeFromBaseline',
            'Best % change in sum of target lesion diameters from baseline', 0.01));
        this.itemsModels.push(new ListFilterItemModel('nonTargetLesionsPresent',
            'Non-target lesions present at any assessment', false, 4));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setRecistFilter(serverModel);
    }

    getName(): string {
        return 'tumour';
    }

    getDisplayName(): string {
        return 'Recist';
    }

    getModulePath(): string {
        return 'tumour';
    }

    getFilterPath(): string {
        return 'waterfall-filters';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasTumourResponseData() : false;
    }

    makeFilterRequest(manuallyApplied: boolean): void {
        combineLatest(this._store.select(getCurrentNewColorBy), this._store.select(getCurrentNewYAxisOption))
            .filter(([colorBy, yAxis]) => yAxis && colorBy)
            .take(1)
            .subscribe(([colorBy, yAxis]) => {
                console.log('Sending ' + this.getName() + ' filters request');
                const filtersName = this.getName() + 'Filters';
                const colorByValue = colorBy.get(0);
                const settingsCopy = {
                    settings: {
                        options: {
                            Y_AXIS: handleYAxisOptions(yAxis.get('groupByOption')),
                            COLOR_BY: {
                                groupByOption: colorByValue ?
                                    colorByValue.get('trellisedBy') : YAxisType.BEST_RESPONSE,
                                params: null
                            }
                        },
                        trellisOptions: []

                    },
                    filterByTrellisOptions: [{}]
                };

                this.pendingRequest = this.filterHttpService.getEventFiltersObservable(
                    getServerPath(this.getModulePath(), this.getFilterPath()),
                    this.populationFiltersModel.transformFiltersToServer(false),
                    this.transformFiltersToServer(manuallyApplied),
                    filtersName,
                    'settings',
                    settingsCopy
                )
                    .subscribe(res => {
                        console.log('Got ' + this.getName() + ' filters request');
                        this.transformFiltersFromServer(res);
                        if (this.firstTimeLoaded) {
                            this.hideEmptyFilters(this.datasetViews.getEmptyFilters(this.getName()));
                            this.firstTimeLoaded = false;
                        }
                        this.matchedItemsCount = <number>res.matchedItemsCount;
                        this.filterEventService.setEventFilterEventCount(<number>res.matchedItemsCount);
                        this.loading = false;
                        this.filterEventService.dispatchNewEventFiltersWereApplied();
                    });
            });

    }

}
