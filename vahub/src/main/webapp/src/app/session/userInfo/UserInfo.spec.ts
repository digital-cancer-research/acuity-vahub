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

import {UserInfo, EmptyUserInfo} from './UserInfo';

describe('UserInfo class', () => {

    let userInfo: UserInfo;

    beforeEach(() => {

        const rsd = {
            accountNonExpired: true,
            accountNonLocked: true,
            authenticated: true,
            authorities: [],
            authoritiesAsString: [],
            authoritiesAsStringToString: 'string',
            credentials: null,
            credentialsNonExpired: true,
            details: null,
            enabled: true,
            external: true,
            fullName: 'fullName2',
            group: true,
            linkeduser: null,
            name: 'string',
            password: 'string',
            principal: null,
            sidAsString: 'string',
            userId: 'userId1',
            username: 'string'
        };
        userInfo = new UserInfo(rsd);
    });

    it('THEN it should set the class correctly', () => {
        expect(userInfo.prid).toEqual('userId1');
        expect(userInfo.fullName).toEqual('fullName2');
        expect(userInfo.loggedIn).toBe(true);
        expect(userInfo.userDetails).toBeDefined();
    });
});

describe('EmptyUserInfo class', () => {

    let emptyUserInfo: UserInfo;

    beforeEach(() => {

        // let rsd = {
        //     accountNonExpired: true,
        //     accountNonLocked: true,
        //     authenticated: true,
        //     authorities: [],
        //     authoritiesAsString: [],
        //     authoritiesAsStringToString: 'string',
        //     credentials: null,
        //     credentialsNonExpired: true,
        //     details: null,
        //     enabled: true,
        //     external: true,
        //     fullName: 'fullName2',
        //     group: true,
        //     linkeduser: null,
        //     name: 'string',
        //     password: 'string',
        //     principal: null,
        //     sidAsString: 'string',
        //     userId: 'userId1',
        //     username: 'string'
        // };
        emptyUserInfo = new EmptyUserInfo();
    });

    it('THEN it should set the class correctly', () => {
        expect(emptyUserInfo.prid).toEqual('Not logged in');
        expect(emptyUserInfo.fullName).toEqual('Not logged in');
        expect(emptyUserInfo.loggedIn).toBe(false);
        expect(emptyUserInfo.userDetails).toBeDefined();
    });
});
