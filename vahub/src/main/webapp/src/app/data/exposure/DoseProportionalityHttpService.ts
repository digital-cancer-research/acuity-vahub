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

import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {fromJS, List} from 'immutable';
import {Observable} from 'rxjs/Observable';
import {StudyService} from '../../common/StudyService';
import {isEqual} from 'lodash';

import {DynamicAxis, IPlot, ISelectionDetail, PlotType, YAxisParameters} from '../../common/trellising/store';
import {handleFilterByTrellisOptions} from '../../common/trellising/store/utils/TrellisFormatting';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {DoseProportionalityFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import Dataset = Request.Dataset;
import SortAttrs = Request.SortAttrs;
import PkResultGroupByOptions = InMemory.PkResultGroupByOptions;
import PkResult = Request.PkResult;
import TrellisedBoxPlot = Request.TrellisedBoxPlot;
import PkResultRequest = Request.PkResultRequest;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class DoseProportionalityHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected doseProportionalityFiltersModel: DoseProportionalityFiltersModel,
                protected singleSubjectModel: SingleSubjectModel,
                private studyService: StudyService) {
        super();
    }

    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: any):
        Observable<TrellisOptions<PkResultGroupByOptions>[]> {
        const path = getServerPath('pkresult', 'trellising');
        const postData: PkResultRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as Request.TrellisOptions<PkResultGroupByOptions>[]);
    }

    getYAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        const path = getServerPath('pkresult', 'boxplot-options');
        const timepointType =  this.studyService.metadataInfo['pkResult']['timepointType'];

        const postData: any = {
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            timepointType: timepointType
        };

        return this.http.post(path, JSON.stringify(postData));
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('pkresult', 'boxplot-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: {},
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as DynamicAxis[]);
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {

        const params = {TIMESTAMP_TYPE: this.studyService.metadataInfo['pkResult']['timepointType']};
        const yAxis = {
            groupByOption: this.studyService.metadataInfo['pkResult']['availableYAxisOptions'][0],
            params: {}
        };
        const trellisOptions = settings.settings.trellisOptions[0] ? [settings.settings.trellisOptions[0],
            {groupByOption: YAxisParameters.MEASUREMENT},
            {
                groupByOption: YAxisParameters.MEASUREMENT_TIMEPOINT,
                params: params
            }
        ] : [];

        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': yAxis,
                    'X_AXIS': settings.settings.options['X_AXIS']
                },
                trellisOptions: trellisOptions
            },
            // TODO handleFilterByTrellisOptions should not be called directly from TrellisFormatting
            // TODO it should be called from Trellising
            filterByTrellisOptions: handleFilterByTrellisOptions(settings.settings.options['Y_AXIS'].params.trellisingParams)
        };
        const postData = {
            datasets,
            settings: settingsCopy,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer()
        };

        return this.http.post(getServerPath('pkresult', 'boxplot'), JSON.stringify(postData))
            .map(data => data as TrellisedBoxPlot<PkResult, PkResultGroupByOptions>[])
            .map(data => {
                return fromJS(data.map((value: TrellisedBoxPlot<PkResult, PkResultGroupByOptions>) => {
                    return {
                        plotType: PlotType.BOXPLOT,
                        trellising: value.trellisedBy.filter(tr => tr.trellisedBy === 'ANALYTE'),
                        data: value.stats
                    };
                }));
            });
    }

    getSelection(datasets: Request.Dataset[],
                 selection: any,
                 settings: Request.ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {
        const path = getServerPath('pkresult', 'boxplot-selection');
        const yAxis = {
            groupByOption: this.studyService.metadataInfo['pkResult']['availableYAxisOptions'][0],
            params: {}
        };

        const params = {TIMESTAMP_TYPE: this.studyService.metadataInfo['pkResult']['timepointType']};

        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': yAxis,
                    'X_AXIS': settings.settings.options['X_AXIS']
                },
                trellisOptions: [{groupByOption: settings.settings.trellisOptions[0].groupByOption, params: {}},
                    {groupByOption: YAxisParameters.MEASUREMENT, params: {}},
                    {groupByOption: YAxisParameters.MEASUREMENT_TIMEPOINT, params: params}]
            }
        };
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selection,
                settings: settingsCopy.settings
            }
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath('pkresult', 'filters-subjects');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                       eventIds: string[],
                       startRow: number,
                       endRow: number,
                       sortBy: SortAttrs[]): Observable<any[]> {
        const defaultSort = [
            {
                sortBy: 'subjectId',
                reversed: false
            }, {
                sortBy: 'analyte',
                reversed: false
            }, {
                sortBy: 'cycle',
                reversed: false
            }, {
                sortBy: 'protocolScheduleStartDay',
                reversed: false
            }, {
                sortBy: 'parameter',
                reversed: false
            }];
        const sortOrder = isEqual(sortBy, []) ? defaultSort : sortBy;
        const requestBody = {
            eventIds: eventIds,
            sortAttrs: sortOrder,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('pkresult', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as any[]);
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                                eventIds: string[]): void {
        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('pkresult', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.doseProportionalityFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('pkresult', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

}

