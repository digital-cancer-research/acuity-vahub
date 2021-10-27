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

import {getServerPath} from '../../common/utils/Utils';
import {Response} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';

import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {PopulationFiltersModel} from '../../filters/dataTypes/population/PopulationFiltersModel';
import Dataset = Request.Dataset;

@Injectable()
export class TumourRespTLDPerSubjectHttpService extends BaseChartsHttpService {
    constructor(private http: HttpClient,
                private populationFiltersModel: PopulationFiltersModel) {
        super();
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('tumour', 'linechart-by-lesion-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            tumourFilters: {},
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(datasets: Dataset[],
                countType: any,
                settings: any): Observable<List<IPlot>> {
        const path = getServerPath('tumour', 'linechart-by-lesion');

        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': settings.settings.options['Y_AXIS'],
                    'X_AXIS': settings.settings.options['X_AXIS']
                },
                trellisOptions: settings.settings.trellisOptions
            },
            filterByTrellisOptions: settings.filterByTrellisOptions
        };

        const postData: any = {
            datasets,
            tumourFilters: {},
            populationFilters: this.getSelectedSubjectsPopulationFilter(settings.mainPlotSelection),
            settings: settingsCopy
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: Response) => {
                const data = <any>response;
                return <List<IPlot>>fromJS(data.map((plotItem: any) => {
                    return {
                        plotType: PlotType.SIMPLE_LINEPLOT,
                        trellising: plotItem.trellisedBy,
                        data: plotItem.data
                    };
                }));
            });
    }

    private getSelectedSubjectsPopulationFilter(selection): any {
        const selectedSubjects = selection.map(b => b.get('bars')).first().map(c => c.get('category')).first();
        return {subjectId: {values: selectedSubjects ? [selectedSubjects] : []}};
    }
}
