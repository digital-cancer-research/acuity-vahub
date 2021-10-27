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
import {Observable} from 'rxjs/Observable';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {CardiacFiltersModel} from '../../filters/dataTypes/cardiac/CardiacFiltersModel';
import {HttpClient} from '@angular/common/http';
import {Map} from 'immutable';
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import DetailsOnDemandResponse = Request.DetailsOnDemandResponse;
import CardiacRequest = Request.CardiacRequest;
import Dataset = Request.Dataset;
import SortAttrs = Request.SortAttrs;

@Injectable()
export class CardiacHttpService extends BaseChartsHttpService {
    API = getServerPath('cardiac');

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cardiacFiltersModel: CardiacFiltersModel) {
        super();
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = `${this.API}/filtered-subjects`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[] | Map<string, string[]>,
                       startRow: number,
                       endRow: number,
                       sortBy: SortAttrs[]): Observable<any[]> {
        const path = `${this.API}/details-on-demand/data`;

        const postData = {
            eventIds: eventIds,
            sortAttrs: sortBy,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        return this.http.post(path, postData).map((data: DetailsOnDemandResponse) => data.dodData);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[]): void {
        const path = `${this.API}/details-on-demand/all-csv`;

        const postData: CardiacRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
        };

        this.http.post(path, postData, {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {
        const path = `${this.API}/details-on-demand/selected-csv`;

        const postData: Partial<DetailsOnDemandRequest> = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        this.http.post(path, postData, {responseType: 'blob'})
            .subscribe((response) => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
