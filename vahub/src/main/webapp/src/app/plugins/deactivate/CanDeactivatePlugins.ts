import {Observable} from 'rxjs/Observable';
import {ActivatedRouteSnapshot, RouterStateSnapshot, CanDeactivate} from '@angular/router';
import {Injectable} from '@angular/core';
import {Store} from '@ngrx/store';

import {PluginsComponent} from '../PluginsComponent';
import {AppStore} from '../timeline/store/ITimeline';
import {RESET} from '../timeline/store/TimelineAction';
import {TrellisingDispatcher} from '../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {PluginsService} from '../PluginsService';
import {ClearSubjectSelection} from '../refactored-singlesubject/store/actions/SingleSubjectViewActions';
import {ApplicationState} from '../../common/store/models/ApplicationState';

@Injectable()
export class CanDeactivatePlugins implements CanDeactivate<PluginsComponent> {

    constructor(private trellisingDispatcher: TrellisingDispatcher,
                private _timlineStore: Store<AppStore>,
                private _store: Store<ApplicationState>,
                private pluginsService: PluginsService) {
    }

    canDeactivate(component: PluginsComponent,
                  route: ActivatedRouteSnapshot,
                  state: RouterStateSnapshot): Observable<boolean> | boolean {
        this.trellisingDispatcher.globalResetNotification();
        this._timlineStore.dispatch({type: RESET, payload: {}});
        this.pluginsService.unsubscribeFromRouter();
        // selected subject should be removed when switching to another dataset
        this._store.dispatch(new ClearSubjectSelection());
        // need to show warning on every dataset change in biomarkers
        if (sessionStorage.getItem('displayLimitationBiomarkersAccepted') === 'yes') {
            sessionStorage.setItem('displayLimitationBiomarkersAccepted', 'no');
        }
        // need to show warning on every dataset change in all prior therapies
        if (sessionStorage.getItem('displayLimitationAllTherapiesAccepted') === 'yes') {
            sessionStorage.setItem('displayLimitationAllTherapiesAccepted', 'no');
        }
        return true;
    }
}
