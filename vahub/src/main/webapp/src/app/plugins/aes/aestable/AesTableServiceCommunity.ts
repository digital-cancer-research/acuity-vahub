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
import * as _ from 'lodash';
import {GridOptions, ColDef} from 'ag-grid/main';
import AesTable = InMemory.AesTable;

export interface IAesTableService {
    getColumnDefs(): ColDef[];
    getGridOptions(): GridOptions;
    prepareTableData(rows: InMemory.AesTable[]): Array<any>;
}

export enum RowDataType {
    TotalForTerm,
    NoIncidence
}

@Injectable()
export class AesTableServiceCommunity implements IAesTableService {

    constructor() {
    }

    getColumnDefs(): ColDef[] {
        return [
            {
                headerName: 'Term',
                field: 'term'
            },
            {
                headerName: 'Max. severity grade experienced',
                field: 'grade'
            },
            {
                headerName: 'Number of subjects (All treatment arms)',
                valueGetter: this.getNumberOfSubjects,
                valueFormatter: this.formatNumberOfSubjects
            }
        ];
    }

    getGridOptions(): GridOptions {
        return {
            api: null,
            defaultColDef: {
                menuTabs: []
            },
            enableSorting: true
        };
    }

    getNumberOfSubjects(params): number {
        const data = params.data;
        let subjects: number;

        switch (data.rowDataType) {
            case RowDataType.TotalForTerm:
                subjects = data.subjectCountPerTerm;
                break;
            case RowDataType.NoIncidence:
                subjects = data.noIncidenceCount;
                break;
            default:
                subjects = data.subjectCountPerGrade;
                break;
        }

        return subjects;
    }

    formatNumberOfSubjects(params): string {
        const data = params.data;
        const subjects = params.value;

        if (_.isNull(subjects)) {
            return '0 (0%)';
        } else if (_.isUndefined(subjects)) {
            return '';
        }

        return `${subjects} (${_.round(subjects / data.subjectCountPerArm * 100, 2)}%)`;
    }

    prepareTableData(rows: AesTable[]): Array<any> {
        const rowsByTerm = _.groupBy(rows, row => row.term);
        const tableData = [];
        for (const term of Object.keys(rowsByTerm)) {
            const row: AesTable = rowsByTerm[term][0];

            tableData.push(
                // Add "All grades" row
                {
                    term,
                    grade: 'All grades',
                    subjectCountPerTerm: row.subjectCountPerTerm,
                    subjectCountPerArm: row.subjectCountPerArm,
                    rowDataType: RowDataType.TotalForTerm
                },
                // Add "Severity grade" rows
                ...rowsByTerm[term],
                // Add "No incidence" row
                {
                    term,
                    grade: 'No incidence',
                    noIncidenceCount: row.noIncidenceCount,
                    subjectCountPerArm: row.subjectCountPerArm,
                    rowDataType: RowDataType.NoIncidence
                }
            );
        }

        return tableData;
    }
}
