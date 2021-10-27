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

import {Injectable} from '@angular/core';
import {TrackDataTransformer} from '../TrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord} from '../../store/ITimeline';

@Injectable()
export class EcgTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformEcgSummaryTrackData(result: InMemory.SubjectEcgSummary[]): TrackData[] {
        return <TrackData[]>result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectEcgSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformEcgDetailsTrackData(result: InMemory.SubjectEcgDetail[]): TrackData[] {
        return <TrackData[]>result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectEcgDetailsTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectEcgSummaryTrackData(result: InMemory.SubjectEcgSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];
        if (result.events.length > 0) {
            result.events.sort((e1, e2) => {
                return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0);
            });
        }
        result.events.forEach((event: InMemory.EcgSummaryEvent) => {
            trackData.push(this.transformEcgSummaryEvent(event, result.baseline, result.sex));
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformEcgSummaryEvent(event: InMemory.EcgSummaryEvent, baseline: InMemory.DateDayHour, sex: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                abnormality: event.abnormality,
                significant: event.significant,
                visitNumber: event.visitNumber,

                maxValuePercentChange: event.maxValuePercentChange,
                qtcfChange: event.qtcfChange,
                qtcfValue: event.qtcfValue,
                qtcfUnit: event.qtcfUnit

            }
        });
    }

    private transformSubjectEcgDetailsTrackData(result: InMemory.SubjectEcgDetail): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.tests.forEach((test: InMemory.EcgTest) => {
            if (test.events.length > 0) {
                test.events.sort((e1, e2) => {
                    return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0);
                });
            }
            test.events.forEach((event: InMemory.EcgDetailEvent) => {
                trackData.push(this.transformSubjectEcgDetailsEvent(event, test.baseline, result.sex, test.testName));
            });
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectEcgDetailsEvent(event: InMemory.EcgDetailEvent, baseline: InMemory.DateDayHour,
                                            sex: string, testName: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                abnormality: event.abnormality,
                significant: event.significant,
                visitNumber: event.visitNumber,

                testName: testName,
                baselineFlag: event.baselineFlag,
                baselineValue: event.baselineValue,
                unitChangeFromBaseline: event.unitChangeFromBaseline,
                unitPercentChangeFromBaseline: event.unitPercentChangeFromBaseline,
                unitRaw: event.unitRaw,
                valueChangeFromBaseline: event.valueChangeFromBaseline,
                valuePercentChangeFromBaseline: event.valuePercentChangeFromBaseline,
                valueRaw: event.valueRaw,
                value: event.valueRaw

            }
        });
    }
}
