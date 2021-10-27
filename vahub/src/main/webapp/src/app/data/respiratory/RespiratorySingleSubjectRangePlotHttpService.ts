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
import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {LungFunctionFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {RespiratoryHttpService} from './RespiratoryHttpService';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import LungFunctionValuesRequest = Request.LungFunctionValuesRequest;
import TrellisedRangePlot = Request.TrellisedRangePlot;
import LungFunctionGroupByOptions = InMemory.LungFunctionGroupByOptions;
import LungFunction = Request.LungFunction;
import LungFunctionTrellisRequest = Request.LungFunctionTrellisRequest;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class RespiratorySingleSubjectRangePlotHttpService extends RespiratoryHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected lungFunctionFiltersModel: LungFunctionFiltersModel,
                protected singleSubjectModel: SingleSubjectModel) {
        super(http, populationFiltersModel, lungFunctionFiltersModel);
        this.API = getServerPath('respiratory', 'lung-function', 'mean-range-chart');
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {
        const path = `${this.API}/values`;
        const postData: LungFunctionValuesRequest = {
            datasets,
            settings,
            populationFilters: this.singleSubjectPopulationFilter(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData)
            .map((data: TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>) => {
                    return {
                        plotType: PlotType.RANGEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: any)
        : Observable<TrellisOptions<LungFunctionGroupByOptions>[]> {

        const path = `${this.API}/trellising`;

        const postData: LungFunctionTrellisRequest = {
            populationFilters: this.singleSubjectPopulationFilter(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            resultType: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, postData).map(res => res as TrellisOptions<LungFunctionGroupByOptions>[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {
        const path = `${this.API}/selection`;
        const postData: any = {
            datasets,
            populationFilters: this.singleSubjectPopulationFilter(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, postData).map(res => res as ISelectionDetail);
    }

    private singleSubjectPopulationFilter(): any {
        return {subjectId: {values: this.singleSubjectModel.currentChosenSubject ? [this.singleSubjectModel.currentChosenSubject] : []}};
    }
}
