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
import {flatMap, groupBy, map, reduce, sortBy, values} from 'lodash';

import {AEsSummaryDataService} from '../AEsSummaryDataService';
import {COMMON_AE_NOTES, COMMON_CATEGORY, DEFAULT_HEADER_VALUE, MOST_COMMON_AE_NAME} from '../AEsSummaryConstants';
import AeSummariesCohortCount = InMemory.AeSummariesCohortCount;
import AeSummariesCell = InMemory.AeSummariesCell;

const PT_VALUE = 'PT';
const PT_KEY = 'pt';

@Injectable()
export class AEsSummaryMstCmnDataService extends AEsSummaryDataService {

    /**
     * Convert data from backend to table row data
     * @param {InMemory.AeSummariesTable[]} data from backend
     * @returns {any[]}
     */
    convertData(data: InMemory.AeSummariesTable[]): any[] {
        return map(
            this.sortRowsByTotalFrequency(map(
                values(
                    this.groupDataByRowDescription(data)), (arr: any) => arr.reduce((prev, cur) => {
                    return {
                        rowDescription: cur.rowDescription,
                        id: cur.id,
                        cells: prev.cells.concat(cur.cells)
                    };
                }))), v =>
                reduce(v.cells.filter(cell => cell), (a, b: AeSummariesCell) =>
                    Object.assign(a, {
                        [this.getKeyForValueColumn(b.cohortCount)]: this.getValue(b),
                        [this.getKeyForPercentColumn(b.cohortCount)]: this.getPercentValue(b)
                    }), {[PT_KEY]: v.rowDescription}));
    }

    /**
     * get table columns based on data from backend
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {({headerName: string; field: string; cellStyle: (params) => {'font-weight': string}} | {headerName: string; children: ({} & any)[]})[]}
     */
    getColumns(data: InMemory.AeSummariesTable[]) {
        const ptColumn = {headerName: `${PT_VALUE}`, field: PT_KEY, pinned: 'left', suppressMenu: true};
        const otherColums = map(groupBy(data, 'datasetName'),
            (dsData, datasetName) => (
                {
                    headerName: datasetName || DEFAULT_HEADER_VALUE,
                    children: flatMap(dsData, ds => map(groupBy(ds.cohortCounts, 'studyPart'),
                        (el, studyPart) => ({
                            headerName: studyPart || DEFAULT_HEADER_VALUE,
                            children: map(groupBy(el, 'grouping'), (group, grouping) => ({
                                headerName: grouping || DEFAULT_HEADER_VALUE,
                                children: group.sort(this.sortByCohortName)
                                    .map((cohortCount: AeSummariesCohortCount) => {
                                        return ({
                                            headerName: this.formatCohortColumn(cohortCount.cohort),
                                            children: [{
                                                headerName: this.getCohortCountColumnValue(cohortCount),
                                                children: [
                                                    {
                                                        field: this.getKeyForValueColumn(cohortCount),
                                                        headerName: 'Count of subjects',
                                                        cellStyle: {textAlign: 'right'},
                                                        suppressMenu: true
                                                    },
                                                    {
                                                        field: this.getKeyForPercentColumn(cohortCount),
                                                        headerName: '%',
                                                        cellStyle: {textAlign: 'right'},
                                                        suppressMenu: true,
                                                        comparator: this.sortNumerically
                                                    }]
                                            }]
                                        });
                                    })
                            }))
                        })
                        )
                    ).sort(this.sortByStudyPart)
                }));

        return [ptColumn, ...otherColums];
    }

    getApi(): string {
        return COMMON_CATEGORY.api;
    }

    getNotes(): string[] {
        return COMMON_AE_NOTES;
    }

    getTableName(): string {
        return MOST_COMMON_AE_NAME;
    }

    /**
     * generate key for subject count column(columns that are defined dynamically)
     * @param {AeSummariesCohortCount} cohortCount
     * @returns {string}
     */
    getKeyForValueColumn(cohortCount: AeSummariesCohortCount) {
        return this.replaceDotsWithDashes(cohortCount.studyPart + '-' + cohortCount.cohort + '-' + cohortCount.grouping + '-value');
    }

    /**
     * generate key for subject percent column(columns that are defined dynamically)
     * @param {AeSummariesCohortCount} cohortCount
     * @returns {string}
     */
    getKeyForPercentColumn(cohortCount: AeSummariesCohortCount) {
        return this.replaceDotsWithDashes(cohortCount.studyPart + '-' + cohortCount.cohort + '-' + cohortCount.grouping + '-percent');
    }

    /**
     * count value for non-category cell
     * @param {InMemory.AeSummariesCell} cell
     * @returns {string}
     */
    getValue(cell: AeSummariesCell) {
        return isNaN(+cell.value) ? '-' : cell.value;
    }

    /**
     * percent value for non-category cell
     * @param {InMemory.AeSummariesCell} cell
     * @returns {string}
     */
    getPercentValue(cell: AeSummariesCell) {
        return isNaN(+cell.percentage) ? '-' : (+cell.percentage).toFixed(1);
    }

    /**
     * Convert exising data to list of row
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {Dictionary<any[]>}
     */
    private groupDataByRowDescription(data: InMemory.AeSummariesTable[]): any {
        return groupBy(
            flatMap(data,
                a => sortBy(a.rows.filter(row => row), 'rowDescription'))
            , e => (<any>e).rowDescription
        );
    }

    private sortRowsByTotalFrequency(rows: any[]): any[] {
        return rows.sort((row1, row2) => {
            const val1 = row1.cells.find(cell => cell.cohortCount.cohort === 'TOTAL').value;
            const val2 = row2.cells.find(cell => cell.cohortCount.cohort === 'TOTAL').value;
            const compare = val1 - val2;
            if (compare === 0) {
                return row1.rowDescription.localeCompare(row2.rowDescription);
            } else {
                return -compare;
            }
        });
    }
}

