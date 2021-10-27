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

/**
 * Class holds all the information regarding the user
 */
import AcuitySidDetails = Request.AcuitySidDetails;

export class UserInfo {

    fullName: string;
    prid: string;
    loggedIn: boolean;

    /**
     * @param {Rest.AcuitySidDetails} userDetails. user info send from the server.
     * @param {AcuityObjectIdentityWithPermission} acls. Array of AcuityObjectIdentity with their permission mask.
     */
    // constructor(public userDetails: AcuitySidDetails, public acls: AcuityObjectIdentityWithPermission[]) {
    constructor(public userDetails: AcuitySidDetails) {
        this.fullName = userDetails.fullName;
        this.prid = userDetails.userId;
        this.loggedIn = true;
    }

    toString(): string {
        // return 'UserInfo[Fullname ' + this.fullName + ', Prid ' + this.prid + ', Acls: # ' + this.acls.length + ' ]';
        return 'UserInfo[Fullname ' + this.fullName + ', Prid ' + this.prid + ' ]';
    }
}

/**
 * Holds an empty user that isnt logged in
 */
export class EmptyUserInfo extends UserInfo {

    private static nli = 'Not logged in';
    private static emptyRsd = {
        fullName: EmptyUserInfo.nli, userId: EmptyUserInfo.nli,
        accountNonExpired: true,
        accountNonLocked: true,
        authenticated: false,
        authorities: [],
        authoritiesAsString: [],
        authoritiesAsStringToString: '',
        credentials: null,
        credentialsNonExpired: false,
        details: '',
        enabled: false,
        external: false,
        group: false,
        linkeduser: null,
        name: EmptyUserInfo.nli,
        password: EmptyUserInfo.nli,
        principal: '',
        sidAsString: EmptyUserInfo.nli,
        username: EmptyUserInfo.nli
    };

    constructor() {
        // super(EmptyUserInfo.emptyRsd, []);
        super(EmptyUserInfo.emptyRsd);
        this.fullName = EmptyUserInfo.nli;
        this.prid = EmptyUserInfo.nli;
        this.loggedIn = false;
    }

    toString(): string {
        return 'EmptyUserInfo[]';
    }
}
