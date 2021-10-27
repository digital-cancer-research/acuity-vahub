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
import {IPlot, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {PopulationFiltersModel, RenalFiltersModel} from '../../filters/dataTypes/module';
import {RenalHttpService} from './RenalHttpService';
import Dataset = Request.Dataset;
import TrellisedBoxPlot = InMemory.TrellisedBoxPlot;
import RenalTrellisResponse = Request.RenalTrellisResponse;
import Renal = Request.Renal;
import RenalGroupByOptions = InMemory.RenalGroupByOptions;


@Injectable()
export class RenalBoxPlotHttpService extends RenalHttpService {

    public header: Headers;

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected renalFiltersModel: RenalFiltersModel) {
        super(http, populationFiltersModel, renalFiltersModel);
        this.API = getServerPath('renal', 'creatinine-clearance-box-plot');
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                      yAxisOption: any): Observable<Request.TrellisOptions<RenalGroupByOptions>[]> {
        const path = `${this.API}/trellising`;

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };
        return this.http.post(path, postData)
            .map((response: RenalTrellisResponse) => response.trellisOptions);
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: any) {
        const path = `${this.API}/selection`;
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };
        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                return response;
            });
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<any> {
        const postData = {
            datasets, settings,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            renalFilters: this.renalFiltersModel.transformFiltersToServer()
        };

        const path = `${this.API}/boxplot`;
        return this.http.post(path, JSON.stringify(postData))
            .map(data => {
                return <List<IPlot>>fromJS(data['boxPlotData'].map((value: TrellisedBoxPlot<Renal, RenalGroupByOptions>) => {
                    return {
                        plotType: PlotType.BOXPLOT,
                        trellising: value.trellisedBy,
                        data: value.stats
                    };
                }));
            });
    }
}
