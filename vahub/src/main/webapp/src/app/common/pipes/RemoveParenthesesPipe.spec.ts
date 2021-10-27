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

import {RemoveParenthesesPipe} from './RemoveParenthesesPipe';

describe('Given RemoveParenthesesPipe', () => {
    let pipe: RemoveParenthesesPipe;
    beforeEach(() => {
        pipe = new RemoveParenthesesPipe();
    });
    it('should not transform undefined or null values', () => {
        expect(pipe.transform(undefined)).toBeUndefined();
        expect(pipe.transform(null)).toBeNull();
    });
    it('should not transform values which does not match the pattern [(1 latin char)anything else]', () => {
        expect(pipe.transform('(Empty)')).toEqual('(Empty)');
        expect(pipe.transform('(Empty)anything else')).toEqual('(Empty)anything else');
        expect(pipe.transform('anything (E) else')).toEqual('anything (E) else');
        expect(pipe.transform('(1)text')).toEqual('(1)text');
    });
    it('should transform values which matches the pattern [(1 latin char)anything else]', () => {
        expect(pipe.transform('()anything else')).toEqual('anything else');
        expect(pipe.transform('(A)anything else')).toEqual('anything else');
        expect(pipe.transform('(B)5anything else')).toEqual('5anything else');
        expect(pipe.transform('(C)6')).toEqual('6');
        expect(pipe.transform('(D)7 anything')).toEqual('7 anything');
        expect(pipe.transform('(E)text')).toEqual('text');
    });
});
