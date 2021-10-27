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
import {EnvService} from './EnvService';

describe('EnvService class', () => {
    describe('WHEN in non-local env', () => {
        beforeEach(() => {
            spyOn(EnvService, 'getHostName').and.returnValue('http://acuity.com/#/');
            TestBed.configureTestingModule({
                providers: [
                    EnvService
                ]
            });
        });
        it('THEN isLocalhost flag should be false', inject([EnvService], (service: EnvService) => {
            expect(service.env.isLocalHost).toBeFalsy();
        }));
    });

    describe('WHEN in local env', () => {
        beforeEach(() => {
            spyOn(EnvService, 'getHostName').and.returnValue('http://localhost:3000/#/');
            TestBed.configureTestingModule({
                providers: [
                    EnvService
                ]
            });
        });
        it('THEN it should set isLocalHost flag', inject([EnvService], (service: EnvService) => {
            expect(service.env.isLocalHost).toBeTruthy();
        }));
    });
});
