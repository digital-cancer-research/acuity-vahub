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
import {DynamicAxis, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {fromJS, List} from 'immutable';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {ExacerbationsFiltersModel} from '../../filters/dataTypes/exacerbations/ExacerbationsFiltersModel';
import {ExacerbationsHttpService} from './ExacerbationsHttpService';
import Dataset = Request.Dataset;
import ExacerbationRequest = Request.ExacerbationRequest;

import ExacerbationColorByOptionsResponse = Request.ExacerbationColorByOptionsResponse;
import TrellisOptions = Request.TrellisOptions;
import TrellisedBarChart = Request.TrellisedBarChart;
import ExacerbationBarChartResponse = Request.ExacerbationBarChartResponse;
import Exacerbation = Request.Exacerbation;
import ExacerbationGroupByOptions = InMemory.ExacerbationGroupByOptions;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;

const API = getServerPath('respiratory', 'exacerbation', 'bar-chart');

@Injectable()
export class ExacerbationsGroupedBarChartHttpService extends ExacerbationsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected exacerbationsFiltersModel: ExacerbationsFiltersModel) {
        super(http, populationFiltersModel, exacerbationsFiltersModel);
    }

    public getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('respiratory', 'exacerbation', 'bar-chart', 'x-axis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res['xaxis'] as DynamicAxis[]);
    }

    getPlotData(datasets: Dataset[], countType: any, settings: ChartGroupByOptionsFiltered<string, string>): Observable<List<IPlot>> {
        const path = `${API}/values`;
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': this.getPreparedXAxisDataForServer(settings.settings.options['X_AXIS']),
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
        .map((data: ExacerbationBarChartResponse) => data.barChartData)
        .map((data: TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>[]) => {
            return <List<IPlot>>fromJS(data.map((value) => {
                return {
                    plotType: PlotType.GROUPED_BARCHART,
                    trellising: value.trellisedBy,
                    data: value.data
                };
            }));
        });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = `${API}/selection`;
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': this.getPreparedXAxisDataForServer(settings.settings.options['X_AXIS']),
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            }
        };

        const postData = {
            datasets,
            selection: {
                selectionItems: selectionItems,
                settings: barChartSettings.settings
            },
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData)).map((res => res as  ISelectionDetail));
    }

    getColorByOptions(datasets: Dataset[]): Observable<TrellisOptions<any>[]> {
        const path = `${API}/color-by-options`;
        const postData: ExacerbationRequest = {
            datasets: datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
        .map((data: ExacerbationColorByOptionsResponse) => data.trellisOptions);
    }
}
