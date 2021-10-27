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
import {DynamicAxis, IContinuousSelection, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {LiverFunctionFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {LiverHttpService} from './LiverHttpService';
import {
    translateOptionsToServer,
    translateOptionToServer,
    translateTrellisingFromServer,
    translateValueFromServer
} from './LiverCompatibility';
import TrellisOptions = Request.TrellisOptions;
import Dataset = Request.Dataset;
import LiverGroupByOptions = InMemory.LiverGroupByOptions;
import LiverRequest = Request.LiverRequest;
import HysRequest = Request.HysRequest;
import HysSelectionRequest = Request.HysSelectionRequest;
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import Liver = Request.Liver;

@Injectable()
export class LiverScatterPlotHttpService extends LiverHttpService {
    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected liverFiltersModel: LiverFunctionFiltersModel) {
        super(http, populationFiltersModel, liverFiltersModel);
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: string)
        : Observable<TrellisOptions<LiverGroupByOptions>[]> {

        const path = getServerPath('liver', 'trellising');

        const postData: LiverRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            liverFilters: this.liverFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as TrellisOptions<LiverGroupByOptions>[]).map(res => translateTrellisingFromServer(res));
    }

    // TODO old approach (but uses new backend API)! implement getPlotData instead
    // TODO and please do all the same as for this class in LiverSingleSubjectScatterPlotHttpService
    getData(currentDatasets: Dataset[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: any): Observable<List<IPlot>> {

        const path = getServerPath('liver', 'hysscatter');

        const postData: HysRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            liverFilters: this.liverFiltersModel.transformFiltersToServer(),
            settings: translateOptionsToServer(trellising),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedScatterPlot<Liver, LiverGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: any) => {
                    return {
                        plotType: PlotType.SCATTERPLOT,
                        trellising:  translateTrellisingFromServer(value.trellisedBy),
                        data: translateValueFromServer(value)
                    };
                }));
            });
    }

    // TODO old approach (but uses new backend API)! please implement getSelection instead
    getSelectionDetail(currentDatasets: Dataset[],
                       xAxisOption: DynamicAxis,
                       yAxisOption: string,
                       trellising: any[],   // type was Rest.TrellisOption<Rest.LabsTrellises>[], but these classes were removed
                       series: any[],       // same type before. God forgive me for "any"... TODO set correct class
                       selection: IContinuousSelection): Observable<ISelectionDetail> {

        const path = getServerPath('liver', 'hysscatter-selection');

        const postData: HysSelectionRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            liverFilters: this.liverFiltersModel.transformFiltersToServer(),
            settings: translateOptionToServer(trellising),
            maxX: selection.xMax,
            minX: selection.xMin,
            maxY: selection.yMax,
            minY: selection.yMin,
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }
}
