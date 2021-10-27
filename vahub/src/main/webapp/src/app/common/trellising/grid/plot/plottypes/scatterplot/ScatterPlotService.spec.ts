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
import {ScatterPlotService} from './ScatterPlotService';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

describe('GIVEN ScatterPlotService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                ScatterPlotService
            ]
        });
    });

    describe('WHEN transforming server data', () => {

        let data: OutputScatterPlotEntry[];
        beforeEach(() => {
            data = [{
                color: 'red',
                name: '123',
                x: 0,
                y: 1,
                measurementValue: null
            },
                {
                    color: 'red',
                    name: '124',
                    x: 1,
                    y: 1,
                    measurementValue: null
                },
                {
                    color: 'green',
                    name: '128',
                    x: 2,
                    y: 3,
                    measurementValue: null
                }];
        });


        it('THEN flattens data to separate arrays',
            inject([ScatterPlotService], (s: ScatterPlotService) => {
                const result = s.reformatServerData(data);
                expect(result.length).toBe(2);
                expect(result[0].color).toBe('red');
                expect(result[0].data).toEqual([[0, 1], [1, 1]]);
                expect(result[1].data).toEqual([[2, 3]]);
            })
        );
    });

});
