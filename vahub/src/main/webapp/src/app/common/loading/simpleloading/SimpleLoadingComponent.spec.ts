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

import {SimpleLoadingComponent} from './SimpleLoadingComponent';

describe('GIVEN SimpleLoadingComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [SimpleLoadingComponent]
        });
    });

    function initFixture(fixture, loading: boolean): any {
        fixture.componentInstance.loading = loading;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the loader is not visible by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    rootTC.detectChanges();
                    expect(rootTC.componentInstance.loading).toBeFalsy();
                });
            }));

    });

    describe('WHEN loading is changed to TRUE', () => {
        it('THEN the loader is shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    const element = initFixture(rootTC, true);
                    rootTC.detectChanges();
                    expect(element.getElementsByClassName('loader').length).toBeGreaterThan(0);
                });
            }));
    });

    describe('WHEN loading is changed to FALSE', () => {
        it('THEN the loader is shown',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(SimpleLoadingComponent);
                    const element = initFixture(rootTC, false);
                    rootTC.detectChanges();
                    expect(element.getElementsByClassName('loader').length).toBe(0);
                });
            }));
    });
});
