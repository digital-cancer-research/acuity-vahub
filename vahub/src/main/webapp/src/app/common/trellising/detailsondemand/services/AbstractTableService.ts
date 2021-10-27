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

import {ColumnGroup, GridOptions} from 'ag-grid/main';
import {isEmpty, filter, map, isUndefined} from 'lodash';
import {Map} from 'immutable';

import {DataService} from '../../data/DataService';
import {TabId, DetailsOnDemandRecord, NEW_APPROACH_TAB_LIST} from '../../store/ITrellising';
import {DatasetViews} from '../../../../security/DatasetViews';
import {DetailsOnDemandHeightService} from './DetailsOnDemandHeightService';
import SortAttrs = Request.SortAttrs;

export abstract class AbstractTableService {

    public filter: string;
    public loading: boolean;

    protected dataService: DataService;
    protected datasetViews: DatasetViews;
    protected gridOptions: GridOptions;
    protected gridOptionsMap: Map<string, GridOptions> = Map<string, GridOptions>();
    protected detailsOnDemandHeightService: DetailsOnDemandHeightService;

    setGridOptions(gridOptions: GridOptions, table?: string): void {
        if (!isUndefined(table)) {
            this.gridOptionsMap = this.gridOptionsMap.set(table, gridOptions);
        }
        this.gridOptions = gridOptions;
    }

    getTableData(tabId: TabId, ids: string[] | Map<string, string[]>): any {
        const sortModelsList = [];
        if (!this.gridOptionsMap.isEmpty() && Map.isMap(ids)) {
            const idsMap = ids as Map<string, string[]>;
            idsMap.mapKeys((key) => {
                sortModelsList.push(this.gridOptionsMap.get(key).api.getSortModel());
            });
        }
        const sortModel = this.gridOptions.api.getSortModel();
        if (!NEW_APPROACH_TAB_LIST.contains(tabId)) {
            return this.dataService.getDetailsOnDemandData(tabId, ids, 0, 1000, this.getSortBy(sortModel),
                this.getSortDirection(sortModel));
        } else {
            let sortsBy = [];
            sortModelsList.forEach(model => sortsBy.push(this.getNewApproachSortBy(model, tabId)));
            if (sortModelsList.length === 0) {
                sortsBy = this.getNewApproachSortBy(sortModel, tabId);
            }
            return this.dataService.getDetailsOnDemand(tabId, ids, 0, 1000, sortsBy);
        }
    }

    gridIsEmpty(table?: string): boolean {
        const options = !isUndefined(table) ? this.gridOptionsMap.get(table) : this.gridOptions;
        return options.api.getRenderedNodes().length === 0;
    }

    getExpandedColumnGroups(): any[] {
        return (this.gridOptions && this.gridOptions.columnApi)
            ? map(filter(this.gridOptions.columnApi.getAllDisplayedColumnGroups(), (group: any) => {
                return group.originalColumnGroup ? group.originalColumnGroup.expanded : false;
            }), (group: ColumnGroup) => {
                return group.getGroupId();
            }) : [];
    }

    saveToStore(context: any): void {
        const updatedData = new DetailsOnDemandRecord({
            summary: context.summary,
            isOpen: context.isOpen,
            isExpanded: !this.detailsOnDemandHeightService.isClosed(),
            tableData: context.tableData,
            expandedGroups: this.getExpandedColumnGroups()
        });
        if (!isEmpty(context.modelChanged)) {
            context.modelChanged.emit(updatedData);
        }
    }

    saveMultipleToStore(context: any, modelChanged: any): void {
        let updatedData = Map();
        context.mapKeys((key, value) => {
            updatedData = updatedData.set(key, new DetailsOnDemandRecord({
                summary: value.summary,
                isOpen: value.isOpen,
                isExpanded: !this.detailsOnDemandHeightService.isClosed(),
                tableData: value.tableData,
                expandedGroups: this.getExpandedColumnGroups()
            }));
        });
        if (!isEmpty(modelChanged)) {
            modelChanged.emit(updatedData);
        }
    }
    protected setColumnWidth(): void {
        const savedState = this.gridOptions.columnApi.getColumnState();
        const changedState = savedState.map(item => {
            item.width = 205;
            return item;
        });
        this.gridOptions.columnApi.setColumnState(changedState);
    }

    abstract setColumnDefs(expandedGroupsIds: string[]): void;

    private getSortBy(sortModel: any): string {
        if (this.isSortPresent(sortModel)) {
            return sortModel[0].colId;
        }
        return 'subjectId';
    }

    private getNewApproachSortBy(sortModel: any, tabId: TabId): SortAttrs[] {
        if (this.isSortPresent(sortModel)) {
            return [{sortBy: sortModel[0].colId, reversed: sortModel[0].sort !== 'asc'}];
        }
        return [];
    }

    private getSortDirection(sortModel: any): string {
        if (this.isSortPresent(sortModel)) {
            return sortModel[0].sort;
        }
        return 'asc';
    }

    private isSortPresent(sortModel: any): boolean {
        return sortModel && sortModel.length > 0;
    }
}
