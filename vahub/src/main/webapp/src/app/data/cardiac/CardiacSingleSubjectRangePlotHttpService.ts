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

import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {CardiacFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import {CardiacHttpService} from './CardiacHttpService';
import {HttpClient} from '@angular/common/http';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import Dataset = Request.Dataset;
import CardiacMeanRangeValuesRequest = Request.CardiacMeanRangeValuesRequest;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Cardiac = Request.Cardiac;
import CardiacGroupByOptions = InMemory.CardiacGroupByOptions;

@Injectable()
export class CardiacSingleSubjectRangePlotHttpService extends CardiacHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cardiacFiltersModel: CardiacFiltersModel,
                protected singleSubjectModel: SingleSubjectModel) {
        super(http, populationFiltersModel, cardiacFiltersModel);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<XAxisOptions> {
        const path = `${this.API}/mean-range-plot/x-axis`;
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, postData).map((res: any) => <XAxisOptions>res);
    }

    private singleSubjectPopulationFilter(): any {
        return {subjectId: {values: this.singleSubjectModel.currentChosenSubject ? [this.singleSubjectModel.currentChosenSubject] : []}};
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[], yAxisOption: any):
        Observable<Request.TrellisOptions<InMemory.CardiacGroupByOptions>[]> {

        const path = `${this.API}/mean-range-plot/trellising`;
        const postData: any = {
            populationFilters: this.singleSubjectPopulationFilter(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, postData).map(res => res as Request.TrellisOptions<InMemory.CardiacGroupByOptions>[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = `${this.API}/mean-range-plot/selection`;

        const postData: any = {
            datasets,
            populationFilters: this.singleSubjectPopulationFilter(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, postData).map(res => res as ISelectionDetail);
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<any> {
        const path = `${this.API}/mean-range-plot/values`;

        const postData: CardiacMeanRangeValuesRequest = {
            datasets,
            settings,
            populationFilters: this.singleSubjectPopulationFilter(),
            cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData)
            .map((data: any) => {
                return <List<IPlot>>fromJS(data.map((value: InMemory.TrellisedRangePlot<Cardiac, CardiacGroupByOptions>) => {
                    return {
                        plotType: PlotType.RANGEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
