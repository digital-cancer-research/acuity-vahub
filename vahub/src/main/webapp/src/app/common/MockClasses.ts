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

/* tslint:disable */

import {BaseEventService} from './module';
import {Response, ResponseOptions} from '@angular/http';
import {PlotSettings, TabId} from './trellising/store';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';
import {XAxisOptions} from './trellising/store/actions/TrellisingActionCreator';
import {List} from 'immutable';
import {ScaleTypes} from './trellising/store';
import Dataset = Request.Dataset;

export class MockSessionEventService {
    currentSelectedDatasets = [{
        id: 1,
        type: 'DetectDataset'
    }];
    userDetails: {
        userId: 'A user ID'
    };
    subjectUserInfo: Subject<any> = new Subject();
    currentDatasets: Subject<any> = new Subject();

    addSubscription(key: string, subscription: Subject<any>): void {
    }

    setSelectedDataset(selectedStudy: Dataset): void {
    }
}

export class MockTrellisingDispatcher {
    localResetNotification(): void {

    }

    updatePlotSettings(settings: PlotSettings): void {

    }

    updateZoom(): void {

    }

    updateScale(scaleType: ScaleTypes): void {

    }
}

export class MockTrellisingObservables {

}

export class MockTrellising {
    updateTrellisingOptions(): void {

    }

    updatePlotSettings(settings: List<any>): void {

    }
}

export class MockTimelineDispatcher {
    globalResetNotification(): void {

    }

    localResetNotification(timelineId: any): void {

    }
}

export class MockTimelineConfigService {
    getInitialState(): any {
        return ['sometrack'];
    }
}

export class MockFilterModel {

    transformFiltersToServer(): string {
        return '';
    }

    getFilters(): void {
    }

    clearNotAppliedSelectedValues(): void {
        return;
    }

    getName(): string {
        return '';
    }

    getDisplayName(): string {
        return '';
    }

    addCohortFilter(): void {
    }

    renameCohortIfApplied(oldName: string, newName: string): void {
    }

    getSubjectIdFilter(): any {
        return {};
    }

    removeCohortFilter(): void {
    }

    getCohortEditorFilters(): any {
        return [1];
    }

    hasCohortSelected(name: string): boolean {
        return true;
    }

    isVisible(): boolean {
        return true;
    }
    toggleSubjectIdFilterVisibility(): any {
    }
}

export class MockRouter {
    changes = new MockSubject();
    events = {
        subscribe: function (): void {
        }
    };

    navigateByUrl(url: string): void {
    }

    navigate(route: Array<string>): void {
    }

    createUrlTree(commands: any[], segment?: MockRouteSegment): any {
    }
}

export class MockRouteSegment {

}


export class MockStudyService {
    public metadataInfo: any;

