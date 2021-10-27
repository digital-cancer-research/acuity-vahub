import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import AcuitySidDetails = Request.AcuitySidDetails;

/**
 *  Calls all the rest endpoints for session information
 */
@Injectable()
export class SessionHttpService {

    constructor(private http: HttpClient) {
    }

    getUserDetails(): Observable<AcuitySidDetails> {
        return this.getUserDetailsObservable();
    }

    getUserDetailsObservable(): Observable<any> {
        return this.http.get('resources/session/whoami');
    }

    getActivityStatusObservable(): Observable<any> {
        return this.http.get('resources/session/ping');
    }
}
