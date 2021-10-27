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

import {flatMap, groupBy, map} from 'lodash';
import {DEFAULT_HEADER_VALUE} from './AEsSummaryConstants';
import AeSummariesCell = InMemory.AeSummariesCell;
import AeSummariesCohortCount = InMemory.AeSummariesCohortCount;

/**
 * Sorts the rows in alphabetical order, but (Empty) is always last
 * Result of sorting: Any AE, PSYCHIATRIC DISORDERS, (Empty)
 * @param row1
 * @param row2
 * @returns {number}
 */
const sortBySoc = (row1, row2): number => {
    const soc1 = row1.soc;
    const soc2 = row2.soc;

    if (/\(.+\)/.test(soc1)) {
        return 1;
    }
    if (/\(.+\)/.test(soc2)) {
        return -1;
    }

    return soc1.localeCompare(soc2);
};

/**
 * Sorts the rows in alphabetical order, but (Empty) is always last but one, Subtotal is last
 * Result of sorting: FATIGUE, PYURIA, (Empty), Subtotal
 * @param row1
 * @param row2
 * @returns {number}
 */
const sortByPt = (row1, row2): number => {
    const pt1 = row1.pt;
    const pt2 = row2.pt;
    const SUBTOTAL = 'Subtotal';
    const firstRowShouldBeAbove = pt2 === SUBTOTAL || /\(.+\)/.test(pt2);
    const secondRowShouldBeAbove = pt1 === SUBTOTAL || /\(.+\)/.test(pt1);

    if (pt2 === SUBTOTAL && /\(.+\)/.test(pt1)) {
        return -1;
    }

    if (pt1 === SUBTOTAL && /\(.+\)/.test(pt2)) {
        return 1;
    }

    if (firstRowShouldBeAbove) {
        return -1;
    }

    if (secondRowShouldBeAbove) {
        return 1;
    }

    return pt1.localeCompare(pt2);
};

export abstract class AEsSummaryDataService {

    /**
     * Convert data from backend to table row data
     * @param {InMemory.AeSummariesTable[]} data from backend
     * @returns {any[]}
     */
    abstract convertData(data: InMemory.AeSummariesTable[]): any[];

    /**
     * get table columns based on data from backend
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {({headerName: string; field: string; cellStyle: (params) => {'font-weight': string}}
     * | {headerName: string; children: ({} & any)[]})[]}
     */
    abstract getColumns(data: InMemory.AeSummariesTable[]): any[];

    abstract getApi(): string;

    abstract getNotes(): string[];

    abstract getTableName(): string;

    /**
     * check that data from backend is valid
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {InMemory.AeSummariesTable[] | boolean}
     */
    isValidData(data: InMemory.AeSummariesTable[]) {
        return data && data.length > 0;
    }

    /**
     * key value for non-category cell
     * @param {InMemory.AeSummariesCell} cell
     * @returns {string}
     */
    computeValue(cell: AeSummariesCell) {
        return `${isNaN(+cell.value) ? '-' : +cell.value} (${isNaN(+cell.percentage) ? '-' : (+cell.percentage).toFixed(1)})`;
    }

    /**
     * generate key for non-category column(columns that are defined dynamically)
     * @param {AeSummariesCohortCount} cohortCount
     * @returns {string}
     */
    protected getKeyForColumn(cohortCount: AeSummariesCohortCount) {
        return this.replaceDotsWithDashes(cohortCount.studyPart + '-' + cohortCount.cohort + '-' + cohortCount.grouping);
    }

    /**
     * Get cohort columndef text
     * @param {AeSummariesCohortCount} cohortCount
     * @returns {string}
     */
    protected getCohortCountColumnValue(cohortCount: AeSummariesCohortCount) {
        return `(N=${cohortCount.count})`;
    }

    protected formatCohortColumn(cohort: string): string {
        return cohort.replace(/^\s*\([^(^)]*\)/, '') || DEFAULT_HEADER_VALUE;
    }

    /**
     * Group data by soc and pt
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {InMemory.AeSummariesRow[]}
     */
    public groupDataBySocAndPt(data: InMemory.AeSummariesTable[]): any {
        return map(groupBy(flatMap(data, a => (a.rows.filter(row => row))), 'soc'),
            soc => groupBy(soc, 'pt'));

    }

    /**
     * Group data by soc, pt and drug
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {InMemory.AeSummariesRow[]}
     */
    public groupDataBySocAndPtAndDrug(data: InMemory.AeSummariesTable[]): any {
        return map(this.groupDataBySocAndPt(data), d => map(<any>d, arr => groupBy(<any>arr, 'drug')));
    }

    protected sortByStudyPart(el1, el2) {
        if (el1.headerName === DEFAULT_HEADER_VALUE) {
            return el1.headerName === el2.headerName ? 0 : 1;
        } else if (el2.headerName === DEFAULT_HEADER_VALUE) {
            return -1;
        }
        return el1.headerName.localeCompare(el2.headerName);
    }

    protected sortByCohortName(i1, i2) {
        const c1 = i1.cohort;
        const c2 = i2.cohort;
        if (/\(.+\)/.test(c1)) {
            if (/\(.+\)/.test(c2)) {
                return c1.localeCompare(c2);
            } else {
                return -1;
            }
        } else if (/\(.+\)/.test(c2)) {
            return 1;
        } else {
            return c1.localeCompare(c2);
        }
    }

    protected sortBySocAndPt(row1, row2): number {
        return row1.soc !== row2.soc
            ? sortBySoc(row1, row2)
            : sortByPt(row1, row2);
    }

    //extracts number from string like '12 (4%)'
    //and sorts numerically
    protected sortByNumericValue(o1, o2) {
        return parseFloat(o1) - parseFloat(o2);
    }

    protected sortNumerically(o1, o2) {
        return o1 - o2;
    }

    protected replaceDotsWithDashes(value: string): string {
        return value.replace(/\./g, '-');
    }
}
