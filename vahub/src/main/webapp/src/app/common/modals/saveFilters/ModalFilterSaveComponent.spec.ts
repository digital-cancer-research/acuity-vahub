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
