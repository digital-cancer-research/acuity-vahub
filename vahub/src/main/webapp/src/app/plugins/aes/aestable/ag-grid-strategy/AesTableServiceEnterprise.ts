import {Injectable} from '@angular/core';
import {ColDef, GridOptions, RowNode} from 'ag-grid/main';
import * as _ from 'lodash';
import {IAesTableService} from './AesTableAgGridStrategy';
import AesTable = InMemory.AesTable;

@Injectable()
export class AesTableServiceEnterprise implements IAesTableService {

    constructor() {
    }

    getColumnDefs(): ColDef[] {
        return [
            {headerName: 'Term', field: 'term', rowGroupIndex: 0},
            {headerName: 'Severity', field: 'grade', rowGroupIndex: 1},
            {headerName: 'Treatment Arm', field: 'treatmentArm', pivotIndex: 1},
            {
                headerName: 'Number of subjects',
                field: 'subjectCountPerGrade',
                aggFunc: 'sum',
                cellRenderer: this.renderCell.bind(this)
            }
        ];
    }

    getGridOptions(): GridOptions {
        return {
            api: null,
            defaultColDef: {
                menuTabs: []
            },
            pivotMode: true,
            enableSorting: true,
            suppressAggFuncInHeader: true,
            groupUseEntireRow: false,
            groupIncludeFooter: true,
            autoGroupColumnDef: {
                cellRenderer: 'group',
                cellRendererParams: {
                    suppressCount: true,
                    footerValueGetter: () => {
                        return '<span style="padding-left: 20px">No incidence</span>';
                    }
                },
                headerName: 'Term/Max. severity grade experienced',
                menuTabs: []
            },
            getContextMenuItems: () => {
                return [
                    'copy',
                    'copyWithHeaders',
                    'separator',
                    'toolPanel'
                ];
            },
            toolPanelSuppressRowGroups: true,
            toolPanelSuppressValues: true,
            toolPanelSuppressPivots: true,
            toolPanelSuppressPivotMode: true
        };
    }

    prepareTableData(rows: AesTable[]): Array<any> {
        return rows;
    }

    renderCell(params: any): string {
        if (params.node.footer) {
            return this.renderFooterCell(params);
        } else if (this.isSeverityRow(params)) {
            return this.renderSeverityCell(params);
        } else {
            return this.renderTermCell(params);
        }
    }

    /* tslint:disable */
    private renderFooterCell(params: any): string {
        let noIncidenceCount = this.getNoIncidenceCount(params);
        let numberOfSubjectsPerArm = <number> _.chain(params.api.getModel().rowsToDisplay)
            .map('allLeafChildren')
            .flatten()
            .map('data')
            .filter((row: AesTable) => row.treatmentArm == this.getTreatmentArm(params))
            .map('subjectCountPerArm')
            .value()[0];
        if (_.isUndefined(noIncidenceCount)) {
            return numberOfSubjectsPerArm + ' (100%)';
        }
        return noIncidenceCount + ' (' + _.round(noIncidenceCount / numberOfSubjectsPerArm * 100, 2) + '%)';
    }

    private renderTermCell(params: any): string {
        if (_.isNull(params.value)) {
            return '0 (0%)';
        } else if (_.isUndefined(params.value)) {
            return '';
        }
        return this.getNumberOfSubjectsPerTerm(params) + ' (' + this.calculatePercentagePerTerm(params) + '%)';
    }

    private renderSeverityCell(params: any): string {
        if (_.isNull(params.value)) {
            return '0 (0%)';
        } else if (_.isUndefined(params.value)) {
            return '';
        }
        return params.value + ' (' + this.calculatePercentagePerGrade(params) + '%)';
    }

    private calculatePercentagePerTerm(params: any): number {
        // ag-grid has no concept unique subject count, so we need to get it from the REST return, rather than param.value
        return _.round(this.getNumberOfSubjectsPerTerm(params) / this.getNumberOfSubjectsPerArm(params) * 100, 2);
    }

    private calculatePercentagePerGrade(params: any): number {
        return _.round(params.value / this.getNumberOfSubjectsPerArm(params) * 100, 2);
    }

    private getNoIncidenceCount(params: any): number {
        return this.getPropertyFromChildren(params, 'noIncidenceCount');
    }

    private getNumberOfSubjectsPerArm(params: any): number {
        return this.getPropertyFromChildren(params, 'subjectCountPerArm');
    }

    private getNumberOfSubjectsPerTerm(params: any): number {
        return this.getPropertyFromChildren(params, 'subjectCountPerTerm');
    }

    private getPropertyFromChildren(params: any, property: string): number {
        let rowChildren = this.getRowChildren(params);
        let treatmentArm = this.getTreatmentArm(params);
        return <number> _.chain(rowChildren)
                .map('data')
                .filter((row: AesTable) => row.treatmentArm === treatmentArm)
                .map(property)
                .value()[0];
    }

    private getRowChildren(params: any): RowNode[] {
        return params.api.getModel().getRow(params.rowIndex).allLeafChildren;
    }

    private getTreatmentArm(params: any): string {
        return params.colDef.pivotKeys ? params.colDef.pivotKeys[0] : null;
    }

    private isSeverityRow(params: any): boolean {
        return params.api.getModel().getRow(params.rowIndex).level === 1;
    }
    /* tslint:enable */
}
