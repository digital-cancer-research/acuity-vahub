import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {TimelineConfigurationService} from './TracksConfigurationService';
import {
    SpirometryYAxisValue, EcgYAxisValue, VitalsYAxisValue,
    LabsYAxisValue, EcgWarnings
} from '../../store/ITimeline';

describe('GIVEN TimelineConfigurationService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                TimelineConfigurationService
            ]
        });
    });
    // describe('WHEN the initial configuation is set', () => {
    //     it('THEN configuration property is updated',
    //         inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
    //             let config = {
    //                 spirometryYAxisValue: SpirometryYAxisValue.RAW,
    //                 ecgYAxisValue: EcgYAxisValue.RAW,
    //                 ecgWarnings: {},
    //                 vitalsYAxisValue: VitalsYAxisValue.RAW,
    //                 labsYAxisValue: LabsYAxisValue.RAW,
    //             };
    //             timelineConfigurationService.setInitialConfiguration(config);
    //
    //             expect(timelineConfigurationService.configuration).toEqual(config);
    //         })
    //     );
    // });

    describe('WHEN the ECG warnings are updated', () => {
        it('THEN the event is emitted',
            inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
                spyOn(timelineConfigurationService.updateEcgWarnings, 'next');

                timelineConfigurationService.setEcgWarnings(<EcgWarnings>{});

                expect(timelineConfigurationService.updateEcgWarnings.next).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN the labs Y axis value is updated', () => {
        it('THEN the event is emitted',
            inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
                spyOn(timelineConfigurationService.updateLabsYAxisValue, 'next');

                timelineConfigurationService.setLabsYAxisValue(LabsYAxisValue.CHANGE_FROM_BASELINE);

                expect(timelineConfigurationService.updateLabsYAxisValue.next).toHaveBeenCalledWith(LabsYAxisValue.CHANGE_FROM_BASELINE);
            })
        );
    });

    describe('WHEN the spirometry Y axis value is updated', () => {
        it('THEN the event is emitted',
            inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
                spyOn(timelineConfigurationService.updateSpirometryYAxisValue, 'next');

                timelineConfigurationService.setSpirometryYAxisValue(SpirometryYAxisValue.CHANGE_FROM_BASELINE);

                expect(timelineConfigurationService.updateSpirometryYAxisValue.next).toHaveBeenCalledWith(SpirometryYAxisValue.CHANGE_FROM_BASELINE);
            })
        );
    });

    describe('WHEN the ecg Y axis value is updated', () => {
        it('THEN the event is emitted',
            inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
                spyOn(timelineConfigurationService.updateEcgYAxisValue, 'next');

                timelineConfigurationService.setEcgYAxisValue(EcgYAxisValue.CHANGE_FROM_BASELINE);

                expect(timelineConfigurationService.updateEcgYAxisValue.next).toHaveBeenCalledWith(EcgYAxisValue.CHANGE_FROM_BASELINE);
            })
        );
    });

    describe('WHEN the ecg Y axis value is updated', () => {
        it('THEN the event is emitted',
            inject([TimelineConfigurationService], (timelineConfigurationService: TimelineConfigurationService) => {
                spyOn(timelineConfigurationService.updateVitalsYAxisValue, 'next');

                timelineConfigurationService.setVitalsYAxisValue(VitalsYAxisValue.CHANGE_FROM_BASELINE);

                expect(timelineConfigurationService.updateVitalsYAxisValue.next).toHaveBeenCalledWith(VitalsYAxisValue.CHANGE_FROM_BASELINE);
            })
        );
    });
});
