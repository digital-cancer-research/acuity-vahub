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

import {Subscription} from 'rxjs/Subscription';

/**
 * Base class for all event services, this is to solve the bug https://github.com/angular/angular/pull/6460
 */
export abstract class BaseEventService {

    // To solve the bug of https://github.com/angular/angular/pull/6460
    // ngOnDestroy not been called
    // so keep a map reference to check if its been called already and destroy that if there is one
    protected dict: { [key: string]: Subscription; } = {};

    /**
     * Adds a new Subscription but removes and cancels an old one because of a bug with ngOnDestroy
     */
    addSubscription(key: string, sub: Subscription): void {
        const gotSub = this.dict[key];
        if (gotSub) {
            gotSub.unsubscribe();
            delete this.dict[key];
        }
        this.dict[key] = sub;
    }
}
