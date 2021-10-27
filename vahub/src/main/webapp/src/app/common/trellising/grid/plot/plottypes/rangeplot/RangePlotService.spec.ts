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

import {RangePlotService} from './RangePlotService';
import RangeChartSeries = Request.RangeChartSeries;

describe('GIVEN RangePlotService class', () => {
    describe('WHEN getting bin for categorical > centre', () => {
        it('THEN returns min and max', () => {
            const result = RangePlotService.getCategoricalBin(0.3);
            expect(result).toEqual({min: -0.49, max: 0.49});
        });
    });
    describe('WHEN getting bin for categorical < centre', () => {
        it('THEN returns min and max', () => {
            const result = RangePlotService.getCategoricalBin(-0.3);
            expect(result).toEqual({min: -0.49, max: 0.49});
        });
    });
    describe('WHEN no range and value 0.0', () => {
        it('THEN sets the range to be same as value', () => {
            const data: RangeChartSeries<any, any>[] = [{
                data: [{
                    dataPoints: 1,
                    max: null,
                    min: null,
                    y: 0.0,
                    x: '1',
                    xrank: 0,
                    stdDev: 0.0,
                    stdErr: 0.0,
                    name: 'some name',
                }],
                name: 'all',
                // nonSeriesTrellis: [],
                // trellisedBy: [],
                // color: null
            }];
            const service = new RangePlotService();
            const result = service.splitServerData(data, true);
            expect(result.data[0].ranges).toEqual([[0, 0.0, 0.0]]);
            expect(result.data[0].averages).toEqual([
                {x: 0, y: 0.0, dataPoints: 1, stdErr: 0.0, ranges: [0.0, 0.0], name: 'some name'}
            ]);
        });
    });
    describe('WHEN no range and value null', () => {
        it('THEN do not include that data', () => {
            const data: RangeChartSeries<any, any>[] = [{
                data: [{
                    dataPoints: 1,
                    max: null,
                    min: null,
                    y: null,
                    x: '1',
                    xrank: 0,
                    stdDev: 0.0,
                    stdErr: 0.0,
                    name: null
                }, {
                    dataPoints: 2,
                    max: 2.0,
                    min: 0.5,
                    y: 1.0,
                    x: '3',
                    xrank: 2,
                    stdDev: 0.0,
                    stdErr: 0.0,
                    name: 'some name'
                }],
                name: 'all',
                // nonSeriesTrellis: [],
                // trellisedBy: [],
                // color: null
            }];
            const service = new RangePlotService();
            const result = service.splitServerData(data, true);
            expect(result.data[0].ranges).toEqual([[1, 0.5, 2.0]]);
            expect(result.data[0].averages).toEqual([
                {x: 1, y: 1.0, dataPoints: 2, stdErr: 0.0, ranges: [0.5, 2.0], name: 'some name'}
            ]);
        });
    });
});
