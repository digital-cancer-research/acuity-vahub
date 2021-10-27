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

import {
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {GridOptions} from 'ag-grid/main';
import {Subscription} from 'rxjs/Subscription';
import {clone, isEmpty, isNull, isUndefined} from 'lodash';

import {DetailsOnDemandHeightService} from './services/DetailsOnDemandHeightService';
import {DetailsOnDemandSummaryService} from './services/DetailsOnDemandSummaryService';
import {DetailsOnDemandEventTableService} from './services/DetailsOnDemandEventTableService';
import {DetailsOnDemandSubjectTableService} from './services/DetailsOnDemandSubjectTableService';
import {IDetailsOnDemand, ISelectionDetail, PlotSettings, TabId, TableType} from '../store/ITrellising';
import {TabStoreUtils} from '../store/utils/TabStoreUtils';
import {DataService} from '../data/DataService';

@Component({
    selector: 'details-on-demand',
    templateUrl: 'DetailsOnDemandComponent.html',
    styleUrls: ['./DetailsOnDemandComponent.css'],
    providers: [
        DetailsOnDemandHeightService,
        DetailsOnDemandSummaryService,
        DetailsOnDemandEventTableService
    ]
})

export class DetailsOnDemandComponent implements OnInit, OnChanges, OnDestroy {

    hasRecentlyUpdated: boolean;
    subjectsTableVisible: boolean;
    saveModalVisible = false;
    saveModalTitle = 'Download displayed records?';
    subjectSummary: string;
    eventSummary: string;
    tabName: string;
    subjectLoading = false;
    eventLoading = false;

    eventGridOptions: GridOptions;
    subjectGridOptions: GridOptions;

    pageChangeListener: Subscription;
    eventsDataSubscription: Subscription;
    subjectsDataSubscription: Subscription;

    showEventsWarningMessage: boolean;
    showSubjectsWarningMessage: boolean;
    eventsData = [];
    subjectsData = [];
    eventCount: number;
    private readonly ROW_LIMIT = 1000;

    @Input() tabId: TabId;
    @Input() selectionDetail: ISelectionDetail;
    @Input() eventDetailsOnDemandDisabled: boolean;
    @Input() eventModel: IDetailsOnDemand;
    @Input() subjectModel: IDetailsOnDemand;
    @Input() plotSettings: PlotSettings;

    @Output() updatedEvent = new EventEmitter<IDetailsOnDemand>();
    @Output() updatedSubject = new EventEmitter<IDetailsOnDemand>();

    constructor(public eventTableService: DetailsOnDemandEventTableService,
                public subjectTableService: DetailsOnDemandSubjectTableService,
                public detailsOnDemandHeightService: DetailsOnDemandHeightService,
                private detailsOnDemandSummaryService: DetailsOnDemandSummaryService,
                private dataService: DataService,
                private router: Router,
                private changeDetectorRef: ChangeDetectorRef) {
        const gridOptionsTemplate: GridOptions = {
            defaultColDef: {
                menuTabs: []
            },
            context: {},
            getContextMenuItems: () => {
                return [
                    'copy',
                    'copyWithHeaders',
                    'separator',
                    'toolPanel'
                ];
            },
            enableServerSideSorting: true,
            toolPanelSuppressRowGroups: true,
            toolPanelSuppressValues: true,
            toolPanelSuppressPivots: true,
            toolPanelSuppressPivotMode: true
        };
        this.eventGridOptions = clone(gridOptionsTemplate);
        this.subjectGridOptions = clone(gridOptionsTemplate);
        (<any> this.subjectGridOptions).groupHeaders = true;
        if (this.eventTableService.hasOnlyGroups()) {
            (<any> this.eventGridOptions).groupHeaders = true;
        }
    }

    ngOnInit(): void {
        // Doesn't work
        const $draggable = $('.draggable').draggabilly({
            axis: 'y',
            handle: '.details-area-header'
        });

        this.bindToDrag($draggable);
        this.setTabName();
        this.openPreviouslyOpenTab();
        this.setTableExpansion();
        this.listenToPageChanges();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['selectionDetail'] && this.gridApiReady()) {
            if (isEmpty(this.selectionDetail)) {
                this.showWarningMessage('events', false);
                this.showWarningMessage('subjects', false);
                if (!this.hasStoredData(this.eventModel)) {
                    this.clearTables();
                }
            } else {
                this.eventCount = changes['selectionDetail'].currentValue.eventCount
                    || changes['selectionDetail'].currentValue.eventIds.length;
                this.updateTables();
            }
        }
        // Subjects tab is the only available tab for population
        if (this.tabId === TabId.POPULATION_BARCHART
            || this.eventDetailsOnDemandDisabled) {
            this.openSubjectsTab();
        }
        this.updateSummary();
    }

    ngOnDestroy(): void {
        if (this.pageChangeListener) {
            this.pageChangeListener.unsubscribe();
        }
        if (this.eventsDataSubscription) {
            this.eventsDataSubscription.unsubscribe();
        }
        if (this.subjectsDataSubscription) {
            this.subjectsDataSubscription.unsubscribe();
        }
    }

    openOrClose(): void {
        this.detailsOnDemandHeightService.onExpandCollapseButtonPress();
    }

    openSaveModal(): void {
        if (!this.subjectsTableVisible && this.eventTableService.gridIsEmpty()) {
            this.saveModalTitle = 'Download all records?';
        } else if (this.subjectsTableVisible && this.subjectTableService.gridIsEmpty()) {
            this.saveModalTitle = 'Download all records?';
        } else {
            this.saveModalTitle = 'Download displayed records?';
        }
        this.saveModalVisible = true;
    }

    saveModalSubmitted(answer: any): void {
        this.saveModalVisible = false;
        if (answer === true) {
            if (this.saveModalTitle === 'Download displayed records?') {
                if (!this.subjectsTableVisible) {
                    this.dataService.downloadDetailsOnDemandData(this.tabId, this.selectionDetail.eventIds);
                } else {
                    this.dataService.downloadDetailsOnDemandData(TabId.POPULATION_BARCHART, this.selectionDetail.subjectIds);
                }
            } else {
                if (!this.subjectsTableVisible) {
                    this.dataService.downloadAllDetailsOnDemandData(this.tabId, undefined, this.plotSettings);
                } else {
                    this.dataService.downloadAllDetailsOnDemandData(TabId.POPULATION_BARCHART);
                }
            }
        }
    }

    openEventsTab(): void {
        this.subjectsTableVisible = false;
    }

    openSubjectsTab(): void {
        this.subjectsTableVisible = true;
    }

    onEventTableInitialised(): void {
        this.eventTableService.setGridOptions(this.eventGridOptions);
        this.eventTableService.setColumnDefs(this.eventModel.get('expandedGroups'));
        // It can happen that user leaves plot page before event dods request is completed.
        // If this happens, request is cancelled and nothing is saved to store.
        // So if user comes back to page and there is some selection but no data in dods, we want to make a request for this data
        // and update table with it.
        if (!this.eventDetailsOnDemandDisabled && this.selectionDetail
            && this.selectionDetail.eventCount > 0 && this.gridApiReady()
            && this.eventModel.get('tableData') && this.eventModel.get('tableData').length === 0) {
            this.updateEventsTable();
        }
        if (this.hasStoredData(this.eventModel)) {
            this.reloadEventsTable();
        }
    }

    onSubjectTableInitialised(): void {
        this.subjectTableService.setGridOptions(this.subjectGridOptions);
        this.subjectTableService.setColumnDefs(this.subjectModel.get('expandedGroups'));
        // It can happen that user leaves plot page before subject dods request is completed.
        // If this happens, request is cancelled and nothing is saved to store.
        // So if user comes back to page and there is some selection but no data in dods, we want to make a request for this data
        // and update table with it.
        if (this.selectionDetail && this.selectionDetail.subjectIds.length > 0 && this.gridApiReady()
            && this.subjectModel.get('tableData') && this.subjectModel.get('tableData').length === 0) {
            this.updateSubjectsTable();
        }
        if (this.hasStoredData(this.subjectModel)) {
            this.reloadSubjectsTable();
        }
    }

    onWindowResize($event): void {
        this.detailsOnDemandHeightService.adjustTableHeightAfterWindowResize();
    }

    private openPreviouslyOpenTab(): void {
        this.subjectsTableVisible = !isUndefined(this.subjectModel) && this.subjectModel.get('isOpen') === true;
    }

    private setTableExpansion(): void {
        if (!isUndefined(this.subjectModel) && this.subjectModel.get('isExpanded')) {
            this.detailsOnDemandHeightService.open();
        } else {
            this.detailsOnDemandHeightService.close();
        }
    }

    private gridApiReady(): boolean {
        return !isUndefined(this.eventGridOptions.api)
            && !isUndefined(this.subjectGridOptions.api);
    }

    private hasStoredData(storedData: IDetailsOnDemand): boolean {
        return !isEmpty(storedData) && !isNull(storedData.get('tableData'));
    }

    private bindToDrag($draggable: any): void {
        const draggie = $draggable.data('draggabilly');
        $draggable.on('dragMove', () => this.detailsOnDemandHeightService.onDragBar(draggie));
        this.detailsOnDemandHeightService.openToPreviousHeight();
    }

    private setTabName(): void {
        this.tabName = TabStoreUtils.getPageNameFromTabId(this.tabId);
    }

    private clearTables(): void {
        if (this.tabId !== TabId.POPULATION_BARCHART) {
            this.clearEventTable();
        }
        this.clearSubjectsTable();
    }

    private clearEventTable(): void {
        this.setEventsRowData([]);
    }

    private clearSubjectsTable(): void {
        this.setSubjectsRowData([]);
    }

    /**
     * update summary based on chart selection value
     * if it's not empty - show the summary
     * if it's empty - "nullify" the summary
     */
    private updateSummary(): void {
        if (!isEmpty(this.selectionDetail)) {
            this.subjectSummary = this.detailsOnDemandSummaryService.getSubjectSummary(this.selectionDetail);
            this.eventSummary = this.detailsOnDemandSummaryService.getEventSummary(this.selectionDetail);
        } else {
            this.subjectSummary = this.eventSummary = '';
        }
    }

    private updateTables(): void {
        if (this.tabId !== TabId.POPULATION_BARCHART) {
            this.updateEventsTable();
        }
        this.updateSubjectsTable();
    }

    private updateEventsTable(): void {
        if (!isEmpty(this.selectionDetail) && this.selectionDetail.eventIds.length > this.ROW_LIMIT) {
            this.showWarningMessage('events', true);
        } else {
            this.showWarningMessage('events', false);
            this.fetchEventTableData();
        }
    }

    fetchEventTableData(): void {
        if (!isEmpty(this.selectionDetail) && !this.eventLoading) {
            this.eventLoading = true;
            this.eventsDataSubscription = this.eventTableService.getTableData(this.tabId, this.selectionDetail.eventIds)
                .finally(() => {
                    this.eventLoading = false;
                    if (!this.changeDetectorRef['destroyed']) {
                        this.changeDetectorRef.detectChanges();
                    }
                })
                .subscribe((data) => {
                    this.setEventsRowData(data);
                });
        }
    }

    private updateSubjectsTable(): void {
        if (!isEmpty(this.selectionDetail) && this.selectionDetail.subjectIds.length > this.ROW_LIMIT) {
            this.showWarningMessage('subjects', true);
        } else {
            this.showWarningMessage('subjects', false);
            this.fetchSubjectTableData();
        }
    }

    fetchSubjectTableData(): void {
        if (!isEmpty(this.selectionDetail) && !this.subjectLoading) {
            this.subjectLoading = true;
            this.subjectsDataSubscription = this.subjectTableService
                .getTableData(TabId.POPULATION_BARCHART, this.selectionDetail.subjectIds)
                .finally(() => {
                    this.subjectLoading = false;
                    if (!this.changeDetectorRef['destroyed']) {
                        this.changeDetectorRef.detectChanges();
                    }
                })
                .subscribe((data) => {
                    if (!isEmpty(data)) {
                        this.subjectTableService.formatRows(data);
                    }
                    this.togleUpdatePopup();
                    this.setSubjectsRowData(data);
                    this.subjectsData = data;
                });
        }
    }

    private showWarningMessage(tableType: TableType, show: boolean): void {
        if (tableType === 'subjects') {
            this.showSubjectsWarningMessage = show;
        } else {
            this.showEventsWarningMessage = show;
        }
    }

    private reloadEventsTable(): void {
        this.eventSummary = this.detailsOnDemandSummaryService.getEventSummary(this.selectionDetail);
        let data = [];
        if (!isEmpty(this.selectionDetail) && !isEmpty(this.selectionDetail.eventIds)) {
            data = this.eventModel.get('tableData');
        }
        this.setEventsRowData(data);
    }

    private setEventsRowData(data) {
        this.eventsData = data;
        this.eventGridOptions.api.setRowData(this.eventsData);
        setTimeout(() => {
            this.eventGridOptions.api.redrawRows();
        });
    }

    private setSubjectsRowData(data) {
        this.subjectsData = data;
        this.subjectGridOptions.api.setRowData(this.subjectsData);
        setTimeout(() => {
            this.subjectGridOptions.api.redrawRows();
        });
    }

    private reloadSubjectsTable(): void {
        this.subjectSummary = this.detailsOnDemandSummaryService.getSubjectSummary(this.selectionDetail);
        let data = [];
        if (!isEmpty(this.selectionDetail) && !isEmpty(this.selectionDetail.subjectIds)) {
            data = this.subjectModel.get('tableData');
        }
        this.setSubjectsRowData(data);
    }

    private listenToPageChanges(): void {
        this.pageChangeListener = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationStart) {
                if (this.eventGridOptions) {
                    this.eventTableService.saveToStore({
                        summary: this.eventSummary,
                        isOpen: !this.subjectsTableVisible,
                        tableData: this.eventsData,
                        modelChanged: this.updatedEvent
                    });
                }

                if (this.subjectGridOptions) {
                    this.subjectTableService.saveToStore({
                        summary: this.subjectSummary,
                        isOpen: this.subjectsTableVisible,
                        tableData: this.subjectsData,
                        modelChanged: this.updatedSubject
                    });
                }
            }
        });
    }

    private togleUpdatePopup(): void {
        this.hasRecentlyUpdated = true;
        setTimeout(() => {
            this.hasRecentlyUpdated = false;
        }, 1000);
    }
}