    constructor() {
        this.metadataInfo = {
            'aes': {
                'hasData': true,
                'hasCustomGroups': true,
                'aeSeverityType': 'CTC_GRADES',
                'detailsOnDemandColumns': ['test', 'test2', 'test3'],
                'availableXAxisOptions': [
                    'PT',
                    'HLT',
                    'SOC'
                ],
                'availableYAxisOptions': [
                    'COUNT_OF_SUBJECTS',
                    'PERCENTAGE_OF_ALL_SUBJECTS',
                    'PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT'
                ]
            },
            'biomarker': {
                'hasData': true
            },
            'dose': {
                'hasData': true
            },
            'dose_2': {
                'hasData': true,
                'detailsOnDemandColumns': []
            },
            'dose_3': {
                'hasData': true,
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'dose_4': {
                'hasData': false,
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'population': {
                'hasData': true,
                'availableXAxisOptions': [
                    'STUDY_ID',
                    'STUDY_NAME',
                    'STUDY_PART_ID',
                    'PLANNED_TREATMENT_ARM',
                    'ACTUAL_TREATMENT_ARM',
                    'CENTER_NUMBER',
                    'COUNTRY_AND_REGION',
                    'SEX',
                    'RACE',
                    'FIRST_TREATMENT_DATE',
                    'DEATH',
                    'AGE',
                    'HEIGHT',
                    'WEIGHT'
                ],
                'availableYAxisOptions': [
                    'COUNT_OF_SUBJECTS',
                    'PERCENTAGE_OF_ALL_SUBJECTS',
                    'PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED'
                ],
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'cardiac': {
                'hasData': true,
                'availableXAxisOptions': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ],
                'detailsOnDemandColumns': ['test', 'test2', 'test3']

            },
            'vitals': {
                'hasData': true,
                'availableXAxisOptions': [
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ],
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'vitals-java': {
                'hasData': true,
                'availableXAxisOptions': [
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ],
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'renal': {
                'hasData': false,
                'xAxisOptionsForBoxPlot': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'yAxisOptionsForBoxPlot': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ]
            },
            'lungFunction': {
                'hasData': false,
                'availableXAxisOptions': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ]
            },
            'labs': {
                'hasData': false,
                'availableXAxisOptions': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ]
            },
            'oncology': {
                'hasData': false,
                'availableXAxisOptions': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ],
                'spotfireModules': [
                    {id: 12, name: 'spotfire1', module_type: 'Oncology'},
                    {id: 121, name: 'spotfire3', module_type: 'Oncology'}
                ]
            },
            'respiratory': {
                'hasData': false,
                'availableXAxisOptions': [
                    'VISIT_NUMBER',
                    'STUDY_DEFINED_WEEK'
                ],
                'availableYAxisOptions': [
                    'ABSOLUTE_CHANGE_FROM_BASELINE',
                    'PERCENTAGE_CHANGE_FROM_BASELINE',
                    'ACTUAL_VALUE'
                ],
                'spotfireModules': [
                    {id: 22, name: 'spotfire2', module_type: 'Respiratory'}
                ]
            },
            'exacerbation': {
                'count': 260,
                'hasData': true,
                'availableXAxisOptions': [
                    'DAYS_SINCE_FIRST_TREATMENT',
                    'WEEKS_SINCE_FIRST_TREATMENT'
                ],
                'availableYAxisOptions': [
                    'COUNT_INCLUDING_DURATION',
                    'COUNT_START_DATES_ONLY'
                ],
                'detailsOnDemandColumns': ['test', 'test2', 'test3']
            },
            'cievents': {
                'detailsOnDemandColumns': [
                    'studyId',
                    'part',
                    'subjectId',
                    'aeNumber',
                    'startDate',
                    'term'
                ],
                'detailsOnDemandTitledColumns': {
                    'studyId': 'Study ID',
                    'part': 'Study Part',
                    'subjectId': 'Subject ID',
                    'aeNumber': 'Associated AE No.',
                    'startDate': 'Event Start Date',
                    'term': 'Event Term',
                },
                'availableYAxisOptions': [
                    'COUNT_OF_SUBJECTS',
                    'COUNT_OF_EVENTS',
                    'PERCENTAGE_OF_ALL_SUBJECTS',
                    'PERCENTAGE_OF_ALL_EVENTS',
                    'PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT',
                    'PERCENTAGE_OF_EVENTS_WITHIN_PLOT'
                ],
                'count': 152,
                'hasData': true
            }
        };
    }

    getMetadataInfoObservable(currentDatasetsId: any): Observable<any> {
        let res = [{testMetadata: 'test'}];
        return Observable.of(res);
    }

    getCombinedStudyInfo(): Observable<any> {
        let res = {
            roisWithPermission: this.getUsersAcls(),
            studySelectionDatasetInfo: this._getStudyDatasetInfo(),
            studyWarnings: this._getStudyWarnings()
        };
        return Observable.of(res);
    }

