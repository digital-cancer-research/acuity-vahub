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

import {Component, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {ConfigurationService} from '../configuration/ConfigurationService';
import SuperUser = Request.SuperUser;

@Component({
    selector: 'support',
    templateUrl: 'SupportComponent.html',
    styleUrls: ['./SupportComponent.css']
})
export class SupportComponent implements OnDestroy {
    support: any;
    superUsers: SuperUser[];
    private routeFragmentSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                public configurationService: ConfigurationService) {
        this.support = configurationService.brandingProperties.support;
        this.superUsers = configurationService.brandingProperties.superUsers
            .filter(su => su.name.length !== 0 && su.email.length !== 0);
    }

    isSuperUsersEmpty() {
        return this.superUsers.length === 0;
    }

    /*
     * Scroll down to fragment if there is a fragment
     */
    onAnchorClick(): void {
        this.routeFragmentSubscription = this.route.fragment.subscribe(f => {
            const element = document.querySelector('#' + f);
            if (element) {
                element.scrollIntoView();
            }
        });
    }

    ngOnDestroy(): void {
        if (this.routeFragmentSubscription) {
            this.routeFragmentSubscription.unsubscribe();
        }
    }
}
