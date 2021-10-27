import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { ApplicationState } from '../store/models/ApplicationState';

@Injectable()
export class StateUtilsService {
    constructor(private store: Store<ApplicationState>) {}

    getCurrentTab() {
        let state;
        this.store.take(1).subscribe(s => state = s);
        state = state.sharedStateReducer.toJS();
        return {
            tab: state.activeTabId,
            module: state.activeModuleMap || {name: null, id: null}
        };
    }
}
