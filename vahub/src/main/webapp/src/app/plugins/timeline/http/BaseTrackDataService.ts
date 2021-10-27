import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {TrackData, TrackRequest} from './IDataService';

@Injectable()
export class BaseTrackDataService {

    constructor(protected http: HttpClient) {
    }

    fetchTimelineTrackData(request: TrackRequest, path: string, postData: any): any {
        return this.http.post(path, JSON.stringify(postData));
    }

    extendTrackData(trackData: TrackData[], request: TrackRequest): TrackData[] {
        trackData.forEach((data: TrackData) => {
            data.request = request;
        });
        return trackData;
    }
}
