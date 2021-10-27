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
import {flatMap, groupBy, map, reduce} from 'lodash';
import {ColDef} from 'ag-grid/main';

import {AEsSummaryDataService} from '../AEsSummaryDataService';
import {
    COMMON_AE_NOTES,
    DEATH_OUTCOME_AE_NAME,
    DEATH_OUTCOME_CATEGORY,
    DEFAULT_HEADER_VALUE
} from '../AEsSummaryConstants';

const SOC_VALUE = 'SOC';
const SOC_KEY = 'soc';
const PT_VALUE = 'PT';
const PT_KEY = 'pt';

@Injectable()
export class AEsSummaryDeathOutcomeDataService extends AEsSummaryDataService {

    constructor() {
        super();
    }

    /**
     * Convert data from backend to table row data
     * @param {InMemory.AeSummariesTable[]} data from backend
     * @returns {any[]}
     */
    convertData(data: InMemory.AeSummariesTable[]): any[] {
        return map(
            flatMap(
                map(this.groupDataBySocAndPt(data), d => map(d, (arr: any) => arr.reduce((prev, cur) => {
                        return {
                            [SOC_KEY]: cur.soc,
                            [PT_KEY]: cur.pt,
                            cells: prev.cells.concat(cur.cells)
                        };
                    }))
                ), arr => arr), v =>
                reduce(v.cells.filter(cell => cell), (a, b) =>
                        Object.assign(a, {[this.getKeyForColumn(b.cohortCount)]: this.computeValue(b)}), {
                        [SOC_KEY]: v.soc,
                        [PT_KEY]: v.pt
                    }
                )).sort(this.sortBySocAndPt);
    }

    /**
     * get table columns based on data from backend
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {({
     *  headerName: string;
     *  field: string;
     *  cellStyle: (params) => {'font-weight': string}
     * }
     * |
     * {
     *  headerName: string;
     *  children: ({} & any)[]
     * })[]}
     */
    getColumns(data: InMemory.AeSummariesTable[]): ColDef[] {
        const columnDefs = [];

        columnDefs.push({
            field: `${SOC_KEY}`,
            headerName: `${SOC_VALUE}`,
            pinned: 'left'
        });

        columnDefs.push(
            {
                headerName: `${PT_VALUE}`,
                field: PT_KEY,
                suppressMenu: true
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
                                            .map(cohortCount => {
                                                return {
                                                    headerName: this.formatCohortColumn(cohortCount.cohort),
                                                    children: [{
                                                        headerName: this.getCohortCountColumnValue(cohortCount),
                                                        field: this.getKeyForColumn(cohortCount),
                                                        comparator: this.sortByNumericValue,
                                                        suppressMenu: true
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
            });

        return columnDefs;
    }

    getApi(): string {
        return DEATH_OUTCOME_CATEGORY.api;
    }

    getNotes(): string[] {
        return COMMON_AE_NOTES;
    }

    getTableName(): string {
        return DEATH_OUTCOME_AE_NAME;
    }
}
