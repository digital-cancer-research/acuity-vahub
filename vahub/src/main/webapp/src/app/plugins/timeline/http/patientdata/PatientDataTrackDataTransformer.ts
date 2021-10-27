import {Injectable} from '@angular/core';
import {TrackDataTransformer} from '../TrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord} from '../../store/ITimeline';

@Injectable()
export class PatientDataTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformPatientDataSummaryTrackData(result: InMemory.SubjectPatientDataSummary[]): TrackData[] {
        return <TrackData[]> result.map(re => this.transformSubjectPatientDataSummaryTrackData(re));
    }

    transformPatientDataDetailsTrackData(result: InMemory.SubjectPatientDataDetail[]): TrackData[] {
        return <TrackData[]> result.map(re => this.transformSubjectPatientDataDetailsTrackData(re));
    }

    private transformSubjectPatientDataSummaryTrackData(result: InMemory.SubjectPatientDataSummary): TrackData {
        const trackData: ITrackDataPoint[] = result.events
            .filter((e) => e.start != null)
            .sort((e1, e2) =>
                e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0))
            .map((event: InMemory.PatientDataEvent) => this.transformPatientDataSummaryEvent(event));

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformPatientDataSummaryEvent(event: InMemory.PatientDataEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                numberOfEvents: event.numberOfEvents,
                details: event.details
            }
        });
    }

    private transformSubjectPatientDataDetailsTrackData(dataDetail: InMemory.SubjectPatientDataDetail): TrackData {
        const result: ITrackDataPoint[] = dataDetail.tests.reduce((res, test: InMemory.PatientDataTests) =>
            [...res, ...this.transformSubjectPatientDataTest(test)], [])
            .filter(e => e.start != null)
            .sort((e1, e2) =>
                e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0));
        return {
            subjectId: dataDetail.subjectId,
            data: result
        };
    }

    private transformSubjectPatientDataTest(event: InMemory.PatientDataTests): ITrackDataPoint[] {
        const testName = event.testName;
        return <ITrackDataPoint[]> event.details.map(detail => this.transformSubjectPatientDataTestDetail(detail, testName));
    }

    private transformSubjectPatientDataTestDetail(detail: InMemory.PatientDataTestsDetails, testName: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(detail.startDate),
            end: null,
            metadata: {
                testName: testName,
                unit: detail.unit,
                value: detail.value
            }
        });
    }
}
