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
import {Map} from 'immutable';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {AesFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;

@Injectable()
export class AesHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected aesFiltersModel: AesFiltersModel) {
        super();
    }

    getColorByOptions(datasets: Dataset[]): Observable<any[]> {
        const path = getServerPath('aes', 'colorby-options');
        const request = JSON.stringify({
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer()
        });

        return this.http.post(path, request).map(res => res as any);
    }

    getAssociatedAesNumbersFromEventIds(currentDatasets: Dataset[],
                                        eventIds: string[] | Map<string, string[]>, fromPlot: string): Observable<string[]> {
        const path = getServerPath('aes', 'associatedaesnumbers');

        const postData = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            eventIds: eventIds,
            fromPlot: fromPlot
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        const path = getServerPath('aes', 'filters-subjects');

        const postData = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                           eventIds: string[],
                           startRow: number,
                           endRow: number,
                           sortAttrs: SortAttrs[]): Observable<Map<string, string>[]> {

        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('aes', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as Map<string, string>[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('aes', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('aes', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
