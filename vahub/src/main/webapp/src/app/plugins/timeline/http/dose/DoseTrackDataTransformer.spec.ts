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
import {DoseTrackDataTransformer} from './DoseTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import {EventDateType} from '../../chart/IChartEvent';
import DoseAndFrequency = InMemory.DoseAndFrequency;
import DateDayHour = InMemory.DateDayHour;
import SubjectDosingSummary = InMemory.SubjectDosingSummary;
import SubjectDrugDosingSummary = InMemory.SubjectDrugDosingSummary;

describe('GIVEN a DoseTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                DoseTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming dose summary', () => {

        const simpleDataFromServer: SubjectDosingSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectDosingSummary>{
                subjectId: 'subjectA',
                subject: null,
                ongoing: true,
                events: [
                    {
                        start: <DateDayHour>{
                            date: {},
                            dayHour: 20.4,
                            dayHourAsString: '20d 09:36',
                            studyDayHourAsString: '21d 09:36'
                        },
                        end: <DateDayHour>{
                            date: {},
                            dayHour: 21.4,
                            dayHourAsString: '21d 09:36',
                            studyDayHourAsString: '22d 09:36'
                        },
                        ongoing: true,
                        imputedEndDate: true,
                        duration: 0.0,
                        // inactive: false,
                        percentChange: {
                            perDay: -50,
                            perAdmin: -50
                        },
                        drugDoses: [
                            <DoseAndFrequency>{
                                drug: 'drug 1',
                                dose: 10,
                                dosePerAdmin: -50,
                                dosePerDay: -10,
                                doseUnit: 'mg',
                                frequency: {
                                    name: 'QD',
                                    rank: 1
                                }
                            },
                            <DoseAndFrequency>{
                                drug: 'drug 2',
                                dose: 40,
                                doseUnit: 'mg',
                                dosePerAdmin: -50,
                                dosePerDay: -10,
                                frequency: {
                                    name: 'QD',
                                    rank: 1
                                }
                            }
                        ],
                        periodType: 'ACTIVE',
                        subsequentPeriodType: 'INACTIVE'
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([DoseTrackDataTransformer], (restDataTransformer: DoseTrackDataTransformer) => {
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
                                // inactive: false,
                                percentChange: {
                                    perDay: -50,
                                    perAdmin: -50
                                },
                                drugDoses: [
                                    <DoseAndFrequency>{
                                        drug: 'drug 1',
                                        dose: 10,
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        doseUnit: 'mg',
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    },
                                    <DoseAndFrequency>{
                                        drug: 'drug 2',
                                        dose: 40,
                                        doseUnit: 'mg',
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    }
                                ],
                                periodType: 'ACTIVE',
                                subsequentPeriodType: 'INACTIVE'
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
                                ongoing: true,
                                drugs: ['drug 1', 'drug 2']
                            }
                        })
                    ]
                });

                const transformedData = restDataTransformer.transformDoseSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming dose details', () => {

        const simpleDataFromServer: SubjectDrugDosingSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectDrugDosingSummary>{
                subjectId: 'subjectA',
                subject: null,
                drugs: [
                    {
                        drug: 'drug 1',
                        ongoing: true,
                        events: [
                            {
                                start: <DateDayHour>{
                                    date: {},
                                    dayHour: 20.4,
                                    dayHourAsString: '20d 09:36',
                                    studyDayHourAsString: '21d 09:36'
                                },
                                end: <DateDayHour>{
                                    date: {},
                                    dayHour: 21.4,
                                    dayHourAsString: '21d 09:36',
                                    studyDayHourAsString: '22d 09:36'
                                },
                                ongoing: true,
                                imputedEndDate: true,
                                duration: 0.0,
                                // inactive: false,
                                percentChange: {
                                    perDay: -50,
                                    perAdmin: -50
                                },
                                drugDoses: [
                                    <DoseAndFrequency>{
                                        drug: 'drug 1',
                                        dose: 10,
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        doseUnit: 'mg',
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    },
                                    <DoseAndFrequency>{
                                        drug: 'drug 2',
                                        dose: 40,
                                        doseUnit: 'mg',
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    }
                                ],
                                periodType: 'ACTIVE',
                                subsequentPeriodType: 'INACTIVE'
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([DoseTrackDataTransformer], (restDataTransformer: DoseTrackDataTransformer) => {
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
                                drug: 'drug 1',
                                ongoing: true,
                                imputedEndDate: true,
                                duration: 0.0,
                                // inactive: false,
                                percentChange: {
                                    perDay: -50,
                                    perAdmin: -50
                                },
                                drugDoses: [
                                    <DoseAndFrequency>{
                                        drug: 'drug 1',
                                        dose: 10,
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        doseUnit: 'mg',
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    },
                                    <DoseAndFrequency>{
                                        drug: 'drug 2',
                                        dose: 40,
                                        doseUnit: 'mg',
                                        dosePerAdmin: -50,
                                        dosePerDay: -10,
                                        frequency: {
                                            name: 'QD',
                                            rank: 1
                                        }
                                    }
                                ],
                                periodType: 'ACTIVE',
                                subsequentPeriodType: 'INACTIVE'
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
                                ongoing: true,
                                type: EventDateType.ONGOING,
                                drug: 'drug 1'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformDoseDetailTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });
});
