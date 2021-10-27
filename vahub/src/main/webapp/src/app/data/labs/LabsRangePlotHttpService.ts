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
import {omit} from 'lodash';

import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {LabsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {LabsHttpService} from './LabsHttpService';
import Dataset = Request.Dataset;
import LabStatsRequest = Request.LabStatsRequest;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import LabGroupByOptions = InMemory.LabGroupByOptions;
import GroupByOptionAndParams = Request.GroupByOptionAndParams;
import Lab = Request.Lab;
import TrellisOptions = Request.TrellisOptions;
import LabsRequest = Request.LabsRequest;
import LabsTrellisRequest = Request.LabsTrellisRequest;
import TrellisedRangePlot = InMemory.TrellisedRangePlot;

@Injectable()
export class LabsRangePlotHttpService extends LabsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected labsFiltersModel: LabsFiltersModel) {
        super(http, populationFiltersModel, labsFiltersModel);
    }

    getColorByOptions(datasets: Dataset[], yAxisOption: string): Observable<TrellisOptions<LabGroupByOptions>[]> {
        const path = getServerPath('labs', 'range-series-by-options');

        const postData: LabsRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<LabGroupByOptions>[]);
    }


    getTrellisOptions(currentDatasets: Dataset[],
                      yAxisOption: any): Observable<TrellisOptions<LabGroupByOptions>[]> {

        const path = getServerPath('labs', 'range-trellising');

        const postData: LabsTrellisRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<LabGroupByOptions>[]);
    }


    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {

        const chartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'SERIES_BY': settings.settings.options['COLOR_BY'],
                    'NAME': <GroupByOptionAndParams<Lab, LabGroupByOptions>>{
                        groupByOption: <LabGroupByOptions>'SOURCE_TYPE',
                    }
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: LabStatsRequest = {
            datasets,
            settings: chartSettings,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            statType: 'MEAN'
        };

        return this.http.post(getServerPath('labs', 'mean-range-plot'), JSON.stringify(postData))
            .map((data: TrellisedRangePlot<Lab, LabGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map(value => {
                    return {
                        plotType: PlotType.RANGEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('labs', 'mean-range-selection');

        const chartSettings = {
            settings: {
                options: {
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'SERIES_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selectionItems.map(item => {
                    return {
                        ...omit(item, 'range'),
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                            SERIES_BY: item.selectedItems.COLOR_BY
                        }
                    };
                }),
                settings: chartSettings.settings
            }
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }
}
