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
