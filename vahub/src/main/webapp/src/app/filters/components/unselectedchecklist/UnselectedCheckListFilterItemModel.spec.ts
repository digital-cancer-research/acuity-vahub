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

import {UnselectedCheckListFilterItemModel} from './UnselectedCheckListFilterItemModel';
import {Values, ValuesWithEmpty} from '../dtos';

describe('GIVEN a UnselectedCheckListFilterItemModel class', () => {

    let unselectedCheckListFilterItemModel: UnselectedCheckListFilterItemModel;

    beforeEach(() => {
        unselectedCheckListFilterItemModel = new UnselectedCheckListFilterItemModel('key1', 'displayName1');
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance vars set', () => {
            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.availableValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.initialValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.includeEmptyValues).toEqual(true);
            expect(unselectedCheckListFilterItemModel.filterText).toEqual('');
        });
    });

    describe('WHEN transforming data from server', () => {

        it('SHOULD have instance vars set correctly when set first', () => {
            const values: Values = {values: ['ABDOMINAL PAIN']};

            unselectedCheckListFilterItemModel.fromServerObject(values);

            expect(unselectedCheckListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN']);
            expect(unselectedCheckListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN']);
            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.appliedSelectedValues).toEqual([]);
        });

        it('SHOULD have instance vars set correctly when set first with null', () => {
            const values: Values = {values: ['ABDOMINAL PAIN', null]};

            unselectedCheckListFilterItemModel.fromServerObject(values);

            expect(unselectedCheckListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN', null]);
            expect(unselectedCheckListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', null]);
            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.appliedSelectedValues).toEqual([]);
        });

        it('SHOULD have instance vars set correctly when initialValues already set', () => {

            const values: Values = {values: ['ABDOMINAL PAIN', 'OTHER']};
            const valuesSecondTime: Values = {values: ['ABDOMINAL PAIN']};

            unselectedCheckListFilterItemModel.fromServerObject(values);

            expect(unselectedCheckListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(unselectedCheckListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.appliedSelectedValues).toEqual([]);

            unselectedCheckListFilterItemModel.fromServerObject(valuesSecondTime);

            expect(unselectedCheckListFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN']);
            expect(unselectedCheckListFilterItemModel.initialValues).toEqual(['ABDOMINAL PAIN', 'OTHER']);
            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.appliedSelectedValues).toEqual([]);
        });
    });

    describe('WHEN transforming data to server', () => {

        it('SHOULD get null object when initial is []', () => {

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get null object when initial is [] and selectedValues length = 0', () => {

            unselectedCheckListFilterItemModel.selectedValues = [];
            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues', () => {

            unselectedCheckListFilterItemModel.selectedValues = ['A'];
            unselectedCheckListFilterItemModel.appliedSelectedValues = ['A'];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
        });

        it('SHOULD get initial values serverObject when initial = selectedValues', () => {

            unselectedCheckListFilterItemModel.selectedValues = ['A', 'B'];
            unselectedCheckListFilterItemModel.appliedSelectedValues = ['A', 'B'];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A', 'B']);
        });

        it('SHOULD get null serverObject when initial is not null and selectedValues = 0', () => {

            unselectedCheckListFilterItemModel.selectedValues = [];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B'];

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues without null', () => {

            unselectedCheckListFilterItemModel.selectedValues = ['A'];
            unselectedCheckListFilterItemModel.appliedSelectedValues = ['A'];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B', null];

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toBeUndefined();
        });

        it('SHOULD get serverObject when initial is not empty, selectedValues > 0 and selectedValues < initialValues with null', () => {

            unselectedCheckListFilterItemModel.selectedValues = ['A', null];
            unselectedCheckListFilterItemModel.appliedSelectedValues = ['A', null];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B', null];

            const serverObject: ValuesWithEmpty = unselectedCheckListFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toBe(true);
        });
    });

    describe('WHEN check is item in checklist', () => {

        it('THEN it should check correctly', () => {

            unselectedCheckListFilterItemModel.selectedValues = ['A', 'C'];
            unselectedCheckListFilterItemModel.availableValues = ['A', 'N'];
            unselectedCheckListFilterItemModel.includeEmptyValues = false;

            const hasSelectedA = unselectedCheckListFilterItemModel.isInSelectedList('A');
            const hasSelectedB = unselectedCheckListFilterItemModel.isInSelectedList('B');
            const hasAvailableA = unselectedCheckListFilterItemModel.isInAvaliableList('A');
            const hasAvailableB = unselectedCheckListFilterItemModel.isInAvaliableList('B');

            expect(hasSelectedA).toBeTruthy();
            expect(hasSelectedB).toBeFalsy();
            expect(hasAvailableA).toBeTruthy();
            expect(hasAvailableB).toBeFalsy();
        });
    });

    describe('WHEN cleared', () => {
        it('THEN the selected values should be cleared', () => {
            unselectedCheckListFilterItemModel.selectedValues = ['A', 'B'];
            unselectedCheckListFilterItemModel.initialValues = ['A', 'B', 'C'];

            unselectedCheckListFilterItemModel.clear();

            expect(unselectedCheckListFilterItemModel.selectedValues).toEqual([]);
            expect(unselectedCheckListFilterItemModel.appliedSelectedValues).toEqual([]);
        });
    });

    describe('WHEN we get the number of selected filters', () => {
        it('THEN the correct number is returned', () => {
            unselectedCheckListFilterItemModel.selectedValues = ['A', 'B'];
            unselectedCheckListFilterItemModel.appliedSelectedValues = ['A', 'B'];

            unselectedCheckListFilterItemModel.updateNumberOfSelectedFilters();
            expect(unselectedCheckListFilterItemModel.numberOfSelectedFilters).toEqual(2);
        });
    });
});
