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
import {ColDef, ColGroupDef} from 'ag-grid/main';
import {each, forEach, isEmpty, includes, values, keys} from 'lodash';

import {AbstractTableService} from './AbstractTableService';
import {DataService} from '../../data/DataService';
import {DatasetViews} from '../../../../security/DatasetViews';
import {SubjectTableColumns} from './model/SubjectTableColumns';
import {DetailsOnDemandHeightService} from './DetailsOnDemandHeightService';

@Injectable()
export class DetailsOnDemandSubjectTableService extends AbstractTableService {

    private readonly STUDY_SPECIFIC_FILTER_PREFIX = 'studySpecificFilters';
    private readonly COLUMNS_PER_DRUG_PREFIX = 'columnsPerDrug';
    private readonly MAPPED_COLUMN_SEPARATOR = '--';

    constructor(protected dataService: DataService,
                protected datasetViews: DatasetViews,
                protected detailsOnDemandHeightService: DetailsOnDemandHeightService) {
        super();
    }

    formatRows(rowsThisPage: any): void {
        each(rowsThisPage, (row) => {
            this.formatColumns(row);
        });
    }

    formatMappedColumns(rowsThisPage: any): void {
        each(rowsThisPage, (row) => {
            this.formatStudySpecificFilterData(row);
            this.formatColumnsPerDrugData(row);
        });
    }

    private formatStudySpecificFilterData(row: any): void {
        const that = this;
        if (!isEmpty(row.studySpecificFilters)) {
            each(row.studySpecificFilters, (filter) => {
                const field = that.STUDY_SPECIFIC_FILTER_PREFIX + that.MAPPED_COLUMN_SEPARATOR + keys(filter)[0];
                row[field] = values(filter)[0];
            });
        }
    }

    private formatColumnsPerDrugData(row: any): void {
        const that = this;
        if (!isEmpty(row.columnsPerDrug)) {
            each(row.columnsPerDrug, (filter) => {
                const field = that.COLUMNS_PER_DRUG_PREFIX + that.MAPPED_COLUMN_SEPARATOR + keys(filter)[0];
                row[field] = values(filter)[0];
            });
        }
    }

    setColumnDefs(expandedGroupsIds: string[]): void {
        this.gridOptions.api.setColumnDefs(this.getAvailableColumns());
        this.setColumnWidth();
        forEach(expandedGroupsIds, (groupId: string) => {
            this.gridOptions.columnApi.setColumnGroupOpened(groupId, true);
        });
    }

    private getAvailableColumns(): ColGroupDef[] {
        const columnGroupDefs = SubjectTableColumns.getColumnGroupDefs();
        const availableColumns = this.datasetViews.getDetailsOnDemandColumns(this.getMetadataKey());
        const availableColumnGroupDefs: ColGroupDef[] = [];

        each(columnGroupDefs, (columnGroup: ColGroupDef) => {
            this.addColumnsToColumnGroupDefinition(columnGroup, availableColumns);
            const availableColumnDefs = this.getDefinitionsForAvailableColumns(columnGroup, availableColumns);
            if (!isEmpty(availableColumnDefs)) {
                this.setFirstColumnToAlwaysBeVisible(availableColumnDefs);
                this.addColumnGroupToTable(availableColumnGroupDefs, columnGroup, availableColumnDefs);
            }
        });

        return availableColumnGroupDefs;
    }

    private addColumnsToColumnGroupDefinition(columnGroup: ColGroupDef, availableColumns: any): void {
        if (columnGroup.headerName === 'Study Specific Filters') {
            columnGroup.children = this.getChildColumns(availableColumns, this.isStudySpecificFilter);
        }
        if (columnGroup.headerName === 'Dose details') {
            columnGroup.children = this.getChildColumns(availableColumns, this.isDrug);
        }
    }

    private addColumnGroupToTable(availableColumnGroupDefs: ColGroupDef[], columnGroup: ColGroupDef, availableColumnDefs: ColDef[]): void {
        availableColumnGroupDefs.push({
            headerName: columnGroup.headerName,
            children: availableColumnDefs
        });
    }

    private getDefinitionsForAvailableColumns(columnGroup: ColGroupDef, availableColumns: any): ColDef[] {
        return columnGroup.children
            .filter((columnDef: ColDef) => includes(Object.keys(availableColumns), columnDef.field))
            .map((columnDef: ColDef) => {
                columnDef.headerTooltip = columnDef.headerName;
                columnDef.tooltipField = columnDef.field;
                return columnDef;
            });
    }

    private formatColumns(row: any): void {
        Object.keys(row)
            .forEach(key => {
                if (this.isDrug(key) || this.isStudySpecificFilter(key)) {
                    row[key] = this.getPresentationValue(row[key]);
                }
            });
    }

    private getChildColumns(availableColumns: any, isDrugOrStudySpecificFilter: Function): (ColDef | ColGroupDef)[] {
        return Object.keys(availableColumns)
            .filter(key => isDrugOrStudySpecificFilter(key))
            .map(key => {
                return {
                    headerName: availableColumns[key],
                    field: key,
                    columnGroupShow: 'open',
                    suppressSorting: true
                };
            });
    }

    private setFirstColumnToAlwaysBeVisible(availableColumnDefs: ColGroupDef[] | ColDef[]): void {
        availableColumnDefs[0].columnGroupShow = null;
    }

    private isDrug(key: string): boolean {
        const COLUMNS_DRUG_PREFIX = 'drug';

        return key.indexOf(COLUMNS_DRUG_PREFIX) > -1;
    }

    private isStudySpecificFilter(key: string): boolean {
        const STUDY_SPECIFIC_FILTER_PREFIX = 'studySpecificFilters';

        return key.indexOf(STUDY_SPECIFIC_FILTER_PREFIX) > -1;
    }

    private getPresentationValue(value: string): string {
        return value === 'null' ? '' : value;
    }

    private getMetadataKey(): string {
        return 'population';
    }
}
