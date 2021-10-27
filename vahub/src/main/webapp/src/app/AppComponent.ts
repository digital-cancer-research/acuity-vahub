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

import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';

import {SessionEventService} from './session/event/SessionEventService';
import {UserActivityService} from './UserActivityService';


@Component({
    selector: 'app',
    templateUrl: 'AppComponent.html',
    styleUrls: ['./AppComponent.css']
})
export class AppComponent implements OnDestroy, AfterViewInit {
    //refreshModal
    private routerSub: Subscription;

    constructor(public sessionEventService: SessionEventService,
                private router: Router,
                private userActivityService: UserActivityService) {
    }


    ngAfterViewInit(): void {
        this.routerSub = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationEnd) {
                if (event.url.indexOf('plugins') !== -1) {
                    this.userActivityService.send(event.urlAfterRedirects);
                }
            }
        });
    }

    ngOnDestroy(): void {
        this.routerSub.unsubscribe();
    }
}
