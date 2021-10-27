import {Injectable} from '@angular/core';
import {DatasetViews} from '../../security/DatasetViews';
import {UserPermissions} from '../../security/UserPermissions';
import {Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class CanActivateMachineInsights implements CanActivate {
    constructor(private router: Router,
                private datasetViews: DatasetViews,
                private userPermissions: UserPermissions) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean | Promise<boolean> {
        return this.datasetViews.hasQTProlongationData()
            && this.userPermissions.hasViewMachineInsightsPackagePermission();
    }
}
