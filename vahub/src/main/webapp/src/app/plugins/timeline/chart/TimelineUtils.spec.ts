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

import {formatDayHourString} from './TimelineUtils';

describe('GIVEN TimelineUtils', () => {

    describe('WHEN formatting day hour string', () => {

        it('THEN day hour at mid night will be one second less ', () => {
            expect(formatDayHourString('12d 00:00', 12, false)).toEqual('11d 23:59');
            expect(formatDayHourString('12d 00:00', 12.01, false)).toEqual('12d 00:00');
            expect(formatDayHourString('-12d 00:00', 12, false)).toEqual('-13d 23:59');
            expect(formatDayHourString('-12d 00:00', 12.01, false)).toEqual('-12d 00:00');
            expect(formatDayHourString('-12d 12:34', 12.55, false)).toEqual('-12d 12:34');
            expect(formatDayHourString(null, null, false)).toBe(null);
        });
    });
});
