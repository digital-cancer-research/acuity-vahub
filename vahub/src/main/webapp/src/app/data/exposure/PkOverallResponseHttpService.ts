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
import {fromJS, List, Map} from 'immutable';
import {isEqual} from 'lodash';
import {Observable} from 'rxjs/Observable';
import {StudyService} from '../../common/StudyService';

import {
    DynamicAxis,
    IMultiSelectionDetail,
    IPlot,
    ISelectionDetail,
    PlotType,
    YAxisParameters
} from '../../common/trellising/store';
import {handleFilterByTrellisOptions} from '../../common/trellising/store/utils/TrellisFormatting';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {PkOverallResponseFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {SingleSubjectModel} from '../../plugins/refactored-singlesubject/SingleSubjectModel';
import {handleXAxisOptions} from '../../common/CommonChartUtils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {forkJoin} from "rxjs/observable/forkJoin";
import SortAttrs = Request.SortAttrs;
import Dataset = Request.Dataset;
import PkResultGroupByOptions = InMemory.PkResultGroupByOptions;
import PkResult = Request.PkResult;
import TrellisedBoxPlot = Request.TrellisedBoxPlot;

@Injectable()
export class PkOverallResponseHttpService extends BaseChartsHttpService {
    private readonly pkPath = 'pkresultwithresponse';

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected singleSubjectModel: SingleSubjectModel,
                protected pkOverallResponseFiltersModel: PkOverallResponseFiltersModel,
                private studyService: StudyService) {
        super();
    }

    getTrellisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                      yAxisOption: any): Observable<Request.TrellisOptions<any>[]> {
        const path = getServerPath(this.pkPath, 'trellising');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };
        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as Request.TrellisOptions<any>[]);
    }

    getYAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<any> {
        const path = getServerPath(this.pkPath, 'boxplot-options');
        const timepointType =  this.studyService.metadataInfo['pkResultWithResponse']['timepointType'];

        const postData: any = {
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            timepointType: timepointType
        };
        return this.http.post(path, JSON.stringify(postData));
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath(this.pkPath, 'boxplot-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((response: any) => {
                const options = isEqual(response.assessmentTypes, ['BEST_CHANGE'])
                    ? ['BEST_RESPONSE']
                    : response.assessmentTypes;
                response.weeks.forEach((week) => {
                    options.push(`Overall response, week ${week}`);
                });
                return options;
            });
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {

        const params = {TIMESTAMP_TYPE: this.studyService.metadataInfo['pkResultWithResponse']['timepointType']};
        const yAxis = {
            groupByOption: this.studyService.metadataInfo['pkResultWithResponse']['availableYAxisOptions'][0],
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
                    'X_AXIS': handleXAxisOptions(settings.settings.options['X_AXIS'].groupByOption)
                },
                trellisOptions: trellisOptions
            },
            filterByTrellisOptions:
            // TODO handleFilterByTrellisOptions should not be called directly from TrellisFormatting
            // TODO it should be called from Trellising
                handleFilterByTrellisOptions(settings.settings.options['Y_AXIS'].params.trellisingParams)
        };
        const postData = {
            datasets,
            settings: settingsCopy,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
        };

        return this.http.post(getServerPath(this.pkPath, 'boxplot'), JSON.stringify(postData))
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
                 settings: Request.ChartGroupByOptionsFiltered<string, string>): Observable<IMultiSelectionDetail> {
        const path = getServerPath(this.pkPath, 'boxplot-selection');
        const recistPath = getServerPath('tumour', 'selection-by-subjectids');
        const yAxis = {
            groupByOption: this.studyService.metadataInfo['pkResultWithResponse']['availableYAxisOptions'][0],
            params: {}
        };

        const params = {TIMESTAMP_TYPE: this.studyService.metadataInfo['pkResultWithResponse']['timepointType']};

        const settingsCopy = {
            settings: {
                options: {
                    'Y_AXIS': yAxis,
                    'X_AXIS': handleXAxisOptions(settings.settings.options['X_AXIS'].groupByOption)
                },
                trellisOptions: [{groupByOption: settings.settings.trellisOptions[0].groupByOption, params: {}},
                    {groupByOption: YAxisParameters.MEASUREMENT, params: {}},
                    {groupByOption: YAxisParameters.MEASUREMENT_TIMEPOINT, params: params}]
            }
        };
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems: selection,
                settings: settingsCopy.settings
            }
        };
        const pkResultWithResponsedetails = this.http.post(path, JSON.stringify(postData));

        return pkResultWithResponsedetails.flatMap((pkOverallResponse: ISelectionDetail) => {
            const postDataRecist = {
                datasets,
                subjectIds: pkOverallResponse.subjectIds
            };
            return this.http.post(recistPath, JSON.stringify(postDataRecist))
                .map((recistDetails: ISelectionDetail) => {
                    return {
                        eventIds: Map({pkResultWithResponse: pkOverallResponse.eventIds, 'recist-pk': recistDetails.eventIds}),
                        subjectIds: pkOverallResponse.subjectIds,
                        totalSubjects: pkOverallResponse.totalSubjects,
                        totalEvents: Map({pkResultWithResponse: pkOverallResponse.totalEvents, 'recist-pk': recistDetails.totalEvents})
                    };
                });
        }).map(res => res as IMultiSelectionDetail);
    }

    getSubjectsInFilters(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<string[]> {
        const path = getServerPath(this.pkPath, 'filters-subjects');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as string[]);
    }

    getDetailsOnDemand(currentDatasets: Request.AcuityObjectIdentityWithPermission[],
                       eventIds: Map<string, any>,
                       startRow: number,
                       endRow: number,
                       sortBy: SortAttrs[]): Observable<any> {
        const pkResultWithResponseDefaultSort = [
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
        const recistDefaultSortOrder = [
            {
                sortBy: 'subjectId',
                reversed: false
            }, {
                sortBy: 'assessmentWeek',
                reversed: false
            },
        ];
        const pkResultSortOrder = isEqual(sortBy[0], []) ? pkResultWithResponseDefaultSort : sortBy[0];
        const recistSortOrder = isEqual(sortBy[1], []) ? recistDefaultSortOrder : sortBy[1];
        const pkResultWithResponseRequestBody = {
            eventIds: eventIds.get('pkResultWithResponse'),
            sortAttrs: pkResultSortOrder,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };
        const recistRequestBody = {
            eventIds: eventIds.get('recist-pk'),
            pkIds: eventIds.get('pkResultWithResponse'),
            sortAttrs: recistSortOrder,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };
        const pkResultWithResponsePath = getServerPath(this.pkPath, 'details-on-demand');
        const recistPath = getServerPath(this.pkPath, 'recist-details-on-demand');

        const pkResultWithResponseDetailsOnDemandRequest =
            this.http.post(pkResultWithResponsePath, JSON.stringify(pkResultWithResponseRequestBody));
        const recistDetailsOnDemandRequest = this.http.post(recistPath, JSON.stringify(recistRequestBody));
        return forkJoin([pkResultWithResponseDetailsOnDemandRequest, recistDetailsOnDemandRequest]).map(value => {
            return Map({
                pkResultWithResponse: value[0],
                'recist-pk': value[1]
            });
        }).map(res => res as any);
    }

    downloadDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], eventIds: string[],
                                selectedTable?: string): void {

        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };
        const path = selectedTable === 'pkResultWithResponse' ?
            getServerPath(this.pkPath, 'download-selected-details-on-demand') :
            getServerPath(this.pkPath, 'download-recist-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadAllDetailsOnDemandData(currentDatasets: Request.AcuityObjectIdentityWithPermission[], selectedTable?: string): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            pkResultFilters: this.pkOverallResponseFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = selectedTable === 'pkResultWithResponse' ?
            getServerPath(this.pkPath, 'download-details-on-demand') :
            getServerPath(this.pkPath, 'download-recist-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}

