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
