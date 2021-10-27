import {inject, TestBed} from '@angular/core/testing';
import {TrackDataTransformer} from './TrackDataTransformer';
import {ITrackTimePoint, TrackTimePointRecord} from '../store/ITimeline';

describe('GIVEN a TrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming date day hour', () => {

        const simpleDataFromServer: Request.DateDayHour = <Request.DateDayHour>{
            date: null,
            dayHour: 11.5,
            dayHourAsString: '2016-05-12',
            studyDayHourAsString: '2016-05-13'
        };

        it('THEN it should just return the correct ITrackTimePoint ',
            inject([TrackDataTransformer], (restDataTransformer: TrackDataTransformer) => {
                const transformedDataArray: ITrackTimePoint = <ITrackTimePoint>new TrackTimePointRecord({
                    date: simpleDataFromServer.date,
                    dayHour: simpleDataFromServer.dayHour,
                    dayHourAsString: simpleDataFromServer.dayHourAsString,
                    studyDayHourAsString: simpleDataFromServer.studyDayHourAsString
                });

                const transformedData = restDataTransformer.transformDateDayHour(simpleDataFromServer);
                expect(transformedData.toJS()).toEqual(transformedDataArray.toJS());
            }));
    });
});