    private getUsersAcls(): any {
        return [{
            'typeForJackson': 'com.acuity.va.security.acl.domain.DetectDataset',
            'type': 'com.acuity.va.security.acl.domain.DetectDataset',
            'supertype': 'com.acuity.va.security.acl.domain.Dataset',
            'id': 401234524,
            'name': 'dataset',
            'rolePermissionMask': 524240,
            'viewPermissionMask': 3,
            'lockdown': false,
            'inherited': false,
            'moduleType': 'detect',
            'drugProgramme': 'Drug X',
            'clinicalStudyCode': 'DummyData',
            'clinicalStudyName': 'DummyData',
            'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.DetectDataset',
            'type': 'com.acuity.va.security.acl.domain.DetectDataset',
            'supertype': 'com.acuity.va.security.acl.domain.Dataset',
            'id': 401234664, 'name': 'dataset', 'rolePermissionMask': 3, 'viewPermissionMask': 3, 'lockdown': false,
            'inherited': false, 'moduleType': 'detect', 'drugProgramme': 'Drug X',
            'clinicalStudyCode': 'Dummy2000', 'clinicalStudyName': 'Dummy2000', 'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.DetectDataset',
            'type': 'com.acuity.va.security.acl.domain.DetectDataset',
            'supertype': 'com.acuity.va.security.acl.domain.Dataset',
            'id': 401234684,
            'name': 'dataset',
            'permissionMask': 32,
            'lockdown': false,
            'inherited': false,
            'moduleType': 'detect',
            'drugProgramme': 'Drug X',
            'clinicalStudyCode': 'Dummy1000',
            'clinicalStudyName': 'Dummy1000',
            'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.DetectDataset',
            'type': 'com.acuity.va.security.acl.domain.DetectDataset',
            'supertype': 'com.acuity.va.security.acl.domain.Dataset',
            'id': 4012324525,
            'name': 'dataset',
            'rolePermissionMask': 3,
            'viewPermissionMask': 3,
            'lockdown': false,
            'inherited': false,
            'moduleType': 'detect',
            'drugProgramme': 'Drug X',
            'clinicalStudyCode': 'DummyCombined',
            'clinicalStudyName': 'DummyCombined',
            'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.AcuityDataset',
            'type': 'com.acuity.va.security.acl.domain.AcuityDataset',
            'supertype': 'com.acuity.va.security.acl.domain.Dataset',
            'id': 145,
            'name': 'Dummy study STUDYID001',
            'rolePermissionMask': 3,
            'viewPermissionMask': 3,
            'lockdown': false,
            'inherited': false,
            'drugProgramme': 'STDY4321',
            'clinicalStudyCode': 'STDY4321 Dummy Instance',
            'clinicalStudyName': 'STDY4321 Dummy Instance',
            'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.AcuityDrugProgramme',
            'type': 'com.acuity.va.security.acl.domain.AcuityDrugProgramme',
            'supertype': 'com.acuity.va.security.acl.domain.DrugProgramme',
            'id': 1451, 'name': 'STDY4321', 'lockdown': false, 'inherited': false, 'autoGeneratedId': false
        }, {
            'typeForJackson': 'com.acuity.va.security.acl.domain.AcuityClinicalStudy',
            'type': 'com.acuity.va.security.acl.domain.AcuityClinicalStudy',
            'supertype': 'com.acuity.va.security.acl.domain.ClinicalStudy',
            'id': 1452, 'name': 'Dummy study STUDYID001', 'lockdown': false,
            'inherited': false, 'autoGeneratedId': false
        }];
    }

    private _getStudyDatasetInfo(): any {
        return [
            {datasetId: '401234524', numberOfSubjects: 197, dataCutoffDate: new Date()},
            {datesetId: '401234664', numberOfSubjects: 1987, dataCutoffDate: new Date()},
            {datesetId: '401234684', numberOfSubjects: 378, dataCutoffDate: new Date()},
            {datesetId: '4012324525', numberOfSubjects: 0, dataCutoffDate: new Date()}
        ];
    }

    private _getStudyWarnings(): any {
        return [
            {studyId: 'DummyData', blinded: true, randomised: true, forRegulatoryPurposes: true},
            {studyId: 'Dummy2000', blinded: true, randomised: false, forRegulatoryPurposes: false},
            {studyId: 'Dummy1000', blinded: false, randomised: false, forRegulatoryPurposes: false}
        ];
    }
}

export class MockFilterHttpService {
    getPopulationFiltersObservable(path: string, currentDatasetsId: any): Observable<any> {
        let res = {matchedItemsCount: '10'};
        return Observable.of(res);
    }

