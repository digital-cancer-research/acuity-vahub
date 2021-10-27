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
import {AesTrackDataTransformer} from './AesTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import {EventDateType} from '../../chart/IChartEvent';

describe('GIVEN a AesTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                AesTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming aes summary', () => {

        const simpleDataFromServer: InMemory.SubjectAesSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<InMemory.SubjectAesSummary>{
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
                        maxSeverityGradeNum: 1,
                        maxSeverityGrade: 'MILD',
                        numberOfEvents: 5,
                        pts: ['pt1', 'pt2']
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([AesTrackDataTransformer], (restDataTransformer: AesTrackDataTransformer) => {
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
                                maxSeverityGradeNum: 1,
                                maxSeverityGrade: 'MILD',
                                numberOfEvents: 5,
                                pts: ['pt1', 'pt2']
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
                                type: EventDateType.ONGOING
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformAesSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming aes details', () => {

        const simpleDataFromServer: InMemory.SubjectAesDetail[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<InMemory.SubjectAesDetail>{
                subjectId: 'subjectA',
                subject: null,
                aes: [
                    {
                        pt: 'pt1',
                        hlt: 'hlt1',
                        soc: 'soc1',
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
                                actionTaken: 'none',
                                causality: 'related',
                                serious: 'n',
                                severityGradeNum: 1,
                                severityGrade: 'MILD',
                                pt: 'pt1'
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([AesTrackDataTransformer], (restDataTransformer: AesTrackDataTransformer) => {
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
                                severityGradeNum: 1,
                                severityGrade: 'MILD',
                                pt: 'pt1',
                                hlt: 'hlt1',
                                soc: 'soc1',
                                actionTaken: 'none',
                                causality: 'related',
                                serious: 'n'
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
                                pt: 'pt1',
                                hlt: 'hlt1',
                                soc: 'soc1'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformAesDetailTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });
});
