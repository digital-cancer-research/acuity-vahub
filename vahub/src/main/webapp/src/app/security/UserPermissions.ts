import {SessionEventService} from '../session/module';
import {VAPermissionEvalutator, VAPermissions} from './VAPermissionEvalutator';
import {Injectable} from '@angular/core';

/**
 * Class holds all the information regarding the user
 */
@Injectable()
export class UserPermissions {

    constructor(private sessionEventService: SessionEventService) {
    }

    hasViewOncologyPackagePermission(): boolean {
        return this.checkPermission(VAPermissions.VIEW_ONCOLOGY_PACKAGE);
    }

    hasViewMachineInsightsPackagePermission(): boolean {
        return this.checkPermission(VAPermissions.VIEW_MACHINE_INSIGHTS_PACKAGE);
    }

    private checkPermission(vAPermission: VAPermissions): boolean {
        if (this.sessionEventService.currentSelectedDatasets) {
            return VAPermissionEvalutator.checkViewPermission(this.sessionEventService.currentSelectedDatasets, vAPermission);
        } else {
            return false;
        }
    }
}
