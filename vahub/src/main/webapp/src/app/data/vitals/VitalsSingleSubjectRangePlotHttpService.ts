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

import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {PopulationFiltersModel, VitalsFiltersModel} from '../../filters/dataTypes/module';
import {VitalsHttpService} from './VitalsHttpService';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import Dataset = Request.Dataset;
import TrellisedRangePlot = Request.TrellisedRangePlot;
import TrellisOptions = Request.TrellisOptions;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import VitalsMeanRangeValuesRequest = Request.VitalsMeanRangeValuesRequest;
import VitalsTrellisRequest = Request.VitalsTrellisRequest;
import VitalGroupByOptions = InMemory.VitalGroupByOptions;
import Vital = Request.Vital;

@Injectable()
export class VitalsSingleSubjectRangePlotHttpService extends VitalsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected vitalsFiltersModel: VitalsFiltersModel,
                protected singleSubjectModel: SingleSubjectModel) {
        super(http, populationFiltersModel, vitalsFiltersModel);
    }


    private singleSubjectPopulationFilter(): any {
        return {subjectId: {values: this.singleSubjectModel.currentChosenSubject ? [this.singleSubjectModel.currentChosenSubject] : []}};
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: any)
        : Observable<TrellisOptions<VitalGroupByOptions>[]> {

        const path = getServerPath('vitals', 'mean-range-plot', 'trellising');
        const postData: VitalsTrellisRequest = {
            populationFilters: this.singleSubjectPopulationFilter(),
            vitalsFilters: this.vitalsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response) => {
                return <TrellisOptions<VitalGroupByOptions>[]>response;
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('vitals', 'mean-range-plot', 'selection');

        const postData: any = {
            datasets,
            populationFilters: this.singleSubjectPopulationFilter(),
            vitalsFilters: this.vitalsFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response) => {
                return <ISelectionDetail>response;
            });
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<any> {
        const postData: VitalsMeanRangeValuesRequest = {
            datasets,
            settings,
            populationFilters: this.singleSubjectPopulationFilter(),
            vitalsFilters: this.vitalsFiltersModel.transformFiltersToServer()
        };

        return this.http.post(getServerPath('vitals', 'mean-range-plot', 'values'), JSON.stringify(postData))
            .map((data: TrellisedRangePlot<Vital, VitalGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedRangePlot<Vital, VitalGroupByOptions>) => {
                    return {
                        plotType: PlotType.RANGEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
