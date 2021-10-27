import {async, TestBed, inject} from '@angular/core/testing';
import {GovernanceStatementModalComponent} from './module';
import {ModalMessageComponent} from '../modalMessage/ModalMessageComponent';
import {CookieService, CookieModule} from 'ngx-cookie';
import {CookieOptionsProvider} from 'ngx-cookie/src/cookie-options-provider';
import {ConfigurationService} from '../../../configuration/ConfigurationService';

class MockConfigurationService {
}

describe('GIVEN GovernanceStatementModalComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [ModalMessageComponent, GovernanceStatementModalComponent],
            providers: [
                {provide: CookieService, useClass: CookieService},
                {provide: ConfigurationService, useClass: MockConfigurationService},
                CookieOptionsProvider
            ],
            imports: [CookieModule.forRoot()]
        });
    });

    function initFixture(fixture, canBeDismissed: boolean, isVisable: boolean): any {
        fixture.componentInstance.GovernanceModalBeDismissed = canBeDismissed;
        fixture.componentInstance.GovernanceModalIsVisible = isVisable;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        describe('AND the accepted cookie has not been found', () => {

            beforeEach(inject([CookieService], (cookieService: CookieService) => {
                cookieService.remove('acceptedGovernance');
            }));

            it('THEN the Governance is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(GovernanceStatementModalComponent);
                        rootTC.detectChanges();

                        const result = rootTC.componentInstance.GovernanceModalIsVisible;

                        expect(result).toBeTruthy();
                    });
                }));

        });

        describe('WHEN GovernanceModalIsVisible is set to true', () => {

            it('THEN the modal is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(GovernanceStatementModalComponent);

                        const element = initFixture(rootTC, false, true);
                        rootTC.detectChanges();

                        const res = element.querySelector('.modal-title').textContent;

                        expect(res).toContain('Governance Statement');
                    });
                }));
        });

        describe('WHEN okGovernance() is called', () => {

            it('THEN the governance modal is closed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(GovernanceStatementModalComponent);
                        rootTC.componentInstance.okGovernance(true);
                        rootTC.detectChanges();
                        const result = rootTC.componentInstance.GovernanceModalIsVisible;
                        expect(result).not.toBeTruthy();
                    });
                }));
        });
    });
});
