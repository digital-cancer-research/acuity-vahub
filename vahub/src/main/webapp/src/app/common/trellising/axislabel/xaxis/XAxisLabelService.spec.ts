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

import {XAxisLabelService} from './XAxisLabelService';
import {List, fromJS} from 'immutable';
import {DynamicAxis, DynamicAxisRecord} from '../../store/ITrellising';
import {TestBed, inject} from '@angular/core/testing';

describe('GIVEN XAxisLabelService', () => {
    let options: List<DynamicAxis>;
    let optionsWithNone: List<DynamicAxis>;
    let localOption: DynamicAxis;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                XAxisLabelService
            ]
        });

    });

    beforeEach(() => {
        options = fromJS([
            {
                'value': 'VISIT_DESCRIPTION',
                'intarg': null,
                'stringarg': null
            }, {
                'value': 'VISIT_NUMBER',
                'intarg': null,
                'stringarg': null
            }, {
                'value': 'STUDY_DEFINED_WEEK',
                'intarg': null,
                'stringarg': null
            }, {
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 1,
                'stringarg': null
            }, {
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 2,
                'stringarg': null
            }, {
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 3,
                'stringarg': null
            }, {
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 4,
                'stringarg': null
            }, {
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 5,
                'stringarg': null
            }, {
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': null,
                'stringarg': null
            }, {
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': 2,
                'stringarg': null
            }, {
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': 3,
                'stringarg': null
            }
        ]);

        optionsWithNone = fromJS([{
            'value': 'VISIT_DESCRIPTION',
            'intarg': null,
            'stringarg': null
        }, {
            'value': 'VISIT_NUMBER',
            'intarg': null,
            'stringarg': null
        }, {
            'value': 'NONE',
            'intarg': null,
            'stringarg': null
        }, {
            'value': 'STUDY_DEFINED_WEEK',
            'intarg': null,
            'stringarg': null
        }]);
    });

    describe('WHEN generating additional selection', () => {
        it('THEN gets no bin options', inject([XAxisLabelService], (service: XAxisLabelService) => {
            localOption = fromJS({
                'value': 'VISIT_DESCRIPTION',
                'intarg': null,
                'stringarg': null
            });
            expect(service.generateAdditionalSelection(localOption, options)).toEqual([]);
        }));
        it('THEN gets all numerical values if bin options', inject([XAxisLabelService], (service: XAxisLabelService) => {
            localOption = fromJS({
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 1,
                'stringarg': null
            });
            expect(service.generateAdditionalSelection(localOption, options)).toEqual([1, 2, 3, 4, 5]);
        }));
        it('THEN gets all numerical values if bin options counting null as no bin', inject([XAxisLabelService], (service: XAxisLabelService) => {
            localOption = fromJS({
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': null,
                'stringarg': null
            });
            expect(service.generateAdditionalSelection(localOption, options)).toEqual([2, 3]);
        }));
    });

    describe('WHEN generating available value strings', () => {
        it('THEN returns the distinct set of values and stringargs', inject([XAxisLabelService], (service: XAxisLabelService) => {
            expect(service.generateAvailableValueStrings(options)).toEqual(
                [
                    {
                        value: 'VISIT_DESCRIPTION',
                        stringarg: null,
                        optionValue: '{"value":"VISIT_DESCRIPTION","stringarg":null}',
                        valueStringArg: 'VISIT_DESCRIPTION'
                    },
                    {
                        value: 'VISIT_NUMBER',
                        stringarg: null,
                        optionValue: '{"value":"VISIT_NUMBER","stringarg":null}',
                        valueStringArg: 'VISIT_NUMBER'
                    },
                    {
                        value: 'STUDY_DEFINED_WEEK',
                        stringarg: null,
                        optionValue: '{"value":"STUDY_DEFINED_WEEK","stringarg":null}',
                        valueStringArg: 'STUDY_DEFINED_WEEK'
                    },
                    {
                        value: 'DAYS_SINCE_FIRST_TREATMENT',
                        stringarg: null,
                        optionValue: '{"value":"DAYS_SINCE_FIRST_TREATMENT","stringarg":null}',
                        valueStringArg: 'DAYS_SINCE_FIRST_TREATMENT'
                    },
                    {
                        value: 'WEEKS_SINCE_FIRST_TREATMENT',
                        stringarg: null,
                        optionValue: '{"value":"WEEKS_SINCE_FIRST_TREATMENT","stringarg":null}',
                        valueStringArg: 'WEEKS_SINCE_FIRST_TREATMENT'
                    }
                ]);
        }));

        describe('AND list of options contains NONE', () => {
            it('SHOULD return array with NONE option as the 1st', inject([XAxisLabelService], (service: XAxisLabelService) => {
                expect(service.generateAvailableValueStrings(optionsWithNone)).toEqual(
                    [
                        {
                            value: 'NONE',
                            stringarg: null,
                            optionValue: '{"value":"NONE","stringarg":null}',
                            valueStringArg: 'NONE'
                        },
                        {
                            value: 'VISIT_DESCRIPTION',
                            stringarg: null,
                            optionValue: '{"value":"VISIT_DESCRIPTION","stringarg":null}',
                            valueStringArg: 'VISIT_DESCRIPTION'
                        },
                        {
                            value: 'VISIT_NUMBER',
                            stringarg: null,
                            optionValue: '{"value":"VISIT_NUMBER","stringarg":null}',
                            valueStringArg: 'VISIT_NUMBER'
                        },
                        {
                            value: 'STUDY_DEFINED_WEEK',
                            stringarg: null,
                            optionValue: '{"value":"STUDY_DEFINED_WEEK","stringarg":null}',
                            valueStringArg: 'STUDY_DEFINED_WEEK'
                        }
                    ]
                );
            }));
        });
    });

    describe('WHEN getting options from value selection', () => {
        let event: any;
        it('THEN returns the option with lowest bin size', inject([XAxisLabelService], (service: XAxisLabelService) => {
            event = {
                target: {
                    value: '{"value": "DAYS_SINCE_FIRST_TREATMENT", "stringarg":null}'
                }
            };
            expect(service.optionFromValueSelection(event, options)).toEqual(fromJS({
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 1,
                'stringarg': null
            }));
        }));
        it('THEN returns the option with lowest bin size taking null as 1', inject([XAxisLabelService], (service: XAxisLabelService) => {
            event = {
                target: {
                    value: '{"value": "WEEKS_SINCE_FIRST_TREATMENT", "stringarg":null}'
                }
            };
            expect(service.optionFromValueSelection(event, options)).toEqual(fromJS({
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': null,
                'stringarg': null
            }));
        }));
    });

    describe('WHEN getting options from bin selection', () => {
        let event: any;
        it('THEN returns the option with matching bin size', inject([XAxisLabelService], (service: XAxisLabelService) => {
            localOption = fromJS({
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 1,
                'stringarg': null
            });
            event = {target: {value: '3'}};
            expect(service.optionFromAdditionalSelection(event, localOption, options)).toEqual(fromJS({
                'value': 'DAYS_SINCE_FIRST_TREATMENT',
                'intarg': 3,
                'stringarg': null
            }));
        }));
        it('THEN returns the null result if 1', inject([XAxisLabelService], (service: XAxisLabelService) => {
            localOption = fromJS({
                'value': 'WEEKS_SINCE_FIRST_TREATMENT',
                'intarg': null,
                'stringarg': null
            });
            event = {target: {value: '1'}};
            expect(service.optionFromAdditionalSelection(event, localOption, options)).toEqual(localOption);
        }));
    });
});
