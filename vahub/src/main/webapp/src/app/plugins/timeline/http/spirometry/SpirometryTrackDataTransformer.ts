import {Injectable} from '@angular/core';
import {TrackDataTransformer} from '../TrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord} from '../../store/ITimeline';
import LungFunctionSummaryEvent = InMemory.LungFunctionSummaryEvent;
import SubjectLungFunctionSummary = InMemory.SubjectLungFunctionSummary;
import SubjectLungFunctionDetail = InMemory.SubjectLungFunctionDetail;
import LungFunctionCodes = InMemory.LungFunctionCodes;
import LungFunctionDetailsEvent = InMemory.LungFunctionDetailsEvent;
import DateDayHour = InMemory.DateDayHour;

@Injectable()
export class SpirometryTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformSpirometrySummaryTrackData(result: SubjectLungFunctionSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectSpirometrySummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformSpirometryDetailTrackData(result: any): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectSpirometryDetailTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectSpirometrySummaryTrackData(result: SubjectLungFunctionSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.events.forEach((event: LungFunctionSummaryEvent) => {
            trackData.push(this.transformSubjectSpirometrySummaryEvent(event, result.baseline, result.sex));
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectSpirometrySummaryEvent(event: LungFunctionSummaryEvent, baseline: DateDayHour, sex: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                maxValuePercentChange: event.maxValuePercentChange,
                visitNumber: event.visitNumber
            }
        });
    }

    private transformSubjectSpirometryDetailTrackData(result: SubjectLungFunctionDetail): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.lungFunctionCodes.forEach((lungFunctionCode: LungFunctionCodes) => {
            lungFunctionCode.events.forEach((event: LungFunctionDetailsEvent) => {
                trackData.push(this.transformSubjectSpirometryDetailEvent(
                    event, lungFunctionCode.code, lungFunctionCode.baseline, result.sex));
            });
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectSpirometryDetailEvent(event: LungFunctionDetailsEvent,
                                                  code: string, baseline: DateDayHour, sex: string): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                category: code,
                code: code,
                baseline: baseline ? this.transformDateDayHour(baseline) : null,
                sex: sex,
                visitNumber: event.visitNumber,
                baselineValue: event.baselineValue,
                baselineFlag: event.baselineFlag,
                value: event.valueRaw,
                valueRaw: event.valueRaw,
                unit: event.unitRaw,
                valueChangeFromBaseline: event.valueChangeFromBaseline,
                valuePercentChangeFromBaseline: event.valuePercentChangeFromBaseline,
                unitPercentChangeFromBaseline: event.unitPercentChangeFromBaseline,
                unitChangeFromBaseline: event.unitChangeFromBaseline
            }
        });
    }
}
