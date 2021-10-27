import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';

/**
 * Service for things like cache management
 */
@Injectable()
export class AdminService {

    constructor(private http: HttpClient) {
    }

    clearCacheAll(): Observable<any> {
        return this.http.get('resources/cache/clear/all');
    }
}
