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
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {ExacerbationsFiltersModel} from '../../filters/dataTypes/exacerbations/ExacerbationsFiltersModel';
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import Dataset = Request.Dataset;
import ExacerbationRequest = Request.ExacerbationRequest;
import SortAttrs = Request.SortAttrs;

const API = getServerPath('respiratory', 'exacerbation');
const DoDAPI = `${API}/details-on-demand`;

@Injectable()
export class ExacerbationsHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected exacerbationsFiltersModel: ExacerbationsFiltersModel) {
        super();
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        const path = `${API}/filtered-subjects`;

        const postData: ExacerbationRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {
        const postData: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };
        const path = `${DoDAPI}/data`;

        return this.http.post(path, JSON.stringify(postData)).map((data: any) => data.dodData);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[]): void {
        const postData: ExacerbationRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };
        const path = `${DoDAPI}/all-csv`;

        this.http.post(path, JSON.stringify(postData), {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {
        const postData: Partial<DetailsOnDemandRequest> = {
            eventIds: eventIds,
            datasets: currentDatasets
        };
        const path = `${DoDAPI}/selected-csv`;

        this.http.post(path, JSON.stringify(postData), {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
