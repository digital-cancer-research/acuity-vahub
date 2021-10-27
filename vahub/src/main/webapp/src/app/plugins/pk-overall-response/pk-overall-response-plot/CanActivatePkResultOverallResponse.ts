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
