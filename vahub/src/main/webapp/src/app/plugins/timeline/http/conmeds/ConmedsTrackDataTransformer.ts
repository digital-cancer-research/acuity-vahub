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
import {EventDateType} from '../../chart/IChartEvent';

@Injectable()
export class ConmedsTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformConmedsSummaryTrackData(result: InMemory.SubjectConmedSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectConmedssSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformConmedsClassTrackData(result: InMemory.SubjectConmedByClass[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectConmedsByClassTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformConmedsDrugTrackData(result: InMemory.SubjectConmedByDrug[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectConmedsByDrugTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectConmedssSummaryTrackData(result: InMemory.SubjectConmedSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.events.forEach((event: InMemory.ConmedSummaryEvent) => {
            trackData.push(this.transformConmedsSummaryEvent(event));
        });

        if (result.events.length > 0) {
            const sortedEvents = result.events.sort((e1, e2) => {
                return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
            });
            const lastEvent = sortedEvents[sortedEvents.length - 1];
            if (lastEvent.ongoing) {
                trackData.push(this.transformOngoingConmedSummaryEvent(lastEvent));
            }
        }

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformConmedsSummaryEvent(event: InMemory.ConmedSummaryEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                numberOfConmeds: event.numberOfConmeds,
                duration: event.duration,
                conmeds: event.conmeds
            }
        });
    }

    private transformOngoingConmedSummaryEvent(event: InMemory.ConmedSummaryEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING
            }
        });
    }

    private transformSubjectConmedsByClassTrackData(result: InMemory.SubjectConmedByClass): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.conmedClasses.forEach((conmedClass: InMemory.ConmedEventsByClass) => {
            conmedClass.events.forEach((event: InMemory.ConmedSummaryEvent) => {
                trackData.push(this.transformSubjectConmedsByClassEvent(event, conmedClass.conmedClass));
            });

            if (conmedClass.events.length > 0) {
                const sortedEvents = conmedClass.events.sort((e1, e2) => {
                    return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
                });
                const lastEvent = sortedEvents[sortedEvents.length - 1];
                if (lastEvent.ongoing) {
                    trackData.push(this.transformOngoingSubjectConmedsByClassEvent(lastEvent, conmedClass.conmedClass));
                }
            }
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectConmedsByClassEvent(event: InMemory.ConmedSummaryEvent, conmedClass: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                duration: event.duration,
                numberOfConmeds: event.numberOfConmeds,
                conmeds: event.conmeds,
                conmedClass: conmedClass
            }
        });
    }

    private transformOngoingSubjectConmedsByClassEvent(event: InMemory.ConmedSummaryEvent, conmedClass: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING,
                conmedClass: conmedClass
            }
        });
    }

    private transformSubjectConmedsByDrugTrackData(result: InMemory.SubjectConmedByDrug): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.conmedMedications.forEach((conmedMedication: InMemory.ConmedEventsByDrug) => {
            conmedMedication.events.forEach((event: InMemory.ConmedSingleEvent) => {
                trackData.push(this.transformSubjectConmedsByDrugEvent(event, conmedMedication.conmedMedication));
            });

            if (conmedMedication.events.length > 0) {
                const sortedEvents = conmedMedication.events.sort((e1, e2) => {
                    return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
                });
                const lastEvent = sortedEvents[sortedEvents.length - 1];
                if (lastEvent.ongoing) {
                    trackData.push(this.transformOngoingSubjectConmedsByDrugEvent(lastEvent, conmedMedication.conmedMedication));
                }
            }
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectConmedsByDrugEvent(event: InMemory.ConmedSingleEvent, conmedMedication: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                duration: event.duration,
                conmed: event.conmed,
                dose: event.dose,
                frequency: event.frequency,
                indication: event.indication,
                conmedMedication: conmedMedication
            }
        });
    }

    private transformOngoingSubjectConmedsByDrugEvent(event: InMemory.ConmedSingleEvent, conmedMedication: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING,
                conmedMedication: conmedMedication
            }
        });
    }
}
