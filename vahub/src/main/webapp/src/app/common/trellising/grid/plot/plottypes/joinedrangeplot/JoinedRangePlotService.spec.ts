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

import {JoinedRangePlotService} from './JoinedRangePlotService';
import RangeChartSeries = Request.RangeChartSeries;

describe('GIVEN JoinedRangePlotService class', () => {
    describe('WHEN renal single subject view plot', () => {
        it('THEN name and colour are added when continous', () => {
            const data: RangeChartSeries<any, any>[] = [
                {
                    data: [{
                        dataPoints: 1,
                        max: 10,
                        min: 10,
                        y: 10,
                        x: '5',
                        xrank: 5,
                        stdDev: null,
                        stdErr: null,
                        name: null
                    }],
                    name: 'CKD Stage 1',
                    // nonSeriesTrellis: [],
                    // trellisedBy: [],
                    // color: null
                },
                {
                    data: [{
                        dataPoints: 1,
                        max: 13,
                        min: 13,
                        y: 13,
                        x: '9',
                        xrank: 9,
                        stdDev: null,
                        stdErr: null,
                        name: null
                    }],
                    name: 'CKD Stage 2',
                    // nonSeriesTrellis: [],
                    // trellisedBy: [],
                    // color: null
                }];
            const service = new JoinedRangePlotService();
            const result = service.splitServerData(data, false);
            expect(result.data[0].name).toEqual('All');
            expect(result.data[0].ranges).toEqual([[5, 10, 10], [9, 13, 13]]);
            expect(result.data[0].averages).toEqual([
                {x: 5, y: 10, dataPoints: 1, stdErr: null, ranges: [10, 10], marker: {lineColor: '#b4da50'}, name: 'CKD Stage 1'},
                {x: 9, y: 13, dataPoints: 1, stdErr: null, ranges: [13, 13], marker: {lineColor: '#f7d533'}, name: 'CKD Stage 2'}
            ]);
        });
        it('THEN name and colour are added when categorical', () => {
            const data: RangeChartSeries<any, any>[] = [
                {
                    data: [{
                        dataPoints: 1,
                        max: 10,
                        min: 10,
                        y: 10,
                        x: '5',
                        xrank: 1,
                        stdDev: null,
                        stdErr: null,
                        name: null
                    }],
                    name: 'CKD Stage 1',
                    // nonSeriesTrellis: [],
                    // trellisedBy: [],
                    // color: null
                },
                {
                    data: [{
                        dataPoints: 1,
                        max: 13,
                        min: 13,
                        y: 13,
                        x: '9',
                        xrank: 2,
                        stdDev: null,
                        stdErr: null,
                        name: null
                    }],
                    name: 'CKD Stage 2',
                    // nonSeriesTrellis: [],
                    // trellisedBy: [],
                    // color: null
                }];
            const service = new JoinedRangePlotService();
            const result = service.splitServerData(data, true);
            expect(result.data[0].name).toEqual('All');
            expect(result.data[0].ranges).toEqual([[0, 10, 10], [1, 13, 13]]);
            expect(result.categories).toEqual(['5', '9']);
            expect(result.data[0].averages).toEqual([
                {x: 0, y: 10, dataPoints: 1, stdErr: null, ranges: [10, 10], marker: {lineColor: '#b4da50'}, name: 'CKD Stage 1'},
                {x: 1, y: 13, dataPoints: 1, stdErr: null, ranges: [13, 13], marker: {lineColor: '#f7d533'}, name: 'CKD Stage 2'}
            ]);
        });
    });
});
