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

import {ModalFilterSaveComponent} from './module';
import {CookieService} from 'ngx-cookie';

describe('GIVEN ModalFilterSaveComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [ModalFilterSaveComponent],
            providers: [
                {provide: CookieService, useClass: CookieService}
            ]
        });
    });

    describe('WHEN the component is loaded', () => {
        it('THEN the modal is not visible by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(ModalFilterSaveComponent);
                    expect(rootTC.componentInstance.ModalIsVisible).toBeFalsy();
                });
            })
        );

        describe('WHEN Cancel is pressed', () => {
            it('THEN the save modal is closed with false answer',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ModalFilterSaveComponent);
                        let answer: boolean;
                        rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                            answer = modalAnswer;
                        });
                        rootTC.componentInstance.cancelModal();
                        rootTC.detectChanges();
                        expect(answer).toBeFalsy();
                    });
                })
            );
        });

        describe('WHEN Save is pressed', () => {
            it('THEN the save modal is closed with true answer',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ModalFilterSaveComponent);
                        let answer: boolean;
                        rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                            answer = modalAnswer;
                        });
                        rootTC.componentInstance.okModal();
                        rootTC.detectChanges();
                        expect(answer).toBeTruthy();
                    });
                })
            );
        });
    });
});
