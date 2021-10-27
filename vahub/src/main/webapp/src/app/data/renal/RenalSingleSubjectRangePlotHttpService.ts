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
import {PopulationFiltersModel, RenalFiltersModel} from '../../filters/dataTypes/module';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import {RenalHttpService} from './RenalHttpService';
import {omit} from 'lodash';
import Renal = Request.Renal;
import RenalGroupByOptions = InMemory.RenalGroupByOptions;
import TrellisedRangePlot = Request.TrellisedRangePlot;
import RenalMeanRangeChartResponse = Request.RenalMeanRangeChartResponse;

@Injectable()
export class RenalSingleSubjectRangePlotHttpService extends RenalHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected renalFiltersModel: RenalFiltersModel,
                protected singleSubjectModel: SingleSubjectModel) {
        super(http, populationFiltersModel, renalFiltersModel);
        this.API = getServerPath('renal', 'mean-range-chart');
    }

    private singleSubjectPopulationFilter(): any {
        return {subjectId: {values: this.singleSubjectModel.currentChosenSubject ? [this.singleSubjectModel.currentChosenSubject] : []}};
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[], yAxisOption: any)
        : Observable<Request.TrellisOptions<RenalGroupByOptions>[]> {

        const path = `${this.API}/trellising`;

        const postData: any = {
            populationFilters: this.singleSubjectPopulationFilter(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, postData)
            .map((res: Request.RenalTrellisResponse) => res.trellisOptions);
    }

    getSelection(datasets: Request.Dataset[],
                 selectionItems: any,
                 settings: Request.ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = `${this.API}/selection`;
        const chartSettings = {
            options: {
                'X_AXIS': settings.settings.options['X_AXIS'],
                'Y_AXIS': settings.settings.options['Y_AXIS'],
            },
            trellisOptions: settings.settings.trellisOptions
        };
        const postData: any = {
            datasets,
            populationFilters: this.singleSubjectPopulationFilter(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selectionItems.map(item => {
                    return {
                        ...omit(item, 'range'),
                        selectedItems: {
                            X_AXIS: item.selectedItems.X_AXIS,
                            Y_AXIS: item.selectedItems.Y_AXIS,
                        }
                    };
                }),
                settings: chartSettings
            }
        };
        return this.http.post(path, postData).map(res => res as ISelectionDetail);
    }

    getColorByOptions(datasets: Request.Dataset[]): Observable<Request.TrellisOptions<any>[]> {
        const path = `${this.API}/coloring`;
        const postData: Request.RenalRequest = {
            datasets: datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, postData)
            .map((data: Request.RenalTrellisResponse) => data.trellisOptions as Request.TrellisOptions<any>[]);
    }

    getPlotData(datasets: Request.Dataset[],
                countType,
                settings: any): Observable<any> {
        const path = `${this.API}/values`;
        const chartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'SERIES_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };
        const postData = {
            datasets,
            settings: chartSettings,
            populationFilters: this.singleSubjectPopulationFilter(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer()
        };
        return this.http.post(path, postData)
            .map((data: RenalMeanRangeChartResponse) => {
                return <List<IPlot>>fromJS(data.meanRangeChart.map((value: TrellisedRangePlot<Renal, RenalGroupByOptions>) => {
                    return {
                        plotType: PlotType.JOINEDRANGEPLOT,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
