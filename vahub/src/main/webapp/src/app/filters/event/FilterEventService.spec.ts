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

import {TestBed, inject} from '@angular/core/testing';

import {FilterEventService} from './FilterEventService';
import {Store} from '@ngrx/store';
// import {MockStore} from '../../common/MockClasses';

describe('GIVEN a FilterEventService class', () => {
    let filterEventService: FilterEventService;
    let validator: any;

    beforeEach(() => {
        TestBed.configureTestingModule({providers: [FilterEventService, {provide: Store}]});
    });

    beforeEach(inject([FilterEventService], (_filterEventService_) => {
        filterEventService = _filterEventService_;

        filterEventService.populationFilter.subscribe(
            (pop: any) => {
                validator.called(pop);
            }
        );
        filterEventService.aesFilter.subscribe(
            (aes: any) => {
                validator.calledAes(aes);
            }
        );
        filterEventService.labsFilter.subscribe(
            (labs: any) => {
                validator.calledLabs(labs);
            }
        );
        filterEventService.lungFunctionFilter.subscribe(
            (lungFunction: any) => {
                validator.calledLungFunction(lungFunction);
            }
        );
        validator = jasmine.createSpyObj('validator', ['called', 'calledAes', 'calledLabs', 'calledLungFunction']);
    }));

    it('should listen to pop subscribe events', inject([FilterEventService], (filterEventService) => {

        filterEventService.setPopulationFilter('pop1');

        expect(validator.called).toHaveBeenCalledWith('pop1');
    }));

    it('should listen to aes subscribe events', () => {
        filterEventService.setAesFilter('aes1');

        expect(validator.calledAes).toHaveBeenCalledWith('aes1');
    });

    it('should listen to labs subscribe events', () => {
        filterEventService.setLabsFilter('labs1');

        expect(validator.calledLabs).toHaveBeenCalledWith('labs1');
    });

    it('should listen to lung function subscribe events', () => {
        filterEventService.setLungFunctionFilter('lung1');

        expect(validator.calledLungFunction).toHaveBeenCalledWith('lung1');
    });
});
