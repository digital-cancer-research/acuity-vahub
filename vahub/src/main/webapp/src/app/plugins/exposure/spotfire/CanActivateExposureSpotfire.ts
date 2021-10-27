import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {DatasetViews} from '../../../security/DatasetViews';

@Injectable()
export class CanActivateExposureSpotfire implements CanActivate {
    constructor(private router: Router,
                private datasetViews: DatasetViews) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean | Promise<boolean> {
        if (this.datasetViews.hasExposureSpotfireModules()) {
            return true;
        } else {
            if (this.datasetViews.hasExposureData()) {
                this.router.navigate(['/plugins/exposure/analyte-concentration']);
                // There are no redirect to overall-response because if we have data for overall-response plot
                // we definitely have data for dose-proportionality
            } else if (this.datasetViews.hasPkResultData()) {
                this.router.navigate(['/plugins/dose-proportionality/box-plot']);
            }
            return false;
        }
    }
}
