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

import {InitialTrellisService} from './InitialTrellisService';
import {TabId, TrellisCategory} from '../ITrellising';
import {fromJS} from 'immutable';

describe('GIVEN InitialTrellisService', () => {
    let baseTrellis: any;
    let previousTrellis: any[];
    let baseSeries: any[];
    let previousSeries: any[];
    let currentTrellis: any;

    describe('WHEN determining if all has been seleted', () => {

        it('THEN return true if no current trellis and base trellis', () => {
            baseTrellis = fromJS([
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                },
                {
                    trellisedBy: 'AGE',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ]);
            currentTrellis = fromJS([]);
            expect(InitialTrellisService.isAll(currentTrellis, baseTrellis, TrellisCategory.NON_MANDATORY_TRELLIS)).toBeTruthy();
        });

        it('THEN return false if no current trellis and no base trellis', () => {
            baseTrellis = fromJS([]);
            currentTrellis = fromJS([]);
            expect(InitialTrellisService.isAll(currentTrellis, baseTrellis, TrellisCategory.NON_MANDATORY_TRELLIS)).toBeFalsy();
        });

        it('THEN return false if current trellis', () => {
            baseTrellis = fromJS([
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                },
                {
                    trellisedBy: 'AGE',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ]);
            currentTrellis = fromJS([
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ]);
            expect(InitialTrellisService.isAll(currentTrellis, baseTrellis, TrellisCategory.NON_MANDATORY_TRELLIS)).toBeFalsy();
        });
    });

    describe('WHEN POPULATION_BARCHART', () => {
        beforeEach(() => {
            baseSeries = [
                {
                    trellisedBy: 'COUNTRY',
                    trellisOptions: ['FR', 'GB', 'DE'],
                    category: TrellisCategory.NON_MANDATORY_SERIES
                },
                {
                    trellisedBy: 'SEX',
                    trellisOptions: ['M', 'F'],
                    category: TrellisCategory.NON_MANDATORY_SERIES
                }
            ];
            previousSeries = [
                {
                    trellisedBy: 'SEX',
                    trellisOptions: ['M', 'F'],
                    category: TrellisCategory.NON_MANDATORY_SERIES
                }
            ];
            baseTrellis = [
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                },
                {
                    trellisedBy: 'AGE',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ];
            previousTrellis = [
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ];
        });

        it('THEN conserves ALL option when refreshing', () => {
            expect(InitialTrellisService.generateInitialTrellis(fromJS([]), fromJS(baseSeries.concat(baseTrellis)), TabId.POPULATION_BARCHART, false, false, false, false)).toEqual(fromJS([]));
        });

        it('THEN returns COUNTRY for series and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatorySeries(undefined, baseSeries, TabId.POPULATION_BARCHART, false, false))
                .toEqual([
                    {
                        trellisedBy: 'COUNTRY',
                        trellisOptions: ['FR', 'GB', 'DE'],
                        category: TrellisCategory.NON_MANDATORY_SERIES
                    }
                ]);
        });

        it('THEN returns previous for series and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatorySeries(previousSeries, baseSeries, TabId.POPULATION_BARCHART, false, false))
                .toEqual(previousSeries);
        });

        it('THEN returns COUNTRY for series and ongoing', () => {
            expect(InitialTrellisService.initialNonMandatorySeries(undefined, baseSeries, TabId.POPULATION_BARCHART, true, false))
                .toEqual([
                    {
                        trellisedBy: 'COUNTRY',
                        trellisOptions: ['FR', 'GB', 'DE'],
                        category: TrellisCategory.NON_MANDATORY_SERIES
                    }
                ]);
        });

        it('THEN returns previous for series and  ongoing', () => {
            expect(InitialTrellisService.initialNonMandatorySeries(previousSeries, baseSeries, TabId.POPULATION_BARCHART, false, false))
                .toEqual(previousSeries);
        });

        it('THEN returns nothing for trellis and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(undefined, baseTrellis, TabId.POPULATION_BARCHART, false, false))
                .toEqual([]);
        });

        it('THEN returns previous for trellis and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(previousTrellis, baseTrellis, TabId.POPULATION_BARCHART, false, false))
                .toEqual(previousTrellis);
        });

        it('THEN returns nothing for trellis and ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(undefined, baseTrellis, TabId.POPULATION_BARCHART, true, false))
                .toEqual([]);
        });

        it('THEN returns previous for trellis and ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(previousTrellis, baseTrellis, TabId.POPULATION_BARCHART, true, false))
                .toEqual(previousTrellis);
        });

        describe('AND COUNTRY is not populated', () => {

            it('THEN STUDY_ID is used instead', () => {
                baseSeries = [
                    {
                        trellisedBy: 'SEX',
                        trellisOptions: ['M', 'F'],
                        category: TrellisCategory.NON_MANDATORY_SERIES
                    },
                    {
                        trellisedBy: 'STUDY_CODE',
                        trellisOptions: ['DummyData'],
                        category: TrellisCategory.NON_MANDATORY_SERIES
                    }
                ];

                expect(InitialTrellisService.initialNonMandatorySeries(undefined, baseSeries, TabId.POPULATION_BARCHART, false, false))
                    .toEqual([
                        {
                            trellisedBy: 'STUDY_CODE',
                            trellisOptions: ['DummyData'],
                            category: TrellisCategory.NON_MANDATORY_SERIES
                        }
                    ]);
            });
        });
    });

    describe('WHEN aes over time', () => {
        let previousSeries1: any[];
        let previousSeries2: any[];
        beforeEach(() => {
            previousSeries1 = [{
                trellisedBy: 'MAX_SEVERITY_GRADE',
                trellisOptions: ['1', '2'],
                category: TrellisCategory.NON_MANDATORY_SERIES
            }];
            previousSeries2 = [{
                trellisedBy: 'SEVERITY_GRADE',
                trellisOptions: ['1', '2'],
                category: TrellisCategory.NON_MANDATORY_SERIES
            }];
        });

        describe('AND changing y axis option', () => {
            it('THEN conserves state when going not changing', () => {
                expect(InitialTrellisService.initialNonMandatorySeries(previousSeries1, previousSeries1, TabId.AES_OVER_TIME, true, false))
                    .toEqual(previousSeries1);
            });

            it('THEN knows that that the new trellis options contain the old ones', () => {
                expect(InitialTrellisService.inOptions(previousSeries1, 'MAX_SEVERITY_GRADE')).toBeTruthy();
            });

            it('THEN knows that that the new trellis options dont contain the old ones', () => {
                expect(InitialTrellisService.inOptions(previousSeries1, 'SEVERITY_GRADE')).toBeFalsy();
            });

            it('THEN knows no series is contained in previous series', () => {
                expect(InitialTrellisService.inOptions(previousSeries1)).toBeTruthy();
            });

            it('THEN picks the next default when changing', () => {
                expect(InitialTrellisService.initialNonMandatorySeries(previousSeries1, previousSeries2, TabId.AES_OVER_TIME, true, false))
                    .toEqual(previousSeries2);
            });
        });
    });

    describe('WHEN AES_COUNTS_BARCHART', () => {
        beforeEach(() => {
            baseSeries = [
                {
                    trellisedBy: 'COUNTRY',
                    trellisOptions: ['FR', 'GB', 'DE'],
                    category: TrellisCategory.NON_MANDATORY_SERIES
                },
                {
                    trellisedBy: 'MAX_SEVERITY_GRADE',
                    trellisOptions: ['1', '2'],
                    category: TrellisCategory.NON_MANDATORY_SERIES
                }
            ];
            baseTrellis = [
                {
                    trellisedBy: 'ARM',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                },
                {
                    trellisedBy: 'AGE',
                    trellisOptions: ['1', '2', '3'],
                    category: TrellisCategory.NON_MANDATORY_TRELLIS
                }
            ];
        });

        it('THEN returns MAX_SEVERITY_GRADE for series and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatorySeries(undefined, baseSeries, TabId.AES_COUNTS_BARCHART, false, false))
                .toEqual([
                    {
                        trellisedBy: 'MAX_SEVERITY_GRADE',
                        trellisOptions: ['1', '2'],
                        category: TrellisCategory.NON_MANDATORY_SERIES
                    }
                ]);
        });

        it('THEN returns ARM for trellis and not ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(undefined, baseTrellis, TabId.AES_COUNTS_BARCHART, false, false))
                .toEqual([
                    {
                        trellisedBy: 'ARM',
                        trellisOptions: ['1', '2', '3'],
                        category: TrellisCategory.NON_MANDATORY_TRELLIS
                    }
                ]);
        });

        it('THEN returns nothing for trellis and ongoing', () => {
            expect(InitialTrellisService.initialNonMandatoryTrellis(undefined, baseTrellis, TabId.AES_COUNTS_BARCHART, true, false))
                .toEqual([]);
        });

    });
});
