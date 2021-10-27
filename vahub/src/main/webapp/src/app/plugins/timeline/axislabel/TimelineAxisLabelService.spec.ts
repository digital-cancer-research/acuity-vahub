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

import {TestBed, inject} from '@angular/core/testing';
import {fromJS} from 'immutable';
import {TimelineAxisLabelService} from './TimelineAxisLabelService';
import * as _ from 'lodash';
import {DynamicAxisRecord} from '../../../common/trellising/store/ITrellising';
import {DayZero} from '../store/ITimeline';

describe('GIVEN a TimelineAxisLabelService class', () => {
    let service, eventForDose, eventForTreatment, optionsWithDrugs, optionsWithoutDrugs;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TimelineAxisLabelService
            ]
        });
    });

    beforeEach(inject([TimelineAxisLabelService], (_service: TimelineAxisLabelService) => {
        service = _service;
        optionsWithDrugs = fromJS([{
            'value': DayZero.DAYS_SINCE_FIRST_DOSE,
            'intarg': null,
            'stringarg': null
        }, {
            'value': DayZero.DAYS_SINCE_RANDOMISATION,
            'intarg': null,
            'stringarg': null
        }, {
            'value': DayZero.DAYS_SINCE_FIRST_TREATMENT,
            'intarg': null,
            'stringarg': 'STDY4321'
        }, {
            'value': DayZero.DAYS_SINCE_FIRST_TREATMENT,
            'intarg': null,
            'stringarg': 'drugX'
        }]);

        optionsWithoutDrugs = fromJS([{
            'value': DayZero.DAYS_SINCE_FIRST_DOSE,
            'intarg': null,
            'stringarg': null
        }, {
            'value': DayZero.DAYS_SINCE_RANDOMISATION,
            'intarg': null,
            'stringarg': null
        }]);

        // Event mock
        eventForDose = {
            target: {
                value: `DAYS_SINCE_FIRST_DOSE`
            }
        };

        eventForTreatment = {
            target: {
                value: `STDY4321`
            }
        };
    }));

    describe('WHEN generating additional selection', () => {
        describe('AND option without drugs in in has been selected', () => {
            it('SHOULD return empty array', () => {
                const selection = service.generateAdditionalSelection(new DynamicAxisRecord({
                    value: DayZero.DAYS_SINCE_FIRST_DOSE,
                    intarg: null,
                    stringarg: null
                }), optionsWithDrugs);

                expect(_.isEmpty(selection)).toBeTruthy();
            });
        });
        describe('AND drugs have been applied', () => {
            it('SHOULD return not empty array', () => {
                const selection = service.generateAdditionalSelection(new DynamicAxisRecord({
                    value: DayZero.DAYS_SINCE_FIRST_TREATMENT,
                    intarg: null,
                    stringarg: 'drugX'
                }), optionsWithDrugs);

                expect(_.isEmpty(selection)).toBeFalsy();
            });
        });
    });

    describe('WHEN getting option by additional selection', () => {
        it('SHOULD return appropriate option', () => {
            const option = service.optionFromAdditionalSelection(eventForTreatment, new DynamicAxisRecord({
                value: 'DAYS_SINCE_FIRST_TREATMENT',
                intarg: null,
                stringarg: 'STDY4321'
            }), optionsWithDrugs);
            expect(option.get('stringarg')).toEqual('STDY4321');
        });
    });
});
