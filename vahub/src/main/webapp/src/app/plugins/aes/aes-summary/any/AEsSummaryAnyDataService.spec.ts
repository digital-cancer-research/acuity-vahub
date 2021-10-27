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

import {AEsSummaryAnyDataService} from './AEsSummaryAnyDataService';

describe('GIVEN AEsSummaryAnyDataService', () => {
    let aesDataService: AEsSummaryAnyDataService;

    beforeEach(() => {
        aesDataService = new AEsSummaryAnyDataService();
    });
    describe('Data validation', () => {
        it('should be invalid when undefined', () => {
            expect(aesDataService.isValidData(undefined)).toBeFalsy();
        });
        it('should be invalid when is empty array', () => {
            expect(aesDataService.isValidData([])).toBeFalsy();
        });
        it('should be valid for non-empty array', () => {
            expect(aesDataService.isValidData([{
                cohortCounts: [],
                rows: [],
                datasetName: '',
                countDosedSubject: 1
            }])).toBeTruthy();
        });
    });


    describe('Value calculation for the non-category cell', () => {
        it('should correctly calculate name for non-empty object',
            () => {
                expect(aesDataService.computeValue({
                    cohortCount: {cohort: 'abc', studyPart: 'a', grouping: 'cd', count: 1, groupingType: 'NONE'},
                    value: 2,
                    percentage: 1
                }))
                    .toBe('2 (1.0)');
            });

        it('should place 0 instead of null',
            () => {
                expect(aesDataService.computeValue({
                    cohortCount: {cohort: 'abc', studyPart: 'a', grouping: 'cd', count: 1, groupingType: 'NONE'},
                    value: null,
                    percentage: 1
                }))
                    .toBe('0 (1.0)');
            });
        it('should place - instead of empty string', () => {
            expect(aesDataService.computeValue({
                cohortCount: {cohort: 'abc', grouping: 'cd', studyPart: 'a', count: 1, groupingType: 'NONE'},
                value: NaN,
                percentage: 1
            }))
                .toBe('- (1.0)');
        });
    });

    describe('WHEN keys generate for columns', () => {
        describe('AND key consists dots', () => {
            it('SHOULD replace dots with dashes', () => {
                const key = 'A-()B.1.ACQ-Cohort and Module';
                const expectedKey = 'A-()B-1-ACQ-Cohort and Module';

                expect((<any>aesDataService).replaceDotsWithDashes(key)).toBe(expectedKey);
            });
        });

        describe('AND key does not consist dots', () => {
            it('SHOULD return the same key', () => {
                const key = 'A-()B1ACQ-Cohort and Module';

                expect((<any>aesDataService).replaceDotsWithDashes(key)).toBe(key);
            });
        });
    });
});
