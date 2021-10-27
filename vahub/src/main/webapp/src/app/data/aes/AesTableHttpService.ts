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
import {AesFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import AesTable = InMemory.AesTable;
import Dataset = Request.Dataset;


@Injectable()
export class AesTableHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected aesFiltersModel: AesFiltersModel) {
    }

    getData(currentDatasets: Dataset[], aeLevel: string): Observable<AesTable[]> {
        const path = getServerPath('aes', 'aes-table');
        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            aeLevel: aeLevel
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as AesTable[]);
    }

    exportData(currentDatasets: Dataset[], aeLevel: string): void {
        const path = getServerPath('aes', 'aes-table-export');
        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            aeLevel: aeLevel
        };

        this.http.post(path, JSON.stringify(postData), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('aes_table.csv', response);
            });
    }
}
