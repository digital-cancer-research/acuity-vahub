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

import {Map, Set} from 'immutable';
import {Actions, ActionTypes, UpdateColumnsAction} from '../actions/DetailsOnDemandActions';
import {createSelector} from 'reselect';
import {ApplicationState} from '../../../../store/models/ApplicationState';

export interface DetailsOnDemandState extends Map<string, any> {
    columns: Map<any, Set<any>>;
}

export const initialState = <DetailsOnDemandState>Map(<DetailsOnDemandState>{
    columns: Map({

    })
});

export function reducer(state: DetailsOnDemandState = initialState, action: Actions): DetailsOnDemandState {
    switch (action.type) {
        case ActionTypes.UPDATE_COLUMNS:
            return updateColumns(state, action);
        default:
            return state;
    }

    function updateColumns(state, action: UpdateColumnsAction): DetailsOnDemandState {
        return state.setIn(['columns'], action.payload.columns);
    }
}

export const getDetailsOnDemandState = (state: ApplicationState): DetailsOnDemandState => {
    return state.detailsOnDemand;
};

export const getAvailableDetailsOnDemandColumns = createSelector(getDetailsOnDemandState, (detailsOnDemandState: DetailsOnDemandState): DetailsOnDemandState => {
    return detailsOnDemandState.get('columns');
});
