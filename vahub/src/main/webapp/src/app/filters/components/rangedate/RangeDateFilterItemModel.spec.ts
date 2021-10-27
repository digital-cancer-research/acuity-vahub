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

import {RangeDateFilterItemModel} from './RangeDateFilterItemModel';
import {Range, RangeWithEmpty} from '../dtos';

describe('GIVEN a RangeDateFilterItemModel class', () => {
    let rangeDateFilterItemModel: RangeDateFilterItemModel;

    beforeEach(() => {
        rangeDateFilterItemModel = new RangeDateFilterItemModel('key1', 'displayName1');
    });

    describe('WHEN constructing', () => {

        it('THEN it should have instance variables set', () => {
            expect(rangeDateFilterItemModel.selectedValues).toEqual({from: null, to: null, includeEmptyValues: true});
            expect(rangeDateFilterItemModel.availableValues).toEqual({from: 0, to: 0});
            expect(rangeDateFilterItemModel.disabled).toBeFalsy();
        });
    });

    describe('WHEN data first comes from the server', () => {

        it('THEN it should set the filter range correctly', () => {
            const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

            rangeDateFilterItemModel.fromServerObject(serverObj);

            expect(rangeDateFilterItemModel.availableValues.from).toEqual('01-Dec-2000');
            expect(rangeDateFilterItemModel.availableValues.to).toEqual('01-Dec-2001');
        });

        it('THEN it should set the selected range correctly', () => {
            const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

            rangeDateFilterItemModel.fromServerObject(serverObj);

            expect(rangeDateFilterItemModel.selectedValues.from).toEqual('01-Dec-2000');
            expect(rangeDateFilterItemModel.selectedValues.to).toEqual('01-Dec-2001');
        });
    });

    describe('WHEN data is updated because of a change in a different filter', () => {
        describe('AND the new from date is before the current from date', () => {
            it('THEN the current from date is updated', () => {
                rangeDateFilterItemModel.availableValues.from = '02-Dec-2000';
                const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

                rangeDateFilterItemModel.fromServerObject(serverObj);

                expect(rangeDateFilterItemModel.availableValues.from).toEqual('01-Dec-2000');
            });
        });

        describe('AND the new from date is after the current from date', () => {
            it('THEN the current from date is not updated', () => {
                rangeDateFilterItemModel.availableValues.from = '30-Nov-2000';
                const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

                rangeDateFilterItemModel.fromServerObject(serverObj);

                expect(rangeDateFilterItemModel.availableValues.from).toEqual('30-Nov-2000');
            });
        });

        describe('AND the new to date is after the current to date', () => {
            it('THEN the current to date is updated', () => {
                rangeDateFilterItemModel.availableValues.to = '30-Nov-2001';
                const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

                rangeDateFilterItemModel.fromServerObject(serverObj);

                expect(rangeDateFilterItemModel.availableValues.to).toEqual('01-Dec-2001');
            });
        });

        describe('AND the new to date is before the current to date', () => {
            it('THEN the current to date is not updated', () => {
                rangeDateFilterItemModel.availableValues.to = '02-Dec-2001';
                const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

                rangeDateFilterItemModel.fromServerObject(serverObj);

                expect(rangeDateFilterItemModel.availableValues.to).toEqual('02-Dec-2001');
            });
        });

        describe('AND the user has previously updated the date range', () => {
            it('THEN the selected date range is not updated', () => {
                rangeDateFilterItemModel.selectedValues.from = '31-Nov-2000';
                rangeDateFilterItemModel.selectedValues.to = '02-Dec-2001';
                rangeDateFilterItemModel.haveMadeChange = true;
                const serverObj: Range = {from: '2000-12-01T00:00', to: '2001-12-01T00:00'};

                rangeDateFilterItemModel.fromServerObject(serverObj);

                expect(rangeDateFilterItemModel.selectedValues.from).toEqual('31-Nov-2000');
                expect(rangeDateFilterItemModel.selectedValues.to).toEqual('02-Dec-2001');
            });
        });
    });

    describe('WHEN transforming changed range to server', () => {

        it('THEN an empty object is sent', () => {

            rangeDateFilterItemModel.selectedValues = {from: '01-Feb-2000', to: '01-Feb-2001', includeEmptyValues: true};
            rangeDateFilterItemModel.appliedSelectedValues = {from: '01-Feb-2000', to: '01-Feb-2001', includeEmptyValues: true};
            rangeDateFilterItemModel.haveMadeChange = true;

            const serverObject: RangeWithEmpty = rangeDateFilterItemModel.toServerObject();

            expect(serverObject.from).toEqual('2000-02-01T00:00:00');
            expect(serverObject.to).toEqual('2001-02-01T00:00:00');
        });
    });

    describe('WHEN transforming null range to server', () => {

        it('THEN an empty object is sent', () => {

            rangeDateFilterItemModel.appliedSelectedValues = {from: null, to: null, includeEmptyValues: true};
            const serverObject: RangeWithEmpty = rangeDateFilterItemModel.toServerObject();

            expect(serverObject).toEqual({});
        });
    });

    describe('WHEN reseting', () => {

        it('THEN the ranges and selections should be reset', () => {
            rangeDateFilterItemModel.reset();

            expect(rangeDateFilterItemModel.selectedValues).toEqual({from: null, to: null, includeEmptyValues: true});
            expect(rangeDateFilterItemModel.availableValues).toEqual({from: 0, to: 0});
            expect(rangeDateFilterItemModel.disabled).toBeFalsy();
        });
    });

    describe('WHEN cleared', () => {
        it('THEN the selected values are set the the available values', () => {
            rangeDateFilterItemModel.availableValues = {from: -1, to: 99};
            rangeDateFilterItemModel.selectedValues = {from: 10, to: 20, includeEmptyValues: true};

            rangeDateFilterItemModel.clear();

            expect(rangeDateFilterItemModel.selectedValues.from).toEqual(rangeDateFilterItemModel.availableValues.from);
            expect(rangeDateFilterItemModel.selectedValues.to).toEqual(rangeDateFilterItemModel.availableValues.to);
        });
    });

    describe('WHEN we get the number of selected filters', () => {
        it('THEN the correct number is returned', () => {
            rangeDateFilterItemModel.selectedValues = {from: 10, to: 20, includeEmptyValues: true};
            rangeDateFilterItemModel.haveMadeChange = true;

            rangeDateFilterItemModel.updateNumberOfSelectedFilters();
            const result = rangeDateFilterItemModel.numberOfSelectedFilters;

            expect(result).toEqual(1);
        });
    });

    describe('WHEN setting null values', () => {
        it('THEN the text box contains the original values', () => {
            rangeDateFilterItemModel.availableValues = {from: '14-Jan-2013', to: '15-Jan-2013'};

            rangeDateFilterItemModel.setSelectedValues({from: null, to: null});

            expect(rangeDateFilterItemModel.textBoxSelectedValues.from).toBe('14-Jan-2013');
            expect(rangeDateFilterItemModel.textBoxSelectedValues.to).toBe('15-Jan-2013');
        });
    });
});
