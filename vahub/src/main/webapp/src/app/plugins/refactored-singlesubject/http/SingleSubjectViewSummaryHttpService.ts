/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {getFormattedTimeZone, getServerPath} from '../../../common/utils/Utils';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class SingleSubjectViewSummaryHttpService {
    constructor(private http: HttpClient,
                private datasetViews: DatasetViews,
                private sessionEventService: SessionEventService) {
    }

    getSubjectDetail(subjectID: string): Observable<InMemory.OutputSSVSummaryData[]> {
        const path = getServerPath('subjects', 'detail');
        subjectID = this.datasetViews.getSubjectIdByEcode(subjectID);

        const postData: any = {
            subjectId: subjectID,
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map((res: any) => {
            return res;
        });
    }

    getSubjectDetailMetadata(): Observable<any> {
        const path = getServerPath('subjects', 'metadata');

        const postData: any = {
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(postData));
    }

    getSummaryTablesMetadata(subjectID: string): Observable<any> {
        const path = getServerPath('summary', 'metadata');
        subjectID = this.datasetViews.getSubjectIdByEcode(subjectID);

        const postData: any = {
            subjectId: subjectID,
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(postData));
    }

    downloadSummaryTablesDoc(subjectID: string): Observable<any> {
        const path = getServerPath('summary', 'document');
        subjectID = this.datasetViews.getSubjectIdByEcode(subjectID);

        const postData: any = {
            eventFilters: {},
            subjectId: subjectID,
            datasets: this.sessionEventService.currentSelectedDatasets,
            timeZoneOffset: getFormattedTimeZone(new Date().getTimezoneOffset())
        };

        return this.http.post(path, JSON.stringify(postData), {responseType: 'blob'});
    }

    getSubjectSummaryTable(subjectID: string, path: string): Observable<any> {
        const serverPath = getServerPath('summary', path);
        subjectID = this.datasetViews.getSubjectIdByEcode(subjectID);

        const postData: any = {
            eventFilters: {},
            subjectId: subjectID,
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(serverPath, JSON.stringify(postData));
    }

    getSummaryTablesHeader(subjectID: string): Observable<any> {
        return this.getSubjectSummaryTable(subjectID, 'header');
    }
}
