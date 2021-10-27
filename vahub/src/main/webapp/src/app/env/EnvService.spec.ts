import {TestBed, inject} from '@angular/core/testing';
import {EnvService} from './EnvService';

describe('EnvService class', () => {
    describe('WHEN in non-local env', () => {
        beforeEach(() => {
            spyOn(EnvService, 'getHostName').and.returnValue('http://acuity.com/#/');
            TestBed.configureTestingModule({
                providers: [
                    EnvService
                ]
            });
        });
        it('THEN isLocalhost flag should be false', inject([EnvService], (service: EnvService) => {
            expect(service.env.isLocalHost).toBeFalsy();
        }));
    });

    describe('WHEN in local env', () => {
        beforeEach(() => {
            spyOn(EnvService, 'getHostName').and.returnValue('http://localhost:3000/#/');
            TestBed.configureTestingModule({
                providers: [
                    EnvService
                ]
            });
        });
        it('THEN it should set isLocalHost flag', inject([EnvService], (service: EnvService) => {
            expect(service.env.isLocalHost).toBeTruthy();
        }));
    });
});
