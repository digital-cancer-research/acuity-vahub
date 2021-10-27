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

import {Map, fromJS} from 'immutable';
import {generateAvailableOptions} from './XAxisLabelService';
import {
    MockAxisOptionsTimestampDrugsRandomization,
    MockAxisOptionsNoMeasurement,
    MockAxisOptionsTimestampNoDrugsRandomization, MockAxisOptionsTimestampNoDrugsNoRandomization, MockAxisOptionsNoMeasurementBinable
} from '../../../MockClasses';
import {TabId} from '../../store/ITrellising';

describe('GIVEN XAxisLabelService', () => {
    describe('WHEN generating options', () => {
        describe('AND options don\'t contain measurement option', () => {
            describe('AND the option is not binable', () => {
                it('SHOULD generate options', () => {
                    const expectedOptions = [
                        {
                            displayedOption: 'VISIT_NUMBER',
                            groupByOption: 'VISIT_NUMBER',
                            params: {}
                        }
                    ];

                    expect(generateAvailableOptions(fromJS(MockAxisOptionsNoMeasurement))).toEqual(expectedOptions);
                });
            });

            describe('AND the option is binable', () => {
                it('SHOULD generate options', () => {
                    const expectedOptions = [
                        {
                            displayedOption: 'VISIT_NUMBER',
                            groupByOption: 'VISIT_NUMBER',
                            params: {
                                BIN_SIZE: 1
                            }
                        }
                    ];

                    expect(generateAvailableOptions(fromJS(MockAxisOptionsNoMeasurementBinable))).toEqual(expectedOptions);
                });
            });
        });

        describe('AND options include timestamp option', () => {
            describe('AND options do not include randomization' /*' and drugs' */ , () => {
                it('SHOULD generate options', () => {
                    const expectedOptions = [
                        {
                            displayedOption: 'VISIT_NUMBER',
                            groupByOption: 'VISIT_NUMBER',
                            params: {}
                        },
                        // {
                        //     displayedOption: 'DATE',
                        //     groupByOption: 'MEASUREMENT_TIME_POINT',
                        //     params: {
                        //         TIMESTAMP_TYPE: 'DATE',
                        //         BIN_SIZE: 1
                        //     }
                        // },
                        {
                            displayedOption: 'DAYS_SINCE_FIRST_DOSE',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'DAYS_SINCE_FIRST_DOSE',
                                BIN_SIZE: 1
                            }
                        },
                        {
                            displayedOption: 'WEEKS_SINCE_FIRST_DOSE',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'WEEKS_SINCE_FIRST_DOSE',
                                BIN_SIZE: 1
                            }
                        },
                    ];

                    expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampNoDrugsNoRandomization))).toEqual(expectedOptions);
                });
            });
            describe('AND options include randomization' /*' but no drugs' */, () => {
                it('SHOULD generate options', () => {
                    const expectedOptions = [
                        {
                            displayedOption: 'VISIT_NUMBER',
                            groupByOption: 'VISIT_NUMBER',
                            params: {}
                        },
                        // {
                        //     displayedOption: 'DATE',
                        //     groupByOption: 'MEASUREMENT_TIME_POINT',
                        //     params: {
                        //         TIMESTAMP_TYPE: 'DATE',
                        //         BIN_SIZE: 1
                        //     }
                        // },
                        {
                            displayedOption: 'DAYS_SINCE_FIRST_DOSE',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'DAYS_SINCE_FIRST_DOSE',
                                BIN_SIZE: 1
                            }
                        },
                        {
                            displayedOption: 'WEEKS_SINCE_FIRST_DOSE',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'WEEKS_SINCE_FIRST_DOSE',
                                BIN_SIZE: 1
                            }
                        },
                        {
                            displayedOption: 'DAYS_SINCE_RANDOMISATION',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'DAYS_SINCE_RANDOMISATION',
                                BIN_SIZE: 1
                            }
                        },
                        {
                            displayedOption: 'WEEKS_SINCE_RANDOMISATION',
                            groupByOption: 'MEASUREMENT_TIME_POINT',
                            params: {
                                TIMESTAMP_TYPE: 'WEEKS_SINCE_RANDOMISATION',
                                BIN_SIZE: 1
                            }
                        }
                    ];

                    expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampNoDrugsRandomization))).toEqual(expectedOptions);
                });
            });
            describe('AND options include randomization and drugs', () => {
                describe('AND tabId is none of LAB_BOXPLOT, LAB_LINEPLOT, AES_OVERTIME', () => {
                    it('THEN options SHOULD contain randomization and drugs', () => {
                        const expectedOptions = [
                            {
                                displayedOption: 'DAYS_SINCE_RANDOMISATION',
                                groupByOption: 'MEASUREMENT_TIME_POINT',
                                params: {
                                    TIMESTAMP_TYPE: 'DAYS_SINCE_RANDOMISATION',
                                    BIN_SIZE: 1
                                }
                            },
                            {
                                displayedOption: 'WEEKS_SINCE_RANDOMISATION',
                                groupByOption: 'MEASUREMENT_TIME_POINT',
                                params: {
                                    TIMESTAMP_TYPE: 'WEEKS_SINCE_RANDOMISATION',
                                    BIN_SIZE: 1
                                }
                            },
                            // {
                            //     displayedOption: 'DAYS_SINCE_FIRST_DOSE_OF_DRUG',
                            //     groupByOption: 'MEASUREMENT_TIME_POINT',
                            //     params: {
                            //         TIMESTAMP_TYPE: 'DAYS_SINCE_FIRST_DOSE_OF_DRUG',
                            //         DRUG_NAME: 'STDY4321',
                            //         BIN_SIZE: 1
                            //     }
                            // },
                            // {
                            //     displayedOption: 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG',
                            //     groupByOption: 'MEASUREMENT_TIME_POINT',
                            //     params: {
                            //         TIMESTAMP_TYPE: 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG',
                            //         DRUG_NAME: 'STDY4321',
                            //         BIN_SIZE: 1
                            //     }
                            // }
                        ];

                        expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization))).toContain(expectedOptions[0]);
                        expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization))).toContain(expectedOptions[1]);
                        // expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization))).toContain(expectedOptions[2]);
                        // expect(generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization))).toContain(expectedOptions[3]);
                    });
                });
                describe('AND tabId is one of LAB_BOXPLOT, LAB_LINEPLOT, AES_OVER_TIME', () => {
                    it('THEN options SHOULD not contain days/weeks since first dose', () => {
                        const expectedOptions = [
                            {
                                displayedOption: 'DATE',
                                groupByOption: 'MEASUREMENT_TIME_POINT',
                                params: {
                                    TIMESTAMP_TYPE: 'DATE',
                                    DRUG_NAME: 'STDY4321',
                                    BIN_SIZE: 1
                                }
                            },
                            {
                                displayedOption: 'DAYS_SINCE_FIRST_DOSE_OF_DRUG',
                                groupByOption: 'MEASUREMENT_TIME_POINT',
                                params: {
                                    TIMESTAMP_TYPE: 'DAYS_SINCE_FIRST_DOSE_OF_DRUG',
                                    DRUG_NAME: 'STDY4321',
                                    BIN_SIZE: 1
                                }
                            },
                            {
                                displayedOption: 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG',
                                groupByOption: 'MEASUREMENT_TIME_POINT',
                                params: {
                                    TIMESTAMP_TYPE: 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG',
                                    DRUG_NAME: 'STDY4321',
                                    BIN_SIZE: 1
                                }
                            }
                        ];

                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_LINEPLOT)
                        ).not.toContain(expectedOptions[0]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_LINEPLOT)
                        ).not.toContain(expectedOptions[1]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_LINEPLOT)
                        ).not.toContain(expectedOptions[2]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_BOXPLOT)
                        ).not.toContain(expectedOptions[0]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_BOXPLOT)
                        ).not.toContain(expectedOptions[1]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.LAB_BOXPLOT)
                        ).not.toContain(expectedOptions[2]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.AES_OVER_TIME)
                        ).not.toContain(expectedOptions[0]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.AES_OVER_TIME)
                        ).not.toContain(expectedOptions[1]);
                        expect(
                            generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.AES_OVER_TIME)
                        ).not.toContain(expectedOptions[2]);
                    });
                });
            });
        });

        describe('AND tabId is one of CVOT_ENDPOINTS_COUNTS, CI_EVENT_COUNTS, CEREBROVASCULAR_COUNTS', () => {
            it('SHOULD generate options containing NONE option', () => {
                const expectedOption = {
                    displayedOption: 'NONE',
                    groupByOption: 'NONE',
                    params: {}
                };

                expect(
                    generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.CVOT_ENDPOINTS_COUNTS)
                ).toContain(expectedOption);
                expect(
                    generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.CI_EVENT_COUNTS)
                ).toContain(expectedOption);
                expect(
                    generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization), TabId.CEREBROVASCULAR_COUNTS)
                ).toContain(expectedOption);
            });
        });

        describe('AND tabId is none of CVOT_ENDPOINTS_COUNTS, CI_EVENT_COUNTS, CEREBROVASCULAR_COUNTS', () => {
            it('SHOULD generate options without NONE option', () => {
                const expectedOption = {
                    displayedOption: 'NONE',
                    groupByOption: 'NONE',
                    params: {}
                };

                expect(
                    generateAvailableOptions(fromJS(MockAxisOptionsTimestampDrugsRandomization))
                ).not.toContain(expectedOption);
            });
        });
    });
});
