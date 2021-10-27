import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {getServerPath} from '../../common/utils/Utils';
import Dataset = Request.Dataset;

@Injectable()
export class AesSummaryHttpService {

    constructor(protected http: HttpClient) {
    }

    getData(datasets: Dataset[], category: string): Observable<InMemory.AeSummariesTable[]> {
        const requestUrl = getServerPath('aes', category);
        const payload = {
            datasets: datasets
        };
        return this.http.post(requestUrl, JSON.stringify(payload))
            .map(res => res as InMemory.AeSummariesTable[]);
    }
}
