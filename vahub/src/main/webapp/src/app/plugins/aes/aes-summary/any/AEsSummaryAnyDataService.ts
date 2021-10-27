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

import {DEFAULT_HEADER_VALUE, ANY_AE_NAME, ANY_AE_NOTES, ANY_CATEGORY} from '../AEsSummaryConstants';
import {AEsSummaryDataService} from '../AEsSummaryDataService';
import AeSummariesCell = InMemory.AeSummariesCell;
import AeSummariesCohortCount = InMemory.AeSummariesCohortCount;

const CATEGORY_VALUE = 'Category';
const CATEGORY_KEY = 'category';

@Injectable()
export class AEsSummaryAnyDataService extends AEsSummaryDataService {


    /**
     * Convert data from backend to table row data
     * @param {InMemory.AeSummariesTable[]} data from backend
     * @returns {any[]}
     */
    convertData(data: InMemory.AeSummariesTable[]): any[] {
        return map(
            map(
                values(
                    this.groupDataByRowDescription(data)
                ), (arr: any) => arr.reduce((prev, cur) => {
                    return {
                        rowDescription: cur.rowDescription,
                        id: cur.id,
                        cells: prev.cells.concat(cur.cells)
                    };
                })), v =>
                reduce(v.cells.filter(cell => cell), (a, b: AeSummariesCell) =>
                    Object.assign(a, {[this.getKeyForColumn(b.cohortCount)]: this.computeValue(b)}), {[CATEGORY_KEY]: v.rowDescription}));
    }

    /**
     * get table columns based on data from backend
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {({headerName: string; field: string; cellStyle: (params) => {'font-weight': string}}
     * | {headerName: string; children: ({} & any)[]})[]}
     */
    getColumns(data: InMemory.AeSummariesTable[]): any[] {
        return [
            {
                headerName: `${CATEGORY_VALUE}`, field: CATEGORY_KEY, pinned: 'left', suppressMenu: true
            },
            {
                headerName: 'Number (%) of subjects',
                children: map(
                    groupBy(data, 'datasetName'),
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
                                                return {
                                                    headerName: this.formatCohortColumn(cohortCount.cohort),
                                                    children: [{
                                                        headerName: this.getCohortCountColumnValue(cohortCount),
                                                        field: this.getKeyForColumn(cohortCount),
                                                        suppressMenu: true,
                                                        comparator: this.sortByNumericValue
                                                    }]
                                                };
                                            })
                                    }))
                                })
                                )
                            ).sort(this.sortByStudyPart)
                        }
                    )
                )
            }
        ];
    }

    getApi(): string {
        return ANY_CATEGORY.api;
    }

    getNotes(): string[] {
        return ANY_AE_NOTES;
    }

    getTableName(): string {
        return ANY_AE_NAME;
    }

    /**
     * Convert exising data to list of row
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {_.Dictionary<any[]>}
     */
    private groupDataByRowDescription(data: InMemory.AeSummariesTable[]): any {
        return groupBy(
            flatMap(data,
                a => sortBy(a.rows.filter(row => row), 'rowDescription'))
            , e => (<any>e).rowDescription
        );
    }
}

