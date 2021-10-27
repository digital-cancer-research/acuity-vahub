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
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {LiverFunctionFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {DynamicAxis} from '../../common/trellising/store/ITrellising';
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;

@Injectable()
export class LiverHttpService extends BaseChartsHttpService {
    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected liverFiltersModel: LiverFunctionFiltersModel) {
        super();
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        return Observable.from([[<DynamicAxis>{
            value: 'Max. normalised total bilirubin',
            intarg: null,
            stringarg: null
        }]]);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('liver', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            liverFilters: this.liverFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[],
                           startRow: number, endRow: number, sortBy: string, sortDirection: string)
        : Observable<Map<string, string>[]> {


        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: [{sortBy: sortBy, reversed: sortDirection === 'asc' ? false : true}],
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('liver', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as Map<string, string>[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            liverFilters: this.liverFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('liver', 'download-details-on-demand');

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

        const path = getServerPath('liver', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
