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

import {BoxPlotService} from './BoxPlotService';
import OutputBoxplotEntry = Request.OutputBoxplotEntry;

describe('GIVEN BoxPlotService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({providers: [BoxPlotService]});
    });

    const data: OutputBoxplotEntry[] = [
        {
            x: '1.0',
            xrank: 1,
            outliers: [],
            eventCount: null,
            upperQuartile: 1,
            lowerQuartile: 1,
            upperWhisker: 2,
            lowerWhisker: 1,
            median: 1,
            subjectCount: null
        }, {

            x: '2.0',
            xrank: 2,
            outliers: [
                {x: '2.0', /* xrank: 2,*/ outlierValue: -24.66, subjectId: 'DummyData-2009589165'},
                {x: '2.0', /* xrank: 2,*/ outlierValue: 18.0, subjectId: 'DummyData-9525132641'}
            ],
            eventCount: 1,
            upperQuartile: 1,
            lowerQuartile: 1,
            upperWhisker: 1,
            lowerWhisker: 1,
            median: 1,
            subjectCount: 1
        }, {
            x: '4.0',
            xrank: 4,
            outliers: [
                {x: '4.0', /*xrank: 4,*/ outlierValue: -66.59, subjectId: 'DummyData-2209555426'}
            ],
            eventCount: 1,
            upperQuartile: 1,
            lowerQuartile: 1,
            upperWhisker: 1,
            lowerWhisker: 1,
            median: 1,
            subjectCount: null
        }, {
            x: '5.0',
            xrank: 5,
            outliers: [
                {x: '5.0', /*xrank: 5,*/ outlierValue: -58.15, subjectId: 'DummyData-2209555426'}
            ],
            eventCount: null,
            upperQuartile: 2,
            lowerQuartile: 2,
            upperWhisker: 2,
            lowerWhisker: 1,
            median: 2,
            subjectCount: null
        }, {
            x: '6.0',
            xrank: 6,
            outliers: [],
            eventCount: 1,
            upperQuartile: 2,
            lowerQuartile: 3,
            upperWhisker: 4,
            lowerWhisker: 4,
            median: 3,
            subjectCount: 4
        }, {
            x: '8.0',
            xrank: 8,
            outliers: [
                {x: '8.0', /*xrank: 8,*/ outlierValue: -32.87, subjectId: 'DummyData-2209555426'}
            ],
            eventCount: 3,
            upperQuartile: 2,
            lowerQuartile: 3,
            upperWhisker: 2,
            lowerWhisker: 2,
            median: 2,
            subjectCount: null
        }, {
            x: '10.0',
            xrank: 10,
            outliers: [
                {x: '10.0', /*xrank: 10,*/ outlierValue: 41.15, subjectId: 'DummyData-2009589165'}
            ],
            eventCount: null,
            upperQuartile: 2,
            lowerQuartile: 2,
            upperWhisker: 1,
            lowerWhisker: 1,
            median: 1,
            subjectCount: null
        }, {
            x: '11.0',
            xrank: 11,
            outliers: [
                {x: '11.0', /*xrank: 11,*/ outlierValue: -27.59, subjectId: 'DummyData-2009589165'}
            ],
            eventCount: null,
            upperQuartile: 1,
            lowerQuartile: 1,
            upperWhisker: -2,
            lowerWhisker: -3,
            median: 0,
            subjectCount: null
        }];

    const data2: OutputBoxplotEntry[] = [
        {
            x: 'Spring',
            xrank: 1,
            outliers: [],
            eventCount: null,
            lowerQuartile: -4,
            lowerWhisker: -4,
            median: 4,
            subjectCount: 3,
            upperQuartile: 3,
            upperWhisker: 3,
        }, {
            x: 'Summer',
            xrank: 2,
            outliers: [
                {x: 'Summer', /*xrank: 2, */ outlierValue: -24.66, subjectId: 'DummyData-2009589165'},
                {x: 'Summer', /* xrank: 2, */outlierValue: 18.0, subjectId: 'DummyData-9525132641'}
            ],
            eventCount: null,
            lowerQuartile: 3,
            lowerWhisker: 3,
            median: 3,
            subjectCount: 3,
            upperQuartile: 3,
            upperWhisker: 3,
        }, {
            x: 'Autumn',
            xrank: 3,
            outliers: [
                {x: 'Autumn', /* xrank: 3,*/ outlierValue: -66.59, subjectId: 'DummyData-2209555426'}
            ],
            eventCount: null,
            lowerQuartile: -2,
            lowerWhisker: -2,
            median: -2,
            subjectCount: null,
            upperQuartile: -2,
            upperWhisker: -1,
        }, {
            x: 'Winter',
            xrank: 4,
            outliers: [
                {x: 'Winter', /* xrank: 4, */ outlierValue: -58.15, subjectId: 'DummyData-2209555426'}
            ],
            eventCount: null,
            upperQuartile: 0,
            lowerQuartile: 0,
            upperWhisker: 1,
            lowerWhisker: 1,
            median: 1,
            subjectCount: null
        }];

    describe('WHEN the x axis is categorical', () => {
        it('THEN the outliers are calculated correctly', inject([BoxPlotService], (service: BoxPlotService) => {
            const boxPlotData = service.splitServerData(<OutputBoxplotEntry[]>data, true);
            console.log(boxPlotData.outliers);
            expect(boxPlotData.outliers.length).toBe(7);
            expect(boxPlotData.outliers[0]).toEqual({x: 1, y: -24.66, subjectId: 'DummyData-2009589165'});
            expect(boxPlotData.outliers[2]).toEqual({x: 2, y: -66.59, subjectId: 'DummyData-2209555426'});
        }));
        it('THEN preserves the categorical order for numeric values', inject([BoxPlotService], (service: BoxPlotService) => {
            const boxPlotData = service.splitServerData(<OutputBoxplotEntry[]>data, true);
            expect(boxPlotData.categories).toEqual(['1.0', '2.0', '4.0', '5.0', '6.0', '8.0', '10.0', '11.0']);
        }));
        it('THEN preserves the categorical order', inject([BoxPlotService], (service: BoxPlotService) => {
            const boxPlotData = service.splitServerData(<OutputBoxplotEntry[]>data2, true);
            expect(boxPlotData.categories).toEqual(['Spring', 'Summer', 'Autumn', 'Winter']);
            expect(boxPlotData.outliers[0]).toEqual({x: 1, y: -24.66, subjectId: 'DummyData-2009589165'});
            expect(boxPlotData.outliers[2]).toEqual({x: 2, y: -66.59, subjectId: 'DummyData-2209555426'});
        }));
    });

    describe('WHEN the x axis is not categorical', () => {
        it('THEN the outliers are calculated correctly', inject([BoxPlotService], (service: BoxPlotService) => {
            const boxPlotData = service.splitServerData(<OutputBoxplotEntry[]>data, false);
            expect(boxPlotData.outliers.length).toBe(7);
            expect(boxPlotData.outliers[0]).toEqual({x: 2.0, y: -24.66, subjectId: 'DummyData-2009589165'});
            expect(boxPlotData.outliers[4]).toEqual({x: 8.0, y: -32.87, subjectId: 'DummyData-2209555426'});
        }));
    });

});
