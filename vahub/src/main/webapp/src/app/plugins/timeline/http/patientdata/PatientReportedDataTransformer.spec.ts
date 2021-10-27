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
import {PatientDataTrackDataTransformer} from './PatientDataTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';

describe('GIVEN a PatientReportedDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                PatientDataTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming patient data summary', () => {

        const simpleDataFromServer: InMemory.SubjectPatientDataSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<InMemory.SubjectPatientDataSummary>{
                subjectId: 'subjectA',
                subject: null,
                events: [
                    <InMemory.PatientDataEvent>{
                        start: <Request.DateDayHour>{
                            date: {},
                            dayHour: 13.45139,
                            dayHourAsString: '13d 10:50',
                            doseDayHour: 13.45139,
                            studyDayHourAsString: '14d 10:50'
                        },
                        numberOfEvents: 1,
                        details: [{
                            measurementName: 'Daily step count',
                            value: 110.0,
                            unit: 'steps',
                            startDate: {
                                date: {},
                                dayHour: 13.45139,
                                dayHourAsString: '13d 10:50',
                                doseDayHour: 13.45139,
                                studyDayHourAsString: '14d 10:50'
                            }
                        }]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([PatientDataTrackDataTransformer], (restDataTransformer: PatientDataTrackDataTransformer) => {
                const transformedDataArray: TrackData[] = [];
                transformedDataArray.push(<TrackData>{
                    subjectId: 'subjectA',
                    data: [
                        <ITrackDataPoint>new TrackDataPointRecord({
                            start: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 13.45139,
                                dayHourAsString: '13d 10:50',
                                doseDayHour: 13.45139,
                                studyDayHourAsString: '14d 10:50'
                            }),
                            end: null,
                            metadata: {
                                numberOfEvents: 1,
                                details: [{
                                    measurementName: 'Daily step count',
                                    value: 110.0,
                                    unit: 'steps',
                                    startDate: {
                                        date: {},
                                        dayHour: 13.45139,
                                        dayHourAsString: '13d 10:50',
                                        doseDayHour: 13.45139,
                                        studyDayHourAsString: '14d 10:50'
                                    }
                                }]
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformPatientDataSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming patient data details', () => {

        const simpleDataFromServer: InMemory.SubjectPatientDataDetail[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<InMemory.SubjectPatientDataDetail>{
                subjectId: 'subjectA',
                subject: null,
                tests: [
                    {
                        testName: 'Daily step count',
                        details: [{
                            startDate: {
                                date: {},
                                dayHour: 14.40973,
                                dayHourAsString: '14d 09:50',
                                doseDayHour: 14.40973,
                                studyDayHourAsString: '15d 09:50'
                            },
                            value: 110.0,
                            unit: 'steps'
                        }]
                    },
                    {
                        testName: 'Fatigue Score',
                        details: [{
                            startDate: {
                                date: {},
                                dayHour: 14.45141,
                                dayHourAsString: '14d 10:50',
                                doseDayHour: 14.45141,
                                studyDayHourAsString: '15d 10:50'
                            },
                            value: 0.3,
                            unit: null
                        }]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([PatientDataTrackDataTransformer], (restDataTransformer: PatientDataTrackDataTransformer) => {
                const transformedDataArray: TrackData[] = [];
                transformedDataArray.push(<TrackData>{
                    subjectId: 'subjectA',
                    data: [
                        <ITrackDataPoint>new TrackDataPointRecord({
                            start: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 14.40973,
                                dayHourAsString: '14d 09:50',
                                doseDayHour: 14.40973,
                                studyDayHourAsString: '15d 09:50'
                            }),
                            end: null,
                            metadata: {
                                testName: 'Daily step count',
                                unit: 'steps',
                                value: 110.0
                            }
                        }),
                        <ITrackDataPoint>new TrackDataPointRecord({
                            start: <ITrackTimePoint>new TrackTimePointRecord({
                                date: {},
                                dayHour: 14.45141,
                                dayHourAsString: '14d 10:50',
                                doseDayHour: 14.45141,
                                studyDayHourAsString: '15d 10:50'
                            }),
                            end: null,
                            metadata: {
                                testName: 'Fatigue Score',
                                unit: null,
                                value: 0.3
                            }
                        })
                    ]
                });

                const transformedData = restDataTransformer.transformPatientDataDetailsTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

});
