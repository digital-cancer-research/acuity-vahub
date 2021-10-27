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
import {CerebrovascularFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {CerebrovascularHttpService} from './CerebrovascularHttpService';
import Dataset = Request.Dataset;
import CerebrovascularBarLineChartRequest = Request.CerebrovascularBarLineChartRequest;
import TrellisedOvertime = InMemory.TrellisedOvertime;
import CerebrovascularGroupByOptions = InMemory.CerebrovascularGroupByOptions;
import Cerebrovascular = Request.Cerebrovascular;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class CerebrovascularBarLineChartHttpService extends CerebrovascularHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected eventFiltersModel: CerebrovascularFiltersModel) {
        super(http, populationFiltersModel, eventFiltersModel);
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: string)
        : Observable<TrellisOptions<any>[]> {
        const path = getServerPath('cerebrovascular', 'overtime-trellising');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.eventFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<any>[]);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cerebrovascular', 'overtime-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.eventFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('cerebrovascular', 'overtime');

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: CerebrovascularBarLineChartRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.eventFiltersModel.transformFiltersToServer(),
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>) => {
                    return {
                        plotType: PlotType.BARLINECHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
