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
import {findIndex, forEach, map, sample, sum, sumBy} from 'lodash';

import {Dashboard, DashboardCell, DashboardRow, Header} from '../../../common/trellising';
import OutputBarChartData = Request.OutputBarChartData;

const Y_GROUP_BY_OPTIONS = {
    percentageOfSubjects: 'PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED',
    percentageOfSubjectsByColumn: 'PERCENTAGE_OF_EVENTS_100_STACKED',
    percentageOfAllSubjects: 'PERCENTAGE_OF_ALL_SUBJECTS'
};

@Injectable()
export class PopulationSummaryTableService {
    dashboard: Dashboard;
    flattenData: any[];

    constructor() {
        this.reset();
    }

    private reset(): void {
        this.dashboard = <Dashboard>{
            table: [],
            tableHeaders: []
        };
        this.flattenData = [];
    }

    private orEmpty(value: string): string {
        return value || '(Empty)';
    }

    public resetSelections(): void {
        // this.dashboard.highlightedCells.length = 0;
    }

    public processData(response: OutputBarChartData[], valueOption: string): void {
        this.reset();
        if (response.length > 0) {
            this.flattenData = response;

            this.dashboard.tableHeaders = map(sample(response).categories, (category: any) => {
                return <Header>{columnName: this.orEmpty(category), total: 0};
            });
            const header: Header = {columnName: 'Total', total: 0};
            this.dashboard.tableHeaders.push(header);
            const _that = this;
            const totalSubjectNumber = sum(response.map((data: OutputBarChartData) => {
                return sum(map(data.series, 'totalSubjects'));
            }));

            forEach(response, (value: OutputBarChartData) => {
                const row: DashboardCell[] = [];
                for (let i = 0; i < this.dashboard.tableHeaders.length - 1; i++) {
                    const cell = value.series.find((cell_) => {
                        return this.orEmpty(cell_.category) === this.orEmpty(this.dashboard.tableHeaders[i].columnName);
                    });
                    if (cell) {
                        row.push(<DashboardCell>{
                            columnName: this.orEmpty(cell.category),
                            rowName: this.orEmpty(value.name),
                            countValue: cell.value
                        });
                    } else {
                        row.push(<DashboardCell>{
                            columnName: this.orEmpty(this.dashboard.tableHeaders[i].columnName),
                            rowName: this.orEmpty(value.name),
                            countValue: 0
                        });
                    }
                }
                const rowSum = this.isYPercantage(valueOption)
                    ? (sumBy((<any>value.series), 'totalSubjects') / totalSubjectNumber) * 100
                    : sumBy((<any>value.series), 'value');

                row.push(<DashboardCell>{
                    columnName: 'Total',
                    rowName: this.orEmpty(value.name),
                    countValue: rowSum
                });

                forEach(_that.dashboard.tableHeaders, (header_: Header) => {
                    if (findIndex(row, (cell: any) => {
                            return this.orEmpty(cell.columnName) === this.orEmpty(header_.columnName);
                        }
                    ) === -1) {
                        row.push({
                            columnName: this.orEmpty(header_.columnName),
                            rowName: this.orEmpty(value.name),
                            countValue: 0
                        });
                    }
                });

                // New rows are added with "unshift" to show them in a correct order.
                // The original order is reversed in PopulationHttpService.ts in getPlotData() method
                // to show bar charts in the correct order
                _that.dashboard.table.unshift(<DashboardRow>{rowName: this.orEmpty(value.name), rowValue: row});
            });

            const columnTotalRow = {
                rowName: 'Total',
                rowValue: []
            };

            forEach(this.dashboard.tableHeaders, (header_: Header, index: number) => {
                let columnSum;
                if (valueOption === Y_GROUP_BY_OPTIONS.percentageOfSubjects ||
                    valueOption === Y_GROUP_BY_OPTIONS.percentageOfSubjectsByColumn) {
                    columnSum = 100;
                } else {
                    columnSum = sumBy(_that.dashboard.table, (row: DashboardRow) => {
                        return row.rowValue[index].countValue;
                    });
                }

                columnTotalRow.rowValue.push({
                        rowName: 'Total',
                        countValue: columnSum,
                        columnName: header_.columnName
                    }
                );
                header_.total = columnSum;
            });
            this.dashboard.table.push(columnTotalRow);
        }
    }

    private isYPercantage(option: string): boolean {
        return option === Y_GROUP_BY_OPTIONS.percentageOfSubjects
            || option === Y_GROUP_BY_OPTIONS.percentageOfAllSubjects
            || option === Y_GROUP_BY_OPTIONS.percentageOfSubjectsByColumn;
    }

    // private selectRow(row: DashboardRow): any {
    //     if (row.rowName === 'Total') {
    //         return map(this.flattenData, (cell: any) => {
    //             return omit(cell, ['countValue', 'column']);
    //         });
    //     } else {
    //         return map(filter(row.rowValue, (cell: DashboardCell) => {
    //             return cell.columnName !== 'Total';
    //         }), (cell) => {
    //             return omit(cell, ['countValue', 'column']);
    //         });
    //     }
    // };
    //
    // private selectCell(cell: DashboardCell): any {
    //     if (cell.columnName === 'Total') {
    //         let row = find(this.dashboard.table, (row: DashboardRow) => {
    //             return row.rowName === cell.rowName;
    //         });
    //         return this.selectRow(row);
    //     } else {
    //         if (cell.rowName === 'Total') {
    //             return this.selectColumn(cell);
    //         } else {
    //             return [omit(cell, ['countValue', 'column'])];
    //         }
    //     }
    // };
    //
    // private selectColumn(headerCell: DashboardCell): any {
    //     let selectedCells;
    //     if (headerCell.columnName === 'Total') {
    //         selectedCells = cloneDeep(this.flattenData);
    //     } else {
    //         selectedCells = filter(this.flattenData, (cell: any) => {
    //             return cell.columnName === headerCell.columnName;
    //         });
    //     }
    //     return map(selectedCells, (cell) => {
    //         return omit(cell, ['countValue', 'column']);
    //     });
    // };
    //
    // public selectData(event: MouseEvent, selection: any, type: string, rowName: string): any {
    //     let selectedData = [];
    //     let highlightedCells = [];
    //     if (type === 'column') {
    //         forEach(this.dashboard.table, (row: DashboardRow) => {
    //             highlightedCells.push(selection.columnName + ' ' + row.rowName);
    //         });
    //         selectedData = this.selectColumn(selection);
    //     } else {
    //         if (type === 'row') {
    //             forEach(selection.rowValue, (cell: DashboardCell) => {
    //                 highlightedCells.push(cell.columnName + ' ' + selection.rowName);
    //             });
    //             selectedData = this.selectRow(selection);
    //         } else {
    //             selectedData = this.selectCell(selection);
    //             highlightedCells = [selection.columnName + ' ' + rowName];
    //         }
    //     }
    //     if (event.ctrlKey) {
    //         this.dashboard.selectedData = uniq(union(this.dashboard.selectedData, selectedData), (cell) => {
    //             return cell.columnName + cell.rowName;
    //         });
    //         this.dashboard.highlightedCells = uniq(union(this.dashboard.highlightedCells, highlightedCells));
    //     } else {
    //         this.dashboard.highlightedCells = highlightedCells;
    //         this.dashboard.selectedData = selectedData;
    //     }
    // };
}
