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
