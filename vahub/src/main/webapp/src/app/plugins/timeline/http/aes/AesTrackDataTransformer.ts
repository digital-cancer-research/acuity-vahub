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
import {EMPTY} from '../../../../common/trellising/store';

@Injectable()
export class AesTrackDataTransformer extends TrackDataTransformer {
    private readonly DEATH = 'DEATH';
    private readonly WITHDRAWAL = 'WITHDRAWAL';
    private readonly LAST_VISIT = 'LAST_VISIT';

    constructor() {
        super();
    }

    transformAesSummaryTrackData(result: InMemory.SubjectAesSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectAesSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformAesDetailTrackData(result: InMemory.SubjectAesDetail[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectAesDetailTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectAesSummaryTrackData(result: InMemory.SubjectAesSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        if (result.events.length > 0) {
            result.events.forEach((event: InMemory.AeMaxCtcEvent) => {
                trackData.push(this.transformSubjectAeSummaryEvent(event));
            });

            const sortedEvents = result.events.sort((e1, e2) => {
                return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
            });
            const lastEvent = sortedEvents[sortedEvents.length - 1];
            if (lastEvent.ongoing) {
                trackData.push(this.transformNonDurationalSubjectAeSummaryEvent(lastEvent, EventDateType.ONGOING));
            }
            if (lastEvent.endType === this.DEATH) {
                trackData.push(this.transformNonDurationalSubjectAeSummaryEvent(lastEvent, EventDateType.DEATH));
            }
            if (lastEvent.endType === this.WITHDRAWAL) {
                trackData.push(this.transformNonDurationalSubjectAeSummaryEvent(lastEvent, EventDateType.WITHDRAWAL_COMPLETION));
            }
            if (lastEvent.endType === this.LAST_VISIT) {
                trackData.push(this.transformNonDurationalSubjectAeSummaryEvent(lastEvent, EventDateType.LAST_VISIT_END));
            }
        }

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformNonDurationalSubjectAeSummaryEvent(event: InMemory.AeMaxCtcEvent, eventType: EventDateType): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: eventType === EventDateType.LAST_VISIT_END ? 'Visit ' + event['lastVisitNumber'] : eventType
            }
        });
    }

    private transformSubjectAeSummaryEvent(event: InMemory.AeMaxCtcEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                maxSeverityGradeNum: event.maxSeverityGradeNum,
                maxSeverityGrade: event.maxSeverityGrade,
                numberOfEvents: event.numberOfEvents,
                duration: event.duration,
                pts: event.pts
            }
        });
    }

    private transformSubjectAesDetailTrackData(result: InMemory.SubjectAesDetail): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.aes.forEach((ae: InMemory.AeDetail) => {
            ae.events.forEach((event: InMemory.AeDetailEvent) => {
                trackData.push(this.transformSubjectAeDetailEvent(event, ae));
            });

            if (ae.events.length > 0) {
                const sortedEvents = ae.events.sort((e1, e2) => {
                    return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
                });
                const lastEvent = sortedEvents[sortedEvents.length - 1];
                if (lastEvent.ongoing) {
                    trackData.push(this.transformNonDurationalAeDetailEvent(lastEvent, ae, EventDateType.ONGOING));
                }
                if (lastEvent.endType === this.DEATH) {
                    trackData.push(this.transformNonDurationalAeDetailEvent(lastEvent, ae, EventDateType.DEATH));
                }
                if (lastEvent.endType === this.WITHDRAWAL) {
                    trackData.push(this.transformNonDurationalAeDetailEvent(lastEvent, ae, EventDateType.WITHDRAWAL_COMPLETION));
                }
                if (lastEvent.endType === this.LAST_VISIT) {
                    trackData.push(this.transformNonDurationalAeDetailEvent(lastEvent, ae, EventDateType.LAST_VISIT_END));
                }
            }
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectAeDetailEvent(event: InMemory.AeDetailEvent, ae: InMemory.AeDetail): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                severityGradeNum: event.severityGradeNum,
                severityGrade: event.severityGrade,
                duration: event.duration,
                actionTaken: event.actionTaken,
                causality: event.causality,
                serious: event.serious,
                pt: event.pt === null ? EMPTY : event.pt,
                hlt: ae.hlt === null ? EMPTY : ae.hlt,
                soc: ae.soc === null ? EMPTY : ae.soc
            }
        });
    }

    private transformNonDurationalAeDetailEvent(event: InMemory.AeDetailEvent, ae: InMemory.AeDetail, eventType: EventDateType) {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: eventType === EventDateType.LAST_VISIT_END ? 'Visit ' + event['lastVisitNumber'] : eventType,
                pt: event.pt === null ? EMPTY : event.pt,
                hlt: ae.hlt === null ? EMPTY : ae.hlt,
                soc: ae.soc === null ? EMPTY : ae.soc
            }
        });
    }
}
