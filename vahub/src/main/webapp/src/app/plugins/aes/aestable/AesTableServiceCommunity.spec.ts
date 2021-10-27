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

import {AesTableServiceCommunity, RowDataType} from './AesTableServiceCommunity';
import * as _ from 'lodash';

describe('GIVEN AesTableServiceCommunity', () => {
    let aesTableServiceCommunity: AesTableServiceCommunity;

    beforeEach(() => { aesTableServiceCommunity = new AesTableServiceCommunity(); });

    describe('WHEN table data rows are prepared', () => {
        let rows;

        beforeEach(() => rows = [
            {
                term: 'ABDOMINAL DISCOMFORT',
                grade: 'CTC Grade 1',
                treatmentArm: 'All',
                subjectCountPerGrade: 1,
                subjectCountPerTerm: 1,
                subjectCountPerArm: 124,
                noIncidenceCount: 123
            },
            {
                term: 'BACK PAIN',
                grade: 'CTC Grade 2',
                treatmentArm: 'All',
                subjectCountPerGrade: 1,
                subjectCountPerTerm: 2,
                subjectCountPerArm: 124,
                noIncidenceCount: 122
            },
            {
                term: 'BACK PAIN',
                grade: 'CTC Grade 1',
                treatmentArm: 'All',
                subjectCountPerGrade: 1,
                subjectCountPerTerm: 2,
                subjectCountPerArm: 124,
                noIncidenceCount: 122
            }
        ]);

        it('THEN no incidence rows are added for each term', () => {
            const tableData = aesTableServiceCommunity.prepareTableData(rows);
            expect(tableData).toEqual(jasmine.arrayContaining([
                jasmine.objectContaining({
                    term: 'ABDOMINAL DISCOMFORT',
                    grade: 'No incidence',
                    subjectCountPerArm: 124,
                    noIncidenceCount: 123,
                    rowDataType: RowDataType.NoIncidence
                }),
                jasmine.objectContaining({
                    term: 'BACK PAIN',
                    grade: 'No incidence',
                    subjectCountPerArm: 124,
                    noIncidenceCount: 122,
                    rowDataType: RowDataType.NoIncidence
                })
            ]));
        });

        it('THEN total for term rows are added for each term', () => {
            const tableData = aesTableServiceCommunity.prepareTableData(rows);
            expect(tableData).toEqual(jasmine.arrayContaining([
                jasmine.objectContaining({
                    term: 'ABDOMINAL DISCOMFORT',
                    grade: 'All grades',
                    subjectCountPerArm: 124,
                    subjectCountPerTerm: 1,
                    rowDataType: RowDataType.TotalForTerm
                }),
                jasmine.objectContaining({
                    term: 'BACK PAIN',
                    grade: 'All grades',
                    subjectCountPerArm: 124,
                    subjectCountPerTerm: 2,
                    rowDataType: RowDataType.TotalForTerm
                })
            ]));
        });

        it('THEN 2 additional rows (no incidence and total per term) are added for each term', () => {
            const tableData = aesTableServiceCommunity.prepareTableData(rows);
            const terms = _.uniq(rows.map(row => row.term));
            expect(tableData.length).toEqual(rows.length + terms.length * 2);
        });
    });

    describe('WHEN formatting subject number', () => {
        let params;

        beforeEach(() => params = {
            data: {
                subjectCountPerArm: 124
            }
        });

        it('THEN numeric values are formatted correctly', () => {
            params.value = 2;

            expect(aesTableServiceCommunity.formatNumberOfSubjects(params)).toEqual('2 (1.61%)');
        });

        it('THEN undefined value is formatted as empty string', () => {
            params.value = undefined;

            expect(aesTableServiceCommunity.formatNumberOfSubjects(params)).toEqual('');
        });

        it('THEN null value is treated like zero number of subjects', () => {
            params.value = null;

            expect(aesTableServiceCommunity.formatNumberOfSubjects(params)).toEqual('0 (0%)');
        });
    });

    describe('WHEN getting subject number for different row types', () => {
        let params;

        beforeEach(() => params = {
                data: {
                    subjectCountPerTerm: 16,
                    noIncidenceCount: 108,
                    subjectCountPerGrade: 12
                }
            }
        );

        it('THEN correct number returned for rows with no incidence subjects number', () => {
            params.data.rowDataType = RowDataType.NoIncidence;

            expect(aesTableServiceCommunity.getNumberOfSubjects(params)).toEqual(108);
        });

        it('THEN correct number returned for rows with subject number for severity grade', () => {
            expect(aesTableServiceCommunity.getNumberOfSubjects(params)).toEqual(12);
        });

        it('THEN correct number returned for rows with total subject number per term', () => {
            params.data.rowDataType = RowDataType.TotalForTerm;

            expect(aesTableServiceCommunity.getNumberOfSubjects(params)).toEqual(16);
        });
    });
});
