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

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {DatasetViews} from '../../../security/DatasetViews';
import {UserPermissions} from '../../../security/UserPermissions';

@Injectable()
export class CanActivatePkResultOverallResponse implements CanActivate {
    constructor(private router: Router,
                private datasetViews: DatasetViews,
                private userPermissions: UserPermissions) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        if (this.datasetViews.hasPkResultWithResponseData() && this.userPermissions.hasViewOncologyPackagePermission()) {
            return true;
        } else {
            this.router.navigate(['/plugins/dose-proportionality/box-plot']);
            return false;
        }
    }
}
