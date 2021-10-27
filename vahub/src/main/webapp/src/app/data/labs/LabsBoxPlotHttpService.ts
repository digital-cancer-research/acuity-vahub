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
import {LabsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import {LabsHttpService} from './LabsHttpService';
import Dataset = Request.Dataset;
import LabStatsRequest = Request.LabStatsRequest;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import TrellisedBoxPlot = InMemory.TrellisedBoxPlot;
import Lab = Request.Lab;
import LabGroupByOptions = InMemory.LabGroupByOptions;
import LabsTrellisRequest = Request.LabsTrellisRequest;
import LabSelectionRequest = Request.LabSelectionRequest;
import ChartSelectionItemRange = Request.ChartSelectionItemRange;
import LabFilters = Request.LabFilters;
import PopulationFilters = InMemory.PopulationFilters;
import ChartSelection = Request.ChartSelection;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class LabsBoxPlotHttpService extends LabsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected labsFiltersModel: LabsFiltersModel,
                protected singleSubjectModel: SingleSubjectModel) {
        super(http, populationFiltersModel, labsFiltersModel);
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: any)
        : Observable<TrellisOptions<LabGroupByOptions>[]> {

        const path = getServerPath('labs', 'trellising');

        const postData: LabsTrellisRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<LabGroupByOptions>[]);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: ChartSelectionItemRange<Lab, LabGroupByOptions, number>[],
                 settings: ChartGroupByOptionsFiltered<Lab, LabGroupByOptions>): Observable<ISelectionDetail> {
        const path = getServerPath('labs', 'boxplot-selection');

        const postData: LabSelectionRequest = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {
        const postData: LabStatsRequest = {
            datasets, settings,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            statType: 'MEDIAN'
        };

        return this.http.post(getServerPath('labs', 'boxplot'), JSON.stringify(postData))
            .map((data: any) => {
                return fromJS(data.map((value: TrellisedBoxPlot<Lab, LabGroupByOptions>) => {
                    return {
                        plotType: PlotType.BOXPLOT,
                        trellising: value.trellisedBy,
                        data: value.stats
                    };
                }));
            });
    }
}
