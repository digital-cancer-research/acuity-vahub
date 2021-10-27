/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Injectable} from '@angular/core';
import {Store} from '@ngrx/store';

import {AbstractEventFiltersModel} from '../AbstractEventFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {ListFilterItemModel, RangeDateFilterItemModel, RangeFilterItemModel} from '../../components/module';

import {DatasetViews} from '../../../security/DatasetViews';
import {getServerPath} from '../../../common/utils/Utils';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {getCurrentPlotSettings} from '../../../common/trellising/store/reducer/TrellisingReducer';
import {transformFiltersSettings} from '../../../common/trellising/store/utils/TrellisFormatting';
import {PlotSettings} from '../../../common/trellising/store';

@Injectable()
export class TumourResponseFiltersModel extends AbstractEventFiltersModel {

    public liverLabNames: ListFilterItemModel;
    currentPlotSettings: PlotSettings;

    constructor(populationFiltersModel: PopulationFiltersModel,
                filterHttpService: FilterHttpService,
                filterEventService: FilterEventService,
                datasetViews: DatasetViews,
                private _store: Store<ApplicationState>) {
        super(populationFiltersModel, filterHttpService, filterEventService, datasetViews);

        this._store.select(getCurrentPlotSettings).subscribe(plotSettings => {
            this.currentPlotSettings = plotSettings;
        });

        this.itemsModels.push(new ListFilterItemModel('chemoTherapyStatus', 'Chemotherapy therapy status'));
        this.itemsModels.push(new ListFilterItemModel('radioTherapyStatus', 'Radiotherapy therapy status'));
        this.itemsModels.push(new ListFilterItemModel('therapyDescription', 'Therapy description'));
        this.itemsModels.push(new ListFilterItemModel('chemotherapyClass', 'Chemotherapy class'));
        this.itemsModels.push(new ListFilterItemModel('chemotherapyBestResponse', 'Chemotherapy best response'));
        this.itemsModels.push(new ListFilterItemModel('reasonForChemotherapyFailure', 'Reason for chemotherapy failure'));
        this.itemsModels.push(new RangeFilterItemModel('radiationDose', 'Radiation dose', 1));
        this.itemsModels.push(new RangeFilterItemModel('numberOfChemotherapyCycles', 'Number of chemotherapy cycles', 1));
        this.itemsModels.push(new RangeDateFilterItemModel('diagnosisDate', 'Diagnosis date'));
        this.itemsModels.push(new RangeDateFilterItemModel('recentProgressionDate', 'Date of most recent progression prior to study'));
        this.itemsModels.push(new RangeFilterItemModel('daysFromDiagnosisDate', 'Days from original diagnosis to first dose', 1));
    }

    emitEvent(serverModel: any): void {
        this.filterEventService.setTumourResponseFilter(serverModel);
    }

    getName(): string {
        return 'therapy';
    }

    getDisplayName(): string {
        return 'Previous Lines';
    }

    getModulePath(): string {
        return 'tumour';
    }
    getFilterPath(): string {
        return 'therapy-filters';
    }

    isVisible(): boolean {
        return this.datasetViews ? this.datasetViews.hasTumourResponseData() : false;
    }

    protected makeFilterRequest(manuallyApplied: boolean): void {
        console.log('Sending ' + this.getName() + ' filters request');

        const filtersName = this.getName() + 'Filters';

        this.pendingRequest = this.filterHttpService.getEventFiltersObservable(
            getServerPath(this.getModulePath(), this.getFilterPath()),
            this.populationFiltersModel.transformFiltersToServer(false),
            this.transformFiltersToServer(manuallyApplied),
            filtersName,
            'therapiesSettings',
            transformFiltersSettings(this.currentPlotSettings)
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
    }
}
