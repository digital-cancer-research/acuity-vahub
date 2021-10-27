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
import {isEmpty} from 'lodash';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import TrellisOptions = Request.TrellisOptions;

const API = getServerPath('respiratory', 'exacerbation', 'over-time-line-bar-chart');

@Injectable()
export class ExacerbationsBarLineChartHttpService extends ExacerbationsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected exacerbationsFiltersModel: ExacerbationsFiltersModel) {
        super(http, populationFiltersModel, exacerbationsFiltersModel);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = `${API}/x-axis`;
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getColorByOptions(datasets: Dataset[]): Observable<TrellisOptions<any>[]> {
        const path = `${API}/color-by-options`;
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(data => data as Request.ExacerbationColorByOptionsResponse)
            .map(data => data.trellisOptions);
    }

    getPlotData(currentDatasets: Dataset[],
                countType: any,
                settings: ChartGroupByOptionsFiltered<string, string>): Observable<List<IPlot>> {
        const path = `${API}/values`;
        const overTimeXAxis = settings.settings.options['X_AXIS'];
        if (overTimeXAxis.params) {
            overTimeXAxis.params['BIN_INCL_DURATION'] = countType.groupByOption === 'COUNT_INCLUDING_DURATION';
        }
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': isEmpty(overTimeXAxis) ? undefined : overTimeXAxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: InMemory.TrellisedOvertime<Request.Exacerbation, InMemory.ExacerbationGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map(value => {
                    return {
                        plotType: PlotType.BARLINECHART,
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

        const overTimeXAxis = settings.settings.options['X_AXIS'];
        if (overTimeXAxis.params) {
            overTimeXAxis.params['BIN_INCL_DURATION'] = countType.groupByOption === 'COUNT_INCLUDING_DURATION';
        }
        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': overTimeXAxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };
        const postData: any = {
            datasets,
            selection: {
                selectionItems: selectionItems,
                settings: barChartSettings.settings
            },
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            exacerbationFilters: this.exacerbationsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }
}
