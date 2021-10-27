import {TrackName, ITrackDataPoint, DayZero} from '../store/ITimeline';

export interface TrackRequest {
    name: TrackName;
    expansionLevel: number;
    dayZero?: DayZero;
    subjects?: string[];
    subjectIds?: string[];
    trackSubjects?: string[];
}

export interface TrackData {
    subjectId: string;
    data: ITrackDataPoint[];
    request?: TrackRequest;
}
