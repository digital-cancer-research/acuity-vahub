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

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {fromJS, List} from 'immutable';
import {Observable} from 'rxjs/Observable';
import {DynamicAxis, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {downloadData, getServerPath} from '../../common/utils/Utils';

import {PopulationFiltersModel} from '../../filters/dataTypes/module';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;
import TrellisedBarChart = Request.TrellisedBarChart;
import QtProlongation = Request.QtProlongation;
import QtProlongationGroupByOptions = InMemory.QtProlongationGroupByOptions;

@Injectable()
export class QtProlongationBarChartHttpService extends BaseChartsHttpService {
    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel) {
        super();
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('qt-prolongation', 'countsbarchart');
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
        const request = {
            datasets: currentDatasets,
            qtProlongationFilters: {},
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };
        return this.http.post(path, request)
            .map((data: TrellisedBarChart<QtProlongation, QtProlongationGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value) => {
                    return {
                        plotType: PlotType.STACKED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('qt-prolongation', 'countsbarchart-xaxis');
        const request = JSON.stringify({
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
            datasets: currentDatasets
        });
        return this.http.post(path, request).map(res => res as DynamicAxis[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('qt-prolongation', 'selection');

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            }
        };

        const postData: any = {
            datasets,
            selection: {
                selectionItems: selectionItems,
                settings: barChartSettings.settings
            },
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as ISelectionDetail);
    }

    getColorByOptions(datasets: Dataset[], yAxisOption: string): Observable<Request.TrellisOptions<any>[]> {
        const path = getServerPath('qt-prolongation', 'colorby-options');
        const request = JSON.stringify({
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
            datasets
        });
        return this.http.post(path, request).map(res => res as Request.TrellisOptions<any>[]);
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                      yAxisOption: string): Observable<Request.TrellisOptions<any>[]> {
        const path = getServerPath('qt-prolongation', 'trellising');
        const request = JSON.stringify({
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
            datasets: currentDatasets,
            countType: yAxisOption
        });
        return this.http.post(path, request).map(res => res as Request.TrellisOptions<any>[]);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('qt-prolongation', 'filters-subjects');
        const request = JSON.stringify({
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
            datasets: currentDatasets
        });
        return this.http.post(path, request).map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: string[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {

        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('qt-prolongation', 'details-on-demand');
        return this.http.post(path, requestBody).map(res => res as any[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            qtProlongationFilters: {},
            datasets: currentDatasets
        };

        const path = getServerPath('qt-prolongation', 'download-details-on-demand');

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

        const path = getServerPath('qt-prolongation', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
