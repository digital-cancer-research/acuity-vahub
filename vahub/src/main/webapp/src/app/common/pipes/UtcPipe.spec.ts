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

import {UtcPipe} from './UtcPipe';
import {DateUtilsService} from '../utils/DateUtilsService';

describe('Given UtcPipe', () => {
    let pipe: UtcPipe;
    beforeEach(() => {
        pipe = new UtcPipe(new DateUtilsService());
    });

    it('transforms timestamp correctly', () => {
        expect(pipe.transform('2014-04-22T00:00:00')).toEqual('22-Apr-2014 00:00:00');
    });

    it('transforms timestamp without time correctly', () => {
        expect(pipe.transform('2014-04-22T00:00:00', true)).toEqual('22-Apr-2014');
    });

    it('transforms empty timestamp correctly', () => {
        expect(pipe.transform(undefined)).toEqual('--');
    });
});
