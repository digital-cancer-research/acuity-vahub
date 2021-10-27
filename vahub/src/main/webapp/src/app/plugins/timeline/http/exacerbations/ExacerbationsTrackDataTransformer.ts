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
import SubjectExacerbationSummary = InMemory.SubjectExacerbationSummary;
import ExacerbationSummaryEvent = InMemory.ExacerbationSummaryEvent;

@Injectable()
export class ExacerbationsTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformExacerbationsSummaryTrackData(result: SubjectExacerbationSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectExacerbationsSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectExacerbationsSummaryTrackData(result: SubjectExacerbationSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.events.forEach((event: ExacerbationSummaryEvent) => {
            trackData.push(this.transformExacerbationsSummaryEvent(event));
        });

        if (result.events.length > 0) {
            const sortedEvents = result.events.sort((e1, e2) => {
                return e1.end.dayHour > e2.end.dayHour ? 1 : ((e2.end.dayHour > e1.end.dayHour) ? -1 : 0);
            });
            const lastEvent = sortedEvents[sortedEvents.length - 1];
            if (lastEvent.ongoing) {
                trackData.push(this.transformOngoingExacerbationsSummaryEvent(lastEvent));
            }
        }

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformExacerbationsSummaryEvent(event: ExacerbationSummaryEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: this.transformDateDayHour(event.end),
            metadata: {
                ongoing: event.ongoing,
                imputedEndDate: event.imputedEndDate,
                duration: event.duration,

                numberOfDoseReceived: event.numberOfDoseReceived,
                severityGrade: event.severityGrade
            }
        });
    }

    private transformOngoingExacerbationsSummaryEvent(event: ExacerbationSummaryEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.end),
            end: null,
            metadata: {
                type: EventDateType.ONGOING,
                numberOfDoseReceived: event.numberOfDoseReceived,
                severityGrade: event.severityGrade
            }
        });
    }
}
