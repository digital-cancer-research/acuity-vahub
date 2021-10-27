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

import * as fromDetailsOnDemand from './DetailsOnDemandReducer';
import {Actions, UpdateColumnsAction} from '../actions/DetailsOnDemandActions';
import {Map, Set, is} from 'immutable';

export function main(): void {
    describe('Details On Demand reducer', () => {
        const initialState = fromDetailsOnDemand.initialState;
        const aesColumnNames = [
            'studyId',
            'studyPart',
            'subjectId',
            'preferredTerm',
            'highLevelTerm',
            'systemOrganClass',
            'maxSeverity',
            'startDate',
            'endDate',
            'daysOnStudyAtAEStart',
            'daysOnStudyAtAEEnd',
            'duration',
            'serious',
            'actionTaken',
            'causality',
            'description',
            'outcome',
            'specialInterestGroup'
        ];

        beforeEach(() => {
            // register immutable matchers
        });

        describe('WHEN called with unknown action', () => {
            it('SHOULD return initial state', () => {
                const result = fromDetailsOnDemand.reducer(initialState, {} as Actions);

                expect(is(result, initialState)).toBeTruthy();
            });
        });

        describe('WHEN updating columns', () => {
            it('SHOULD set appropriate columns', () => {
                const expectedResult = Map({
                    columns: Map({
                        aes: Set.of(...aesColumnNames)
                    })
                });

                const result = fromDetailsOnDemand.reducer(initialState, new UpdateColumnsAction({
                    columns: Map({
                        aes: Set.of(...aesColumnNames)
                    })
                }));

                expect(is(expectedResult, result)).toBeTruthy();
            });
        });
    });
}
