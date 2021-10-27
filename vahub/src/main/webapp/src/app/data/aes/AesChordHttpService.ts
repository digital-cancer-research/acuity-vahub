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

import {fromJS, List} from 'immutable';
import {Observable} from 'rxjs/Observable';

import {downloadData, getServerPath} from '../../common/utils/Utils';
import {AesFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {DynamicAxis, IPlot, ISelectionDetail, PlotSettings, PlotType} from '../../common/trellising/store';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import SortAttrs = Request.SortAttrs;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import AesChordDiagramRequest = Request.AesChordDiagramRequest;
import TermLevel = InMemory.TermLevel;
import OutputChordDiagramData = InMemory.OutputChordDiagramData;
import AeGroupByOptions = InMemory.AeGroupByOptions;
import Ae = Request.Ae;

@Injectable()
export class AesChordHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected aesFiltersModel: AesFiltersModel) {
        super();
    }

    getPlotData(datasets: Dataset[], countType: any,
                settings: ChartGroupByOptionsFiltered<Ae, AeGroupByOptions>): Observable<List<IPlot>> {
        const path = getServerPath('aes', 'chord');

        const settingsCopy = {
            settings: {
                options: {
                    'SERIES_BY': {
                        groupByOption: settings.settings.options['SERIES_BY']['groupByOption'],
                        params: {}
                    }
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: AesChordDiagramRequest = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            settings: settingsCopy,
            additionalSettings: settings.settings.options['SERIES_BY']['additionalSettings']
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: Map<TermLevel, OutputChordDiagramData>[]) => {
                return <List<IPlot>>fromJS([{
                    plotType: PlotType.CHORD,
                    trellising: [],
                    data: [data]
                }]);
            });
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        return Observable.of([]);
    }

    getColorByOptions(datasets: Dataset[],
                      yAxisOption: string,
                      settings: PlotSettings): Observable<any[]> {
        const path = getServerPath('aes', 'chord/colorby-options');

        const plotSettings = {
            settings: {
                options: {
                    'SERIES_BY': {
                        groupByOption: settings.get('trellisedBy'),
                        params: {}
                    }
                },
                trellisOptions: []
            },
            filterByTrellisOptions: [{}]
        };

        const request = JSON.stringify({
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            datasets,
            settings: plotSettings
        });

        return this.http.post(path, request).map(res => res as any);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('aes', 'chord-selection');
        const settingsCopy = {
            options: {
                START: {
                    groupByOption: 'START',
                    params: {}
                },
                END: {
                    groupByOption: 'END',
                    params: {}
                }
            },
            trellisOptions: settings.settings.trellisOptions
        };
        const additionalSettings = settings.settings.options['SERIES_BY']['additionalSettings'];
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            additionalSettings: additionalSettings.set('termLevel', settings.settings.options['SERIES_BY']['groupByOption']),
            selection: {
                selectionItems,
                settings: settingsCopy
            }
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as any);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any> {
        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('aes', 'chord-details-on-demand');

        return this.http.post(path, requestBody).map(res => res as any[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[], selectedTable: string,
                                   plotSettings: PlotSettings): void {
        const termLevel = plotSettings.get('trellisedBy');
        const additionalSettings = plotSettings.get('trellisOptions').set('termLevel', termLevel);

        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            additionalSettings
        };

        const path = getServerPath('aes', 'chord-download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('aes', 'chord-download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
