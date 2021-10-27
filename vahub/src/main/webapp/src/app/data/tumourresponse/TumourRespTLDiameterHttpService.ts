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

import {downloadData, getServerPath} from '../../common/utils/Utils';
import {Response} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';
import {omit} from 'lodash';

import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;
import TrellisedLineFloatChart = InMemory.TrellisedLineFloatChart;
import OutputLineChartData = InMemory.OutputLineChartData;
import ATLGroupByOptions = InMemory.ATLGroupByOptions;
import AssessedTargetLesion = Request.AssessedTargetLesion;

@Injectable()
export class TumourRespTLDiameterHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel) {
        super();
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('tumour', 'linechart-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: {},
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                      yAxisOption: string): Observable<Request.TrellisOptions<any>[]> {
        return Observable.from([[]]);
    }


    getData(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: Request.TrellisOptions<any>[]): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'linechart');

        const postData: any = {
            tumourFilters: {},
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            // no color by option for this plot, so this is to force to send seriesBy instead of giving it from trellising.
            datasets: currentDatasets,
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>[]) => {
                return <List<IPlot>>fromJS(data.map((value: any) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: [],
                        data: value.data
                    };
                }));
            });
    }

    getPlotData(datasets: Dataset[],
                countType: any,
                settings: any): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'linechart');

        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'X_AXIS': settings.settings.options['X_AXIS']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets,
            tumourFilters: {},
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            settings: settingsCopy
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: any): Observable<any> {
        const path = getServerPath('tumour', 'linechart', 'selection');

        const request = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: {},
            selection: {
                selectionItems: selectionItems.map(item => {
                    return {
                        ...omit(item, 'range'),
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                            Y_AXIS: item.selectedItems.Y_AXIS,
                            SERIES_BY: item.selectedItems.COLOR_BY
                        }
                    };
                }),
                settings: {
                    options: {
                        'Y_AXIS': settings.settings.options['Y_AXIS'],
                        'X_AXIS': settings.settings.options['X_AXIS']
                    },
                    trellisOptions: settings.settings.trellisOptions
                }
            }
        };

        return this.http.post(path, request);
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

        const path = getServerPath('tumour', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as any[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: {},
            datasets: currentDatasets
        };

        const path = getServerPath('tumour', 'download-details-on-demand');

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

        const path = getServerPath('tumour', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
