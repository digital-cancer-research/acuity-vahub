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
import {
    TrackDataPointRecord,
    ITrackDataPoint
} from '../../store/ITimeline';
import {EventDateType} from '../../chart/IChartEvent';

@Injectable()
export class StatusTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformStatusTrackData(result: InMemory.SubjectStatusSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectStatusTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformSubjectStatusTrackData(apiTrackData: InMemory.SubjectStatusSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        apiTrackData.phases.forEach(phase => {
            trackData.push(this.transformPhaseData(phase, apiTrackData.drugs));
        });

        // completion
        if (apiTrackData.completion) {
            trackData.push(this.transformDatePoint(EventDateType.WITHDRAWAL_COMPLETION, apiTrackData.completion));
        }

        // death
        if (apiTrackData.death) {
            trackData.push(this.transformDatePoint(EventDateType.DEATH, apiTrackData.death));
        }

        // first treatment
        if (apiTrackData.firstTreatment) {
            trackData.push(this.transformDatePoint(EventDateType.FIRST_DOSE, apiTrackData.firstTreatment));
        }

        // first visit
        if (apiTrackData.firstVisit) {
            trackData.push(this.transformDatePoint(EventDateType.SCREENING_VISIT, apiTrackData.firstVisit));
        }

        // last treatment
        if (apiTrackData.lastTreatment) {
            trackData.push(this.transformDatePoint(EventDateType.LAST_DOSE, apiTrackData.lastTreatment));
        }

        // last visit
        if (apiTrackData.lastVisit) {
            trackData.push(this.transformDatePoint(EventDateType.LAST_VISIT, apiTrackData.lastVisit));
        }

        // randomisation
        if (apiTrackData.randomisation) {
            trackData.push(this.transformDatePoint(EventDateType.RANDOMISATION, apiTrackData.randomisation));
        }

        // ongoing
        if (apiTrackData.ongoing) {
            trackData.push(this.transformDatePoint(EventDateType.ONGOING, apiTrackData.ongoing));
        }

        return {
            subjectId: apiTrackData.subjectId,
            data: trackData
        };
    }

    transformPhaseData(phase: InMemory.StudyPhase, drugs: string[]): ITrackDataPoint {
        return <ITrackDataPoint> new TrackDataPointRecord({
            start: this.transformDateDayHour(phase.start),
            end: this.transformDateDayHour(phase.end),
            metadata: {
                type: this.transformStatusType(phase.phaseType),
                duration: phase.duration,
                drugs: drugs
            }
        });
    }

    transformDatePoint(type: EventDateType, dateDayHour: InMemory.DateDayHour): ITrackDataPoint {
        return <ITrackDataPoint> new TrackDataPointRecord({
            start: this.transformDateDayHour(dateDayHour),
            end: null,
            metadata: {
                type: type,
            }
        });
    }

    transformStatusType(type: string): EventDateType {
        switch (type) {
            case 'RUN_IN':
                return EventDateType.RUN_IN;
            case 'RANDOMISED_DRUG':
                return EventDateType.RANDOMIZED_DRUG;
            case 'ON_STUDY_DRUG':
                return EventDateType.ON_STUDY_DRUG;
            default:
                return undefined;
        }
    }
}
