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
import {List, Map} from 'immutable';
import {capitalize, forEach, intersectionWith, isArray, isEmpty, startCase} from 'lodash';
import {ColDef, ColGroupDef} from 'ag-grid/main';

import {DataService} from '../../data/DataService';
import {DatasetViews} from '../../../../security/DatasetViews';
import {AbstractTableService} from './AbstractTableService';
import {DetailsOnDemandHeightService} from './DetailsOnDemandHeightService';
import {AdverseEventsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/adverse-events/AdverseEventsColumnModel';
import {ConmedsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/conmeds/ConmedsColumnModel';
import {ExacerbationsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/exacerbations/ExacerbationsColumnModel';
import {LabsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/labs/LabsColumnModel';
import {RenalColumnsModel} from '../../../../plugins/refactored-singlesubject/datatypes/renal/RenalColumnsModel';
import {VitalsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/vitals/VitalsColumnModel';
import {CardiacColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cardiac/CardiacColumnModel';
import {
    CerebrovascularColumnModel
} from '../../../../plugins/refactored-singlesubject/datatypes/cerebrovascular/CerebrovascularColumnModel';
import {CIEventsColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cievents/CIEventsColumnModel';
import {CvotColumnModel} from '../../../../plugins/refactored-singlesubject/datatypes/cvot/CvotColumnModel';
import {getColumnGroupDefs} from './model/BiomarkersTableColumns';
import {QTProlongationColumnModel} from './model/QTProlongationColumnModel';

export enum ColumnTooltips {
    actualDose = 'From the file containing summary PK data'
}

@Injectable()
export class DetailsOnDemandEventTableService extends AbstractTableService {

    constructor(protected dataService: DataService,
                protected datasetViews: DatasetViews,
                protected detailsOnDemandHeightService: DetailsOnDemandHeightService,
                protected aesColumnModel: AdverseEventsColumnModel,
                protected conmedsColumnModel: ConmedsColumnModel,
                protected exacerbationsColumnModel: ExacerbationsColumnModel,
                protected labsColumnModel: LabsColumnModel,
                protected renalColumnModel: RenalColumnsModel,
                protected vitalsColumnModel: VitalsColumnModel,
                protected cardiacColumnModel: CardiacColumnModel,
                protected cieventsColumnModel: CIEventsColumnModel,
                protected cerebrovascularColumnModel: CerebrovascularColumnModel,
                protected cvotColumnModel: CvotColumnModel,
                protected qtProlongationColumnModel: QTProlongationColumnModel) {
        super();
    }

    private static getDefinitionsForAvailableColumns(columnGroup: ColGroupDef): ColDef[] {
        return columnGroup.children
            .map((columnDef: ColDef) => {
                columnDef.headerTooltip = columnDef.headerName;
                columnDef.tooltipField = columnDef.field;
                return columnDef;
            });
    }

    setColumnDefs(expandedGroupsIds: string[], tableName?: any): void {
        const page: string = this.getMetadataKey();
        let columns = this.datasetViews.getDetailsOnDemandColumns(page);
        columns = Map.isMap(columns) ? columns.get(tableName) : columns;
        const titles = this.datasetViews.getDetailsOnDemandColumnsTitles(tableName || page);
        let columnDefs = this.getColumnsByPage(page, tableName).toArray();
        const hasOnlyGroups = this.hasOnlyGroups(tableName);
        // if we have columns from b/e and
        if (isArray(columns) && columns.length !== 0) {
            // we don't have column model on the front
            if (isEmpty(columnDefs)) {
                columnDefs = columns.map((column) => {
                    const headerName = titles ? titles[column] : capitalize(startCase(column).toLowerCase());
                    const headerTooltip = ColumnTooltips[column] || headerName;
                    return {
                        headerName: headerName,
                        headerTooltip: headerTooltip,
                        tooltipField: column,
                        field: column
                    };
                });
            } else {
                // if we have columns from BE and we also have column model on the front
                // here we get intersection of columns from b/e and coldefs from the frontend
                // to get the actual columns
                const actualColumns = intersectionWith(columnDefs, <any>columns, (coldef, column) => {
                    if (column === coldef.field) {
                        return true;
                    } else if (coldef.children) {
                        return coldef.children[0].field === column;
                    }
                });
                columnDefs = actualColumns.map((coldef) => {
                    const headerName = titles && titles[coldef.field] ? titles[coldef.field]
                        : capitalize(startCase(coldef.field || coldef.headerName).toLowerCase());
                    return {
                        ...coldef,
                        headerName: headerName,
                        headerTooltip: headerName,
                        tooltipField: coldef.field,
                    };
                });
                columnDefs.forEach(colDef => {
                    if (colDef.children) {
                        colDef.children = colDef.children.filter(child => {
                            if (!child.headerName) {
                                child.headerName = titles[child.field];
                            }
                            return !!columns.find(availableColumn =>
                                availableColumn === child.field);
                        });
                    }
                });
            }
        }
        if (!isEmpty(columnDefs) || hasOnlyGroups) {
            const availableColumnGroupDefs = new Array<any>();
            if (hasOnlyGroups) {
                columnDefs = getColumnGroupDefs(columns).toArray();
            }
            columnDefs.forEach(column => {
                if (column.children) {
                    this.extractColumns(column, availableColumnGroupDefs);
                }
            });
            this.gridOptions.api.setColumnDefs(columnDefs);
            forEach(expandedGroupsIds, (groupId: string) => {
                this.gridOptions.columnApi.setColumnGroupOpened(groupId, true);
            });
           this.setColumnWidth();
        }

        if (tableName && tableName === 'ctdna') {
            this.gridOptions.headerHeight = 50;
        }
    }

    /**
     * If there are multiple event tables on a view, we need to get names of these tables.
     * Here they are taken from metadata.
     * @returns {List<string>} List of tables names
     */
    getMultipleEventTablesNames(): List<string> {
        const page: string = this.getMetadataKey();
        const columns = this.datasetViews.getDetailsOnDemandColumns(page);
        return Map.isMap(columns) ? List(<string[]>columns.keys()) : null;
    }

    hasOnlyGroups(tableName?: string): boolean {
        // for now only biomarkers event dods have groupings
        return this.getMetadataKey() === 'biomarker' || tableName === 'biomarker';
    }

    private getMetadataKey(): string {
        const plugin = window.location.hash.split('/')[2];
        const plot = window.location.hash.split('/')[3];
        if (plugin === 'respiratory' && plot.indexOf('exacerbations') > -1) {
            return 'exacerbations';
        }
        if (plot === 'chord-diagram') {
            return 'ae-chord';
        }
        if (plugin === 'machine-insights') {
            return plot;
        }
        return plugin;
    }

    private extractColumns(columnGroup: ColGroupDef, availableColumnGroupDefs: Array<any>) {
        // filtering columns that are absent in info request
        columnGroup.children = columnGroup.children.filter(child => child.headerName);
        if (columnGroup.children.length > 0) {
            const columnDefs = DetailsOnDemandEventTableService.getDefinitionsForAvailableColumns(columnGroup);
            // always show first column
            columnDefs[0].columnGroupShow = null;
            this.addColumnGroupToTable(availableColumnGroupDefs, columnGroup, columnDefs);
        }
    }

    /**
     * Gets columns from model
     * @param key - page key to know which model we need
     * @param tableName - if multiple event tables are needed on one view, we need to know which table is chosen
     * @returns {List<ColDef>} list of column definitions for the view
     */
    private getColumnsByPage(key: string, tableName?: any): List<any> {
        switch (key) {
            case 'aes':
                return this.aesColumnModel.columnDefs;
            case 'conmeds':
                return this.conmedsColumnModel.columnDefs;
            case 'exacerbations':
                return this.exacerbationsColumnModel.columnDefs;
            case 'labs':
                return this.labsColumnModel.columnDefs;
            case 'renal':
                return this.renalColumnModel.columnDefs;
            case 'vitals':
                return this.vitalsColumnModel.columnDefs;
            case 'cardiac':
                return this.cardiacColumnModel.columnDefs;
            case 'cievents':
                return this.cieventsColumnModel.columnDefs;
            case 'cerebrovascular':
                return this.cerebrovascularColumnModel.columnDefs;
            case 'cvot':
                return this.cvotColumnModel.columnDefs;
            case 'qt-prolongation':
                return this.qtProlongationColumnModel.columnDefs;
            default:
                return List();
        }
    }

    private addColumnGroupToTable(availableColumnGroupDefs: ColGroupDef[], columnGroup: ColGroupDef, columnDefs: ColDef[]): void {
        availableColumnGroupDefs.push({
            headerName: columnGroup.headerName,
            children: columnDefs,
        });
    }

}
