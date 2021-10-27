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
import {HttpClient} from '@angular/common/http';
import {fromJS, List} from 'immutable';

import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;

import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import {IPlot, PlotType} from '../../common/trellising/store/ITrellising';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {BiomarkersFiltersModel} from '../../filters/dataTypes/biomarkers/BiomarkersFiltersModel';
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;
import TrellisedHeatMap = InMemory.TrellisedHeatMap;
import Biomarker = Request.Biomarker;
import BiomarkerGroupByOptions = InMemory.BiomarkerGroupByOptions;

@Injectable()
export class BiomarkersHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private biomarkerFiltersModel: BiomarkersFiltersModel,
                private populationFiltersModel: PopulationFiltersModel) {
        super();
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<any> {
        return Observable.of(<XAxisOptions>{
            drugs: [],
            hasRandomization: false,
            options: [
                <any>{
                    groupByOption: 'SUBJECT_ID'
                }
            ]
        });
    }

    getColorByOptions(datasets: Dataset[]): Observable<any[]> {
        const path = getServerPath('biomarker', 'colorby-options');
        const request = JSON.stringify({
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer()
        });

        return this.http.post(path, request).map(res => res as any[]);
    }

    getSelection(datasets: Dataset[],
                 selection: any,
                 settings: ChartGroupByOptionsFiltered<string, string>,
                 countType?: any): Observable<any> {

        const path = getServerPath('biomarker', 'selection-details');
        const items = [];
        selection.forEach(elem => {
            items.push({
                selectedItems: elem,
                selectedTrellises: {},
            });
        });
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer(),
            datasets: datasets,
            selection: {
                settings: null,
                selectionItems: items
            },
        };

        return this.http.post(path, JSON.stringify(postData));

    }

    getPlotData(datasets: Dataset[], countType: any, settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = getServerPath('biomarker', 'heatmap');
        const postData: any = {
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            datasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value) => {
                    return {
                        plotType: PlotType.HEATMAP,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: string[],
                       startRow: number, endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any[]> {
        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('biomarker', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as any[]);
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('biomarker', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('biomarker', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        const path = getServerPath('biomarker', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getCBioData(currentDatasets: Dataset[],
                eventIds: string[]): Observable<any[]> {
        const requestBody = {
            datasets: currentDatasets,
            biomarkerFilters: this.biomarkerFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            eventIds: eventIds,
        };

        const path = getServerPath('biomarker', 'cbio-details');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as any[]);
    }
}
