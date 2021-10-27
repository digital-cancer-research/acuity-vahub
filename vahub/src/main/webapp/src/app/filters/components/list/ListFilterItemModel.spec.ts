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

import {ListFilterItemModel} from './ListFilterItemModel';
import {Values, ValuesWithEmpty} from '../dtos';
import {EMPTY} from '../../../common/trellising/store';

describe('GIVEN a ListFilterItemModel class', () => {
    let listFilterItemModel: ListFilterItemModel;

    beforeEach(() => {
        listFilterItemModel = new ListFilterItemModel('key1', 'displayName1', true, 4);
    });

    describe('WHEN constructing', () => {

        it('THEN it should have instance var set', () => {
            expect(listFilterItemModel.selectedValues).toEqual([]);
            expect(listFilterItemModel.availableValues).toEqual([]);
            expect(listFilterItemModel.includeEmptyValues).toEqual(true);
            expect(listFilterItemModel.filterText).toEqual('');
            expect(listFilterItemModel.sizeDynamic).toEqual(true);
            expect(listFilterItemModel.maxSize).toEqual(4);
        });
    });

    describe('WHEN transforming data from server', () => {

        it('THEN it should have instance vars set correctly', () => {
            const values: Values = {values: ['ABDOMINAL PAIN']};

            listFilterItemModel.fromServerObject(values);

            expect(listFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN']);
        });

        it('THEN it should have instance vars set correctly with null', () => {
            const values: Values = {values: ['ABDOMINAL PAIN', null]};

            listFilterItemModel.fromServerObject(values);

            expect(listFilterItemModel.availableValues).toEqual(['ABDOMINAL PAIN', EMPTY]);
        });

        it('THEN it should have instance vars set correctly with []', () => {
            const values: Values = {values: []};

            listFilterItemModel.fromServerObject(values);

            expect(listFilterItemModel.availableValues).toEqual([]);
        });
    });

    describe('WHEN transforming data to server', () => {

        it('THEN it should get object without empty correctly', () => {

            listFilterItemModel.selectedValues = ['A'];
            listFilterItemModel.appliedSelectedValues = ['A'];
            const serverObject: ValuesWithEmpty = listFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toBeUndefined();
        });

        it('THEN it should get object with empty correctly', () => {

            listFilterItemModel.selectedValues = ['A', EMPTY];
            listFilterItemModel.appliedSelectedValues = ['A', EMPTY];
            const serverObject: ValuesWithEmpty = listFilterItemModel.toServerObject();

            expect(serverObject.values).toEqual(['A']);
            expect(serverObject.includeEmptyValues).toEqual(true);
        });

        it('THEN it should get null object with no selected', () => {

            listFilterItemModel.selectedValues = [];
            const serverObject: ValuesWithEmpty = listFilterItemModel.toServerObject();

            expect(serverObject).toBeNull();
        });
    });

    describe('WHEN resetting the data', () => {

        it('THEN it should reset the data to orginal state', () => {

            listFilterItemModel.selectedValues = ['A'];
            listFilterItemModel.availableValues = ['A'];
            listFilterItemModel.includeEmptyValues = false;

            listFilterItemModel.reset();

            expect(listFilterItemModel.selectedValues).toEqual([]);
            expect(listFilterItemModel.availableValues).toEqual([]);
            expect(listFilterItemModel.includeEmptyValues).toEqual(true);
            expect(listFilterItemModel.filterText).toEqual('');
        });
    });

    describe('WHEN check is item in list', () => {

        it('THEN it should check correctly', () => {

            listFilterItemModel.selectedValues = ['A', 'C'];
            listFilterItemModel.availableValues = ['A', 'N'];
            listFilterItemModel.includeEmptyValues = false;

            const hasSelectedA = listFilterItemModel.isInSelectedList('A');
            const hasSelectedB = listFilterItemModel.isInSelectedList('B');
            const hasAvailableA = listFilterItemModel.isInAvaliableList('A');
            const hasAvailableB = listFilterItemModel.isInAvaliableList('B');

            expect(hasSelectedA).toBeTruthy();
            expect(hasSelectedB).toBeFalsy();
            expect(hasAvailableA).toBeTruthy();
            expect(hasAvailableB).toBeFalsy();
        });
    });

    describe('WHEN cleared', () => {
        it('THEN the selected values are removed', () => {
            listFilterItemModel.selectedValues = ['A', 'B'];

            listFilterItemModel.clear();

            expect(listFilterItemModel.selectedValues).toEqual([]);
        });
    });

    describe('WHEN we get the number of selected filters', () => {
        it('THEN the correct number is returned', () => {
            listFilterItemModel.selectedValues = ['A', 'B'];

            listFilterItemModel.updateNumberOfSelectedFilters();
            const result = listFilterItemModel.numberOfSelectedFilters;

            expect(result).toEqual(2);
        });
    });
});
