import {async, TestBed} from '@angular/core/testing';
import {ModalMessageComponent} from './ModalMessageComponent';

describe('GIVEN ModalMessageComponent', () => {

    function initFixture(fixture, title: string, msg: string, buttonText: string, canBeDismissed: boolean, isVisable: boolean): any {
        fixture.componentInstance.Title = title;
        fixture.componentInstance.Msg = msg;
        fixture.componentInstance.ButtonText = buttonText;
        fixture.componentInstance.ModalBeDismissed = canBeDismissed;
        fixture.componentInstance.ModalIsVisible = isVisable;
        fixture.componentInstance.ngOnInit();
        return fixture.nativeElement;
    }

    beforeEach(() => {
        TestBed.configureTestingModule({declarations: [ModalMessageComponent]});
    });

    describe('WHEN the component is loaded', () => {

        it('THEN the modal is not displayed',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(ModalMessageComponent);
                    rootTC.componentInstance.ngOnInit();
                    rootTC.detectChanges();

                    const result = rootTC.componentInstance.ModalIsVisible;

                    expect(result).not.toBeTruthy();
                });
            }));

        describe('WHEN ModalIsVisible is set to true', () => {

            it('THEN the modal is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ModalMessageComponent);
                        const element = initFixture(rootTC, 'Test', '', '', false, true);
                        rootTC.detectChanges();

                        const res = element.querySelector('.modal-title').textContent;

                        expect(res).toContain('Test');
                    });
                }));
        });

        describe('WHEN okModal() is called', () => {

            it('THEN true event is emitted',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ModalMessageComponent);

                        rootTC.componentInstance.ngOnInit();
                        rootTC.detectChanges();
                        spyOn(rootTC.componentInstance, 'okModal');
                        rootTC.detectChanges();
                        const result = rootTC.componentInstance.modalHasBeenSubmitted;

                        expect(result).toBeTruthy();
                    });
                }));
        });
    });
});
