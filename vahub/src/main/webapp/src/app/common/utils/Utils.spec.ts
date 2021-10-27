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

import * as utils from './Utils';
import {FilterId, TabName} from '../trellising/store';
import {PageName} from '../../plugins/timeline/store/ITimeline';

describe('GIVEN a Utils class', () => {
    describe('WHEN calling getServerPath', () => {
        it('THEN it should swap template path', () => {
            expect(utils.getServerPath('study', 'info')).toEqual('/resources/study/info');
            expect(utils.getServerPath('study/more', 'info2')).toEqual('/resources/study/more/info2');
        });
    });

    describe('WHEN getting plugin summary', () => {
        it('SHOULD return appropriate options', () => {
            const expectedOptions = {
                eventWidgetName: 'Adverse Events',
                eventFiltersName: 'Adverse Event Filters',
                filterId: FilterId.AES,
                pageName: `${PageName.AES} -> ${TabName.AES_TABLE}`,
                showPopulationFilter: true,
                showEventFilter: true,
                showTimelineFilter: false,
                settingsName: '',
                showSettings: false
            };

            expect(utils.getPluginSummary('aes', 'table')).toEqual(expectedOptions);
        });
        it('SHOULD hide population and event filter on cohort-editor page', () => {
            const expectedOptions = {
                eventWidgetName: '',
                eventFiltersName: '',
                showPopulationFilter: false,
                showTimelineFilter: false,
                showEventFilter: false,
                filterId: null,
                pageName: PageName.COHORT_EDITOR,
                settingsName: '',
                showSettings: false
            };

            expect(utils.getPluginSummary('cohort-editor', null)).toEqual(expectedOptions);
        });
        it('SHOULD return correct config for timeline page', () => {
            const expectedOptions = {
                showPopulationFilter: true,
                showEventFilter: true,
                eventWidgetName: 'Timeline Events',
                eventFiltersName: 'Timeline Filters',
                filterId: null,
                showTimelineFilter: true,
                pageName: PageName.TIMELINE,
                settingsName: '',
                showSettings: false
            };

            expect(utils.getPluginSummary('timeline', null)).toEqual(expectedOptions);
        });
        it('SHOULD return correct config for SSV page', () => {
            const expectedOptions = {
                showPopulationFilter: true,
                showEventFilter: false,
                eventWidgetName: '',
                eventFiltersName: '',
                showTimelineFilter: false,
                filterId: FilterId.POPULATION,
                pageName: PageName.SINGLE_SUBJECT + ' -> ' + TabName.SINGLE_SUBJECT_SUMMARY,
                settingsName: '',
                showSettings: false
            };

            expect(utils.getPluginSummary('singlesubject', 'summary-tab')).toEqual(expectedOptions);
        });
        it('SHOULD return correct config for Dosing and Exposure page', () => {
            const expectedOptions = {
                showPopulationFilter: true,
                showEventFilter: true,
                eventWidgetName: 'Analyte Concentration',
                eventFiltersName: 'Analyte Concentration Filters',
                showTimelineFilter: false,
                filterId: FilterId.EXPOSURE,
                pageName: PageName.DOSING_EXPOSURE + ' -> ' + TabName.ANALYTE_CONCENTRATION_OVER_TIME,
                settingsName: 'Analyte Concentration Settings',
                showSettings: true
            };

            expect(utils.getPluginSummary('exposure', 'analyte-concentration')).toEqual(expectedOptions);
        });
    });

    describe('WHEN getting  formatted timezone', () => {
        // Date().getTimezoneOffset() returns number equal to timezone * -60
        describe('AND we are in Sweden GMT+1', () => {
            it('SHOULD return correct timezone', () => {
                expect(utils.getFormattedTimeZone(-60)).toEqual('+01:00');
            });
        });
        describe('AND we are in Kabul GMT+4:30', () => {
            it('SHOULD return correct timezone', () => {
                expect(utils.getFormattedTimeZone(-270)).toEqual('+04:30');
            });
        });
        describe('AND we are in Costa Rica GMT-6', () => {
            it('SHOULD return correct timezone', () => {
                expect(utils.getFormattedTimeZone(360)).toEqual('-06:00');
            });
        });
    });

    describe('WHEN calling parseToNumber', () => {
        describe('AND parse integer', () => {
            it('SHOULD return integer', () => {
                expect(utils.parseNumericalFields({'k': '5'})).toEqual({'k': 5});
            });
        });
        describe('AND parse primitive integer', () => {
            it('SHOULD return integer', () => {
                expect(utils.parseNumericalFields({'k': 5})).toEqual({'k': 5});
            });
        });
        describe('AND parse negative integer', () => {
            it('SHOULD return negative integer', () => {
                expect(utils.parseNumericalFields({'k': '-5'})).toEqual({'k': -5});
            });
        });
        describe('AND parse negative float', () => {
            it('SHOULD return negative float', () => {
                expect(utils.parseNumericalFields({'k': '-5.2'})).toEqual({'k': -5.2});
            });
        });
        describe('AND parse string', () => {
            it('SHOULD return string', () => {
                expect(utils.parseNumericalFields({'k': 'abc'})).toEqual({'k': 'abc'});
            });
        });
        describe('AND parse magic string', () => {
            it('SHOULD return same string', () => {
                expect(utils.parseNumericalFields({'k': '9BX9'})).toEqual({'k': '9BX9'});
            });
        });
        describe('AND parse NaN', () => {
            it('SHOULD return NaN', () => {
                expect(utils.parseNumericalFields({'k': NaN})['k']).toBeNaN();
            });
        });
        describe('AND parse null', () => {
            it('SHOULD return null', () => {
                expect(utils.parseNumericalFields({'k': null})['k']).toBeNull();
            });
        });
        describe('AND parse undefined', () => {
            it('SHOULD return undefined', () => {
                expect(utils.parseNumericalFields({'k': undefined})['k']).toBeUndefined();
            });
        });
        describe('AND parse composed object', () => {
            it('SHOULD return deeply parsed object', () => {
                const testObj = {
                    'k': 'abc',
                    'number': '25',
                    'innerObject': {
                        'k': '-3',
                        'str': 'bca',
                        'anotherInnerObj': {
                            'c': '5'
                        }
                    }
                };
                const expected = {
                    'k': 'abc',
                    'number': 25,
                    'innerObject': {
                        'k': -3,
                        'str': 'bca',
                        'anotherInnerObj': {
                            'c': 5
                        }
                    }
                };
                expect(utils.parseNumericalFields(testObj)).toEqual(expected);
            });
        });
        describe('AND parse self-referential object', () => {
            it('SHOULD not infinite loop', () => {
                const o = {'k': 'abc', 'o2': null};
                const o2 = {'k': 'abc', 'o': null};
                o.o2 = o2;
                o2.o = o;
                expect(utils.parseNumericalFields(o)).toEqual(o);
            });
        });
    });
});
