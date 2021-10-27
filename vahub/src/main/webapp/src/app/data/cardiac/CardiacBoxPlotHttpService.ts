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
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';

import {DynamicAxis, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {CardiacFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {CardiacHttpService} from './CardiacHttpService';
import {HttpClient} from '@angular/common/http';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Cardiac = Request.Cardiac;
import CardiacGroupByOptions = InMemory.CardiacGroupByOptions;

@Injectable()
export class CardiacBoxPlotHttpService extends CardiacHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cardiacFiltersModel: CardiacFiltersModel) {
        super(http, populationFiltersModel, cardiacFiltersModel);
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[], yAxisOption: any):
        Observable<Request.TrellisOptions<InMemory.CardiacGroupByOptions>[]> {
        const path = `${this.API}/measurements-over-time-chart/trellising`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, postData).map(response => response as Request.TrellisOptions<InMemory.CardiacGroupByOptions>[]);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = `${this.API}/measurements-over-time-chart/x-axis`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData)
            .map(res => res as DynamicAxis[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = `${this.API}/measurements-over-time-chart/selection`;

        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, postData) as Observable<ISelectionDetail>;
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {
        const postData: any = {
            datasets, settings,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer()
        };

        const path = `${this.API}/measurements-over-time-chart/values`;

        return this.http.post(path, postData)
            .map((data: any) => <List<IPlot>>fromJS(
                data.map((value: InMemory.TrellisedBoxPlot<Cardiac, CardiacGroupByOptions>) => {
                    return {
                        plotType: PlotType.BOXPLOT,
                        trellising: value.trellisedBy,
                        data: value.stats
                    };
                }))
            );
    }
}
