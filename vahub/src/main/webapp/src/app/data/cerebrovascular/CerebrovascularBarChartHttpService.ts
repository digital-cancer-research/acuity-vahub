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
import {CerebrovascularFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {CerebrovascularHttpService} from './CerebrovascularHttpService';
import {HttpClient} from '@angular/common/http';
import Dataset = Request.Dataset;
import TrellisedBarChart = Request.TrellisedBarChart;
import CerebrovascularBarChartRequest = Request.CerebrovascularBarChartRequest;
import Cerebrovascular = Request.Cerebrovascular;
import CerebrovascularGroupByOptions = InMemory.CerebrovascularGroupByOptions;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class CerebrovascularBarChartHttpService extends CerebrovascularHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cerebrovascularFiltersModel: CerebrovascularFiltersModel) {
        super(http, populationFiltersModel, cerebrovascularFiltersModel);
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: string)
        : Observable<TrellisOptions<any>[]> {
        const path = getServerPath('cerebrovascular', 'trellising');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.cerebrovascularFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            countType: yAxisOption
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<any>[]);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cerebrovascular', 'countsbarchart-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.cerebrovascularFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('cerebrovascular', 'countsbarchart');

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
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: CerebrovascularBarChartRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cerebrovascularFilters: this.cerebrovascularFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>) => {
                    return {
                        plotType: PlotType.GROUPED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
