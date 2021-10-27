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
import {isEmpty} from 'lodash';
import {DynamicAxis, IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {RenalHttpService} from './RenalHttpService';
import {PopulationFiltersModel, RenalFiltersModel} from '../../filters/dataTypes/module';
import RenalGroupByOption = InMemory.RenalGroupByOptions;
import RenalBarChartResponse = Request.RenalBarChartResponse;
import TrellisedBarChart = Request.TrellisedBarChart;
import Renal = Request.Renal;

@Injectable()
export class RenalBarChartHttpService extends RenalHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected renalFiltersModel: RenalFiltersModel) {
        super(http, populationFiltersModel, renalFiltersModel);
    }

    getSelection(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                 selectedItems: any,
                 settings: Request.ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('renal', 'ckd-distribution-bar-chart', 'selection');

        this.populationFiltersModel.clearNotAppliedSelectedValues();
        this.renalFiltersModel.clearNotAppliedSelectedValues();


        const xaxis = settings.settings.options['X_AXIS'];
        const renalBarChartSettings = {
            options: {
                'X_AXIS': isEmpty(xaxis) ? undefined : xaxis,
                'COLOR_BY': settings.settings.options['COLOR_BY']
            },
            trellisOptions: settings.settings.trellisOptions
        };
        const renalBarChartSelection = {
            selectionItems: selectedItems,
            settings: renalBarChartSettings
        };

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            selection: renalBarChartSelection
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response) => {
                return response as ISelectionDetail;
            });
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('renal', 'ckd-distribution-bar-chart', 'x-axis');

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                return <DynamicAxis[]>response['xaxis'];
            });
    }

    getTrellisOptions(
        currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<Request.TrellisOptions<RenalGroupByOption>[]> {
        const path = getServerPath('renal', 'ckd-distribution-bar-chart', 'trellising');

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
        };

        return this.http.post(path, postData).map((data: Request.RenalTrellisResponse) => data.trellisOptions);
    }

    getPlotData(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                countType: any,
                settings: Request.ChartGroupByOptionsFiltered<string, string>): Observable<List<IPlot>> {

        const path = getServerPath('renal', 'ckd-distribution-bar-chart', 'values');

        const xaxis = settings.settings.options['X_AXIS'];
        const renalBarChartSettings = {
            filterByTrellisOptions: settings.filterByTrellisOptions,
            settings: {
                options: {
                    'X_AXIS': isEmpty(xaxis) ? undefined : xaxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            }
        };

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            settings: renalBarChartSettings,
            countType: countType.groupByOption,
        };

        return this.http.post(path, postData)
            .map((data: RenalBarChartResponse) => data.barChartData)
            .map((data: TrellisedBarChart<Renal, RenalGroupByOption>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<Renal, RenalGroupByOption>) => {
                    return {
                        plotType: PlotType.STACKED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }

    getColorByOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<Request.TrellisOptions<any>[]> {
        const path = getServerPath('renal', 'ckd-distribution-bar-chart', 'color-by-options');

        const postData = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data) => data['trellisOptions']);
    }
}
