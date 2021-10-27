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

import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {SessionEventService} from './session/event/SessionEventService';
import {Observable} from 'rxjs/Observable';

import {SessionHttpService} from './session/http/SessionHttpService';
import {UrlParcerService} from './plugins/activate/UrlParcerService';

@Injectable()
export class CanActivateAppService implements CanActivate {

    constructor(protected sessionHttpService: SessionHttpService,
                protected sessionEventService: SessionEventService,
                protected router: Router,
                private urlParcerService: UrlParcerService) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean {
        if (!this.sessionEventService.userDetails) {
            return this.sessionHttpService.getUserDetailsObservable()
                .map((userInfo: any) => {
                        if (userInfo) {
                            this.sessionEventService.setUserInfo(userInfo);
                            const parcedUrlSummary = this.urlParcerService.getParcedUrl(state);
                            if (localStorage.getItem(userInfo.userId) !== 'true' && !parcedUrlSummary.acuityDatasetId) {
                                localStorage.setItem(userInfo.userId, 'true');
                                this.router.navigate(['/home']);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                );
        } else {
            return true;
        }

    }
}
