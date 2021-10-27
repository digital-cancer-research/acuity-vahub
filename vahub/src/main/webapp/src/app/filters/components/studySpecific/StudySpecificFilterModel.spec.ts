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

import {StudySpecificFilter, StudySpecificFilterModel} from './StudySpecificFilterModel';
import {Values, FILTER_TYPE} from '../dtos';

describe('GIVEN a StudySpecificFilter class', () => {
    let studySpecificFilter: StudySpecificFilter;
    let initialOptions: string[];
    beforeEach(() => {
        initialOptions = ['option1', 'option2', 'option3'];
        studySpecificFilter = new StudySpecificFilter('filter1', initialOptions);
    });
    describe('WHEN constructed', () => {
        it('THEN set name to filter name', () => {
            expect(studySpecificFilter.name).toBe('filter1');
        });
        it('THEN set the values to initial options', () => {
            expect(studySpecificFilter.availableValues).toEqual(initialOptions);
            expect(studySpecificFilter.initialValues).toEqual(initialOptions);
            expect(studySpecificFilter.selectedValues).toEqual(initialOptions);
        });
        it('THEN is not ready as selectedValues and initialValues equal', () => {
            expect(studySpecificFilter.ready()).toBeFalsy();
        });
    });

    describe('WHEN you wish to send options to the server', () => {
        it('THEN returns a string of filter names and options split by "--"', () => {
            const result = studySpecificFilter.formattedSelectedValues();
            const expected = ['filter1--option1', 'filter1--option2', 'filter1--option3'];
            expect(result).toEqual(expected);
        });
    });

    describe('WHEN change of options', () => {
        let newOptions: string[];
        beforeEach(() => {
            newOptions = ['option1', 'option2'];
            studySpecificFilter.update(newOptions);
        });
        it('THEN sets availableValues with updated options', () => {
            expect(studySpecificFilter.availableValues).toEqual(newOptions);
        });
        it('THEN is not ready as selectedValues and initialValues equal', () => {
            expect(studySpecificFilter.ready()).toBeFalsy();
        });
    });

    describe('WHEN a selection is made', () => {
        let newOptions: string[];
        beforeEach(() => {
            newOptions = ['option1', 'option2'];
            studySpecificFilter.selectedValues = newOptions;
        });

        it('THEN is not ready as selectedValues and initialValues equal', () => {
            expect(studySpecificFilter.ready()).toBeTruthy();
        });
    });

    describe('WHEN clear is called', () => {
        it('THEN clears selectedValues', () => {
            studySpecificFilter.clear();
            expect(studySpecificFilter.selectedValues.length).toBe(3);
        });
    });

    describe('WHEN an item is not in availableValues', () => {
        it('THEN isItemPresentInAvailableList returns false', () => {
            studySpecificFilter.availableValues = ['option1', 'option2'];
            expect(studySpecificFilter.isItemPresentInAvailableList('option3')).toBeFalsy();
        });
    });

    describe('WHEN an item is in availableValues', () => {
        it('THEN isItemPresentInAvailableList returns true', () => {
            studySpecificFilter.availableValues = ['option1', 'option2'];
            expect(studySpecificFilter.isItemPresentInAvailableList('option2')).toBeTruthy();
        });
    });

    describe('WHEN an item is not in selectedValues', () => {
        it('THEN isItemPresentInSelectedList returns false', () => {
            studySpecificFilter.selectedValues = ['option1', 'option2'];
            expect(studySpecificFilter.isItemPresentInSelectedList('option3')).toBeFalsy();
        });
    });

    describe('WHEN an item is in selectedValues', () => {
        it('THEN isItemPresentInSelectedList returns true', () => {
            studySpecificFilter.selectedValues = ['option1', 'option2'];
            expect(studySpecificFilter.isItemPresentInSelectedList('option2')).toBeTruthy();
        });
    });

    describe('WHEN isLastAvailableItem is called', () => {
        describe('AND there are more than one selected items', () => {
            describe('AND item in selected items', () => {
                it('THEN returns false', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    expect(studySpecificFilter.isLastAvailableItem('option1')).toBeFalsy();
                });
            });
            describe('AND item not in selected items', () => {
                it('THEN returns false', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    expect(studySpecificFilter.isLastAvailableItem('option3')).toBeFalsy();
                });
            });
        });
        describe('AND there is one selected items', () => {
            describe('AND item in selected items', () => {
                it('THEN returns true', () => {
                    studySpecificFilter.selectedValues = ['option1'];
                    expect(studySpecificFilter.isLastAvailableItem('option1')).toBeTruthy();
                });
            });
            describe('AND item not in selected items', () => {
                it('THEN returns false', () => {
                    studySpecificFilter.selectedValues = ['option1'];
                    expect(studySpecificFilter.isLastAvailableItem('option2')).toBeFalsy();
                });
            });
        });
    });

    describe('WHEN a change event occurs', () => {
        describe('AND the item is already selected', () => {
            describe('AND the item is checked', () => {
                it('THEN should not change the selectedValues', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    studySpecificFilter.change('option1', true);
                    expect(studySpecificFilter.selectedValues).toEqual(['option1', 'option2']);
                });
            });
            describe('AND the item is unchecked', () => {
                it('THEN should remove the item from the selectedValues', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    studySpecificFilter.change('option1', false);
                    expect(studySpecificFilter.selectedValues).toEqual(['option2']);
                });
            });

        });
        describe('AND the item is not already selected', () => {
            describe('AND the item is checked', () => {
                it('THEN should add the item to the selectedValues', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    studySpecificFilter.change('option3', true);
                    expect(studySpecificFilter.selectedValues).toEqual(['option1', 'option2', 'option3']);
                });
            });
            describe('AND the item is unchecked', () => {
                it('THEN should not change the selectedValues', () => {
                    studySpecificFilter.selectedValues = ['option1', 'option2'];
                    studySpecificFilter.change('option3', false);
                    expect(studySpecificFilter.selectedValues).toEqual(['option1', 'option2']);
                });
            });
        });

    });

});

