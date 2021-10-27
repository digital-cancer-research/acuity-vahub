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
import {fromJS, List} from 'immutable';
import {isEqual, omit} from 'lodash';

import {DynamicAxis, IPlot, ISelectionDetail, PlotSettings, PlotType} from '../../common/trellising/store';
import {PopulationFiltersModel} from '../../filters/dataTypes/module';
import {downloadData, getServerPath, parseNumericalFields} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import {ExposureFiltersModel} from '../../filters/dataTypes/exposure/ExposureFiltersModel';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import ExposureGroupByOptions = InMemory.ExposureGroupByOptions;
import ExposureLineChartRequest = Request.ExposureLineChartRequest;
import TrellisedLineFloatChart = InMemory.TrellisedLineFloatChart;
import Exposure = Request.Exposure;
import OutputLineChartData = InMemory.OutputLineChartData;

@Injectable()
export class ExposureHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel,
                private exposureFiltersModel: ExposureFiltersModel) {
        super();
    }

    getData(currentDatasets: Dataset[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: Request.TrellisOptions<any>[]): Observable<List<IPlot>> {
        const path = getServerPath('exposure', 'concentration-over-time');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>[]) => {
                return <List<IPlot>>fromJS(data.map((value: any) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getPlotData(datasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('exposure', 'concentration-over-time');
        // TODO: get y axis options from metadata and x axis from request, remove checking of seriesBy from this service
        const xAxisSetting = {
            groupByOption: 'TIME_FROM_ADMINISTRATION',
            params: null
        };
        const yAxisSetting = {
            groupByOption: 'ANALYTE_CONCENTRATION',
            params: null
        };
        const plotSettings = {
            settings: {
                options: {
                    X_AXIS: xAxisSetting,
                    Y_AXIS: yAxisSetting,
                    SERIES_BY: settings.settings.options['SERIES_BY'],
                    COLOR_BY: settings.settings.options['COLOR_BY'] || settings.settings.options['SERIES_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets,
            settings: plotSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>[]) => {
                return <List<IPlot>>fromJS(data.map((value: any) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: value.trellisedBy,
                        data: parseNumericalFields(value.data)
                    };
                }));
            });
    }

    getTrellisOptions(currentDatasets: Dataset[],
                      yAxisOption: string): Observable<Request.TrellisOptions<any>[]> {
        const path = getServerPath('exposure', 'trellising');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as Request.TrellisOptions<any>[]);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        // TODO: implement x-axis with endpoint and units
        return Observable.of(<XAxisOptions>{
            drugs: [],
            hasRandomization: false,
            options: [
                <any>{
                    groupByOption: 'TIME_FROM_ADMINISTRATION'
                }
            ]
        });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('exposure', 'selection');
        const isSubjectAvg = settings.settings.options['SERIES_BY'].groupByOption === 'SUBJECT';
        // TODO: get y axis options from metadata and x axis from request, remove checking of seriesBy from this service
        const xAxisSetting = {
            groupByOption: 'TIME_FROM_ADMINISTRATION',
            params: null
        };
        const yAxisSetting = !isSubjectAvg ? {
            groupByOption: 'ANALYTE_CONCENTRATION',
            params: null
        } : undefined;

        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selectionItems.map(item => {
                    const seriesBy = item.selectedItems.COLOR_BY;

                    return {
                        ...omit(item, 'range'),
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                            SERIES_BY: seriesBy
                        }
                    };
                }),
                settings: {
                    options: {
                        'X_AXIS': xAxisSetting,
                        'SERIES_BY': settings.settings.options['SERIES_BY']
                    },
                    trellisOptions: settings.settings.trellisOptions
                },
            }
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as ISelectionDetail);
    }

    getColorByOptions(
        datasets: Dataset[],
        yAxisOption: string,
        settings: PlotSettings
    ): Observable<TrellisOptions<ExposureGroupByOptions>[]> {
        const path = getServerPath('exposure', 'colorby-options');
        const plotSettings = {
            settings: {
                options: {
                    'SERIES_BY': {
                        groupByOption: settings.get('trellisedBy'),
                        params: {}
                    }
                },
                trellisOptions: []
            },
            filterByTrellisOptions: [{}]
        };

        const postData: ExposureLineChartRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets,
            settings: plotSettings
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<ExposureGroupByOptions>[]);
    }

    getDetailsOnDemand(currentDatasets: Request.Dataset[],
                       eventIds: string[],
                       startRow: number, endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {
        const defaultSort = [
            {
                sortBy: 'subjectId',
                reversed: false
            }, {
                sortBy: 'visitNumber',
                reversed: false
            }, {
                sortBy: 'cycle',
                reversed: false
            }, {
                sortBy: 'nominalDay',
                reversed: false
            }, {
                sortBy: 'nominalHour',
                reversed: false
            }, {
                sortBy: 'nominalMinute',
                reversed: false
            }];
        const sortOrder = isEqual(sortAttrs, []) ? defaultSort : sortAttrs;
        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortOrder,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('exposure', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as any[]);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('exposure', 'filters-subjects');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as string[]);
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[]): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('exposure', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exposureFilters: this.exposureFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('exposure', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
