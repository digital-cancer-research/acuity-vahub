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
import {isEmpty} from 'lodash';

import {DynamicAxis, IContinuousSelection, IPlot, ITrellis, ITrellises, PlotSettings, TabId} from '../store';
import {SessionEventService} from '../../../session/module';
import {StudyService} from '../../StudyService';
import {HttpServiceFactory} from '../../../data/HttpServiceFactory';
import {XAxisOptions} from '../store/actions/TrellisingActionCreator';
import SortAttrs = Request.SortAttrs;
import TrellisOptions = Request.TrellisOptions;
import TrellisOption = Request.TrellisOption;

@Injectable()
export class DataService {
    constructor(private session: SessionEventService,
                private studyService: StudyService,
                private httpServiceFactory: HttpServiceFactory) {
    }

    public getTrellisOptions(tabId: TabId, yAxisOption?: string):
        Observable<TrellisOptions<any>[]> | Observable<Request.TrellisOptions<any>[]> {

        return this.httpServiceFactory.getHttpService(tabId).getTrellisOptions(this.session.currentSelectedDatasets, yAxisOption);
    }

    public getColorByOptions(tabId: TabId, yAxisOption?: string, settings?: PlotSettings): Observable<TrellisOptions<any>[]> {
        return this.httpServiceFactory.getHttpService(tabId).getColorByOptions(this.session.currentSelectedDatasets, yAxisOption, settings);
    }

    public getXAxisOptions(tabId: TabId): Observable<DynamicAxis[] | XAxisOptions> {
        return this.httpServiceFactory.getHttpService(tabId).getXAxisOptions(this.session.currentSelectedDatasets);
    }

    public getYAxisOptions(tabId: TabId): Observable<string[]> {
        switch (tabId) {
            case TabId.CARDIAC_BOXPLOT:
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
                return this.getYAxisOptionsFromMetaData('cardiac');
            case TabId.LAB_BOXPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
                return this.getYAxisOptionsFromMetaData('labs');
            case TabId.LAB_SHIFTPLOT:
                return Observable.from([['ACTUAL_VALUE']]);
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            case TabId.LUNG_FUNCTION_BOXPLOT:
                return this.getYAxisOptionsFromMetaData('lungFunction-java');
            case TabId.EXACERBATIONS_OVER_TIME:
                return this.getYOptionFromMetaData('exacerbation', 'availableOverTimeChartYAxisOptions');
            case TabId.EXACERBATIONS_COUNTS:
                return this.getYOptionFromMetaData('exacerbation', 'availableYAxisOptionsForLineChart');
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
                return this.getYOptionFromMetaData('exacerbation', 'availableYAxisOptionsForGroupedBarChart');
            case TabId.RENAL_LABS_BOXPLOT:
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                return this.getYOptionFromMetaData('renal-java', 'yAxisOptionsForBoxPlot');
            case TabId.RENAL_CKD_BARCHART:
                return this.getYOptionFromMetaData('renal-java', 'availableYAxisOptionsForCKDBarChart');
            case TabId.AES_COUNTS_BARCHART:
                return this.getYAxisOptionsFromMetaData('aes');
            case TabId.CEREBROVASCULAR_COUNTS:
                return this.getYAxisOptionsFromMetaData('cerebrovascular');
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                return this.getYOptionFromMetaData('cerebrovascular', 'availableBarLineYAxisOptions');
            case TabId.QT_PROLONGATION:
                return this.getYAxisOptionsFromMetaData('qt-prolongation');
            case TabId.CI_EVENT_COUNTS:
                return this.getYAxisOptionsFromMetaData('cievents');
            case TabId.CI_EVENT_OVERTIME:
                return this.getYOptionFromMetaData('cievents', 'availableBarLineYAxisOptions');
            case TabId.AES_OVER_TIME:
                return this.getYOptionFromMetaData('aes', 'availableBarLineYAxisOptions');
            case TabId.CVOT_ENDPOINTS_COUNTS:
                return this.getYAxisOptionsFromMetaData('cvotEndpoints');
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
                return this.getYOptionFromMetaData('cvotEndpoints', 'availableBarLineYAxisOptions');
            case TabId.CONMEDS_BARCHART:
                return this.getYAxisOptionsFromMetaData('conmeds');
            case TabId.VITALS_BOXPLOT:
                return this
                    .getYOptionFromMetaData('vitals-java', 'availableMeasurementsOverTimeChartYAxisOptions');
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
                return this
                    .getYOptionFromMetaData('vitals-java', 'availableYAxisOptionsForMeanRangePlot');
            case TabId.POPULATION_BARCHART:
            case TabId.POPULATION_TABLE:
                return this.getYAxisOptionsFromMetaData('population');
            case TabId.TL_DIAMETERS_PLOT:
                return this.getYAxisOptionsFromMetaData('tumour');
            // edit if we have special metadata for this
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return this.getYAxisOptionsFromMetaData('tumour-lesion');
            case TabId.LIVER_HYSLAW:
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
                return Observable.from([['Max. normalised AST/ALT']]);
            case TabId.ANALYTE_CONCENTRATION:
                return Observable.from([['ANALYTE_CONCENTRATION_(NG/ML)']]);
            case TabId.BIOMARKERS_HEATMAP_PLOT:
                return Observable.from([['Gene']]);
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return this.httpServiceFactory.getHttpService(tabId).getYAxisOptions(this.session.currentSelectedDatasets);
            // todo: fetch from BE
            case TabId.TUMOUR_RESPONSE_PRIOR_THERAPY:
                return Observable.from([['Subject ID']]);
            case TabId.CTDNA_PLOT:
                return this.getYAxisOptionsFromMetaData('ctdna');
            case TabId.AES_CHORD_DIAGRAM:
                return Observable.of([]);
            default:
                return;
        }
    }

