import {Injectable} from '@angular/core';
import {getServerPath} from './utils/Utils';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import * as  _ from 'lodash';
import Dataset = Request.Dataset;

/**
 * Service/Model that holds and gets the metadata object for the study
 */
@Injectable()
export class StudyService {
    /**
     * Json object returned from server in the format
     * {
     *  aes {
     *     aeSeverityType: "CTC_GRADES",
     *     hasCustomGroups: false
     *  },
     *  labs {
     *  }
     * }
     */
    public metadataInfo: any = null;

    public combinedStudyInfo: any = null;

    static isOngoingStudy(type: string): boolean {
        return type.toLowerCase().indexOf('acuity') !== -1;
    }

    constructor(private http: HttpClient) {
    }

    /**
     * Sets the active class for the tabs
     */
    getMetadataInfoObservable(currentDatasets: Dataset[]): Observable<any> {
        const path = getServerPath('study', 'info');

        const postData: any = {
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData));
    }

    // Glen: Hack until security properly added
    createDisplayNames(datasets: Dataset[]): string[] {
        if (!_.isEmpty(datasets)) {
            return _.chain(datasets)
                .map((dataset) => `${dataset.name}`)
                .sortBy(value => value.toLowerCase())
                .value();
        } else {
            return ['Select dataset'];
        }
    }

    getCombinedStudyInfo(): Observable<any> {
        return this.http.get('resources/study/available_study_info');
    }

    getAllStudyInfo(): Observable<any> {
        return this.http.get('resources/study/all_study_info');
    }
}
