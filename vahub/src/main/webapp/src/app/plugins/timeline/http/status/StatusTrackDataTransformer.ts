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
