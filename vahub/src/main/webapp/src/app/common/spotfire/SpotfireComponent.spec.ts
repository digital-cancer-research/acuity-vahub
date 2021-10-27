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

import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SpotfireComponent} from './SpotfireComponent';
import {MockStudyService} from '../MockClasses';
import {StudyService} from '../module';

describe('GIVEN SpotfireComponent', () => {
    beforeEach(() => {

        TestBed.configureTestingModule({
            imports: [FormsModule, CommonModule],
            declarations: [SpotfireComponent],
            providers: [
                {provide: StudyService, useValue: new MockStudyService()}
            ]
        });
    });

    function initFixture(fixture, moduletype: string): any {
        fixture.componentInstance.moduletype = moduletype;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the correct number of oncology modules should be shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SpotfireComponent);
                    const element = rootTC.nativeElement;
                    initFixture(rootTC, 'Oncology');

                    rootTC.detectChanges();
                    expect(element.querySelectorAll('.vis').length).toEqual(2);
                });
            }));

        it('THEN the correct number of respiratory modules should be shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SpotfireComponent);
                    const element = rootTC.nativeElement;
                    initFixture(rootTC, 'Respiratory');

                    rootTC.detectChanges();
                    expect(element.querySelectorAll('.vis').length).toEqual(1);
                });
            }));
    });
});