    getEventFiltersObservable(path: string, popFilters: any, eventFilters: any, currentDatasetsId: any): Observable<any> {
        let res = {matchedItemsCount: '10'};
        return Observable.of(res);
    }
}

export class MockFilterEventService extends BaseEventService {
    public populationFilterSubjectCount = new MockSubject();
    public eventFilterEventCount = new MockSubject();
    public aesFilter = new MockSubject();
    public seriousAesFilter = new MockSubject();
    public cardiacFilter = new MockSubject();
    public conmedsFilter = new MockSubject();
    public doseDiscontinuationFilter = new MockSubject();
    public doseFilter = new MockSubject();
    public labsFilter = new MockSubject();
    public liverFilter = new MockSubject();
    public lungFunctionFilter = new MockSubject();
    public exacerbationsFilter = new MockSubject();
    public renalFilter = new MockSubject();
    public vitalsFilter = new MockSubject();
    public populationFilter = new MockSubject();
    public sidePanelTab = new MockSubject();
    public deathFilter = new MockSubject();
    public recistFilter = new MockSubject();
    public liverDiagnosticInvestigationFilter = new MockSubject();
    public medicalHistoryFilter = new MockSubject();
    public liverRiskFactorsFilter = new MockSubject();
    public alcoholFilter = new MockSubject();
    public surgicalHistoryFilter = new MockSubject();
    public nicotineFilter = new MockSubject();
    public cieventsFilter = new MockSubject();
    public cerebrovascularFilter = new MockSubject();
    public biomarkersFilter = new MockSubject();
    public cvotFilter = new MockSubject();
    public tumourResponseFilter = new MockSubject();
    public patientDataFilter = new MockSubject();
    public exposureFilter = new MockSubject();
    public doseProportionalityFilter = new MockSubject();
    public pkResultOverallResponseFilter = new MockSubject();
    public ctDnaFilter = new MockSubject();

    constructor() {
        super();
    }

    addSubscription(a: any, b: any): void {
    }

    setEventFilterEventCount(count: number): void {
    }

    setPopulationFilterSubjectCount(count: number): void {
        this.populationFilterSubjectCount.next(count);
    }

    setAesFilter(newFilter: any): void {
        this.aesFilter.next(newFilter);
    }

    setDoseFilter(newFilter: any): void {
        this.doseFilter.next(newFilter);
    }

    setCardiacFilter(newFilter: any): void {
        this.cardiacFilter.next(newFilter);
    }

    setLabsFilter(newFilter: any): void {
        this.labsFilter.next(newFilter);
    }

    setLiverFunctionFilter(newFilter: any): void {
        this.liverFilter.next(newFilter);
    }

    setLungFunctionFilter(newFilter: any): void {
        this.lungFunctionFilter.next(newFilter);
    }

    setExacerbationsFilter(newFilter: any): void {
        this.exacerbationsFilter.next(newFilter);
    }

    setPopulationFilter(newFilter: any): void {
        this.populationFilter.next(newFilter);
    }

    setVitalsFilter(newFilter: any): void {
        this.vitalsFilter.next(newFilter);
    }

    setConmedsFilter(newFilter: any): void {
        this.conmedsFilter.next(newFilter);
    }

    setDeathFilter(newFilter: any): void {
        this.deathFilter.next(newFilter);
    }

    setDoseDiscontinuationFilter(newFilter: any): void {
        this.doseDiscontinuationFilter.next(newFilter);
    }

    setRecistFilter(newFilter: any): void {
        this.recistFilter.next(newFilter);
    }

    setSeriousAesFilter(newFilter: any): void {
        this.seriousAesFilter.next(newFilter);
    }

    setMedicalHistoryFilter(newFilter: any): void {
        this.medicalHistoryFilter.next(newFilter);
    }

    setLiverDiagnosticInvestigationFilter(newFilter: any): void {
        this.liverDiagnosticInvestigationFilter.next(newFilter);
    }

    setLiverRiskFactorsFilter(newFilter: any): void {
        this.liverRiskFactorsFilter.next(newFilter);
    }

    setAlcoholFilter(newFilter: any): void {
        this.alcoholFilter.next(newFilter);
    }

    setSurgicalHistoryFilter(newFilter: any): void {
        this.surgicalHistoryFilter.next(newFilter);
    }

