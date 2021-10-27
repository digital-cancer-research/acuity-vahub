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

import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {GridOptions} from 'ag-grid/main';
import {Subscription} from 'rxjs/Subscription';
import {List, Map} from 'immutable';
import {clone, isEmpty, isNull, isUndefined} from 'lodash';

import {DetailsOnDemandHeightService} from '../services/DetailsOnDemandHeightService';
import {DetailsOnDemandSummaryService} from '../services/DetailsOnDemandSummaryService';
import {DetailsOnDemandEventTableService} from '../services/DetailsOnDemandEventTableService';
import {DetailsOnDemandSubjectTableService} from '../services/DetailsOnDemandSubjectTableService';
import {IDetailsOnDemand, IMultiSelectionDetail, TabId, TableType} from '../../store/ITrellising';
import {TabStoreUtils} from '../../store/utils/TabStoreUtils';
import {DataService} from '../../data/DataService';
import {DatasetViews} from '../../../../security/DatasetViews';

@Component({
    selector: 'multi-details-on-demand',
    templateUrl: 'MultiDetailsOnDemandComponent.html',
    styleUrls: ['../DetailsOnDemandComponent.css'],
    providers: [
        DetailsOnDemandHeightService,
        DetailsOnDemandSummaryService,
        DetailsOnDemandEventTableService
    ]
})

export class MultiDetailsOnDemandComponent implements OnInit, OnChanges, OnDestroy {
    // TODO: think of something else than immutable map for values that are often changed. Think of extending standard DoDs
    hasRecentlyUpdated: boolean;
    subjectsTableVisible: boolean;
    saveModalVisible = false;
    saveModalTitle = 'Download displayed records?';
    subjectSummary: string;
    eventSummaries = Map<string, string>();
    tabName: string;
    selectedTable: any;
    subjectLoading = false;
    eventLoading = false;

    gridOptionsTemplate: GridOptions;
    eventsGridOptions: Map<string, GridOptions> = Map<string, GridOptions>();
    subjectGridOptions: GridOptions;

    pageChangeListener: Subscription;
    eventsDataSubscription: Subscription;
    subjectsDataSubscription: Subscription;

    showEventsWarningMessage: Map<string, boolean> = Map<string, boolean>();
    showSubjectsWarningMessage: boolean;
    eventsData: Map<string, any> = Map<string, any>();
    subjectsData = [];
    tablesList = List<any>();
    TabId = TabId;
    private readonly ROW_LIMIT = 1000;

    @Input() tabId: TabId;
    @Input() multiSelectionDetail: IMultiSelectionDetail;
    @Input() eventModels: Map<any, IDetailsOnDemand>;
    @Input() subjectModel: IDetailsOnDemand;

    @Output() updatedEvent = new EventEmitter<IDetailsOnDemand>();
    @Output() updatedSubject = new EventEmitter<IDetailsOnDemand>();

    constructor(public eventTableService: DetailsOnDemandEventTableService,
                public subjectTableService: DetailsOnDemandSubjectTableService,
                public detailsOnDemandHeightService: DetailsOnDemandHeightService,
                private detailsOnDemandSummaryService: DetailsOnDemandSummaryService,
                private dataService: DataService,
                private router: Router,
                private datasetViews: DatasetViews,
                private changeDetectorRef: ChangeDetectorRef) {
        this.gridOptionsTemplate = {
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

        this.tablesList = this.eventTableService.getMultipleEventTablesNames();
        this.tablesList.forEach(table => {
            this.eventsGridOptions = this.eventsGridOptions.set(table, clone(this.gridOptionsTemplate));
        });
        this.selectedTable = this.tablesList.first();
        this.subjectGridOptions = clone(this.gridOptionsTemplate);
        (<any> this.subjectGridOptions).groupHeaders = true;
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
        if (changes['multiSelectionDetail'] && this.gridApiReady()) {
            if (isEmpty(this.multiSelectionDetail)) {
                this.tablesList.forEach(table => {
                    this.showWarningMessage(table, false);
                });
                this.showWarningMessage('subjects', false);
                if (!this.eventModels.find(model => this.hasStoredData(model))) {
                    this.clearTables();
                }
            } else {
                this.updateTables();
            }
        }
        // Subjects tab is the only available tab for population
        if (this.tabId === TabId.POPULATION_BARCHART) {
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
        if (!this.subjectsTableVisible && this.eventTableService.gridIsEmpty(this.selectedTable)) {
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
                    this.dataService.downloadDetailsOnDemandData(this.tabId,
                        this.multiSelectionDetail.eventIds.get(this.selectedTable), this.selectedTable);
                } else {
                    this.dataService.downloadDetailsOnDemandData(TabId.POPULATION_BARCHART, this.multiSelectionDetail.subjectIds);
                }
            } else {
                if (!this.subjectsTableVisible) {
                    this.dataService.downloadAllDetailsOnDemandData(this.tabId, this.selectedTable);
                } else {
                    this.dataService.downloadAllDetailsOnDemandData(TabId.POPULATION_BARCHART);
                }
            }
        }
    }

