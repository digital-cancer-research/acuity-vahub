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

import {RangeFilterItemModel} from './RangeFilterItemModel';
import {GENE_PERCENTAGE} from '../../dataTypes/biomarkers/BiomarkersFiltersConstants';
import {Range, RangeWithEmpty} from '../dtos';

describe('GIVEN a RangeFilterItemModel class', () => {
    let rangeFilterItemModel: RangeFilterItemModel;

    beforeEach(() => {
        rangeFilterItemModel = new RangeFilterItemModel('key1', 'displayName1', 1);
    });

    describe('WHEN constructing', () => {

        it('THEN it should have instance variables set', () => {
            expect(rangeFilterItemModel.selectedValues).toEqual({from: 0, to: 0, includeEmptyValues: true});
            expect(rangeFilterItemModel.availableValues).toEqual({absMin: 0, absMax: 0, min: 0, max: 0});
            expect(rangeFilterItemModel.disabled).toBeFalsy();
        });
    });

    describe('WHEN the filter is initialising', () => {

        it('THEN it should set the filter range correctly', () => {
            const serverObj: Range = {from: -99, to: 99};

            rangeFilterItemModel.fromServerObject(serverObj);

            expect(rangeFilterItemModel.availableValues.min).toEqual(-99);
            expect(rangeFilterItemModel.availableValues.max).toEqual(99);
        });

        it('THEN it should set the selected range correctly', () => {
            const serverObj: Range = {from: -99, to: 99};

            rangeFilterItemModel.fromServerObject(serverObj);

            expect(rangeFilterItemModel.selectedValues.from).toEqual(-99);
            expect(rangeFilterItemModel.selectedValues.to).toEqual(99);
        });
    });

    describe('WHEN the server brings back new data from the server', () => {
        describe('AND RangeFilter is GENE_PERCENTAGE', () => {
            beforeEach(() => {
                rangeFilterItemModel = new RangeFilterItemModel(GENE_PERCENTAGE, 'displayName1', 1);
            });
            it('THEN the available range resets to 0-100', () => {
                const initialRange: Range = {from: -99, to: 99};
                rangeFilterItemModel.fromServerObject(initialRange);

                expect(rangeFilterItemModel.availableValues.min).toEqual(0);
                expect(rangeFilterItemModel.availableValues.max).toEqual(100);
            });
        });
        describe('AND new range is inside the current range', () => {

            it('THEN the available range does not change', () => {
                const initialRange: Range = {from: -99, to: 99};
                const newRange: Range = {from: -1, to: 1};

                rangeFilterItemModel.fromServerObject(initialRange);
                rangeFilterItemModel.fromServerObject(newRange);

                expect(rangeFilterItemModel.availableValues.min).toEqual(-99);
                expect(rangeFilterItemModel.availableValues.max).toEqual(99);
            });
        });
        describe('AND new range is wider than the current range', () => {

            it('THEN the available range does not change', () => {
                const initialRange: Range = {from: -99, to: 99};
                const newRange: Range = {from: -100, to: 100};

                rangeFilterItemModel.fromServerObject(initialRange);
                rangeFilterItemModel.fromServerObject(newRange);

                expect(rangeFilterItemModel.availableValues.min).toEqual(-100);
                expect(rangeFilterItemModel.availableValues.max).toEqual(100);
            });
        });

        describe('AND this filter has not been changed by the user', () => {
            it('THEN the selected range changes', () => {
                const initialRange: Range = {from: -99, to: 99};
                const newRange: Range = {from: -1, to: 1};

                rangeFilterItemModel.fromServerObject(initialRange);
                rangeFilterItemModel.fromServerObject(newRange);

                expect(rangeFilterItemModel.selectedValues.from).toEqual(-1);
                expect(rangeFilterItemModel.selectedValues.to).toEqual(1);
            });
        });

        describe('AND this filter has been changed by the user', () => {
            it('THEN the selected range does not change', () => {
                const initialRange: Range = {from: -99, to: 99};
                const newRange: Range = {from: -1, to: 1};
                rangeFilterItemModel.fromServerObject(initialRange);

                rangeFilterItemModel.haveMadeChange = true;
                rangeFilterItemModel.fromServerObject(newRange);

                expect(rangeFilterItemModel.selectedValues.from).toEqual(-99);
                expect(rangeFilterItemModel.selectedValues.to).toEqual(99);
            });
        });

        describe('AND the from and to values are null', () => {
            it('THEN the selected range is changed to 0', () => {
                const initialRange: Range = {from: -99, to: 99};
                const newRange: Range = {from: null, to: null};
                rangeFilterItemModel.fromServerObject(initialRange);

                rangeFilterItemModel.fromServerObject(newRange);

                expect(rangeFilterItemModel.selectedValues.from).toEqual(0);
                expect(rangeFilterItemModel.selectedValues.to).toEqual(0);
            });
        });
    });

    describe('WHEN transforming unchanged range to server', () => {

        it('THEN an empty object is sent', () => {

            rangeFilterItemModel.selectedValues = {from: -99, to: 99, includeEmptyValues: true};
            rangeFilterItemModel.appliedSelectedValues = {from: -99, to: 99, includeEmptyValues: true};
            rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
            const serverObject: RangeWithEmpty = rangeFilterItemModel.toServerObject();

            expect(serverObject).toEqual({});
        });
    });

    describe('WHEN transforming null range to server', () => {

        it('THEN an empty object is sent', () => {

            rangeFilterItemModel.appliedSelectedValues = {from: null, to: null, includeEmptyValues: true};
            const serverObject: RangeWithEmpty = rangeFilterItemModel.toServerObject();

            expect(serverObject).toEqual({});
        });
    });

    describe('WHEN transforming changed range to server', () => {

        it('THEN an object with the correct range is sent', () => {

            rangeFilterItemModel.selectedValues = {from: 1, to: 10, includeEmptyValues: true};
            rangeFilterItemModel.appliedSelectedValues = {from: 1, to: 10, includeEmptyValues: true};
            rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
            rangeFilterItemModel.haveMadeChange = true;
            const serverObject: RangeWithEmpty = rangeFilterItemModel.toServerObject();

            expect(serverObject.from).toEqual(1);
            expect(serverObject.to).toEqual(10);
        });
    });

    describe('WHEN transforming a disabled range filter to server', () => {

        it('THEN object wih min/max available values is sent', () => {

            rangeFilterItemModel.selectedValues = {from: null, to: null, includeEmptyValues: true};
            rangeFilterItemModel.appliedSelectedValues = {from: null, to: null, includeEmptyValues: true};
            rangeFilterItemModel.availableValues = {min: -99, max: 99, absMin: -99, absMax: 99};
            const serverObject: RangeWithEmpty = rangeFilterItemModel.toServerObject();

            expect(serverObject).toEqual({from: -99, to: 99});
        });
    });

    describe('WHEN reseting', () => {

        it('THEN the ranges and selections should be reset', () => {
            rangeFilterItemModel.reset();

            expect(rangeFilterItemModel.selectedValues).toEqual({from: 0, to: 0, includeEmptyValues: true});
            expect(rangeFilterItemModel.availableValues).toEqual({absMin: 0, absMax: 0, min: 0, max: 0});
            expect(rangeFilterItemModel.disabled).toBeFalsy();
            expect(rangeFilterItemModel.haveMadeChange).toBeFalsy();
        });
    });

    describe('WHEN cleared', () => {
        it('THEN the selected values are reset to the available values', () => {
            rangeFilterItemModel.availableValues = {min: -1, max: 99, absMin: 0, absMax: 0};
            rangeFilterItemModel.selectedValues = {from: 10, to: 20, includeEmptyValues: true};

            rangeFilterItemModel.clear();

            expect(rangeFilterItemModel.selectedValues.from).toEqual(rangeFilterItemModel.availableValues.min);
            expect(rangeFilterItemModel.selectedValues.to).toEqual(rangeFilterItemModel.availableValues.max);
            expect(rangeFilterItemModel.haveMadeChange).toBeFalsy();
        });
    });

    describe('WHEN we get the number of selected filters', () => {
        it('THEN the correct number is returned', () => {
            rangeFilterItemModel.haveMadeChange = true;
            rangeFilterItemModel.updateNumberOfSelectedFilters();
            const result = rangeFilterItemModel.numberOfSelectedFilters;

            expect(result).toEqual(1);
        });
    });

    describe('WHEN setting null values', () => {
        it('THEN the text box contains the original values', () => {
            rangeFilterItemModel.availableValues = {min: 0, max: 10, absMin: 0, absMax: 10};

            rangeFilterItemModel.setSelectedValues({from: null, to: null});

            expect(rangeFilterItemModel.textBoxSelectedValues.from).toBe(0);
            expect(rangeFilterItemModel.textBoxSelectedValues.to).toBe(10);
        });
    });
});
