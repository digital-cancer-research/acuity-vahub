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

import {Injectable, EventEmitter} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {SessionHttpService} from '../http/SessionHttpService';
/**
 * Calls whoami incrementally to check the user has not timed out
 */
@Injectable()
export class TimeoutService {

    public currentState: EventEmitter<number> = new EventEmitter<number>(false);

    constructor(private sessionHttpService: SessionHttpService) {
        /* gets the first status response */
        this.sessionHttpService.getActivityStatusObservable().map((res: any) => res).first().subscribe((res: any) => {
            this.currentState.next(res.status);
        }, (err) => {
            this.currentState.next(err.status);
        });
        this.isUserTimedOut();
    }

    isUserTimedOut(): void {
        const that = this;
        Observable.interval((15 * 60 * 1000) /* 15 minutes*/)
            .timeInterval()
            .takeWhile(() => {
                return true;
            })
            .map(() => {
                return this.sessionHttpService.getActivityStatusObservable().map((res: any) => res).subscribe((res) => {
                    that.currentState.next(res.status);
                }, (err) => {
                    that.currentState.next(err.status);
                });
            }).subscribe();
    }
}
