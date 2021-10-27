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
