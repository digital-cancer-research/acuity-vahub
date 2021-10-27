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

import {getCategoricalAxisValues, getTicksForColumnRange} from './utils';
import {items, nonUniqueItems} from './mocks';

describe('Vahub charts utils', () => {
    describe('getCategoricalAxisValues function', () => {
        it('with no categories', () => {
            const value = getCategoricalAxisValues([], 500);
            expect(value).toEqual([]);
        });

        it('all categories fits', () => {
            const value = getCategoricalAxisValues(items, 500);
            expect(value).toEqual(items);
        });

        it('not all categories fits', () => {
            const value = getCategoricalAxisValues([...items, '41'], 500);
            const oddItems = [...items, '41'].filter((d, i) => !(i % 2));
            expect(value).toEqual(oddItems);
        });
    });

    describe('getTicksForColumnRange function', () => {
        it('with no categories', () => {
            const value = getTicksForColumnRange([], [0, 0], 500);
            expect(value).toEqual([]);
        });

        it('all categories fits', () => {
            const value = getTicksForColumnRange(items, [0, 40], 500);
            expect(value).toEqual([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                    21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39]);
        });

        it('not all categories fits', () => {
            const value = getTicksForColumnRange([...items, '41'], [0, 40], 500);
            expect(value).toEqual([0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40]);
        });

        it('duplicated categories', () => {
            const value = getTicksForColumnRange(nonUniqueItems, [0, 40], 500);
            expect(value).toEqual([0, 2, 8, 9, 10, 14, 17, 18, 19, 20, 21, 24, 27, 28, 29, 30, 31, 32, 34, 35]);
        });
    });

});
