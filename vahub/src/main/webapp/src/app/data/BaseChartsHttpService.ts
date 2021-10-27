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
import {List, Map} from 'immutable';

import {DynamicAxis, IPlot, PlotSettings} from '../common/trellising/store';
import {XAxisOptions} from '../common/trellising/store/actions/TrellisingActionCreator';
import {IFiltersServices} from './IFiltersServices';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import TrellisOption = Request.TrellisOption;

@Injectable()
export class BaseChartsHttpService implements IFiltersServices {

    constructor() {
    }

    private getName(): string {
        return (<any>this).constructor.name;
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        console.error(`Method getSubjectsInFilters is not defined in ${this.getName()}`);
        return;
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[] | XAxisOptions> {
        console.error(`Method getXAxisOptions is not defined in ${this.getName()}`);
        return;
    }

    getYAxisOptions(currentDatasets: Dataset[]): Observable<string[]> {
        console.error(`Method getYAxisOptions is not defined in ${this.getName()}`);
        return;
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: string):
        Observable<TrellisOptions<any>[]> | Observable<Request.TrellisOptions<any>[]> {

        console.error(`Method getTrellisOptions is not defined in ${this.getName()}`);
        return;
    }

    getColorByOptions(currentDatasets: Dataset[],
                      yAxisOption: string,
                      settings?: PlotSettings): Observable<Request.TrellisOptions<any>[]> {
                        // TODO please switch to new TrellisOptions class finally - it's the one without 'category' field
        console.error(`Method getColorByOptions is not defined in ${this.getName()}`);
        return;
    }

    // OLD approach
    getData(currentDatasets: Dataset[],
            xAxisOption: DynamicAxis,
            yAxisOption: string,
            trellising: TrellisOptions<any>[]): Observable<List<IPlot>> {
        console.error(`Method getData is not defined in ${this.getName()}`);
        return;
    }

    // NEW approach
    getPlotData(datasets: Dataset[],
                countType: any,
                settings: any): Observable<List<IPlot>> {
        console.error(`Method getPlotData(new trellising) is not defined in ${this.getName()}`);
        return;
    }

    // OLD approach
    getSelectionDetail(currentDatasets: Dataset[],
                       xAxisOption: DynamicAxis,
                       yAxisOption: string,
                       trellising: TrellisOption<any, any>[],
                       series: TrellisOptions<any>[],
                       selection: any): any {
        console.error(`Method getSelectionDetail is not defined in ${this.getName()}`);
        return;
    }

    // NEW approach
    getSelection(datasets: Dataset[],
                 selection: any,
                 settings: ChartGroupByOptionsFiltered<string, string>,
                 countType?: any) { // TODO set AT LEAST SOME generic return type
        console.error(`Method getSelection(new trellis) in not defined in ${this.getName()}`);
        return;
    }

    getDetailsOnDemandData(currentDatasets: Dataset[],
                           eventIds: any[] | Map<string, string[]>,
                           startRow: number,
                           endRow: number,
                           sortBy: string,
                           sortDirection: string): Observable<any[]> {
        console.error(`Method getDetailsOnDemandData is not defined in ${this.getName()}`);
        return;
    }

    // DoD method for new approach with multiple sorting columns
    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: any[] | Map<string, string[]>,
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): (Observable<any[]> | Map<string, string>[]) {
        console.error(`Method getDetailsOnDemand is not defined in ${this.getName()}`);
        return;
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[], selectedTable?: string,
                                   plotSettings?: PlotSettings): void {
        console.error(`Method downloadAllDetailsOnDemandData is not defined in ${this.getName()}`);
        return;
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[],
                                eventIds: any[], selectedTable?: string): void {
        console.error(`Method downloadDetailsOnDemandData is not defined in ${this.getName()}`);
        return;
    }

    getCBioData(currentDatasets: Dataset[], eventIds: any[]): Observable<any> {
        console.error(`Method getCBioData is not defined in ${this.getName()}`);
        return;
    }

    protected getPreparedXAxisDataForServer(xAxisValue: any): any {
        if (xAxisValue && xAxisValue.groupByOption === 'NONE') {
            return;
        }

        return xAxisValue;
    }
}
