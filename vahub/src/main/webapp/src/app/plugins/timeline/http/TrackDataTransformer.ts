import {Injectable} from '@angular/core';
import {
    TrackTimePointRecord,
    ITrackTimePoint
} from '../store/ITimeline';

@Injectable()
export class TrackDataTransformer {

    transformDateDayHour(dateDayHour: Request.DateDayHour): ITrackTimePoint {
        return dateDayHour.doseDayHour ? <ITrackTimePoint> new TrackTimePointRecord({
            date: dateDayHour.date,
            dayHour: dateDayHour.dayHour,
            dayHourAsString: dateDayHour.dayHourAsString,
            doseDayHour: dateDayHour.doseDayHour,
            studyDayHourAsString: dateDayHour.studyDayHourAsString,
        }) : <ITrackTimePoint> new TrackTimePointRecord({
            date: dateDayHour.date,
            dayHour: dateDayHour.dayHour,
            dayHourAsString: dateDayHour.dayHourAsString,
            studyDayHourAsString: dateDayHour.studyDayHourAsString,
        });
    }
}
