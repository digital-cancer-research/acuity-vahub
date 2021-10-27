import {Action, Store} from '@ngrx/store';
import {ApplicationState} from '../../../../store/models/ApplicationState';
import {Actions, Effect} from '@ngrx/effects';
import {Observable} from 'rxjs/Observable';
import {Injectable} from '@angular/core';
import {ActionTypes, ColumnsPayload, SetColumnsAction, UpdateColumnsAction} from '../actions/DetailsOnDemandActions';

@Injectable()
export class DetailsOnDemandEffects {
    @Effect() updateColumns: Observable<Action> = this.actions$
        .ofType(ActionTypes.SET_COLUMNS)
        .map((action: SetColumnsAction) => action.payload)
        .map((columns: ColumnsPayload) => {
            return new UpdateColumnsAction(columns);
        });

    constructor(private actions$: Actions,
                private _store: Store<ApplicationState>) {}
}
