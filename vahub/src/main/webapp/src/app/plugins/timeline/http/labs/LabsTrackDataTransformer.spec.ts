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
import {LabsTrackDataTransformer} from './LabsTrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord, ITrackTimePoint, TrackTimePointRecord} from '../../store/ITimeline';
import DateDayHour = InMemory.DateDayHour;
import SubjectLabsDetail = Request.SubjectLabsDetail;
import SubjectLabsCategories = Request.SubjectLabsCategories;
import Categories = Request.Categories;
import SubjectLabsSummary = Request.SubjectLabsSummary;
import LabsSummaryEvent = Request.LabsSummaryEvent;
import Labcodes = Request.Labcodes;


describe('GIVEN a LabsTrackDataTransformer', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                LabsTrackDataTransformer
            ]
        });
    });

    describe('WHEN transforming labs summary', () => {

        const simpleDataFromServer: SubjectLabsSummary[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectLabsSummary>{
                subjectId: 'subjectA',
                subject: null,
                events: [
                    <LabsSummaryEvent>{
                        start: <DateDayHour>{
                            date: {},
                            dayHour: 20.4,
                            dayHourAsString: '20d 09:36',
                            studyDayHourAsString: '21d 09:36'
                        },
                        visitNumber: 2,
                        numAboveReferenceRange: 1,
                        numBelowReferenceRange: 2,
                        numAboveSeverityThreshold: 3,
                        numBelowSeverityThreshold: 4
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([LabsTrackDataTransformer], (restDataTransformer: LabsTrackDataTransformer) => {
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
                                visitNumber: 2,
                                numAboveReferenceRange: 1,
                                numBelowReferenceRange: 2,
                                numAboveSeverityThreshold: 3,
                                numBelowSeverityThreshold: 4
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformLabsSummaryTrackData(simpleDataFromServer);
                expect(transformedData).toEqual(transformedDataArray);
            }));
    });

    describe('WHEN transforming labs category', () => {

        const simpleDataFromServer: SubjectLabsCategories[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectLabsCategories>{
                subjectId: 'subjectA',
                subject: null,
                labcodes: [
                    <Categories>{
                        category: 'chemistry',
                        events: [
                            {
                                start: <DateDayHour>{
                                    date: {},
                                    dayHour: 20.4,
                                    dayHourAsString: '20d 09:36',
                                    studyDayHourAsString: '21d 09:36',
                                },
                                visitNumber: 2,
                                numAboveReferenceRange: 1,
                                numBelowReferenceRange: 2,
                                numAboveSeverityThreshold: 3,
                                numBelowSeverityThreshold: 4
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([LabsTrackDataTransformer], (restDataTransformer: LabsTrackDataTransformer) => {
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
                                category: 'chemistry',
                                visitNumber: 2,
                                numAboveReferenceRange: 1,
                                numBelowReferenceRange: 2,
                                numAboveSeverityThreshold: 3,
                                numBelowSeverityThreshold: 4
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformLabsCategoryTrackData(simpleDataFromServer);
                expect(transformedData[0].data[0].metadata).toEqual(transformedDataArray[0].data[0].metadata);
            }));
    });

    describe('WHEN transforming labs detail', () => {

        const simpleDataFromServer: SubjectLabsDetail[] = [];

        beforeEach(() => {
            simpleDataFromServer.push(<SubjectLabsDetail>{
                subjectId: 'subjectA',
                subject: null,
                labcodes: [
                    <Labcodes>{
                        labcode: 'Lab 1',
                        refLow: 100,
                        refHigh: 200,
                        events: [
                            {
                                start: <DateDayHour>{
                                    date: {},
                                    dayHour: 20.4,
                                    dayHourAsString: '20d 09:36',
                                    studyDayHourAsString: '21d 09:36'
                                },
                                visitNumber: 2,
                                labcode: 'labcode1',
                                baselineValue: 10.2,
                                baselineFlag: true,
                                valueRaw: 12.3,
                                unitRaw: 'mg',
                                valueChangeFromBaseline: 12.4,
                                unitChangeFromBaseline: 'mg',
                                valuePercentChangeFromBaseline: 50.9,
                                unitPercentChangeFromBaseline: '%',
                                numAboveReferenceRange: 1,
                                numBelowReferenceRange: 2,
                                numAboveSeverityThreshold: 3,
                                numBelowSeverityThreshold: 4
                            }
                        ]
                    }
                ]
            });
        });

        it('THEN it should return the correct TrackData ',
            inject([LabsTrackDataTransformer], (restDataTransformer: LabsTrackDataTransformer) => {
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
                                code: 'Lab 1',
                                visitNumber: 2,
                                baselineValue: 10.2,
                                baselineFlag: true,
                                valueRaw: 12.3,
                                unit: 'mg',
                                valueChangeFromBaseline: 12.4,
                                unitChangeFromBaseline: 'mg',
                                valuePercentChangeFromBaseline: 50.9,
                                unitPercentChangeFromBaseline: '%',
                                referenceRangeHigherLimit: 200,
                                referenceRangeLowerLimit: 100,
                                numAboveReferenceRange: 1,
                                numBelowReferenceRange: 2,
                                numAboveSeverityThreshold: 3,
                                numBelowSeverityThreshold: 4
                            }

                        })
                    ]
                });

                const transformedData = restDataTransformer.transformLabsDetailTrackData(simpleDataFromServer);
                expect(transformedData[0].data[0].metadata).toEqual(transformedDataArray[0].data[0].metadata);
            }));
    });
});
