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

import {Observable} from 'rxjs/Observable';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {ConmedsFiltersModel} from '../../filters/dataTypes/conmeds/ConmedsFiltersModel';
import {Injectable} from '@angular/core';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {HttpClient} from '@angular/common/http';
import DetailsOnDemandResponse = Request.DetailsOnDemandResponse;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import Dataset = Request.Dataset;
import SortAttrs = Request.SortAttrs;

@Injectable()
export class ConmedsHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected conmedsFiltersModel: ConmedsFiltersModel) {
        super();
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('conmeds', 'filtered-subjects');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {

        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('conmeds', 'details-on-demand', 'data');

        return this.http.post(path, JSON.stringify(requestBody))
            .map((data: DetailsOnDemandResponse) => data.dodData);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('conmeds', 'details-on-demand', 'all-csv');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[]): void {
        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('conmeds', 'details-on-demand', 'selected-csv');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
