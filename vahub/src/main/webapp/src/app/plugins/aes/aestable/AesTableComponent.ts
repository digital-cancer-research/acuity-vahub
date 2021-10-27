import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {ColDef, GridOptions} from 'ag-grid/main';
import * as _ from 'lodash';

import {FilterEventService} from '../../../filters/module';
import {DropdownItem, StudyService} from '../../../common/module';
import {AesTableDropdownModel} from './AesTableDropdownModel';
import {AesTableHttpService} from '../../../data/aes';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {FilterId} from '../../../common/trellising/store';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {AesTableAgGridStrategy, IAesTableService} from './ag-grid-strategy/AesTableAgGridStrategy';
import AesTable = InMemory.AesTable;

@Component({
    selector: 'aestable',
    templateUrl: 'AesTableComponent.html',
    providers: [AesTableDropdownModel]
})
export class AesTableComponent implements OnInit, OnDestroy {

    saveModalVisible = false;
    saveModalTitle = 'Download table?';

    gridOptions: GridOptions;
    columnDefs: ColDef[];

    tableData: AesTable[];

    private aesFilterSubscription: Subscription;
    private subKey = 'AesTableComponent';
    private aesTableService: IAesTableService;

    constructor(public aesTableDropdownModel: AesTableDropdownModel,
                aesTableAgGridStrategy: AesTableAgGridStrategy,
                protected filterEventService: FilterEventService,
                private studyService: StudyService,
                private aesTableHttpService: AesTableHttpService,
                private sessionEventService: SessionEventService,
                private trellisingDispatcher: TrellisingDispatcher,
                private timelineDispatcher: TimelineDispatcher) {

        this.aesTableService = aesTableAgGridStrategy.getStrategy().aesTableService;

        this.gridOptions = this.aesTableService.getGridOptions();
        this.columnDefs = this.aesTableService.getColumnDefs();
    }

    ngOnInit(): void {
        this.addSpecialInterestGroupsIfAvailable();
        this.listenToEventFilterChanges();
    }

    ngOnDestroy(): void {
        if (this.aesFilterSubscription) {
            this.aesFilterSubscription.unsubscribe();
        }
    }

    onRowGroupOpened(params): void {
        if (params.node.field === 'grade') {
            params.node.expanded = false;
            this.gridOptions.api.onGroupExpandedOrCollapsed();
        }
    }

    onTableInitialised(): void {
        this.refreshDataFromServer();
    }

    aeLevelClicked(newValue: DropdownItem): void {
        this.aesTableDropdownModel.selectedAeLevel = newValue;
        this.refreshDataFromServer();
    }

    onSearchChanged(searchTerm: Event): void {
        this.gridOptions.api.setQuickFilter((<HTMLInputElement> searchTerm.target).value);
    }

    private listenToEventFilterChanges(): void {
        this.aesFilterSubscription = this.filterEventService.aesFilter.subscribe((aesChanged: any) => {
            if (!_.isNull(aesChanged)) {
                this.refreshDataFromServer();
                this.timelineDispatcher.globalResetNotification();
                this.trellisingDispatcher.localResetNotification(FilterId.AES);
            }
        });
        this.filterEventService.addSubscription(this.subKey, this.aesFilterSubscription);
    }

    private addSpecialInterestGroupsIfAvailable(): void {
        if (!_.isEmpty(this.studyService.metadataInfo)) {
            if (this.studyService.metadataInfo.aes.hasCustomGroups) {
                this.aesTableDropdownModel.addSpecialInterestGroupToDropdown();
            } else {
                this.aesTableDropdownModel.removeSpecialInterestGroupFromDropdown();
            }
        }
    }

    private refreshDataFromServer(): void {
        this.gridOptions.api.showLoadingOverlay();
        this.aesTableHttpService
            .getData(
                this.sessionEventService.currentSelectedDatasets,
                this.aesTableDropdownModel.selectedAeLevel.serverName
            )
            .subscribe((rows: AesTable[]) => {
                this.tableData = this.aesTableService.prepareTableData(rows);
                setTimeout(() => {
                    this.gridOptions.api.sizeColumnsToFit();
                    this.gridOptions.api.hideOverlay();
                });
            });
    }

    openSaveModal(): void {
        this.saveModalVisible = true;
    }

    saveModalSubmitted(answer: any): void {
        this.saveModalVisible = false;
        if (answer === true) {
            this.aesTableHttpService.exportData(
                this.sessionEventService.currentSelectedDatasets,
                this.aesTableDropdownModel.selectedAeLevel.serverName
            );
        }
    }
}