    openEventsTab(selectedTable: any): void {
        this.subjectsTableVisible = false;
        this.selectedTable = selectedTable;
    }

    openSubjectsTab(): void {
        this.subjectsTableVisible = true;
    }

    onEventTableInitialised(): void {
        this.tablesList.forEach(table => {
            this.eventTableService.setGridOptions(this.eventsGridOptions.get(table), table);
            this.eventTableService.setColumnDefs(this.eventModels.get(table)
                ? this.eventModels.get(table).toJS().expandedGroups : [], table);

            if (this.hasStoredData(this.eventModels.get(table))) {
                this.reloadEventsTable();
            }
        });
    }

    onSubjectTableInitialised(): void {
        this.subjectTableService.setGridOptions(this.subjectGridOptions);
        this.subjectTableService.setColumnDefs(this.subjectModel.toJS().expandedGroups);
        if (this.hasStoredData(this.subjectModel)) {
            this.reloadSubjectsTable();
        }
    }

    onWindowResize($event): void {
        this.detailsOnDemandHeightService.adjustTableHeightAfterWindowResize();
    }

    private openPreviouslyOpenTab(): void {
        this.subjectsTableVisible = !isUndefined(this.subjectModel) && this.subjectModel.toJS().isOpen === true;
    }

    private setTableExpansion(): void {
        if (!isUndefined(this.subjectModel) && this.subjectModel.toJS().isExpanded) {
            this.detailsOnDemandHeightService.open();
        } else {
            this.detailsOnDemandHeightService.close();
        }
    }

