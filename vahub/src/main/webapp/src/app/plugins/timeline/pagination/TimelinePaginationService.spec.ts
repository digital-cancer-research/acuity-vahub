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

import {TimelinePaginationService} from './TimelinePaginationService';

describe('GIVEN TimelinePaginationService', () => {
    describe('WHEN getting updated page set', () => {
        it('THEN returns single page for less than limit', () => {
            expect(TimelinePaginationService.getPages(1, 20)).toEqual([1]);
        });
        it('THEN returns single page for equal to limit', () => {
            expect(TimelinePaginationService.getPages(20, 20)).toEqual([1]);
        });
        it('THEN returns multiple pages for over the limit', () => {
            expect(TimelinePaginationService.getPages(21, 20)).toEqual([1, 2]);
        });
    });
});
