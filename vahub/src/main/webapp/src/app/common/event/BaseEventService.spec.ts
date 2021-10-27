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

import {BaseEventService} from './BaseEventService';
import {Subscription} from 'rxjs/Subscription';

class MyBaseEventService extends BaseEventService {

    getDict(): any {
        return this.dict;
    }
}

describe('GIVEN a BaseEventService class', () => {

    let baseEventService: MyBaseEventService;

    beforeEach(() => {
        baseEventService = new MyBaseEventService();
    });

    it('THEN it should remove subscription when adding another', () => {
        const sub1 = new Subscription();
        const sub2 = new Subscription();

        baseEventService.addSubscription('key', sub1);
        baseEventService.addSubscription('key', sub2);

        const gotSub = baseEventService.getDict()['key'];
        console.log(baseEventService.getDict());
        expect(gotSub).toEqual(sub2);
        expect(gotSub).not.toEqual(sub1);
    });
});
