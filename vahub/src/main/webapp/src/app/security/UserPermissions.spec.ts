import {TestBed, inject} from '@angular/core/testing';
import {UserPermissions} from './UserPermissions';
import {VACumulativePermissions} from './VAPermissionEvalutator';
import {SessionEventService} from '../session/module';
import {MockEnvService} from '../common/MockClasses';

describe('GIVEN UserPermissions class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: SessionEventService, useClass: MockEnvService},
                UserPermissions
            ]
        });
    });

    describe('WHEN testing UserPermission methods for data owner', () => {
        it('THEN it SHOULD not have view permissions if no dataset is specified',
                inject([UserPermissions], (userPermissions: UserPermissions) => {
            expect(userPermissions.hasViewOncologyPackagePermission()).not.toBeTruthy();
        }));
    });
});
