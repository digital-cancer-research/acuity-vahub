/**
 * VA/Acuity permissions and masks
 */
export enum VAPermissions {
    VIEW_VISUALISATIONS = 1,
    VIEW_BASE_PACKAGE = 2,
    VIEW_ONCOLOGY_PACKAGE = 4,
    VIEW_PROACT_PACKAGE = 8,
    VIEW_MACHINE_INSIGHTS_PACKAGE = 16
}

export enum VACumulativePermissions {
    DEVELOPMENT_TEAM = 522240,
    AUTHROISERS = 442383,
    ADMINISTRATOR = 32783,
    AUTHORISED_USER = 3
}

export class VAPermissionEvalutator {

    /**
     * Checks to see if any of the acls have permission of DEVELOPMENT_TEAM
     */
    static hasDevelopmentTeamPermission(acls: Request.AcuityObjectIdentityWithPermission[]): boolean {
        return VAPermissionEvalutator.hasPermission(acls, VACumulativePermissions.DEVELOPMENT_TEAM);
    }

    /**
     * Checks to see if any of the acls have permission of the passed in VACumulativePermissions permission
     */
    static hasPermission(acls: Request.AcuityObjectIdentityWithPermission[], permission: VACumulativePermissions): boolean {
        if (acls) {
            return acls.some((acl) => {
                return acl.rolePermissionMask === permission;
            });
        } else {
            return false;
        }
    }

    static checkViewPermission(acls: Request.AcuityObjectIdentityWithPermission[], permissionRequired: VAPermissions): boolean {
        if (acls) {
            return acls.every((acl) => {
                return VAPermissionEvalutator._hasPermission(acl.viewPermissionMask, permissionRequired);
            });
        } else {
            return false;
        }
    }

    static _hasPermission(permissionMask: number, permissionRequired: number): boolean {
        // tslint:disable-next-line
        return (permissionMask | permissionRequired) === permissionMask;
    }
}
