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
export class DoseTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformDoseSummaryTrackData(result: InMemory.SubjectDosingSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectDosingSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformDoseDetailTrackData(result: InMemory.SubjectDrugDosingSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectDosingDetailTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectDosingSummaryTrackData(result: InMemory.SubjectDosingSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        if (result.events.length > 0) {
            const sortedEvents = result.events.sort((e1, e2) => {
                return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
            });

            sortedEvents.forEach((event: InMemory.DosingSummaryEvent) => {
                trackData.push(this.transformSubjectDosingSummaryEvent(event));
            });

            const lastEvent = sortedEvents[sortedEvents.length - 1];

            if (result.ongoing) {
                trackData.push(this.transformOngoingSubjectDosingSummaryEvent(lastEvent));
            }
        }

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectDosingSummaryEvent(event: InMemory.DosingSummaryEvent): ITrackDataPoint {
        const percentChange: any = event.percentChange ? event.percentChange : {
            perAdmin: -100,
            perDay: -100
        };

        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                duration: event.duration,
                percentChange: percentChange,
                drugDoses: event.drugDoses,
                periodType: event.periodType,
                subsequentPeriodType: event.subsequentPeriodType
            }
        });
    }

    private transformOngoingSubjectDosingSummaryEvent(event: InMemory.DosingSummaryEvent): ITrackDataPoint {
        const ongoingDrugs: string[] = [];

        event.drugDoses.forEach((drugDose: InMemory.DoseAndFrequency) => {
            ongoingDrugs.push(drugDose.drug);
        });

        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING,
                ongoing: true,
                drugs: ongoingDrugs
            }
        });
    }

    private transformSubjectDosingDetailTrackData(result: InMemory.SubjectDrugDosingSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.drugs.forEach((drug: InMemory.DrugDosingSummary) => {

            if (drug.events.length > 0) {
                const sortedEvents = drug.events.sort((e1, e2) => {
                    return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
                });

                sortedEvents.forEach((event: InMemory.DosingSummaryEvent) => {
                    trackData.push(this.transformSubjectDosingDetailEvent(event, drug));
                });

                const lastEvent = sortedEvents[sortedEvents.length - 1];

                if (drug.ongoing) {
                    trackData.push(this.transformOngoingSubjectDosingDetailEvent(lastEvent, drug));
                }
            }
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectDosingDetailEvent(event: InMemory.DosingSummaryEvent, drug: InMemory.DrugDosingSummary): ITrackDataPoint {
        const percentChange: any = event.percentChange ? event.percentChange : {
            perAdmin: -100,
            perDay: -100
        };

        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                drug: drug.drug,
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                duration: event.duration,
                percentChange: percentChange,
                drugDoses: event.drugDoses,
                periodType: event.periodType,
                subsequentPeriodType: event.subsequentPeriodType
            }
        });
    }

    private transformOngoingSubjectDosingDetailEvent(event: InMemory.DosingSummaryEvent, drug: InMemory.DrugDosingSummary): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING,
                ongoing: true,
                drug: drug.drug
            }
        });
    }
}