    setNicotineFilter(newFilter: any): void {
        this.nicotineFilter.next(newFilter);
    }

    setCerebrovascularFilter(newFilter: any): void {
        this.cerebrovascularFilter.next(newFilter);
    }

    setCIEventsFilter(newFilter: any): void {
        this.cieventsFilter.next(newFilter);
    }

    setBiomarkersFilter(newFilter: any): void {
        this.biomarkersFilter.next(newFilter);
    }

    setCvotFilter(newFilter: any): void {
        this.cvotFilter.next(newFilter);
    }

    setTumourResponseFilter(newFilter: any): void {
        this.tumourResponseFilter.next(newFilter);
    }

    dispatchNewEventFiltersWereApplied(): void {

    }
}

export class MockUtils {
    getServerPath(modulePath: string, endpoint: any): string {
        return '';
    }

    getPluginSummary(page: string, tab: string): any {
        return {
            eventWidgetName: '',
            showPopulationFilter: true,
            showEventFilter: true,
            filterId: null
        };
    }
}

export class MockFiltersUtils {
    getAvailableEventFilterModels(): any {
        return [];
    }

    getFilterModelById(): any {
    }

    getAvailableTimelineEventFilterModels(): any {
        return [];
    }
}

export class MockSubject {
    private callBack: any;

    next(value: any): void {
        this.callBack(value);
    }

    subscribe(callBack): void {
        this.callBack = callBack;
    }
}

export class MockUserPermissions {

    hasViewOncologyPackagePermission(): boolean {
        return true;
    }
}

export class MockEnvService {
    env = {
        name: 'dev',
        securityUrl: 'securityUrl',
        isLocalHost: false
    };
}

export class MockTabStoreUtils {

}

export class MockStore {

    select() {}
    dispatch() {}
}

export class MockDetailsOnDemandHeightService {
    isClosed(): boolean {
        return true;
    }

    open(): void {

    }

    close(): void {

    }
}

export class MockDatasetViews {
    hasAesData(): boolean {
        return true;
    }

    hasLabsData(): boolean {
        return true;
    }

    hasVitalsData(): boolean {
        return true;
    }

    hasCardiacData(): boolean {
        return true;
    }

    hasRenalData(): boolean {
        return true;
    }

    hasLiverData(): boolean {
        return true;
    }

    hasRespiratryData(): boolean {
        return true;
    }

    hasExacerbationsData(): boolean {
        return true;
    }

    hasConmedsData(): boolean {
        return true;
    }

    hasExposureData(): boolean {
        return true;
    }

    hasSurgicalHistoryData(): boolean {
        return true;
    }

    hasOncologyData(): boolean {
        return true;
    }

    hasDoseData(): boolean {
        return true;
    }

    hasDoseDiscontinuationData(): boolean {
        return true;
    }

    hasLiverDiagnosticInvestigationData(): boolean {
        return true;
    }

    hasDeathData(): boolean {
        return true;
    }

    hasNicotineData(): boolean {
        return true;
    }

    hasLiverRiskFactorsData(): boolean {
        return true;
    }


    hasAlcoholData(): boolean {
        return true;
    }

    hasMedicalHistoryData(): boolean {
        return true;
    }

    hasCvotData(): boolean {
        return true;
    }

    getDetailsOnDemandColumns(tab: string): string[] {
        return ['subjectId', 'measurementName', 'studySpecificFilter--Tumour Location'];
    }

    getDetailsOnDemandColumnsTitles(tab: string): string[] {
        return [];
    }

    getSubjectsEcodesByIds(ids: string[]): string[] {
        return ids;
    }

    hasOncoBiomarkers(): boolean {
        return true;
    }

    hasCerebrovascularData(): boolean {
        return true;
    }

    hasCIEventsData(): boolean {
        return true;
    }

    hasTumourResponseData(): boolean {
        return true;
    }

    getSubjectIdByEcode(subjectCode: string): string {
        return subjectCode;
    }
}

export class MockDataService {
    private mockSubject = new MockSubject();

    getDetailsOnDemandData(): MockSubject {
        return this.mockSubject;
    }
}

