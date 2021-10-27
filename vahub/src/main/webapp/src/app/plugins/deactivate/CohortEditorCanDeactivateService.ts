import {Injectable} from '@angular/core';
import {CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {CohortEditorComponent} from '../module';

@Injectable()
export class CanDeactivateCohortEditor implements CanDeactivate<CohortEditorComponent> {

    constructor(private router: Router) {
    }

    canDeactivate(component: CohortEditorComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        if (component.hasUnsavedChanges()) {
            if (window.confirm('You have unsaved changes which will be lost if you change pages. Do you want to change pages?')) {
                return true;
            } else {
                this.router.navigate([state.url]);
                return false;
            }
        }
        return true;
    }

}
