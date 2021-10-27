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

import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {FilterHttpService} from './FilterHttpService';
import {MockHttpClient, MockSessionEventService} from '../../common/MockClasses';
import {SessionEventService} from '../../session/event/SessionEventService';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

describe('FilterHttpService class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                FilterHttpService,
                {provide: SessionEventService, useClass: MockSessionEventService}
            ]
        });
    });

    it('should get population filters from server', inject([HttpClient, FilterHttpService], (httpClient, filterHttpService) => {
        const response = {
            matchedItemsCount: 1900,
            sex: {
                values: ['M', 'F']
            },
            subjectId: {
                values: ['DummyData-1004573274', 'DummyData-1004573266']
            }
        };
        spyOn(httpClient, 'post').and.returnValue(Observable.of(response));

        filterHttpService.getPopulationFiltersObservable('path', {}).subscribe((res) => {
            expect(res).toEqual(response);
        });
    }));

    it('should get event filters from server', inject([HttpClient, FilterHttpService], (httpClient, filterHttpService) => {
        const response = {
            matchedItemsCount: 1900,
            pt: {
                values: ['PT1']
            },
            serious: {
                values: ['serious']
            }
        };
        spyOn(httpClient, 'post').and.returnValue(Observable.of(response));

        filterHttpService.getEventFiltersObservable('path', {}, {}).subscribe((res) => {
            expect(res).toEqual(response);
        });
    }));
});