describe('GIVEN a StudySpecificFilterModel class', () => {
    let studySpecificFilterModel: StudySpecificFilterModel;
    beforeEach(() => {
        studySpecificFilterModel = new StudySpecificFilterModel('key', 'studyFilters');
    });
    describe('WHEN constructed', () => {
        it('THEN sets filter type to check list', () => {
            expect(studySpecificFilterModel.type).toBe(FILTER_TYPE.STUDY_SPECIFIC_FILTERS);
        });
        it('THEN has no filters', () => {
            expect(studySpecificFilterModel.filters.length).toEqual(0);
        });
    });

    describe('WHEN a server object is requested', () => {
        describe('WHEN there are no filters', () => {
            it('THEN returns null', () => {
                expect(studySpecificFilterModel.toServerObject()).toBeNull();
            });
        });
        describe('WHEN there is a filter in the ready state', () => {
            beforeEach(() => {
                studySpecificFilterModel.filters = [new StudySpecificFilter('filter1', ['option1', 'option2']), new StudySpecificFilter('filter2', ['option1'])];
                studySpecificFilterModel.filters[0].selectedValues = ['option1'];
                const expected: Values = {values: ['filter1--option1', 'filter2-option1']};
                expect(studySpecificFilterModel.toServerObject()).toEqual(expected);
            });
        });
    });

    describe('WHEN clear is called', () => {
        let filters;
        beforeEach(() => {
            filters = [new StudySpecificFilter('filter1', ['option1', 'option2']), new StudySpecificFilter('filter2', ['option1'])];
            spyOn(filters[0], 'clear');
            spyOn(filters[1], 'clear');
            studySpecificFilterModel.filters = filters;
        });
        it('THEN calls clear on all filters', () => {
            studySpecificFilterModel.clear();
            expect(filters[0].clear).toHaveBeenCalled();
            expect(filters[1].clear).toHaveBeenCalled();
        });
    });

    describe('WHEN change is called', () => {
        let filters;
        beforeEach(() => {
            filters = [new StudySpecificFilter('filter1', ['option1', 'option2']), new StudySpecificFilter('filter2', ['option1'])];
            spyOn(filters[0], 'change');
            spyOn(filters[1], 'change');
            studySpecificFilterModel.filters = filters;
        });
        it('THEN calls change on correct filter', () => {
            studySpecificFilterModel.change('filter1', 'option2', true);
            expect(filters[0].change).toHaveBeenCalled();
            expect(filters[1].change).not.toHaveBeenCalled();
            expect(filters[0].change).toHaveBeenCalledWith('option2', true);
        });
    });

    describe('WHEN a server object is returned', () => {
        let returnedServerObject: Values;
        beforeEach(() => {
            returnedServerObject = {values: ['filter1--option1', 'filter1--option2', 'filter2--option1']};
            studySpecificFilterModel.fromServerObject(returnedServerObject);
        });
        describe('WHEN there are no filters', () => {
            it('THEN creates filters', () => {
                const expected = [new StudySpecificFilter('filter1', ['option1', 'option2']), new StudySpecificFilter('filter2', ['option1'])];

                expect(studySpecificFilterModel.filters).toEqual(expected);
            });
        });
        describe('WHEN another server object is returned and filters defined', () => {
            let returnedServerObject2;
            beforeEach(() => {
                returnedServerObject2 = {values: ['filter1--option1']};
                studySpecificFilterModel.fromServerObject(returnedServerObject2);
            });
            it('THEN updates filters', () => {
                const expected = [new StudySpecificFilter('filter1', ['option1', 'option2']), new StudySpecificFilter('filter2', ['option1'])];
                expected[0].availableValues = ['option1'];
                expected[1].availableValues = [];
                expect(studySpecificFilterModel.filters).toEqual(expected);
            });
        });
    });
});
