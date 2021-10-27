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

import {TestBed, inject} from '@angular/core/testing';
import {AEsSummaryDataService} from './AEsSummaryDataService';
import AeSummariesTable = InMemory.AeSummariesTable;
import AeSummariesRow = InMemory.AeSummariesRow;
import {List} from 'immutable';

describe('GIVEN AEsSummaryDataService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [AEsSummaryDataService]
        });
    });

    describe('WHEN group data', () => {

        const data = [{
            datasetName: 'AESummaries',
            rows: [{
                cells: [],
                drug: 'drug 1',
                pt: 'pt 1',
                soc: 'soc 1'
            } as AeSummariesRow,
                {
                    cells: [],
                    drug: 'drug 2',
                    pt: 'pt 2',
                    soc: 'soc 1'
                } as AeSummariesRow,
                {
                    cells: [],
                    drug: 'drug 3',
                    pt: 'pt 2',
                    soc: 'soc 1'
                } as AeSummariesRow],
        } as AeSummariesTable];

        it('by soc and pt', inject([AEsSummaryDataService], (service: AEsSummaryDataService) => {
            const expectedData = [
                {
                    'pt 1': [{cells: [], drug: 'drug 1', pt: 'pt 1', soc: 'soc 1'}],
                    'pt 2': [{cells: [], drug: 'drug 2', pt: 'pt 2', soc: 'soc 1'}, {cells: [], drug: 'drug 3', pt: 'pt 2', soc: 'soc 1'}]
                }
            ];
            expect(service.groupDataBySocAndPt(data)).toEqual(expectedData);
        }));

        it('by soc and pt and drug', inject([AEsSummaryDataService], (service: AEsSummaryDataService) => {
            const expectedData = [
                {
                    'drug 1': [{cells: [], drug: 'drug 1', pt: 'pt 1', soc: 'soc 1'}],
                },
                {
                    'drug 2': [{cells: [], drug: 'drug 2', pt: 'pt 2', soc: 'soc 1'}],
                    'drug 3': [{cells: [], drug: 'drug 3', pt: 'pt 2', soc: 'soc 1'}]
                }

            ];
            expect(service.groupDataBySocAndPtAndDrug(data)[0]).toEqual(expectedData);
        }));
    });
});
