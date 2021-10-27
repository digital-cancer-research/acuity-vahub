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

import {AlphabeticOrderByPipe} from './AlphabeticOrderByPipe';
import {ILegendEntry, LegendSymbol} from '../trellising/store';

describe('GIVEN IntervalsOrderByPipe', () => {
    let pipe: AlphabeticOrderByPipe;
    beforeEach(() => {
        pipe = new AlphabeticOrderByPipe();
    });
    describe('WHEN elements are sorted', () => {

        it('THEN sorted list of group is returned', () => {
            const listOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'A',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'C',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'B',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            const sortedListOfLegendEntries: ILegendEntry[] = [
                {
                    label: 'A',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'B',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                },
                {
                    label: 'C',
                    color: 'black',
                    symbol: LegendSymbol.CIRCLE
                }
            ];

            expect(pipe.transform(listOfLegendEntries, 'GROUP')).toEqual(sortedListOfLegendEntries);
        });

    });


});
