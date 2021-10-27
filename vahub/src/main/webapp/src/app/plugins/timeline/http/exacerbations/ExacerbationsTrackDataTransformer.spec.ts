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
import {ExacerbationsTrackDataTransformer} from './ExacerbationsTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import {EventDateType} from '../../chart/IChartEvent';
import SubjectExacerbationSummary = InMemory.SubjectExacerbationSummary;

describe('GIVEN a ExacerbationsTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                ExacerbationsTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming exacerbations summary', () => {

        const simpleDataFromServer: SubjectExacerbationSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectExacerbationSummary>{
                subjectId: 'subjectA',
                subject: null,
                events: [
                    {
                        start: <Request.DateDayHour>{
                            date: {},
                            dayHour: 20.4,
                            dayHourAsString: '20d 09:36',
                            studyDayHourAsString: '21d 09:36'
                        },
                        end: <Request.DateDayHour>{
                            date: {},
                            dayHour: 21.4,
                            dayHourAsString: '21d 09:36',
                            studyDayHourAsString: '22d 09:36'
                        },
                        ongoing: true,
                        imputedEndDate: true,
                        duration: 0.0,
                        numberOfDoseReceived: 3,
                        severityGrade: 'MILD'
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([ExacerbationsTrackDataTransformer], (restDataTransformer: ExacerbationsTrackDataTransformer) => {
                const transformedDataArray: TrackData[] = [];
                transformedDataArray.push(<TrackData>{
                    subjectId: 'subjectA',
                    data: [
                        <ITrackDataPoint>new TrackDataPointRecord({
                            start: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 20.4,
                                dayHourAsString: '20d 09:36',
                                studyDayHourAsString: '21d 09:36'
                            }),
                            end: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 21.4,
                                dayHourAsString: '21d 09:36',
                                studyDayHourAsString: '22d 09:36'
                            }),
                            metadata: {
                                ongoing: true,
                                imputedEndDate: true,
                                duration: 0.0,
                                numberOfDoseReceived: 3,
                                severityGrade: 'MILD'
                            }

                        }),
                        <ITrackDataPoint>new TrackDataPointRecord({
                            start: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 21.4,
                                dayHourAsString: '21d 09:36',
                                studyDayHourAsString: '22d 09:36'
                            }),
                            end: null,
                            metadata: {
                                type: EventDateType.ONGOING,
                                numberOfDoseReceived: 3,
                                severityGrade: 'MILD'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformExacerbationsSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });
});
