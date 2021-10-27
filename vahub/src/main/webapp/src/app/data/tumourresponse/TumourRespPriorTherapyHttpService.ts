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

import {Response} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';

import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store/ITrellising';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {getServerPath} from '../../common/utils/Utils';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import {TumourResponseFiltersModel} from '../../filters/dataTypes/tumourresponse/TumourResponseFiltersModel';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;

@Injectable()
export class TumourRespPriorTherapyHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel,
                private tumourResponseFilterModel: TumourResponseFiltersModel) {
        super();
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        // todo: go to server for this information

        return Observable.of(<XAxisOptions>{
            drugs: [],
            hasRandomization: false,
            options: [
                <any>{
                    groupByOption: 'WEEKS_SINCE_FIRST_TREATMENT'
                }
            ]
        });
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[], yAxisOption: string)
        : Observable<Request.TrellisOptions<any>[]> {
        return Observable.from([[]]);
    }

    getData(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: Request.TrellisOptions<any>[]): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'prior-therapy');

        const postData: any = {
            therapyFilters: this.tumourResponseFilterModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            trellising: [],
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.COLUMNRANGE,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    getPlotData(datasets: Dataset[],
                countType: any,
                settings: ChartGroupByOptionsFiltered<string, string>): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'prior-therapy');
        const colorBy = settings.settings.options['COLOR_BY'];
        // dividing colorBy for option and drug name
        if (colorBy && colorBy.groupByOption.startsWith('MAX_DOSE_PER_ADMIN_OF_DRUG')) {
            const groupByOption = colorBy.groupByOption;
            const i = groupByOption.lastIndexOf('_');
            colorBy.groupByOption = groupByOption.substr(0, i);
            colorBy.params = { 'DRUG_NAME': groupByOption.substr(i + 1) };
        }

        const tocSettings = {
            settings: {
                options: {
                    'COLOR_BY': colorBy
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const therapiesSettings = {
            settings: {
                options: {
                    'SERIES_BY': settings.settings.options['SERIES_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets,
            therapyFilters: this.tumourResponseFilterModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tocSettings,
            therapiesSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.COLUMNRANGE,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    getColorByOptions(datasets: Dataset[]): Observable<any> {
        const path = getServerPath('tumour', 'prior-therapy-toc-colorby-options');

        const request = JSON.stringify({
            datasets,
            tumourFilters: {},
            populationFilters: this.populationFiltersModel.transformFiltersToServer()
        });
        return this.http.post(path, request)
            .map((response: any) => {
                return response.map(option => {
                    if (option.drug) {
                        option.trellisedBy = `MAX_DOSE_PER_ADMIN_OF_DRUG_${option.drug}`;
                    }
                    return option;
                });
        });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: any): Observable<any> {

        const path = getServerPath('tumour', 'prior-therapy-selection');
        const settingsCopy = {
            options: {
                'SERIES_BY': settings.settings.options.SERIES_BY,
                'Y_AXIS': {groupByOption: 'SUBJECT', params: {}},
                'START': {groupByOption: 'START', params: {}},
                'END': {groupByOption: 'END', params: {}}
            },
            trellisOptions: settings.settings.trellisOptions
        };

        const request = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            therapyFilters: this.tumourResponseFilterModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settingsCopy
            }
        };

        return this.http.post(path, request);
    }

    getDetailsOnDemand(): Observable<any> {
        return Observable.of([]);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('tumour', 'therapy-filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            therapyFilters: this.tumourResponseFilterModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

}
