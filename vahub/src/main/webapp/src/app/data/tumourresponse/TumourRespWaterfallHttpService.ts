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
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';

import {getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {DynamicAxis, IPlot, PlotSettings, PlotType} from '../../common/trellising/store/ITrellising';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import {RecistFiltersModel} from '../../filters/dataTypes/recist/RecistFiltersModel';
import {handleYAxisOptions} from '../../common/CommonChartUtils';
import Dataset = Request.Dataset;
import TrellisOptions = Request.TrellisOptions;
import ATLGroupByOptions = InMemory.ATLGroupByOptions;

@Injectable()
export class TumourRespWaterfallHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel,
                private recistFiltersModel: RecistFiltersModel) {
        super();
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: any): Observable<any> {

        const path = getServerPath('tumour', 'waterfall-selection');
        const settingsCopy = {
            options: {
                Y_AXIS: handleYAxisOptions(settings.settings.options['Y_AXIS'].groupByOption),
                COLOR_BY: settings.settings.options['COLOR_BY']
            },
            trellisOptions: settings.settings.trellisOptions
        };

        const request = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selectionItems.map(item => {
                    return {
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                        },
                        selectedTrellises: {}
                    };
                }),
                settings: settingsCopy
            }
        };

        return this.http.post(path, request);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        return Observable.of(<XAxisOptions>{
            drugs: [],
            hasRandomization: false,
            options: [
                <any>{
                    groupByOption: 'SUBJECT'
                }
            ]
        });
    }

    getYAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('tumour', 'waterfall-yaxis');

        const postData: any = {
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: any) => {
                const data = response;
                const options = data.assessmentTypes;
                data.weeks.forEach((week) => {
                    options.push(`Week ${week}`);
                });
                return options;
            });
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                      yAxisOption: string): Observable<Request.TrellisOptions<any>[]> {
        return Observable.from([[]]);
    }

    getData(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: Request.TrellisOptions<any>[]): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'waterfall');

        const postData: any = {
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.WATERFALL,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    getPlotData(datasets: Dataset[],
                countType: any,
                settings: any): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'waterfall');

        const settingsCopy = {
            settings: {
                options: {
                    Y_AXIS: handleYAxisOptions(settings.settings.options['Y_AXIS'].groupByOption),
                    COLOR_BY: settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets,
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            settings: settingsCopy,
            populationFilters: this.populationFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.WATERFALL,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    getColorByOptions(
        datasets: Dataset[],
        yAxisOption: any,
        settings: PlotSettings
    ): Observable<TrellisOptions<ATLGroupByOptions>[]> {
        const path = getServerPath('tumour', 'waterfall-colorby-options');
        const plotSettings = {
            settings: {
                options: {
                    Y_AXIS: handleYAxisOptions(yAxisOption.groupByOption || yAxisOption.get('groupByOption'))
                },
                trellisOptions: []
            }
        };
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            settings: plotSettings,
            datasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<ATLGroupByOptions>[]);
    }

    getDetailsOnDemand(): Observable<any> {
        return Observable.of([]);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('tumour', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: this.recistFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }
}
