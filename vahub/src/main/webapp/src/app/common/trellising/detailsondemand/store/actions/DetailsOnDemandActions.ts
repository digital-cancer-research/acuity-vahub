import {Action} from '@ngrx/store';
import {TabId} from '../../../store/ITrellising';
export const ActionTypes = {
    UPDATE_COLUMNS: 'UPDATE_COLUMNS',
    SET_COLUMNS: 'SET_COLUMNS'
};

export interface TabIdPayload {
    tabId: TabId;
}

export interface ColumnsPayload {
    columns: Map<any, Set<any>>;
}

export interface UpdateColumnsPayload extends ColumnsPayload {

}

export class SetColumnsAction implements Action {
    readonly type = ActionTypes.SET_COLUMNS;
    constructor (public payload?: any) {} //Should be ColumnsPayload, but for some reason Type 'Map<any, Set<any>>' is not assignable to type 'Map<any, Set<any>>'
}

export class UpdateColumnsAction implements Action {
    readonly type = ActionTypes.UPDATE_COLUMNS;
    constructor (public payload?: any) {}
}

export type Actions = SetColumnsAction | UpdateColumnsAction;
