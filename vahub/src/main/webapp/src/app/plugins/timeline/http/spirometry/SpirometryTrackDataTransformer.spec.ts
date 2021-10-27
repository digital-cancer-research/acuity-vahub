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
import {SpirometryTrackDataTransformer} from './SpirometryTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import LungFunctionCodes = InMemory.LungFunctionCodes;
import DateDayHour = InMemory.DateDayHour;
import SubjectLungFunctionDetail = InMemory.SubjectLungFunctionDetail;
import SubjectLungFunctionSummary = InMemory.SubjectLungFunctionSummary;
import LungFunctionSummaryEvent = InMemory.LungFunctionSummaryEvent;

describe('GIVEN a SpirometryTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                SpirometryTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming spirometry summary', () => {

        const simpleDataFromServer: SubjectLungFunctionSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectLungFunctionSummary>{
                subjectId: 'subjectA',
                subject: null,
                baseline: <DateDayHour>{
                    date: {},
                    dayHour: 0,
                    dayHourAsString: '0d 00:00',
                    studyDayHourAsString: '1d 00:00'
                },
                sex: 'female',
                events: [
                    <LungFunctionSummaryEvent>{
                        start: <DateDayHour>{
                            date: {},
                            dayHour: 20.4,
                            dayHourAsString: '20d 09:36',
                            studyDayHourAsString: '21d 09:36'
                        },
                        visitNumber: 2,
                        maxValuePercentChange: 20
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([SpirometryTrackDataTransformer], (restDataTransformer: SpirometryTrackDataTransformer) => {
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
                            end: null,
                            metadata: {
                                baseline: <ITrackTimePoint>new TrackTimePointRecord({
                                    date: {},
                                    dayHour: 0,
                                    dayHourAsString: '0d 00:00',
                                    studyDayHourAsString: '1d 00:00'
                                }),
                                sex: 'female',
                                visitNumber: 2,
                                maxValuePercentChange: 20
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformSpirometrySummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming spirometry detail', () => {

        const simpleDataFromServer: SubjectLungFunctionDetail[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectLungFunctionDetail>{
                subjectId: 'subjectA',
                subject: null,
                sex: 'female',
                lungFunctionCodes: [
                    <LungFunctionCodes>{
                        code: 'Lab 1',
                        events: [
                            {
                                start: <DateDayHour>{
                                    date: {},
                                    dayHour: 20.4,
                                    dayHourAsString: '20d 09:36'
                                },
                                visitNumber: 2,
                                baselineValue: 10.2,
                                baselineFlag: true,
                                valueRaw: 12.3,
                                unitRaw: 'mg',
                                valueChangeFromBaseline: 12.4,
                                unitChangeFromBaseline: 'mg',
                                valuePercentChangeFromBaseline: 50.9,
                                unitPercentChangeFromBaseline: '%'
                            }
                        ],
                        baseline: <DateDayHour>{
                            date: {},
                            dayHour: 0,
                            dayHourAsString: '0d 00:00',
                            studyDayHourAsString: '1d 00:00'
                        },
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([SpirometryTrackDataTransformer], (restDataTransformer: SpirometryTrackDataTransformer) => {
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
                            end: null,
                            metadata: {
                                category: 'Lab 1',
                                code: 'Lab 1',
                                baseline: <ITrackTimePoint>new TrackTimePointRecord({
                                    date: {},
                                    dayHour: 0,
                                    dayHourAsString: '0d 00:00',
                                    studyDayHourAsString: '1d 00:00'
                                }),
                                sex: 'female',
                                visitNumber: 2,
                                baselineValue: 10.2,
                                baselineFlag: true,
                                value: 12.3,
                                valueRaw: 12.3,
                                unit: 'mg',
                                valueChangeFromBaseline: 12.4,
                                valuePercentChangeFromBaseline: 50.9,
                                unitPercentChangeFromBaseline: '%',
                                unitChangeFromBaseline: 'mg'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformSpirometryDetailTrackData(simpleDataFromServer);
                expect(transformedData[0].data[0].metadata).toEqual(transformedDataArray[0].data[0].metadata);
            }));
    });
});
