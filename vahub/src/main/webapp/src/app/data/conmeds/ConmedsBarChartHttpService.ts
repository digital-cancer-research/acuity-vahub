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

import {ConmedsHttpService} from './ConmedsHttpService';
import {Injectable} from '@angular/core';
import {getServerPath} from '../../common/utils/Utils';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {IPlot, PlotType} from '../../common/trellising/store';
import {ConmedsFiltersModel} from '../../filters/dataTypes/conmeds/ConmedsFiltersModel';
import {Observable} from 'rxjs/Observable';
import {isEmpty} from 'lodash';
import {fromJS, List} from 'immutable';
import {ISelectionDetail} from '../../common/trellising/store';
import {HttpClient} from '@angular/common/http';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import ConmedsBarChartResponse = Request.ConmedsBarChartResponse;
import Conmed = Request.Conmed;
import ConmedGroupByOptions = InMemory.ConmedGroupByOptions;
import TrellisedBarChart = Request.TrellisedBarChart;
import ConmedsRequest = Request.ConmedsRequest;
import ConmedsTrellisingResponse = Request.ConmedsTrellisingResponse;

const API = getServerPath('conmeds', 'counts-bar-chart');

@Injectable()
export class ConmedsBarChartHttpService extends ConmedsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected conmedsFiltersModel: ConmedsFiltersModel) {
        super(http, populationFiltersModel, conmedsFiltersModel);
    }

    public getXAxisOptions(currentDatasets: Dataset[]): Observable<XAxisOptions> {
        const path = `${API}/x-axis`;
        const postData: ConmedsRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData).map((data: any) => data.xaxis);
    }

    getTrellisOptions(datasets: Dataset[]): Observable<Request.TrellisOptions<ConmedGroupByOptions>[]> {
        const path = `${API}/trellising`;
        const postData: ConmedsRequest = {
            datasets: datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData).map((data: ConmedsTrellisingResponse) => data.trellisOptions);
    }

    getColorByOptions(datasets: Dataset[]): Observable<any[]> {
        const path = `${API}/coloring`;
        const postData: ConmedsRequest = {
            datasets: datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData).map((data: ConmedsTrellisingResponse) => data.trellisOptions);
    }

    getPlotData(currentDatasets: Dataset[], countType: any,
                settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = `${API}/values`;
        const xaxis = settings.settings.options['X_AXIS'];
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': isEmpty(xaxis) ? undefined : xaxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, postData)
            .map((data: ConmedsBarChartResponse) => data.barChartData)
            .map((data: TrellisedBarChart<Conmed, ConmedGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value) => {
                    return {
                        plotType: PlotType.STACKED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data.reverse()
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
            conmedsFilters: this.conmedsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData) as Observable<ISelectionDetail>;
    }

}