    private gridApiReady(): boolean {
        return isUndefined(this.eventsGridOptions.find(option => !option.api))
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
            this.clearEventTables();
        }
        this.clearSubjectsTable();
    }

    private clearEventTables(): void {
        this.tablesList.forEach(table => {
            this.setEventsRowData(table, []);
        });
    }
    private setEventsRowData(table, data) {
        this.eventsData = this.eventsData.set(table, data);
        const eventGridOptions = this.eventsGridOptions.get(table);
        eventGridOptions.api.setRowData(data);
        setTimeout(() => {
            eventGridOptions.api.redrawRows();
        });
    }

    private setSubjectsRowData(data) {
        this.subjectsData = data;
        this.subjectGridOptions.api.setRowData(this.subjectsData);

        setTimeout(() => {
            this.subjectGridOptions.api.redrawRows();
        });
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
        this.subjectSummary = !isEmpty(this.multiSelectionDetail)
            ? this.detailsOnDemandSummaryService.getSubjectSummary(this.multiSelectionDetail) : '';
        this.eventSummaries = this.detailsOnDemandSummaryService.getMultipleEventSummary(this.multiSelectionDetail);
    }

    private updateTables(): void {
        if (this.tabId !== TabId.POPULATION_BARCHART) {
            this.updateEventsTable();
        }
        this.updateSubjectsTable();
    }

    private updateEventsTable(): void {
        this.tablesList.forEach(table => {
            if (!isEmpty(this.multiSelectionDetail) && this.multiSelectionDetail.eventIds.get(table).length > this.ROW_LIMIT) {
                this.showWarningMessage(table, true);
            } else {
                this.showWarningMessage(table, false);
            }
        });
        this.fetchEventTableData();
    }

    fetchEventTableData(): void {
        if (!isEmpty(this.multiSelectionDetail) && !this.eventLoading) {
            this.eventLoading = true;
            const eventIds = <Map<string, string[]>>this.multiSelectionDetail.eventIds
                .filter((value, table) => !this.showEventsWarningMessage.get(table));
            this.eventsDataSubscription = this.eventTableService
                .getTableData(this.tabId, eventIds)
                .finally(() => {
                    this.eventLoading = false;
                    if (!this.changeDetectorRef['destroyed']) {
                        this.changeDetectorRef.detectChanges();
                    }
                })
                .subscribe(data => {
                    // foreach element of data set api-s
                    data.mapKeys((key, value) => {
                        this.setEventsRowData(key, value);
                    });
                });
        }
    }

    private getEventsSummary(events: any) {
        let summary = '';
        if (!isEmpty(events)) {
            summary = events.length + ' of ' + this.multiSelectionDetail.totalEvents;
            if (this.multiSelectionDetail.totalEvents === events.length) {
                return '(All) ' + summary;
            }
        }
        return summary;
    }

    private updateSubjectsTable(): void {
        if (!isEmpty(this.multiSelectionDetail) && this.multiSelectionDetail.subjectIds.length > this.ROW_LIMIT) {
            this.showWarningMessage('subjects', true);
        } else {
            this.showWarningMessage('subjects', false);
            this.fetchSubjectTableData();
        }
    }

    fetchSubjectTableData(): void {
        if (!isEmpty(this.multiSelectionDetail) && !this.subjectLoading) {
            this.subjectLoading = true;
            this.subjectsDataSubscription = this.subjectTableService
                .getTableData(TabId.POPULATION_BARCHART, this.multiSelectionDetail.subjectIds)
                .finally(() => {
                    this.subjectLoading = false;
                    if (!this.changeDetectorRef['destroyed']) {
                        this.changeDetectorRef.detectChanges();
                    }
                })
                .subscribe((data) => {
                    if (!isEmpty(data)) {
                        this.subjectTableService.formatMappedColumns(data);
                    }
                    this.togleUpdatePopup();
                    this.setSubjectsRowData(data);
                });
        }
    }

    private showWarningMessage(tableType: TableType, show: boolean): void {
        if (tableType === 'subjects') {
            this.showSubjectsWarningMessage = show;
        } else {
            this.showEventsWarningMessage = this.showEventsWarningMessage.set(tableType, show);
        }
    }

    private reloadEventsTable(): void {
        this.eventSummaries = this.detailsOnDemandSummaryService.getMultipleEventSummary(this.multiSelectionDetail);
        this.eventModels.mapKeys((key, value) => {
            const eventModel = value.toJS();
            const eventData = !isEmpty(this.multiSelectionDetail)
                    && !isEmpty(this.multiSelectionDetail.eventIds) ?  eventModel.tableData : [];
            this.setEventsRowData(key, eventData);
        });

    }

    private reloadSubjectsTable(): void {
        const subjectModel = this.subjectModel.toJS();
        this.subjectSummary = this.detailsOnDemandSummaryService.getSubjectSummary(this.multiSelectionDetail);
        let data = [];
        if (!isEmpty(this.multiSelectionDetail) && !isEmpty(this.multiSelectionDetail.subjectIds)) {
            data = subjectModel.tableData;
        }
        this.setSubjectsRowData(data);
    }

    private listenToPageChanges(): void {
        this.pageChangeListener = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationStart) {
                let context = Map();
                this.tablesList.forEach(table => {
                    context = context.set(table, {
                        summary: this.eventSummaries.get(table),
                        isOpen: !this.subjectsTableVisible,
                        tableData: this.eventsData.get(table)
                    });
                });
                this.eventTableService.saveMultipleToStore(context, this.updatedEvent);
                if (this.subjectGridOptions) {
                    const subjectContext = {
                        summary: this.subjectSummary,
                        isOpen: this.subjectsTableVisible,
                        tableData: this.subjectsData,
                        modelChanged: this.updatedSubject
                    };
                    this.subjectTableService.saveToStore(subjectContext);
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

    public isTableVisible(table: string): boolean {
        switch (table) {
            case 'biomarker':
                return this.datasetViews.hasOncoBiomarkers();
            default:
                return true;
        }
    }
}
