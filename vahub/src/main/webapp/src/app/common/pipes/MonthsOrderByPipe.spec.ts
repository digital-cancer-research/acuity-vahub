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

import {MonthsOrderByPipe} from './MonthsOrderByPipe';
import {ILegendEntry, LegendSymbol} from '../trellising/store/ITrellising';

describe('GIVEN MonthsOrderByPipe', () => {
    let pipe: MonthsOrderByPipe;
    beforeEach(() => {
        pipe = new MonthsOrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN sorted list of months is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
            ];

            expect(pipe.transform(listOfLegendEntries, 'FIRST_TREATMENT_DATE')).toEqual(sortedListOfLegendEntries);
        });
    });

    describe('WHEN elements are sorted', () => {

        it('THEN list of items is sorted by the given property', () => {
            const listOfLegendEntries: any[] = [
                {
                    title: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: any[] = [
                {
                    title: 'DEC-2013',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'JUN-2014',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    title: 'MAY-2016',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
            ];

            expect(pipe.transform(listOfLegendEntries, 'FIRST_TREATMENT_DATE', 'title')).toEqual(sortedListOfLegendEntries);
        });
    });
});
