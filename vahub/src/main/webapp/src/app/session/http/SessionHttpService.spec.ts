import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SessionHttpService} from './SessionHttpService';
import {MockHttpClient} from '../../common/MockClasses';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';


describe('SessionHttpService class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                SessionHttpService
            ]
        });
    });

    it('should get acls', inject([HttpClient, SessionHttpService], (httpClient, sessionHttpService) => {

        spyOn(httpClient, 'get').and.returnValue(Observable.of([]));
        sessionHttpService.getUserDetails().subscribe((res) => {
            expect(res.length).toBe(0);
        });
    }));

    it('should get user details', inject([HttpClient, SessionHttpService], (httpClient, sessionHttpService) => {

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
        spyOn(httpClient, 'get').and.returnValue(Observable.of(rsd));

        sessionHttpService.getUserDetails().subscribe((res) => {
            expect(res.userId).toEqual('userId1');
            expect(res.fullName).toEqual('fullName2');
        });
    }));
});
