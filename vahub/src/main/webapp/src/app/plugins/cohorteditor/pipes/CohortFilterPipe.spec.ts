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

import {CohortFilterPipe} from './CohortFilterPipe';

describe('GIVEN CohortFilterPipe', () => {
    let pipe: CohortFilterPipe;
    const savedFilters = [{
        savedFilter: {
            name: 'abc'
        }
    },
        {
            savedFilter: {
                name: 'bcd'
            }
        },
        {
            savedFilter: {
                name: 'cde'
            }
        }];

    beforeEach(() => {
        pipe = new CohortFilterPipe();
    });

    describe('WHEN elements are filtered', () => {

        it('THEN list of elements that that contain filter is returned', () => {
            expect(pipe.transform(savedFilters, ['b'])).toEqual([savedFilters[0], savedFilters[1]]);
        });

        it('THEN full list is returned in case filter is empty', () => {
            expect(pipe.transform(savedFilters, [null])).toEqual(savedFilters);
        });
    });
});
