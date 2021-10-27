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

import {inject, TestBed} from '@angular/core/testing';

import {BarLineService} from './BarLineService';
import OutputOvertimeData = InMemory.OutputOvertimeData;
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

describe('GIVEN BarLineService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({providers: [BarLineService]});
    });

    describe('WHEN splitting out server data for plotting', () => {
        let data: OutputOvertimeData;
        beforeEach(() => {
            data = {
                categories: ['1', '2', '3'],
                lines: [{
                    color: 'red',
                    name: 'ALL',
                    series: [{
                        category: '1',
                        rank: 1,
                        value: 1,
                        totalSubjects: null
                    }, {
                        category: '2',
                        rank: 2,
                        value: 1.4,
                        totalSubjects: null
                    }, {
                        category: '3',
                        rank: 3,
                        value: 1.6,
                        totalSubjects: null
                    }]
                }],
                series: [({
                    categories: ['1', '2', '3'],
                    color: 'blue',
                    name: 'Grade 1',
                    series: [{
                        category: '1',
                        rank: 1,
                        value: 1,
                        totalSubjects: 1
                    }, {
                        category: '2',
                        rank: 2,
                        value: 1.4,
                        totalSubjects: 1.4
                    }, {
                        category: '3',
                        rank: 3,
                        value: 1.6,
                        totalSubjects: 1.6
                    }]
                } as ColoredOutputBarChartData)]
            };
        });

        it('THEN splits out the data for plotting', inject([BarLineService], (service: BarLineService) => {
            const result = service.splitServerData(data);
            expect(result.categories).toEqual(['1', '2', '3']);
            expect(result.series[0].data).toEqual([[0, 1], [1, 1.4], [2, 1.6]]);
            expect(result.lines[0].data).toEqual([[0, 1], [1, 1.4], [2, 1.6]]);
        }));
    });
});
