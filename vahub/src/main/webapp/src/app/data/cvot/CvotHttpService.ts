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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {CvotFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {ISelectionDetail} from '../../common/trellising/store';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import CvotEndpointGroupByOptions = InMemory.CvotEndpointGroupByOptions;

@Injectable()
export class CvotHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cvotFiltersModel: CvotFiltersModel) {
        super();
    }

    getColorByOptions(datasets: Dataset[], yAxisOption: string): Observable<TrellisOptions<CvotEndpointGroupByOptions>[]> {
        // const path = getServerPath('cievents', 'color-by');
        const path = getServerPath('cvotendpoint', 'colorby-options');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotFiltersModel: this.cvotFiltersModel.transformFiltersToServer(),
            datasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<CvotEndpointGroupByOptions>[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('cvotendpoint', 'selection');

        let xAxis = settings.settings.options['X_AXIS'];

        /**
         * We add "NONE" option for Cerebrovascular on the frontend,
         * @see {@link generateAvailableOptions}
         * although BE can't deserialize it, so we should not send it
         */
        if (xAxis) {
            if (xAxis.groupByOption === 'NONE') {
                // making it undefined will exclude it from request
                xAxis = undefined;
            }
        }

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': xAxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            }
        };

        const postData: any = {
            datasets,
            selection: {
                selectionItems: selectionItems,
                settings: barChartSettings.settings
            },
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        const path = getServerPath('cvotendpoint', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };


        return this.http.post(path, JSON.stringify(postData));
    }

    getDetailsOnDemand(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[], startRow: number,
                       endRow: number, sortBy: SortAttrs[]): Observable<Map<string, string>[]> {

        const requestBody = {
            eventIds: eventIds,
            sortAttrs: sortBy,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('cvotendpoint', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as Map<string, string>[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('cvotendpoint', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[]): void {
        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('cvotendpoint', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
