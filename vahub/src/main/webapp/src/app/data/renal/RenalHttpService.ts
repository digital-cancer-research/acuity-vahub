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
import {DynamicAxis} from '../../common/trellising/store';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {PopulationFiltersModel, RenalFiltersModel} from '../../filters/dataTypes/module';
import SortAttrs = Request.SortAttrs;
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import DetailsOnDemandResponse = Request.DetailsOnDemandResponse;

@Injectable()
export class RenalHttpService extends BaseChartsHttpService {
    API = getServerPath('renal');

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected renalFiltersModel: RenalFiltersModel) {
        super();
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('renal', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map(res => res as string[]);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = `${this.API}/x-axis`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map(res => res['xaxis'] as DynamicAxis[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        const path = getServerPath('renal', 'details-on-demand', 'all-csv');

        this.http.post(path, requestBody, {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[]): void {
        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('renal', 'details-on-demand', 'selected-csv');

        this.http.post(path, requestBody, {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {

        const postData: DetailsOnDemandRequest = {
            eventIds: eventIds,
            datasets: currentDatasets,
            start: startRow,
            end: endRow,
            sortAttrs: sortAttrs
        };

        const path = getServerPath('renal', 'details-on-demand', 'data');

        return this.http.post(path, postData).map((res: DetailsOnDemandResponse) => res.dodData);
    }

}
