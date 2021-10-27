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

import {CheckListFilterItemModel} from './CheckListFilterItemModel';
import {Values, ValuesWithEmpty} from '../dtos';

describe('GIVEN a CheckListFilterItemModel class', () => {

    let checkListFilterItemModel: CheckListFilterItemModel;

    beforeEach(() => {
        checkListFilterItemModel = new CheckListFilterItemModel('key1', 'displayName1');
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance vars set', () => {
            expect(checkListFilterItemModel.selectedValues).toEqual([]);
            expect(checkListFilterItemModel.availableValues).toEqual([]);
            expect(checkListFilterItemModel.initialValues).toEqual([]);
            expect(checkListFilterItemModel.includeEmptyValues).toEqual(true);
            expect(checkListFilterItemModel.filterText).toEqual('');
        });
    });

    describe('WHEN transforming data from server', () => {

        it('SHOULD have instance vars set correctly when set first', () => {
            const values: Values = {values: ['ABDOMINAL PAIN']};

            checkListFilterItemModel.fromServerObject(values);

            expect(checkListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN']);
            expect(checkListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN']);
            expect(checkListFilterItemModel.selectedValues).toEqual(['ABDOMINAL PAIN']);
            expect(checkListFilterItemModel.appliedSelectedValues).toEqual([]);
        });

        it('SHOULD have instance vars set correctly when set first with null', () => {
            const values: Values = {values: ['ABDOMINAL PAIN', null]};

            checkListFilterItemModel.fromServerObject(values);

            expect(checkListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN', null]);
            expect(checkListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', null]);
            expect(checkListFilterItemModel.selectedValues).toEqual(['ABDOMINAL PAIN', null]);
            expect(checkListFilterItemModel.appliedSelectedValues).toEqual([]);
        });

        it('SHOULD have instance vars set correctly when initialValues already set', () => {

            const values: Values = {values: ['ABDOMINAL PAIN', 'OTHER']};
            const valuesSecondTime: Values = {values: ['ABDOMINAL PAIN']};

            checkListFilterItemModel.fromServerObject(values);

            expect(checkListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(checkListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(checkListFilterItemModel.selectedValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(checkListFilterItemModel.appliedSelectedValues).toEqual([]);

            checkListFilterItemModel.fromServerObject(valuesSecondTime);

            expect(checkListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN']);
            expect(checkListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(checkListFilterItemModel.selectedValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(checkListFilterItemModel.appliedSelectedValues).toEqual([]);
        });
    });

    describe('WHEN transforming data to server', () => {

        it('SHOULD get null object when initial is []', () => {

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get null object when initial is [] and selectedValues length = 0', () => {

            checkListFilterItemModel.selectedValues = [];
            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues', () => {

            checkListFilterItemModel.selectedValues = ['A'];
            checkListFilterItemModel.appliedSelectedValues = ['A'];
            checkListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
        });

        it('SHOULD get null serverObject when initial = selectedValues', () => {

            checkListFilterItemModel.selectedValues = ['A', 'B'];
            checkListFilterItemModel.appliedSelectedValues = ['A', 'B'];
            checkListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get null serverObject when initial is not null and selectedValues = 0', () => {

            checkListFilterItemModel.selectedValues = [];
            checkListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues without null', () => {

            checkListFilterItemModel.selectedValues = ['A'];
            checkListFilterItemModel.appliedSelectedValues = ['A'];
            checkListFilterItemModel.initialValues = ['A', 'B', null];

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toBeUndefined();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues with null', () => {

            checkListFilterItemModel.selectedValues = ['A', null];
            checkListFilterItemModel.appliedSelectedValues = ['A', null];
            checkListFilterItemModel.initialValues = ['A', 'B', null];

            const serverObject: ValuesWithEmpty = checkListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toBe(true);
        });
    });

    describe('WHEN check is item in checklist', () => {

        it('THEN it should check correctly', () => {

            checkListFilterItemModel.selectedValues = ['A', 'C'];
            checkListFilterItemModel.availableValues = ['A', 'N'];
            checkListFilterItemModel.includeEmptyValues = false;

            const hasSelectedA = checkListFilterItemModel.isInSelectedList('A');
            const hasSelectedB = checkListFilterItemModel.isInSelectedList('B');
            const hasAvailableA = checkListFilterItemModel.isInAvaliableList('A');
            const hasAvailableB = checkListFilterItemModel.isInAvaliableList('B');

            expect(hasSelectedA).toBeTruthy();
            expect(hasSelectedB).toBeFalsy();
            expect(hasAvailableA).toBeTruthy();
            expect(hasAvailableB).toBeFalsy();
        });
    });

    describe('WHEN cleared', () => {
        it('THEN the selected values are set to be the initial values', () => {
            checkListFilterItemModel.selectedValues = ['A', 'B'];
            checkListFilterItemModel.initialValues = ['A', 'B', 'C'];

            checkListFilterItemModel.clear();

            expect(checkListFilterItemModel.selectedValues).toEqual(checkListFilterItemModel.initialValues);
        });
    });

    describe('WHEN we get the number of selected filters', () => {
        it('THEN the correct number is returned', () => {
            checkListFilterItemModel.selectedValues = ['A', 'B'];
            checkListFilterItemModel.appliedSelectedValues = ['A', 'B'];

            checkListFilterItemModel.updateNumberOfSelectedFilters();
            const result = checkListFilterItemModel.numberOfSelectedFilters;

            expect(result).toEqual(2);
        });
    });
});
