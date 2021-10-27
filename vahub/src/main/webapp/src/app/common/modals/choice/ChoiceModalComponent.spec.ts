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

import {ChoiceModalComponent} from './module';
import {CookieService} from 'ngx-cookie';

describe('GIVEN ChoiceModalComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [ChoiceModalComponent],
            providers: [
                {provide: CookieService, useClass: CookieService}
            ]
        });
    });

    function initFixture(fixture, canBeDismissed: boolean, isVisible: boolean, title: string, msg: string, acceptText: string, declineText: string): any {
        fixture.componentInstance.ModalBeDismissed = canBeDismissed;
        fixture.componentInstance.ModalIsVisible = isVisible;
        fixture.componentInstance.Title = title;
        fixture.componentInstance.Msg = msg;
        fixture.componentInstance.ButtonAcceptText = acceptText;
        fixture.componentInstance.ButtonDeclineText = declineText;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the modal is not visible and doNotShowAgain checkbox is unchecked by default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(ChoiceModalComponent);
                    rootTC.detectChanges();

                    expect(rootTC.componentInstance.ModalIsVisible).toBeFalsy();
                    expect(rootTC.componentInstance.doNotShowAgain).toBeFalsy();
                });
            }));

        describe('WHEN ModalIsVisible is set to true', () => {

            it('THEN the modal is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ChoiceModalComponent);

                        const element = initFixture(rootTC, false, true, 'Title', 'Msg', 'Accept', 'Decline');
                        rootTC.detectChanges();

                        const res = element.querySelector('.modal-title').textContent;

                        expect(res).toContain('Title');
                    });
                }));
        });

        describe('WHEN Cancel is pressed', () => {

            it('THEN the choice modal is closed with Cancel answer',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        let answer: IModalAnswer;
                        const rootTC = TestBed.createComponent(ChoiceModalComponent);
                        rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                            answer = modalAnswer;
                        });
                        rootTC.componentInstance.cancelModal();
                        rootTC.detectChanges();
                        expect(answer.answer).toEqual(ModalAnswer.CANCEL);
                    });
                }));
        });

        describe('WHEN Yes is pressed', () => {

            it('THEN the choice modal is closed with Yes answer',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        let answer: IModalAnswer;
                        const rootTC = TestBed.createComponent(ChoiceModalComponent);
                        rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                            answer = modalAnswer;
                        });
                        rootTC.componentInstance.acceptModal();
                        rootTC.detectChanges();
                        expect(answer.answer).toEqual(ModalAnswer.YES);
                    });
                }));
        });

        describe('WHEN No is pressed', () => {

            it('THEN the choice modal is closed with No answer',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        let answer: IModalAnswer;
                        const rootTC = TestBed.createComponent(ChoiceModalComponent);
                        rootTC.componentInstance.modalHasBeenSubmitted.subscribe((modalAnswer) => {
                            answer = modalAnswer;
                        });
                        rootTC.componentInstance.declineModal();
                        rootTC.detectChanges();
                        expect(answer.answer).toEqual(ModalAnswer.NO);
                    });
                }));
        });
    });
});
