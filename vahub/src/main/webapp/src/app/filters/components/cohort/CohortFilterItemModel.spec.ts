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

import {CohortFilterItemModel} from './CohortFilterItemModel';

describe('GIVEN a CohortFilterItemModel class', () => {
    let model: CohortFilterItemModel;

    beforeEach(() => {
        model = new CohortFilterItemModel('cohortKey', 'cohortName');
    });

    describe('WHEN transforming to server object', () => {
        describe('AND there are no subjects', () => {
            it('THEN returns null', () => {
                model.selectedValues = [];
                expect(model.toServerObject()).toBeNull();
            });
        });

        describe('AND there are subjects', () => {
            it('THEN returns the subjects', () => {
                model.selectedValues = ['subj-1'];
                expect(model.toServerObject()).toEqual({values: ['subj-1']});
            });
        });
    });
});
