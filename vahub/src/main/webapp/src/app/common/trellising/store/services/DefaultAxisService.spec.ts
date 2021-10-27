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

import {DefaultAxisService} from './DefaultAxisService';
import {TabId, DynamicAxis} from '../ITrellising';
import {MockAxisOptionsTimestampDrugsRandomization} from '../../../MockClasses';

describe('GIVEN DefaultAxisService', () => {
    xdescribe('WHEN tabId has no new approach implemented', () => {
        describe('AND getting default x axis option', () => {
            it('THEN returns ACTUAL_TREATMENT_ARM for POPULATION_BARCHART and end of study', () => {
                expect(DefaultAxisService.initialX(TabId.POPULATION_BARCHART, false,
                    [<DynamicAxis>{value: 'ACTUAL_TREATMENT_ARM'},
                        <DynamicAxis>{value: 'STUDY_ID'}])).toEqual({value: 'ACTUAL_TREATMENT_ARM'});
            });

            it('THEN returns WITHDRAWAL for POPULATION_BARCHART and on going', () => {
                expect(DefaultAxisService.initialX(TabId.POPULATION_BARCHART, true,
                    [<DynamicAxis>{value: 'WITHDRAWAL'}, <DynamicAxis>{value: 'STUDY_ID'}])).toEqual({value: 'WITHDRAWAL'});
            });


            it('THEN returns PLANNED_TREATMENT_ARM for end of study for EXACERBATIONS_GROUPED_COUNTS', () => {
                expect(DefaultAxisService.initialX(TabId.EXACERBATIONS_GROUPED_COUNTS, false,
                    [<DynamicAxis>{value: 'ACTUAL_TREATMENT_ARM'}, <DynamicAxis>{value: 'PLANNED_TREATMENT_ARM'},
                        <DynamicAxis>{value: 'STUDY_ID'}])).toEqual({value: 'PLANNED_TREATMENT_ARM'});
            });


            it('THEN returns WEEKS_SINCE_FIRST_TREATMENT for Exacerbations Over Time', () => {
                expect(DefaultAxisService.initialX(TabId.EXACERBATIONS_OVER_TIME, true,
                    [<DynamicAxis>{value: 'WEEKS_SINCE_FIRST_TREATMENT'},
                        <DynamicAxis>{value: 'DAYS_SINCE_FIRST_TREATMENT'}])).toEqual({value: 'WEEKS_SINCE_FIRST_TREATMENT'});
            });

            describe('AND the default option is not in the data', () => {
                it('THEN the Study ID is used instead', () => {
                    expect(DefaultAxisService.initialX(TabId.POPULATION_BARCHART, false,
                        [<DynamicAxis>{value: 'PLANNED_TREATMENT_ARM'}, <DynamicAxis>{value: 'STUDY_ID'}])).toEqual({value: 'STUDY_ID'});
                });
            });
        });
        describe('WHEN getting default y axis option', () => {
            it('THEN returns ACTUAL_VALUE for CARDIAC_BOXPLOT', () => {
                expect(DefaultAxisService.initialY(TabId.CARDIAC_BOXPLOT,
                    ['ABSOLUTE_CHANGE_FROM_BASELINE', 'ACTUAL_VALUE'])).toEqual('ACTUAL_VALUE');
            });
            it('THEN returns COUNT_OF_SUBJECTS for POPULATION_BARCHART', () => {
                expect(DefaultAxisService.initialY(TabId.POPULATION_BARCHART,
                    ['COUNT_OF_SUBJECTS', 'PERCENTAGE_OF_ALL_SUBJECTS'])).toEqual('COUNT_OF_SUBJECTS');
            });
            it('THEN returns COUNT_OF_SUBJECTS for AES_COUNTS_BARCHART', () => {
                expect(DefaultAxisService.initialY(TabId.AES_COUNTS_BARCHART,
                    ['PERCENTAGE_OF_ALL_EVENTS', 'COUNT_OF_SUBJECTS', 'COUNT_OF_SUBJECTS']))
                    .toEqual('COUNT_OF_SUBJECTS');
            });
        });
    });

    describe('WHEN tabId has new approach implemented', () => {
        describe('AND getting default x axis option', () => {
            describe('AND tabId is LAB_BOXPLOT', () => {
                it('THEN returns VISIT_NUMBER for end of study', () => {
                    const initialX = DefaultAxisService.initialX(TabId.LAB_BOXPLOT, false, MockAxisOptionsTimestampDrugsRandomization);
                    const expectedX = {
                        groupByOption: 'VISIT_NUMBER',
                        params: {}
                    };

                    expect(initialX).toEqual(expectedX);
                });

                it('THEN returns WEEKS_SINCE_FIRST_DOSE for ongoing', () => {
                    const initialX = DefaultAxisService.initialX(TabId.LAB_BOXPLOT, true, MockAxisOptionsTimestampDrugsRandomization);

                    const expectedX = {
                        groupByOption: 'MEASUREMENT_TIME_POINT',
                        params: {
                            'TIMESTAMP_TYPE': 'WEEKS_SINCE_FIRST_DOSE',
                            'BIN_SIZE': 1
                        }
                    };

                    expect(initialX).toEqual(expectedX);
                });
            });

            it('THEN returns WEEKS_SINCE_FIRST_TREATMENT for AEs Over Time', () => {
                const initialX = DefaultAxisService.initialX(TabId.AES_OVER_TIME, true, MockAxisOptionsTimestampDrugsRandomization);
                const expectedX = {
                    groupByOption: 'MEASUREMENT_TIME_POINT',
                    params: {
                        'TIMESTAMP_TYPE': 'WEEKS_SINCE_FIRST_DOSE',
                        'BIN_SIZE': 1
                    }
                };

                expect(initialX).toEqual(expectedX);
            });
        });

        xdescribe('AND getting default y axis option', () => {
            it('THEN returns visit description for end of study for LAB_BOXPLOT', () => {
                expect(DefaultAxisService.initialX(TabId.LAB_BOXPLOT, false,
                    [<DynamicAxis>{value: 'STUDY_DEFINED_WEEK'},
                        <DynamicAxis>{value: 'VISIT_DESCRIPTION'}])).toEqual({value: 'STUDY_DEFINED_WEEK'});
            });
        });
    });
});
