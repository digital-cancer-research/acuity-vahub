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

import {TextUtils} from './TextUtils';
import {EMPTY, TimepointConstants} from '../trellising/store';
import {List} from 'immutable';

describe('GIVEN the TextUtils class', () => {

    describe('WHEN transforming to string', () => {

        it('THEN it should stringify a number', () => {
            expect(TextUtils.stringOrEmpty(123)).toEqual('123');
        });

        it('THEN it should replace null', () => {
            expect(TextUtils.stringOrEmpty(null)).toEqual(EMPTY);
        });

        it('THEN it should replace undefined', () => {
            expect(TextUtils.stringOrEmpty(undefined)).toEqual(EMPTY);
        });

        it('THEN it should not change a string', () => {
            expect(TextUtils.stringOrEmpty('abc')).toEqual('abc');
        });

    });

    describe('WHEN checking first word', () => {

        it('THEN it should identify an abbreviation', () => {
            expect(TextUtils.startsWithAbbreviation('AST/ALT stuff')).toBe(true);
        });

        it('THEN it should identify a non-abbreviation', () => {
            expect(TextUtils.startsWithAbbreviation('More AST/ALT stuff')).toBe(false);
        });

        it('THEN it should handle a single word', () => {
            expect(TextUtils.startsWithAbbreviation('SOC')).toBe(true);
        });

        it('THEN it should handle an empty string', () => {
            expect(TextUtils.startsWithAbbreviation('')).toBe(false);
        });

        it('THEN it should handle a blank string', () => {
            expect(TextUtils.startsWithAbbreviation('   ')).toBe(false);
        });

        it('THEN it should handle null', () => {
            expect(TextUtils.startsWithAbbreviation(null)).toBe(false);
        });

        it('THEN it should handle undefined', () => {
            expect(TextUtils.startsWithAbbreviation(undefined)).toBe(false);
        });

    });

    describe('WHEN transforming to sentence case', () => {

        it('THEN it should uppercase the first letter', () => {
            expect(TextUtils.toSentenceCase('hello wordl')).toEqual('Hello wordl');
        });

        it('THEN it should uppercase the first letter and lowercase ordinary words', () => {
            expect(TextUtils.toSentenceCase('THE QUICK BROWN FOX')).toEqual('The quick brown fox');
        });

        it('THEN it should not change known abbreviations', () => {
            expect(TextUtils.toSentenceCase('THE ID OF THE AST/ALT IS HLT')).toEqual('The ID of the AST/ALT is HLT');
        });

        it('THEN it should not change a known abbreviation at the start of a sentence', () => {
            expect(TextUtils.toSentenceCase('AST/ALT is a KNOWN abbreviation')).toEqual('AST/ALT is a known abbreviation');
        });

    });

    describe('WHEN making a sentence', () => {
        it('THEN it should lowercase parts 2-n', () => {
            expect(TextUtils.makeSentence('One Two', 'Three four', 'Five six')).toEqual('One Two three four five six');
        });

        it('THEN it should trim spaces', () => {
            expect(TextUtils.makeSentence('  one two  ', '   Three four  ')).toEqual('one two three four');
        });

        it('THEN it should treat uppercase first words as abbreviations', () => {
            expect(TextUtils.makeSentence('ID JUNK', 'NOT recognised')).toEqual('ID JUNK NOT recognised');
        });

    });

    describe('WHEN adding a description with timepoints', () => {
        it('THEN it should add cycle and day', () => {
            expect(TextUtils.addDescriptionBeforeTimepoints(TimepointConstants.CYCLE_DAY, List(['cycle 1', 'day 1'])))
                .toEqual('Cycle: cycle 1 Day: day 1');
        });

        it('THEN it should add visit', () => {
            expect(TextUtils.addDescriptionBeforeTimepoints(TimepointConstants.VISIT, 'cycle 1 day 1'))
                .toEqual('Visit: cycle 1 day 1');
        });

        it('THEN it should add visit number', () => {
            expect(TextUtils.addDescriptionBeforeTimepoints(TimepointConstants.VISIT_NUMBER, '5'))
                .toEqual('Visit number: 5');
        });

        it('THEN it should add nothing', () => {
            expect(TextUtils.addDescriptionBeforeTimepoints('', 'anything'))
                .toEqual('anything');
        });

    });
});
