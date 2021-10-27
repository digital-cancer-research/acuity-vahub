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
    AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, Input, OnChanges, OnDestroy, OnInit,
    SimpleChanges
} from '@angular/core';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {GridOptions} from 'ag-grid';
import {List} from 'immutable';
import {Subscription} from 'rxjs/Subscription';
import * as _ from 'lodash';
import {UpdateTableConfig} from '../store/actions/SingleSubjectViewActions';
import {TabId} from '../../../common/trellising/store/ITrellising';
import * as fromTrellising from '../../../common/trellising/store/reducer/TrellisingReducer';

@Component({
    templateUrl: 'SingleSubjectViewTableComponent.html',
    selector: 'single-subject-view-table',
    styleUrls: ['SingleSubjectViewTableComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SingleSubjectViewTableComponent implements OnChanges, OnInit, AfterViewInit, OnDestroy {

    @Input() tabTableData: List<any>;
    @Input() columnDefs: List<any>;
    @Input() tableConfig: any;
    @Input() tabId: TabId;

    chartSelectionSubscription: Subscription;

    isToolPanelAvailable = false;
    isToolPanelShown = false;
    updateToolPanelNeeded = false;

    height: string;

    gridOptions: GridOptions = {
        showToolPanel: this.isToolPanelShown,
        enableSorting: true,
        enableColResize: true,
        suppressLoadingOverlay: true,
        suppressNoRowsOverlay: true,
        rowSelection: 'multiple',
        getContextMenuItems: () => {
            return [
                'copy',
                'copyWithHeaders',
                'separator',
                'toolPanel'
            ];
        },
        defaultColDef: {
            comparator: (valueA, valueB) => {
                // sometimes column has mixed string and numeric data due to
                // conversion of string values in SingleSubjectViewEffects.getTabData effect
                // in such case show numbers before strings
                if ((typeof valueA === 'number' && typeof valueB === 'string')
                    || (typeof valueA === 'string' && typeof valueB === 'number')) {

                    if (typeof valueA === 'number') {
                        return -1;
                    } else {
                        return 1;
                    }
                }

                // use regular comparison logic for other cases
                if (valueA < valueB) {
                    return -1;
                }

                if (valueA > valueB) {
                    return 1;
                }

                return 0;
            }
        }
    };

    constructor(private _store: Store<ApplicationState>,
                private element: ElementRef) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.setTableHeight();
    }

    ngOnInit(): void {
        if (this.isToolPanelShown !== this.tableConfig.isToolPanelShown) {
            this.isToolPanelShown = this.tableConfig.isToolPanelShown;
            this.updateToolPanelNeeded = true;
        }
    }

    ngAfterViewInit(): void {
        if (this.updateToolPanelNeeded) {
            this.showToolPanelChange();
        }
        this.setTableHeight();
        this.chartSelectionSubscription = this.listenToSelectionChange();
    }

    onSearchChanged(searchTerm: Event): void {
        this.gridOptions.api.setQuickFilter((<HTMLInputElement> searchTerm.target).value);
    }

    ngOnDestroy(): void {
        if (this.isToolPanelShown !== undefined && this.tabId) {
            const currentTabConfig = {
                tabId: this.tabId,
                tableConfig: {
                    isToolPanelShown: this.isToolPanelShown
                }
            };
            this._store.dispatch(new UpdateTableConfig(currentTabConfig));
        }
        if (this.chartSelectionSubscription) {
            this.chartSelectionSubscription.unsubscribe();
        }
    }

    showToolPanelChange(): void {
        this.gridOptions.api.showToolPanel(this.isToolPanelShown);
    }

    private setTableHeight(): void {
        const height = window.innerHeight - this.element.nativeElement.getBoundingClientRect().top - 35;
        this.height = height > 300 ? height + 'px' : '300px';
    }

    private listenToSelectionChange(): Subscription {
        const subscription = new Subscription();

        subscription.add(
            this._store.select(fromTrellising.getSelectionDetail)
                .filter((detail) => !_.isNil(detail) && !_.isEmpty(detail.eventIds))
                .subscribe((detail: any) => {
                    if (this.gridOptions.api) {
                        this.gridOptions.api.deselectAll();
                        this.gridOptions.api.forEachNode((node) => {
                            if (detail.eventIds.indexOf(node.data.eventId) !== -1) {
                                node.setSelected(true);
                            }
                        });
                    }
                })
        );

        subscription.add(
            this._store.select(fromTrellising.getSelections)
                .filter((selection) => _.isNil(selection) || selection.size === 0)
                .subscribe(() => {
                    if (this.gridOptions.api) {
                        this.gridOptions.api.deselectAll();
                    }
                })
        );
        return subscription;
    }
}
