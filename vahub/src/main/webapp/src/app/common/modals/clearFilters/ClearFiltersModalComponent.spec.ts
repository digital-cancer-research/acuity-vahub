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
import {ModalAnswer, IModalAnswer} from '../../trellising/store/ITrellising';
import {FormsModule} from '@angular/forms';

import {ClearFiltersModalComponent} from './module';
import {CookieService} from 'ngx-cookie';

describe('GIVEN ChoiceModalMessageComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [ClearFiltersModalComponent],
            providers: [
                {provide: CookieService, useClass: CookieService}
            ]
        });
    });

    function initFixture(fixture, isVisible: boolean, filterTitle: string): any {
        fixture.componentInstance.FilterName = filterTitle;
        fixture.componentInstance.ModalIsVisible = isVisible;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the modal is not visible by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(ClearFiltersModalComponent);
                    rootTC.detectChanges();

                    expect(rootTC.componentInstance.ModalIsVisible).toBeFalsy();
                });
            }));

        describe('WHEN ModalIsVisible is set to true', () => {

            it('THEN the modal is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ClearFiltersModalComponent);

                        const element = initFixture(rootTC, true, 'Filter');
                        rootTC.detectChanges();

                        const res = element.querySelector('.modal-title').textContent;

                        expect(res).toContain('Clear All Filters');
                    });
                }));
        });

        describe('WHEN modal is closed', () => {
            describe('AND Cancel is pressed', () => {
                it('THEN the choice modal is closed with Cancel answer',
                    async(() => {
                        TestBed.compileComponents().then(() => {
                            let answer: IModalAnswer;
                            const rootTC = TestBed.createComponent(ClearFiltersModalComponent);
                            rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                                answer = modalAnswer;
                            });
                            rootTC.componentInstance.cancelModal();
                            rootTC.detectChanges();
                            expect(answer).toEqual(ModalAnswer.CANCEL);
                        });
                    }));
            });

            describe('AND Proceed is pressed', () => {
                describe('AND "all" filter option was selected', () => {
                    it('THEN the choice modal is closed with Yes answer',
                        async(() => {
                            TestBed.compileComponents().then(() => {
                                let answer: IModalAnswer;
                                const rootTC = TestBed.createComponent(ClearFiltersModalComponent);
                                rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                                    answer = modalAnswer;
                                });
                                rootTC.componentInstance.updateClearedFilterOption('all');
                                rootTC.componentInstance.okModal();
                                rootTC.detectChanges();
                                expect(answer).toEqual(ModalAnswer.YES);
                            });
                        }));
                });
                describe('AND "current" filter option was selected', () => {
                    it('THEN the choice modal is closed with Yes answer',
                        async(() => {
                            TestBed.compileComponents().then(() => {
                                let answer: IModalAnswer;
                                const rootTC = TestBed.createComponent(ClearFiltersModalComponent);
                                rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                                    answer = modalAnswer;
                                });
                                rootTC.componentInstance.updateClearedFilterOption('current');
                                rootTC.componentInstance.okModal();
                                rootTC.detectChanges();
                                expect(answer).toEqual(ModalAnswer.NO);
                            });
                        }));
                });
            });
        });
    });
});
