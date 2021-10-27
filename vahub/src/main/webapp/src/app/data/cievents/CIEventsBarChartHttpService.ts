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
import {getServerPath} from '../../common/utils/Utils';
import {CIEventsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {CIEventsHttpService} from './CIEventsHttpService';
import {HttpClient} from '@angular/common/http';
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Dataset = Request.Dataset;
import CIEventGroupByOptions = InMemory.CIEventGroupByOptions;
import CIEvent = Request.CIEvent;
import TrellisedBarChart = Request.TrellisedBarChart;

@Injectable()
export class CIEventsBarChartHttpService extends CIEventsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cieventsFiltersModel: CIEventsFiltersModel) {
        super(http, populationFiltersModel, cieventsFiltersModel);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cievents', 'countsbarchart-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[],
                countType: any,
                settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = getServerPath('cievents', 'countsbarchart');

        let xAxis = settings.settings.options['X_AXIS'];


        /**
         * We add "NONE" option for Cerebrovascular on the frontend,
         * @see {@link generateAvailableOptions}
         * although BE can't deserialize it, so we should not send it
         * TODO some more regular solution is required here
         */
        if (xAxis) {
            if ('NONE' === xAxis.groupByOption) {
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
                trellisOptions: []
            },
            filterByTrellisOptions: []
        };

        const postData = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<CIEvent, CIEventGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<CIEvent, CIEventGroupByOptions>) => {
                    return {
                        plotType: PlotType.GROUPED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
