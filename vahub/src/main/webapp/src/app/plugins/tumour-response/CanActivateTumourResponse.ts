import {ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {DatasetViews} from '../../security/DatasetViews';

@Injectable()
export class CanActivateTumourResponse implements CanActivate {
    constructor(private router: Router,
                private datasetViews: DatasetViews,
                private route: ActivatedRoute) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean | Promise<boolean> {
        if (this.datasetViews.hasTumourResponseData()) {
            return true;
        } else if (this.datasetViews.hasTumourTherapyData()) {
            this.router.navigate(['/plugins/tumour-therapy/prior-therapy']);
            return false;
        } else {
            this.router.navigate(['/plugins/tumour-response/spotfire']);
            return false;
        }
    }
}
