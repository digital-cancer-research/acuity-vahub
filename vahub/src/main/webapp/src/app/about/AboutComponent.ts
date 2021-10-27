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
import { ActivatedRoute } from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {ConfigurationService} from '../configuration/module';
@Component({
    selector: 'about',
    templateUrl: 'AboutComponent.html',
    styleUrls: ['./AboutComponent.css']
})
export class AboutComponent implements OnDestroy {
    private routeFragmentSubscription: Subscription;
    public about: { [index: string]: string };

    constructor(private route: ActivatedRoute, private configurationService: ConfigurationService) {
        this.about = configurationService.brandingProperties.about;
    }

    /*
    * Scroll down to fragment if there is a fragment
    */
    onAnchorClick(): void {
        this.routeFragmentSubscription = this.route.fragment.subscribe(f => {
            const element: any = document.querySelector('#' + f);
            if (element) {
                element.scrollIntoView(element);
            }
        });
    }

    ngOnDestroy(): void {
        if (this.routeFragmentSubscription) {
            this.routeFragmentSubscription.unsubscribe();
        }
    }
}
