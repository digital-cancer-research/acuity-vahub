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
