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
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {DynamicAxis, IContinuousSelection, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Dataset = Request.Dataset;
import TrellisedBarChart = Request.TrellisedBarChart;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import PopulationGroupByOptions = InMemory.PopulationGroupByOptions;
import PopulationBarChartSelectionRequest = Request.PopulationBarChartSelectionRequest;
import TrellisOption = Request.TrellisOption;
import PopulationBarChartRequest = Request.PopulationBarChartRequest;
import Subject = InMemory.Subject;

@Injectable()
export class PopulationHttpService extends BaseChartsHttpService {

    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel) {
        super();
    }

    getPlotData(datasets: Dataset[], countType: any, settings: ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions>) {
        const path = getServerPath('population', 'summary-plot', 'values');
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

        const postData: PopulationBarChartRequest = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption === 'PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED' ?
                'PERCENTAGE_OF_SUBJECTS_100_STACKED' : countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<any, any>[]) => {
                return <List<IPlot>>fromJS(data.map((value) => {
                    return {
                        plotType: PlotType.STACKED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data.reverse()
                    };
                }));
            });
    }

    getColorByOptions(datasets: Dataset[]): Observable<any> {
        const path = getServerPath('population', 'summary-plot', 'color-by-options');
        const request = JSON.stringify({
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer()
        });

        return this.http.post(path, request)
            .map(r => r);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('population', 'summary-plot', 'selection');
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': this.getPreparedXAxisDataForServer(settings.settings.options['X_AXIS']),
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
            populationFilters: this.populationFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response) => {
                return response as ISelectionDetail;
            });
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('population', 'filtered-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[], yAxisOption: string)
        : Observable<TrellisOptions<PopulationGroupByOptions>[]> {
        const path = getServerPath('population', 'summary-plot', 'color-by-options');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            countType: yAxisOption
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<PopulationGroupByOptions>[]);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('population', 'summary-plot', 'x-axis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as DynamicAxis[]);
    }

    // TODO probably does not work (old approach) - check it and remove if so
    getSelectionDetail(currentDatasets: Dataset[],
                       xAxisOption: DynamicAxis,
                       yAxisOption: string,
                       trellising: TrellisOption<any, any>[],
                       series: TrellisOptions<any>[],
                       selection: IContinuousSelection): Observable<ISelectionDetail> {

        const path = getServerPath('population', 'summary-plot', 'selection');
        const postData: PopulationBarChartSelectionRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            // trellising: trellising,
            // series: series,
            // countType: yAxisOption,
            // categoryType: xAxisOption,
            // maxX: selection.xMax,
            // minX: selection.xMin,
            // maxY: selection.yMax,
            // minY: selection.yMin,
            datasets: currentDatasets,
            selection: null
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }

    // TODO probably does not work (old approach) - check it and remove if so
    getData(currentDatasets: Dataset[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: TrellisOptions<any>[]): Observable<List<IPlot>> {

        const path = getServerPath('population', 'summary-plot', 'values');

        const postData = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            trellising: trellising,
            countType: yAxisOption,
            categoryType: xAxisOption,
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<Subject, PopulationGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<Subject, PopulationGroupByOptions>) => {
                    return {
                        plotType: PlotType.STACKED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {

        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            datasets: currentDatasets,
            start: startRow,
            end: endRow,
            sortAttrs: sortAttrs
        };

        const path = getServerPath('population', 'details-on-demand', 'data');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as Map<string, string>[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('population', 'details-on-demand', 'all-csv');

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

        const path = getServerPath('population', 'details-on-demand', 'selected-csv');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }


}
