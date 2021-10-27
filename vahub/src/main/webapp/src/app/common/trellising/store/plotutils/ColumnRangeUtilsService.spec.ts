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

import {TestBed} from '@angular/core/testing';
import {ColumnRangeUtilsService} from './ColumnRangeUtilsService';
import {fromJS} from 'immutable';
import {LegendSymbol, TabId} from '../ITrellising';

describe('GIVEN ColumnRangeUtilsService', () => {
    let service;
    const plots = fromJS([
        {
            data: {
                categories: ['a', 'b', 'c'],
                data: [
                    {
                        high: 1,
                        low: -1
                    },
                    {
                        high: 2,
                        low: 1
                    },
                    {
                        high: 3,
                        low: 5
                    }
                ],
                diagnosisDates: [
                    {
                        y: -5
                    }
                ],
                progressionDates: []
            }
        }
    ]);
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [ColumnRangeUtilsService],
        });

        service = TestBed.get(ColumnRangeUtilsService);
    });

    describe('WHEN calculating zoom range', () => {
        it('THEN SHOULD calculate zoom ranges', () => {
            expect(
                service.calculateZoomRanges(plots)
            ).toEqual({
                x: {
                    min: -5,
                    max: 3
                },
                y: {
                    min: 0,
                    max: 2
                }
            });
        });
    });
    describe('WHEN extracting legend', () => {
        it('THEN SHOULD extract legend', () => {
            expect(
                service.extractLegend(plots, TabId.TUMOUR_RESPONSE_PRIOR_THERAPY, [])
            ).toEqual([
                {
                    title: 'All',
                    entries: [
                        {
                            label: undefined,
                            color: undefined,
                            symbol: 'CIRCLE'
                        }
                    ]
                },
                {
                    title: 'Therapy description',
                    entries: [
                        {
                            label: 'All',
                            color: undefined,
                            symbol: 'CIRCLE'
                        }
                    ]
                },
                {
                    title: null,
                    entries: [{
                        label: 'Diagnosis date',
                        color: 'black',
                        symbol: LegendSymbol.DIAMOND
                    }, {
                        label: 'Most recent progression date',
                        color: 'red',
                        symbol: LegendSymbol.TRIANGLE_RIGHT
                    }]
                }]);
        });
    });
});
