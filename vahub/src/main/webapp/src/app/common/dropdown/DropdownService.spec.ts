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
import {DropdownService} from './DropdownService';
import {DropdownModel} from './DropdownModel';

describe('DropdownService class', () => {
    describe('WHEN initialized', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event emitter should be defined', inject([DropdownService], (service: DropdownService) => {
            expect(service).toBeDefined();
        }));
    });

    describe('WHEN opening a dropdown item', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event should be emitted', inject([DropdownService], (service: DropdownService) => {

            const validator = jasmine.createSpyObj('validator', ['called']);
            service.event.subscribe(
                (models: any) => {
                    validator.called(models);
                }
            );
            const model = new DropdownModel();
            service.add(model);
            service.open('1');

            expect(validator.called).toHaveBeenCalledWith([model]   );
        }));
    });

    describe('WHEN close all is called', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event should be emitted', inject([DropdownService], (service: DropdownService) => {

            const validator = jasmine.createSpyObj('validator', ['called']);
            service.event.subscribe(
                (models: any) => {
                    validator.called(models);
                }
            );
            const model = new DropdownModel();
            service.add(model);
            service.open('1');

            expect(validator.called).toHaveBeenCalledWith([model]   );
        }));
    });
});

