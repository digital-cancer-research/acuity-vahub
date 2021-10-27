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

import {fromJS, List} from 'immutable';

import {PaginatedTrellisService} from './PaginatedTrellisService';
import {ITrellises, TrellisCategory, PlotType, IPlot, YAxisParameters} from '../ITrellising';

describe('GIVEN PaginatedTrellisService', () => {
    let trellisingOptions: List<ITrellises>;
    let service: PaginatedTrellisService;

    beforeEach(() => {
        trellisingOptions = fromJS([
            {
                category: TrellisCategory.MANDATORY_TRELLIS,
                trellisedBy: YAxisParameters.MEASUREMENT,
                trellisOptions: ['PR', 'HR']
            },
            {
                category: TrellisCategory.NON_MANDATORY_SERIES,
                trellisedBy: 'ARM',
                trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
            }
        ]);

        service = new PaginatedTrellisService();
    });

    describe('WHEN the pagination limit is less than possible combinations', () => {
        let expectedTrellis: List<ITrellises>;

        beforeEach(() => {
            expectedTrellis = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                }
            ]);
        });

        it('THEN only return possible combinations to fill limit', () => {
            expect(PaginatedTrellisService.paginatedTrellis(2, 1, trellisingOptions)).toEqual(expectedTrellis);
        });

    });

    describe('WHEN the pagination limit is less than possible combinations and has an offset', () => {
        let expectedTrellis: List<ITrellises>;

        beforeEach(() => {
            expectedTrellis = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                }
            ]);
        });

        it('THEN only return possible combinations to fill limit and offset', () => {
            expect(PaginatedTrellisService.paginatedTrellis(2, 2, trellisingOptions)).toEqual(expectedTrellis);
        });
    });

    describe('AND a paginated trellis is generated', () => {
        let trellisingOpts: List<ITrellises>;

        beforeEach(() => {
            trellisingOpts = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR', 'QTCF']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1', 'Drug 2']
                }
            ]);
        });

        describe('WHEN the pagination limit exceeds possible combinations', () => {
            it('THEN does nothing ', () => {
                expect(PaginatedTrellisService.paginatedTrellis(100, 1, trellisingOpts)).toEqual(trellisingOpts);
            });
        });

        describe('WHEN the pagination limit is less than possible combinations', () => {
            let expectedTrellis: List<ITrellises>;

            beforeEach(() => {
                expectedTrellis = fromJS([
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOptions: ['PR']
                    },
                    {
                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                        trellisedBy: 'ARM',
                        trellisOptions: ['Placebo', 'Drug 1']
                    }
                ]);
            });

            it('THEN only return possible combinations to fill limit', () => {
                expect(PaginatedTrellisService.paginatedTrellis(2, 1, trellisingOpts)).toEqual(expectedTrellis);
            });

        });

        describe('WHEN the pagination limit is less than possible combinations and has an offset', () => {
            let expectedTrellis: List<ITrellises>;

            beforeEach(() => {
                expectedTrellis = fromJS([
                    {
                        category: TrellisCategory.MANDATORY_TRELLIS,
                        trellisedBy: YAxisParameters.MEASUREMENT,
                        trellisOptions: ['PR', 'HR']
                    },
                    {
                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                        trellisedBy: 'ARM',
                        trellisOptions: ['Drug 2', 'Placebo']
                    }
                ]);
            });

            it('THEN only return possible combinations to fill limit and offset', () => {
                expect(PaginatedTrellisService.paginatedTrellis(2, 3, trellisingOpts)).toEqual(expectedTrellis);
            });
        });
    });
});
