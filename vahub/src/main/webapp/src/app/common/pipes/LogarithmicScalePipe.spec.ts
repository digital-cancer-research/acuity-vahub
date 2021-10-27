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

import {LogarithmicScalePipe} from './LogarithmicScalePipe';
import {ScaleTypes} from '../trellising/store';

describe('Given LogarithmicScalePipe', () => {
    let pipe: LogarithmicScalePipe;
    beforeEach(() => {
        pipe = new LogarithmicScalePipe();
    });

    it('should not transform with undefined or null scaleType', () => {
        expect(pipe.transform('value', undefined)).toEqual('value');
        expect(pipe.transform('value', null)).toEqual('value');
    });

    it('should not transform with not logarithmic scaleType', () => {
        expect(pipe.transform('value', ScaleTypes.LINEAR_SCALE)).toEqual('Linear scale value');
    });

    it('should transform with logarithmic scaleType', () => {
        expect(pipe.transform('value', ScaleTypes.LOGARITHMIC_SCALE)).toEqual('Log scale value');
    });
});
