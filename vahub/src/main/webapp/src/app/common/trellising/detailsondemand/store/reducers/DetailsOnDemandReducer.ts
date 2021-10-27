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
