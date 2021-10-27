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

import {Component} from '@angular/core';
import {CookieOptions, CookieService} from 'ngx-cookie';
import {ConfigurationService} from '../../../configuration/ConfigurationService';

/**
 * Component for modal window with governance statement acceptance
 *
 * @property {string} GovernanceTitle
 * @property {string} GovernanceMsg
 * @property {string} GovernanceButtonText
 * @property {boolean} GovernanceModalBeDismissed
 * @property {boolean} GovernanceModalIsVisible
 */
@Component({
    selector: 'governance-statement-modal',
    templateUrl: 'GovernanceStatementModalComponent.html'
})
export class GovernanceStatementModalComponent {

    GovernanceTitle = 'Governance Statement';
    GovernanceMsg;
    GovernanceButtonText = 'I accept';
    GovernanceModalBeDismissed = false;
    GovernanceModalIsVisible = false;

    /**
     * @constructor shows modal window if previously statement was not accepted or if there is no information about it n cookies
     * @param _cookieService
     * @param configurationService
     */
    constructor(private _cookieService: CookieService, configurationService: ConfigurationService) {
        console.log(this.getCookie('acceptedGovernance'));
        this.GovernanceMsg = `Please be aware that manual checks have been performed to identify and resolve inconsistencies
                    between the source data and what <strong>ACUITY</strong> displays.<br><br>
                    For the reporting data some checks are made against the TFLs, however we can not
                    guarantee that there are not errors.<br><br>
                    All signals or findings identified using <strong>ACUITY</strong>
                    must be confirmed through the study programmer outputs.<br><br>
                    Information taken directly from <strong>ACUITY</strong> can not be
                    used within publications, regulatory submissions or as input to regulatory reports,
                    such as those provided to Ethics Committees and Regulatory Authorities.`;
        if (this.getCookie('acceptedGovernance') === 'false' || this.getCookie('acceptedGovernance') === undefined) {
            this.GovernanceModalIsVisible = true;
        }
    }

    /**
     * Puts information about whether statement was accepted to cookies
     * @param {boolean} event - true if form was submitted
     */
    okGovernance(event: boolean): void {
        if (event === true) {
            this.put('acceptedGovernance', 'true');
            this.GovernanceModalIsVisible = false;
        } else {
            this.put('acceptedGovernance', 'false');
            this.GovernanceModalIsVisible = true;
        }
    }

    private getCookie(key: string): string {
        return this._cookieService.get(key);
    }

    private put(key: string, value: string, options?: CookieOptions): void {
        this._cookieService.put(key, value);
    }
}
