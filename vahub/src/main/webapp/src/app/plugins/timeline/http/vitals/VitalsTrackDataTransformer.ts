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
export class VitalsTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformVitalsSummaryTrackData(result: InMemory.SubjectVitalsSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectVitalsSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformVitalsDetailsTrackData(result: InMemory.SubjectVitalsDetail[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectVitalsDetailsTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectVitalsSummaryTrackData(result: InMemory.SubjectVitalsSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.events
            .filter((e) => e.start != null)
            .sort((e1, e2) => {
                return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0);
            }).forEach((event: InMemory.VitalsSummaryEvent) => {
            trackData.push(this.transformVitalsSummaryEvent(event, result.baseline, result.sex));
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformVitalsSummaryEvent(event: InMemory.VitalsSummaryEvent, baseline: InMemory.DateDayHour, sex: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                visitNumber: event.visitNumber,
                maxValuePercentChange: event.maxValuePercentChange
            }
        });
    }

    private transformSubjectVitalsDetailsTrackData(result: InMemory.SubjectVitalsDetail): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.tests.forEach((test: InMemory.VitalsTests) => {
            test.events
                .filter((e) => e.start != null)
                .sort((e1, e2) => {
                    return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0);
                }).forEach((event: InMemory.VitalsDetailEvent) => {
                if (event.valueRaw) {
                    // filter out null raw value events
                    trackData.push(this.transformSubjectVitalsDetailsEvent(event, test.baseline, result.sex, test.testName));
                }
            });
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectVitalsDetailsEvent(event: InMemory.VitalsDetailEvent,
                                               baseline: InMemory.DateDayHour,
                                               sex: string, testName: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                category: testName,
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                visitNumber: event.visitNumber,
                testName: testName,
                baselineValue: event.baselineValue,
                baselineFlag: event.baselineFlag,
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
