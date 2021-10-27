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
