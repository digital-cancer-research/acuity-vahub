import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {getServerPath} from './common/utils/Utils';

@Injectable()
export class UserActivityHttpService {
    constructor(private http: HttpClient) {
    }

    sendUserActivity(data: any): Observable<any> {
        const path = getServerPath('logging', 'viewchange');

        return this.http.post(path, JSON.stringify(data));
    }
}

