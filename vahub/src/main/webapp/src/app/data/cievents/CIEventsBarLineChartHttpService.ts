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
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store/ITrellising';
import {getServerPath} from '../../common/utils/Utils';
import {CIEventsFiltersModel} from '../../filters/dataTypes/cievents/CIEventsFiltersModel';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import {CIEventsHttpService} from './CIEventsHttpService';
import Dataset = Request.Dataset;
import CIEventBarChartRequest = Request.CIEventBarChartRequest;
import TrellisedOvertime = InMemory.TrellisedOvertime;
import CIEventGroupByOptions = InMemory.CIEventGroupByOptions;
import CIEvent = Request.CIEvent;

@Injectable()
export class CIEventsBarLineChartHttpService extends CIEventsHttpService {
    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cieventsFiltersModel: CIEventsFiltersModel) {
        super(http, populationFiltersModel, cieventsFiltersModel);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cievents', 'overtime-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('cievents', 'overtime');

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: []
            },
            filterByTrellisOptions: []
        };

        const postData: CIEventBarChartRequest = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedOvertime<CIEvent, CIEventGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedOvertime<CIEvent, CIEventGroupByOptions>) => {
                    return {
                        plotType: PlotType.BARLINECHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
