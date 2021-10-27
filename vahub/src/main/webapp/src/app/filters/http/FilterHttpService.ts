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

import {SessionEventService} from '../../session/event/SessionEventService';

/**
 * Calls all the rest endpoints for filter information
 */
@Injectable()
export class FilterHttpService {

    constructor(private http: HttpClient,
                private sessionEventService: SessionEventService) {
    }

    /**
     * Gets the population filters
     */
    getPopulationFiltersObservable(path: string,
                                   selectedPopulationFilters: any): Observable<any> {

        const postData: any = {
            populationFilters: selectedPopulationFilters,
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(postData));
    }

    /**
     * Gets the event filters, ie labs, aes
     */
    getEventFiltersObservable(path: string,
                              selectedPopulationFilters: any,
                              selectedEventFilters: any,
                              eventFilterName: string,
                              plotInfoName?: string,
                              plotInfo?: any): Observable<any> {

        const postData: any = {
            populationFilters: selectedPopulationFilters,
            datasets: this.sessionEventService.currentSelectedDatasets
        };
        delete selectedEventFilters.reseted;
        //because we have no difference between pkResult and pkResultWith response on backend side
        if (eventFilterName === 'pkResultWithResponseFilters') {
            eventFilterName = 'pkResultFilters';
        }
        postData[eventFilterName] = selectedEventFilters;
        if (plotInfoName) {
            postData[plotInfoName] = plotInfo;
        }

        return this.http.post(path, JSON.stringify(postData));
    }
}
