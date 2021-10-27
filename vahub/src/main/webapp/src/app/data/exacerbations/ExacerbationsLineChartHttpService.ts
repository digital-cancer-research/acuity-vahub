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
import {fromJS, List} from 'immutable';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {ExacerbationsFiltersModel} from '../../filters/dataTypes/exacerbations/ExacerbationsFiltersModel';
import {ExacerbationsHttpService} from './ExacerbationsHttpService';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import TrellisedBarChart = Request.TrellisedBarChart;
import ExacerbationRequest = Request.ExacerbationRequest;
import ExacerbationColorByOptionsResponse = Request.ExacerbationColorByOptionsResponse;
import Exacerbation = Request.Exacerbation;
import ExacerbationGroupByOptions = InMemory.ExacerbationGroupByOptions;
import TrellisOptions = Request.TrellisOptions;

const API = getServerPath('respiratory', 'exacerbation', 'on-set-line-chart');

@Injectable()
export class ExacerbationsLineChartHttpService extends ExacerbationsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected exacerbationsFiltersModel: ExacerbationsFiltersModel) {
        super(http, populationFiltersModel, exacerbationsFiltersModel);
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = `${API}/values`;

        const xAxis = settings.settings.options['X_AXIS'];
        if (xAxis.params) {
            xAxis.params['BIN_INCL_DURATION'] = true;
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

        const postData = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            settings: barChartSettings,
            countType: countType.groupByOption
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>) => {
                    return {
                        plotType: PlotType.LINECHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>,
                 countType: any): Observable<ISelectionDetail> {

        const path = `${API}/selection`;
        const xAxis = settings.settings.options['X_AXIS'];

        if (xAxis.params) {
            xAxis.params['BIN_INCL_DURATION'] = true;
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

        const postData = {
            datasets,
            selection: {
                selectionItems: selectionItems,
                settings: barChartSettings.settings
            },
            countType: countType.groupByOption,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response => response as ISelectionDetail));
    }

    public getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = `${API}/x-axis`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);

    }

    getColorByOptions(datasets: Dataset[]): Observable<TrellisOptions<any>[]> {
        const path = `${API}/color-by-options`;
        const postData: ExacerbationRequest = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData)
            .map((data: ExacerbationColorByOptionsResponse) => data.trellisOptions);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('respiratory', 'exacerbation', 'details-on-demand',  'all-csv');

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

        const path = getServerPath('respiratory', 'exacerbation', 'details-on-demand', 'selected-csv');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
