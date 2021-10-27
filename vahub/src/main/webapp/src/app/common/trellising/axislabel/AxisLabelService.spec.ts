import {TestBed, inject} from '@angular/core/testing';
import {AxisLabelService} from './AxisLabelService';
import {
    XAxisOptions, TimeScaleOptions, AEsYAxisOptions, MeasureNumberOptions, UnitOptions,
    DynamicAxis
} from '../store/ITrellising';
import {fromJS} from 'immutable';

describe('AxisLabelService class', () => {
    describe('WHEN closeOtherAxisLabels is called', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN it event is emmited', inject([AxisLabelService], (service: AxisLabelService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);
            service.axisLabelOpened.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );
            service.closeOtherAxisLabels();
            expect(validator.called).toHaveBeenCalledWith(null);
        }));
    });

    describe('WHEN selected time scale options is requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN it event is emmited', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.getSelectedTimeScaleOption(<any>XAxisOptions.DAYS_SINCE_FIRST_TREATMENT);
            expect(result).toEqual(TimeScaleOptions.DAYS_SINCE);
        }));
    });

    describe('WHEN selected time point options is requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN it event is emmited', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.getSelectedEpochTimePointOption(<any>XAxisOptions.DAYS_SINCE_FIRST_TREATMENT, <any>TimeScaleOptions.DAYS_SINCE);
            expect(result).toEqual('FIRST_TREATMENT');
        }));
    });

    describe('WHEN available measure number options are requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN it event is emmited', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.getAvailableMeasureNumberOptions([AEsYAxisOptions.COUNT_OF_SUBJECTS]);
            expect(result).toEqual([MeasureNumberOptions.SUBJECTS]);
        }));
    });

    describe('WHEN available unit options are requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN it event is emmited', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.getAvailableUnitOptions([AEsYAxisOptions.COUNT_OF_EVENTS]);
            expect(result).toEqual([UnitOptions.COUNT]);
        }));
    });

    describe('WHEN selected measurement option is requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('AND options contains word SUBJECT', () => {

            it('THEN subject option is returned', inject([AxisLabelService], (service: AxisLabelService) => {

                const result = service.getdMeasureNumberOptionByFullOption(AEsYAxisOptions.COUNT_OF_SUBJECTS);
                expect(result).toEqual(MeasureNumberOptions.SUBJECTS);
            }));
        });

        it('AND options contains word EVENT', () => {

            it('THEN event option is returned', inject([AxisLabelService], (service: AxisLabelService) => {

                const result = service.getdMeasureNumberOptionByFullOption(AEsYAxisOptions.COUNT_OF_EVENTS);
                expect(result).toEqual(MeasureNumberOptions.EVENTS);
            }));
        });
    });

    describe('WHEN selected unit option is requested', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('AND options contains word COUNT', () => {

            it('THEN count option is returned', inject([AxisLabelService], (service: AxisLabelService) => {

                const result = service.getUnitOptionByFullOption(AEsYAxisOptions.COUNT_OF_SUBJECTS);
                expect(result).toEqual(UnitOptions.COUNT);
            }));
        });

        it('AND options contains words PERCENT OF TOTAL', () => {

            it('THEN percent option is returned', inject([AxisLabelService], (service: AxisLabelService) => {

                const result = service.getUnitOptionByFullOption(AEsYAxisOptions.PERCENTAGE_OF_ALL_SUBJECTS);
                expect(result).toEqual(UnitOptions.PERCENT_OF_TOTAL);
            }));
        });

        it('AND options contains word PERCENT WITHIN PLOT', () => {

            it('THEN percent option is returned', inject([AxisLabelService], (service: AxisLabelService) => {

                const result = service.getUnitOptionByFullOption(AEsYAxisOptions.PERCENTAGE_OF_EVENTS_WITHIN_PLOT);
                expect(result).toEqual(UnitOptions.PERCENT_WITHIN_PLOT);
            }));
        });
    });

    describe('WHEN DynamicAxis is converted to string', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    AxisLabelService
                ]
            });

        });
        it('THEN result string is returned', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.fromDynamicAxisToString(fromJS({value: 'COUNTRY'}));
            expect(result).toEqual('COUNTRY');
        }));

        it('THEN undefined is returned if object is null', inject([AxisLabelService], (service: AxisLabelService) => {

            const result = service.fromDynamicAxisToString(null);
            expect(result).not.toBeDefined();
        }));
    });
});
