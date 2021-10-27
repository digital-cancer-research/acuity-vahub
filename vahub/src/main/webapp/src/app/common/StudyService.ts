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
