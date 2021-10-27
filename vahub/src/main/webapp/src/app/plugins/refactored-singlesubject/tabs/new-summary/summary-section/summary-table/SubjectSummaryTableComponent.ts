import {
    ChangeDetectionStrategy, Component, Input, OnInit
} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid';
import {List} from 'immutable';

@Component({
    templateUrl: 'SubjectSummaryTableComponent.html',
    selector: 'summary-table',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SubjectSummaryTableComponent implements OnInit {

    readonly MUCH_DATA_NUMBER = 10;

    @Input() tabTableData: any;
    @Input() columnDefs: List<ColDef>;
    gridOptions: GridOptions;
    hasMuchData: boolean;

    ngOnInit(): void {
        this.hasMuchData = this.tabTableData.length > this.MUCH_DATA_NUMBER;
        this.gridOptions = {
            enableSorting: true,
            enableColResize: true,
            suppressLoadingOverlay: true,
            suppressNoRowsOverlay: true,
            rowSelection: 'multiple',
            domLayout: !this.hasMuchData ? 'autoHeight' : undefined,
            defaultColDef: {
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
            onGridReady: () => {
                this.gridOptions.api.sizeColumnsToFit();
            }
        };
    }
}