    // OLD approach
    public getData(tabId: TabId, xAxisOption: DynamicAxis, yAxisOption: string, trellising: List<ITrellises>): Observable<List<IPlot>> {
        const service = this.httpServiceFactory.getHttpService(tabId);
        if (service) {
            return service.getData(
                this.session.currentSelectedDatasets,
                xAxisOption,
                yAxisOption,
                trellising ? <TrellisOptions<any>[]>trellising.toJS() : null);
        } else {
            return;
        }
    }

    // NEW approach
    getPlotData(tabId, yAxisOption, settings): any {
        const service = this.httpServiceFactory.getHttpService(tabId);
        if (service) {
            return service.getPlotData(
                this.session.currentSelectedDatasets,
                yAxisOption,
                settings);
        } else {
            return;
        }
    }

    // OLD approach
    public getSelectionDetail(
        tabId: TabId,
        xAxisOption: DynamicAxis,
        yAxisOption: string,
        series: List<ITrellises>,
        trellising: List<ITrellis>,
        selection: IContinuousSelection,
        selectedBars?: Map<string, string>[]
    ): any {
        const service = this.httpServiceFactory.getHttpService(tabId);
        if (service) {
            return service.getSelectionDetail(
                this.session.currentSelectedDatasets,
                xAxisOption,
                yAxisOption,
                trellising ? <TrellisOption<any, any>[]>trellising.toJS() : null,
                series ? <TrellisOptions<any>[]>series.toJS() : null,
                (tabId === TabId.CI_EVENT_COUNTS ||
                tabId === TabId.CI_EVENT_OVERTIME ||
                tabId === TabId.CEREBROVASCULAR_COUNTS ||
                tabId === TabId.CEREBROVASCULAR_EVENTS_OVER_TIME ||
                tabId === TabId.CVOT_ENDPOINTS_COUNTS ||
                tabId === TabId.CVOT_ENDPOINTS_OVER_TIME) ? selectedBars : selection);
        } else {
            return;
        }
    }

    //Method for new approach
    getSelection(tabId: TabId,
                 selection: any,
                 settings: any,
                 yAxisOption: any
    ): any {
        const service = this.httpServiceFactory.getHttpService(tabId);

        if (service) {
            return service.getSelection(
                this.session.currentSelectedDatasets,
                selection,
                settings,
                yAxisOption
            );
        } else {
            return;
        }
    }

    // Method for old approach
    public getDetailsOnDemandData(tabId: TabId,
                                  eventIds: any[] | Map<string, string[]>,
                                  startRow: number,
                                  endRow: number,
                                  sortBy: string,
                                  sortDirection: string): Observable<any> {
        // In case eventIds are empty we don't event need to act, because there's no point in doing so
        if (isEmpty(eventIds)) {
            return Observable.of([] as any);
        }

        const service = this.httpServiceFactory.getHttpService(tabId);

        if (service) {
            return service.getDetailsOnDemandData(this.session.currentSelectedDatasets, eventIds, startRow, endRow, sortBy, sortDirection);
        } else {
            return this.httpServiceFactory.getHttpService(TabId.POPULATION_BARCHART).getDetailsOnDemandData(
                this.session.currentSelectedDatasets, eventIds, startRow, endRow, sortBy, sortDirection);
        }
    }

    // Method for new approach
    public getDetailsOnDemand(tabId: TabId,
                                  eventIds: any[] | Map<string, string[]>,
                                  startRow: number,
                                  endRow: number,
                                  sortAttrs: SortAttrs[]): Observable<any> {
        // In case eventIds are empty we don't event need to act, because there's no point in doing so
        if ( isEmpty(eventIds) ) {
            return Observable.of([] as any);
        }

        const service = this.httpServiceFactory.getHttpService(tabId);
        // TODO please replace casting to Observable<any> with new variant - it's for old approach only
        return service.getDetailsOnDemand(this.session.currentSelectedDatasets, eventIds, startRow, endRow, sortAttrs) as Observable<any>;
    }

    public downloadAllDetailsOnDemandData(tabId: TabId, selectedTable?: string, plotSettings?: PlotSettings): void {
        return this.httpServiceFactory.getHttpService(tabId)
            .downloadAllDetailsOnDemandData(this.session.currentSelectedDatasets, selectedTable, plotSettings);
    }

    public downloadDetailsOnDemandData(tabId: TabId, eventIds: any[], selectedTable?: string): void {
        return this.httpServiceFactory.getHttpService(tabId)
            .downloadDetailsOnDemandData(this.session.currentSelectedDatasets, eventIds, selectedTable);
    }

    private getYOptionFromMetaData(tab: string, option: string): Observable<string[]> {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return Observable.from([[]]);
        }
        return Observable.from([<string[]>this.studyService.metadataInfo[tab][option]]);
    }

    private getYAxisOptionsFromMetaData(tab: string): Observable<string[]> {
        if (!this.studyService.metadataInfo || !this.studyService.metadataInfo[tab]) {
            return Observable.from([[]]);
        }
        return Observable.from([<string[]>this.studyService.metadataInfo[tab].availableYAxisOptions]);
    }

    public getCBioData(tabId: TabId, eventIds: any[]): Observable<any> {
        return this.httpServiceFactory.getHttpService(tabId)
            .getCBioData(this.session.currentSelectedDatasets, eventIds);
    }
}
