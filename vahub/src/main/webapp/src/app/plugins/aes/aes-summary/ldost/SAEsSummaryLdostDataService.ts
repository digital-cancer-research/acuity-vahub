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
import {LDOST_AE_NOTES, DEFAULT_HEADER_VALUE, LDOST_CATEGORY, LDOST_SAE_NAME} from '../AEsSummaryConstants';
import AeSummariesCell = InMemory.AeSummariesCell;

const SOC_VALUE = 'SOC';
const SOC_KEY = 'soc';
const PT_VALUE = 'PT';
const PT_KEY = 'pt';
const DRUG_VALUE = 'DRUG';
const DRUG_KEY = 'drug';

@Injectable()
export class SAEsSummaryLdostDataService extends AEsSummaryDataService {

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
            flatMap(this.groupDataBySocAndPtAndDrug(data),
                t => map(t, d => flatMap(d, (arr: any) => arr).reduce((prev, cur) => {
                    return {
                        [SOC_KEY]: cur.soc,
                        [PT_KEY]: cur.pt,
                        [DRUG_KEY]: cur.drug,
                        cells: prev.cells.concat(cur.cells)
                    };
                }))), v => {

                //join all cohorts. Key = cohort name, value = [subjects count, percentage]
                const values = reduce(v.cells.filter(cell => cell), (a, b) =>
                    Object.assign(a, {
                        [this.getKeyForColumn(b.cohortCount)]:
                            this.computeValues(a[this.getKeyForColumn(b.cohortCount)], b)
                    }), {});
                //transform [x, y] -> x (y)
                Object.keys(values).forEach(key => values[key] = this.transformToString(values[key]));
                //join soc, drug and pt to result
                return Object.assign(values, {
                    [SOC_KEY]: v.soc,
                    [PT_KEY]: v.pt,
                    [DRUG_KEY]: v.drug
                });
            }).sort(this.sortBySocAndPt);
    }

    computeValues(data: number[], cell: AeSummariesCell): number[] {
        return data ? [data[0] + cell.value, data[1] + cell.percentage] : [cell.value, cell.percentage];
    }

    transformToString(value: number[]): string {
        return `${value[0]} (${value[1].toFixed(1)})`;

    }

    /**
     * get table columns based on data from backend
     * @param {InMemory.AeSummariesTable[]} data
     * @returns {
     *  (
     *      {
     *          headerName: string;
     *          field: string;
     *          cellStyle: (params) => {'font-weight': string}
     *      } |
     *      {
     *          headerName: string;
     *          children: ({} & any)[]
     *      }
     *  )[]
     * }
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
                headerName: `${PT_VALUE}`, field: PT_KEY, suppressMenu: true
            },
            {
                headerName: `${DRUG_VALUE}`, field: DRUG_KEY, suppressMenu: true
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
            }
        );

        return columnDefs;
    }

    getApi(): string {
        return LDOST_CATEGORY.api;
    }

    getNotes(): string[] {
        return LDOST_AE_NOTES;
    }

    getTableName(): string {
        return LDOST_SAE_NAME;
    }
}
