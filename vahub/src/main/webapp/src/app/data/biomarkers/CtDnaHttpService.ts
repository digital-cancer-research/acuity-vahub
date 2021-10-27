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
import {fromJS, List, Map} from 'immutable';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {omit} from '@ngrx/store/src/utils';

import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {IMultiSelectionDetail, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store/ITrellising';
import {CtDnaFiltersModel} from '../../filters/dataTypes/ctdna/CtDnaFiltersModel';
import {forkJoin} from "rxjs/observable/forkJoin";
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Dataset = Request.Dataset;
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import TrellisedLineFloatChart = InMemory.TrellisedLineFloatChart;
import CtDna = Request.CtDna;
import CtDnaGroupByOptions = InMemory.CtDnaGroupByOptions;
import OutputLineChartData = InMemory.OutputLineChartData;

@Injectable()
export class CtDnaHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel,
                private ctDnaFiltersModel: CtDnaFiltersModel) {
        super();
    }

    getColorByOptions(datasets: Dataset[], yAxisOption: string): Observable<TrellisOptions<any>[]> {
        const path = getServerPath('ctdna', 'colorby-options');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            datasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<any>[]);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<any> {
        const path = getServerPath('ctdna', 'linechart-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData));
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        const path = getServerPath('ctdna', 'filters-subjects');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getPlotData(datasets: Dataset[], countType: any, settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = getServerPath('ctdna', 'linechart');
        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'COLOR_BY': settings.settings.options['COLOR_BY'],
                    'X_AXIS': settings.settings.options['X_AXIS']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };
        const postData: any = {
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            settings: settingsCopy,
            datasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>[]) => {
                return <List<IPlot>>fromJS(data.map((value) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }


    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>,
                 countType?: any): Observable<IMultiSelectionDetail> {

        const ctDnaPath = getServerPath('ctdna', 'selection');
        const biomarkersPath = getServerPath('biomarker', 'selection-by-subjectids');
        const seriesBySettings = {
            groupByOption: 'SUBJECT_GENE_MUT',
            params: null
        };
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selectionItems.map(item => {
                    return {
                        ...omit(item, 'range'),
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                            COLOR_BY: item.selectedItems.COLOR_BY
                        }
                    };
                }),
                settings: {
                    options: {
                        'X_AXIS': settings.settings.options['X_AXIS'],
                        'COLOR_BY': seriesBySettings
                    },
                    trellisOptions: settings.settings.trellisOptions
                }
            }
        };

        const ctdnaDetails = this.http.post(ctDnaPath, JSON.stringify(postData)).map(res => res as ISelectionDetail);

        return ctdnaDetails.flatMap((ctDnaDetails: ISelectionDetail) => {
            const postDataGenomicProfile = {
                datasets,
                subjectIds: ctDnaDetails.subjectIds
            };
            return this.http.post(biomarkersPath, JSON.stringify(postDataGenomicProfile))
                .map((genomicProfileDetails: ISelectionDetail) => {
                    return {
                        eventIds: Map({ctdna: ctDnaDetails.eventIds, biomarker: genomicProfileDetails.eventIds}),
                        subjectIds: ctDnaDetails.subjectIds,
                        totalSubjects: ctDnaDetails.totalSubjects,
                        totalEvents: Map({ctdna: ctDnaDetails.totalEvents, biomarker: genomicProfileDetails.totalEvents})
                    };
                });
        }).map(res => res as IMultiSelectionDetail);

    }


    getDetailsOnDemand(currentDatasets: Request.Dataset[],
                       eventIds: any,
                       startRow: number, endRow: number,
                       sortAttrs: SortAttrs[]): Observable<any> {
        const requestBody = {
            eventIds: eventIds.get('ctdna'),
            sortAttrs: sortAttrs[0],
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const biomarkersRequestBody = {
            eventIds: eventIds.get('biomarker'),
            sortAttrs: sortAttrs[1],
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const ctDnaPath = getServerPath('ctdna', 'details-on-demand');
        const biomarkersPath = getServerPath('biomarker', 'details-on-demand');

        const detailsOnDemandRequest = [this.http.post(ctDnaPath, JSON.stringify(requestBody)),
            this.http.post(biomarkersPath, JSON.stringify(biomarkersRequestBody))];

        return forkJoin(detailsOnDemandRequest).map(value => {
            return Map({
                ctdna: value[0],
                biomarker: value[1]
            });
        }).map(res => res as any);
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[],
                                selectedTable?: string): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };
        const path = getServerPath(selectedTable, 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[], selectedTable?: string): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            ctDnaFilters: this.ctDnaFiltersModel.transformFiltersToServer(),
            biomarkerFilters: {}, // as the plot has no biomarker filters, but required in case of genomicProfile DoD
            datasets: currentDatasets
        };

        const path = getServerPath(selectedTable, 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
