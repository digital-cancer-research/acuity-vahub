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

import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute} from '@angular/router';
import {bind} from 'lodash';

import {SUMMARY_CATEGORIES, SAVE_MODAL} from './AEsSummaryConstants';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {DateUtilsService} from '../../../common/utils/DateUtilsService';
import {AesSummaryHttpService} from '../../../data/aes';
import {StudyService} from '../../../common/StudyService';
import {AEsSummaryDataService} from './AEsSummaryDataService';
import {AEsSummaryAnyDataService} from './any/AEsSummaryAnyDataService';
import {AEsSummaryMstCmnDataService} from './mst-cmn/AEsSummaryMstCmnDataService';
import {AEsSummaryDeathOutcomeDataService} from './death-outcome/AEsSummaryDeathOutcomeDataService';
import {SAEsSummaryLdostDataService} from './ldost/SAEsSummaryLdostDataService';
import {EXPORT_DELIMETER, ExportUtils} from '../../../common/utils/ExportUtils';
import {ConfigurationService} from '../../../configuration/ConfigurationService';

@Component({
    selector: 'aes-summary',
    templateUrl: './AEsSummaryComponent.html',
    styleUrls: ['./AEsSummaryComponent.styl'],
    providers: [
        {provide: AEsSummaryDataService, useClass: AEsSummaryAnyDataService, multi: true},
        {provide: AEsSummaryDataService, useClass: AEsSummaryMstCmnDataService, multi: true},
        {provide: AEsSummaryDataService, useClass: AEsSummaryDeathOutcomeDataService, multi: true},
        {provide: AEsSummaryDataService, useClass: SAEsSummaryLdostDataService, multi: true},
    ]
})
export class AEsSummaryComponent implements OnInit, OnDestroy {

    isModalVisible = false;
    modalOptions: any;
    lastUpdateDate = '-';
    isSelectedOneDataset: boolean;
    notes: string[];
    tableName: string;
    summaryCategories = SUMMARY_CATEGORIES;
    rowData = [];
    columnDefs = [];
    csvData: any;
    summaryData: InMemory.AeSummariesTable[];
    gridOptions = {
        api: null,
        enableSorting: true,
        toolPanelSuppressRowGroups: true,
        toolPanelSuppressValues: true,
        groupUseEntireRow: false,
        toolPanelSuppressPivots: true,
        toolPanelSuppressPivotMode: true,
        enableColResize: true,
        enableRangeSelection: true,
        enableFilter: false,
        groupSuppressAutoColumn: true,
        getContextMenuItems: () => [],
        getMainMenuItems: this.getMainMenuItems
    };
    private dataSubscription: Subscription;
    private error = false;
    private summaryService: AEsSummaryDataService;

    constructor(private sessionEventService: SessionEventService,
                private studyService: StudyService,
                private dateUtilService: DateUtilsService,
                private httpService: AesSummaryHttpService,
                private route: ActivatedRoute,
                private configurationService: ConfigurationService,
                @Inject(AEsSummaryDataService) private services: AEsSummaryDataService[]) {
    }

    ngOnInit(): void {
        this.summaryService = this.getRequiredService();
        this.notes = this.summaryService.getNotes();
        this.tableName = this.summaryService.getTableName();
        this.isSelectedOneDataset = this.sessionEventService.currentSelectedDatasets.length <= 1;
        this.lastUpdateDate = this.dateUtilService.toDateTime(this.studyService.metadataInfo['studyInfo'].lastUpdatedDate);

        this.initModal();
    }

    ngOnDestroy(): void {
        if (this.dataSubscription) {
            this.dataSubscription.unsubscribe();
        }
    }

    onTableInitialised(params): void {
        this.getDataFromServer();
    }

    getCalculatedTableHeight(): string {
        const elementsHeightBeforeTable = 270;
        const notesHeight = document.querySelector('.notes').clientHeight;

        return `calc(100vh - ${elementsHeightBeforeTable + notesHeight}px)`;
    }

    getTotalSubjectsCount(): number {
        if (this.isValidData(this.summaryData)) {
            return this.summaryData.reduce((l, r) => l + r.countDosedSubject, 0);
        }
    }

    isError(): boolean {
        return this.error;
    }

    isValidData(data: InMemory.AeSummariesTable[]) {
        return this.summaryService.isValidData(data);
    }

    openSaveModal(): void {
        this.isModalVisible = true;
    }

    saveModalSubmitted(answer: any): void {
        this.isModalVisible = false;
        if (answer === true) {
            const str = ExportUtils.convertTreeToCsv(this.summaryService.getColumns(this.summaryData));
            ExportUtils.export('aesSummaries.csv', str + this.csvData, 'application/csv');
        }
    }

    warningModalSubmitted(): void {
        this.isModalVisible = false;
    }

    private getDataFromServer() {
        if (this.gridOptions.api) {
            this.gridOptions.api.showLoadingOverlay();
        }
        this.dataSubscription = this.httpService.getData(this.sessionEventService.currentSelectedDatasets, this.summaryService.getApi())
            .subscribe(data => this.onReceiveData(data), () => this.error = true);
    }

    private onReceiveData(data: InMemory.AeSummariesTable[]) {
        this.summaryData = data;

        if (this.summaryService.isValidData(data)) {
            this.columnDefs = this.summaryService.getColumns(data);
            this.rowData = this.summaryService.convertData(data);
            setTimeout(() => {
                this.gridOptions.api.sizeColumnsToFit();
                this.gridOptions.api.hideOverlay();
                this.csvData = this.gridOptions.api.getDataAsCsv({columnSeparator: EXPORT_DELIMETER});
            });
        }
    }

    private getRequiredService(): AEsSummaryDataService {
        const service = this.services.find(s => s.getApi() === this.route.snapshot.data['api']);
        if (!service) {
            throw new Error('can\'t fetch service for given api');
        }

        return service;
    }

    private initModal(): void {
        if (this.isSelectedOneDataset) {
            this.modalOptions = SAVE_MODAL;
            this.modalOptions.submit = bind(this.saveModalSubmitted, this);
        } else {
            this.modalOptions = {
                title: 'Warning',
                message: `Functionality of AE Summaries tables is not yet supported for combined datasets in
                 ACUITY. Please select one dataset to see AE Summaries tables.`,
                textButton: 'Ok',
                canBeDismissed: false
            },
            this.modalOptions.submit = bind(this.warningModalSubmitted, this);
            this.isModalVisible = true;
        }
    }

    private getMainMenuItems(params) {
        return params.column.getId() === 'socColumn'
            ? ['expandAll', 'contractAll']
            : [];
    }
}