export class MockDetailsOnDemandHttpService {

    downloadAllDetailsOnDemandData(): void {
    }

    downloadFile(): void {
    }
}

export class MockSingleSubjectHttpService {
    getTableData(subjectId: string, dataType: string): Observable<any> {
        let res: Array<string> = [
            'data-1',
            'data-2'
        ];
        return Observable.of(res);
    }
}

export class MockHttpServiceFactory {
    getHttpService(tabId: TabId): MockHttpService {
        return new MockHttpService();
    }
}

export class MockHttpClient {
    post() {
    }
    get() {
    }
}

export class MockHttpService {
    getDetailsOnDemandData(): void {

    }
}

export class MockFilterReloadService {

    resetFilters(filterModel: any): void {
    }
}

export class MockCohortEditorService {

}

export const MockSelection = {
    'settings': {
        'options': {
            'Y_AXIS': {
                'groupByOption': 'ACTUAL_VALUE',
                'params': null
            },
            'X_AXIS': {
                'groupByOption': 'VISIT_NUMBER',
                'params': null
            }
        },
        'trellisOptions': [
            {
                'groupByOption': 'MEASUREMENT',
                'params': null
            }
        ]
    },
    'selectionItems': [
        {
            'selectedTrellises': {
                'MEASUREMENT': 'ALT (g/dL)'
            },
            'selectedItems': {
                'X_AXIS': '3.0'
            },
            'range': {
                'minimum': 10,
                'maximum': 11
            }
        },
        {
            'selectedTrellises': {
                'MEASUREMENT': 'ALT (g/dL)'
            },
            'selectedItems': {
                'X_AXIS': '2.0'
            },
            'range': {
                'minimum': 10,
                'maximum': 11
            }
        },
        {
            'selectedTrellises': {
                'MEASUREMENT': 'ALT (g/dL)'
            },
            'selectedItems': {
                'X_AXIS': '1.0'
            },
            'range': {
                'minimum': 10,
                'maximum': 11
            }
        }
    ]
};

export const MockAxisOptionsNoMeasurement: XAxisOptions = <XAxisOptions>{
    options: [
        {
            groupByOption: 'VISIT_NUMBER',
            timestampOption: false,
            supportsDuration: false,
            binableOption: false
        }
    ],
    hasRandomization: true,
    drugs: []
};

export const MockAxisOptionsNoMeasurementBinable: XAxisOptions = <XAxisOptions>{
    options: [
        {
            groupByOption: 'VISIT_NUMBER',
            timestampOption: false,
            supportsDuration: false,
            binableOption: true
        }
    ],
    hasRandomization: true,
    drugs: []
};

export const MockAxisOptionsTimestampNoDrugsNoRandomization: XAxisOptions = <XAxisOptions>{
    options: [
        {
            groupByOption: 'VISIT_NUMBER',
            timestampOption: false,
            supportsDuration: false,
            binableOption: false
        },
        {
            groupByOption: 'MEASUREMENT_TIME_POINT',
            timestampOption: true,
            supportsDuration: false,
            binableOption: true
        }
    ],
    hasRandomization: false,
    drugs: []
};

export const MockAxisOptionsTimestampNoDrugsRandomization: XAxisOptions = <XAxisOptions>{
    options: [
        {
            groupByOption: 'VISIT_NUMBER',
            timestampOption: false,
            supportsDuration: false,
            binableOption: false
        },
        {
            groupByOption: 'MEASUREMENT_TIME_POINT',
            timestampOption: true,
            supportsDuration: false,
            binableOption: true
        }
    ],
    hasRandomization: true,
    drugs: []
};

export const MockAxisOptionsTimestampDrugsRandomization: XAxisOptions = <XAxisOptions>{
    options: [
        {
            groupByOption: 'VISIT_NUMBER',
            timestampOption: false,
            supportsDuration: false,
            binableOption: false
        },
        {
            groupByOption: 'MEASUREMENT_TIME_POINT',
            timestampOption: true,
            supportsDuration: false,
            binableOption: true
        }
    ],
    hasRandomization: true,
    drugs: ['STDY4321']
};


