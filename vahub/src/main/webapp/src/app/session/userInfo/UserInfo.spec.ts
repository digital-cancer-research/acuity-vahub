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
