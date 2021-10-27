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
