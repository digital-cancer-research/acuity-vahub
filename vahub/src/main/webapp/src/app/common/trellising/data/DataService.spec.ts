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

import {async, inject, TestBed} from '@angular/core/testing';
import {DataService} from './DataService';
import {HttpServiceFactory} from '../../../data/HttpServiceFactory';
import {MockHttpServiceFactory, MockSessionEventService, MockStudyService} from '../../MockClasses';
import {TabId} from '../store';
import {Observable} from 'rxjs/Observable';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {StudyService} from '../../StudyService';
import {HttpClientModule} from '@angular/common/http';

describe('GIVEN DataService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {
                    provide: HttpServiceFactory,
                    useClass: MockHttpServiceFactory
                },
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: StudyService, useClass: MockStudyService},
                DataService
            ],
            imports: [HttpClientModule]
        });

    });

    describe('WHEN DataService is getting details on demand data', () => {
        describe('AND eventIds are empty', () => {

            it('SHOULD not make a request', async(inject([DataService, HttpServiceFactory], (dataService: DataService,
                                                                                              httpServiceFactory: HttpServiceFactory) => {
                spyOn(httpServiceFactory, 'getHttpService');

                const result = dataService.getDetailsOnDemandData(TabId.POPULATION_BARCHART, [], 0, 1000, '', '');

                expect(httpServiceFactory.getHttpService).not.toHaveBeenCalled();
            })));

            it('SHOULD return empty observable',  async(inject([DataService], (dataService: DataService) => {
                const result = dataService.getDetailsOnDemandData(TabId.POPULATION_BARCHART, [], 0, 1000, '', '');
                expect(result).toEqual(Observable.of([]));
            })));

        });
    });
});
