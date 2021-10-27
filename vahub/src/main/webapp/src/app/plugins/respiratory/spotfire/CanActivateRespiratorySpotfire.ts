import {ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {DatasetViews} from '../../../security/DatasetViews';
@Injectable()
export class CanActivateRespiratorySpotfire implements CanActivate {
    constructor(private router: Router,
                private datasetViews: DatasetViews,
                private route: ActivatedRoute) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean | Promise<boolean> {
        if (this.datasetViews.hasRespiratorySpotfireModules()) {
            return true;
        } else {
            this.router.navigate(['/plugins/respiratory/lung-function-box-plot']);
        }
        return false;
    }
}
