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
import {LinePlotService} from './LinePlotService';
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;

describe('GIVEN LinePlotService', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                LinePlotService
            ]
        });
    });

    it('THEN fills in blank values in a series',
        inject([LinePlotService], (s: LinePlotService) => {
            const series: OutputOvertimeLineChartData[] = [{
                color: 'black',
                name: 'Drug 1',
                series: [
                    {
                        category: '1',
                        value: 1.0,
                        rank: 1,
                        totalSubjects: null
                    },
                    {
                        category: '3',
                        value: 1.0,
                        rank: 3,
                        totalSubjects: null
                    }
                ]
            },
                {
                    color: 'red',
                    name: 'Drug 2',
                    series: [
                        {
                            category: '1',
                            value: 2.0,
                            rank: 1,
                            totalSubjects: null
                        },
                        {
                            category: '2',
                            value: 1.0,
                            rank: 2,
                            totalSubjects: null
                        }
                    ]
                }];

            const result = s.splitServerData(series);
            expect(result.categories).toEqual(['1', '2', '3']);
            expect(result.data[0].data).toEqual([{x: 0, y: 1.0}, {x: 1, y: 0.0}, {x: 2, y: 1.0}]);
            expect(result.data[1].data).toEqual([{x: 0, y: 2.0}, {x: 1, y: 1.0}, {x: 2, y: 0.0}]);
        })
    );
});
