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
import {ConmedsTrackDataTransformer} from './ConmedsTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import {EventDateType} from '../../chart/IChartEvent';
import SubjectConmedByDrug = InMemory.SubjectConmedByDrug;
import DateDayHour = InMemory.DateDayHour;
import SubjectConmedSummary = InMemory.SubjectConmedSummary;
import SubjectConmedByClass = InMemory.SubjectConmedByClass;

describe('GIVEN a ConmedsTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                ConmedsTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming conmeds summary', () => {

        const simpleDataFromServer: SubjectConmedSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectConmedSummary>{
                subjectId: 'subjectA',
                subject: null,
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
                        numberOfConmeds: 5,
                        conmeds: [
                            {
                                conmed: 'conmed1',
                                doses: [
                                    80.0
                                ],
                                frequencies: null,
                                indications: [
                                    'BONEMETASTASIS'
                                ]
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([ConmedsTrackDataTransformer], (restDataTransformer: ConmedsTrackDataTransformer) => {
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
                                numberOfConmeds: 5,
                                conmeds: [
                                    {
                                        conmed: 'conmed1',
                                        doses: [
                                            80.0
                                        ],
                                        frequencies: null,
                                        indications: [
                                            'BONEMETASTASIS'
                                        ]
                                    }
                                ]
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

                const transformedData = restDataTransformer.transformConmedsSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming conmeds by class data', () => {

        const simpleDataFromServer: SubjectConmedByClass[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectConmedByClass>{
                subjectId: 'subjectA',
                subject: null,
                conmedClasses: [
                    {
                        conmedClass: 'Anilides',
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
                                numberOfConmeds: 2,
                                conmeds: [
                                    {
                                        conmed: 'conmed1',
                                        doses: [
                                            80.0
                                        ],
                                        frequencies: null,
                                        indications: [
                                            'BONEMETASTASIS'
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([ConmedsTrackDataTransformer], (restDataTransformer: ConmedsTrackDataTransformer) => {
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
                                numberOfConmeds: 2,
                                conmeds: [
                                    {
                                        conmed: 'conmed1',
                                        doses: [
                                            80.0
                                        ],
                                        frequencies: null,
                                        indications: [
                                            'BONEMETASTASIS'
                                        ]
                                    }
                                ],
                                conmedClass: 'Anilides'
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
                                conmedClass: 'Anilides'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformConmedsClassTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming conmeds by drug data', () => {

        const simpleDataFromServer: SubjectConmedByDrug[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectConmedByDrug>{
                subjectId: 'subjectA',
                subject: null,
                conmedMedications: [
                    {
                        conmedMedication: 'Anilides',
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
                                conmed: 'conmed1',
                                frequency: null,
                                indication: 'BONEMETASTASIS',
                                dose: 80.0,
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([ConmedsTrackDataTransformer], (restDataTransformer: ConmedsTrackDataTransformer) => {
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
                                conmed: 'conmed1',
                                dose: 80.0,
                                frequency: null,
                                indication: 'BONEMETASTASIS',
                                conmedMedication: 'Anilides'
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
                                conmedMedication: 'Anilides'
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformConmedsDrugTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });
});
