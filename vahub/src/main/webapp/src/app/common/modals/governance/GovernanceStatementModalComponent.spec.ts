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
