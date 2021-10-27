import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {TabId} from '../../../../common/trellising/store/ITrellising';
import {UpdateActiveTabId} from '../../../../common/store/actions/SharedStateActions';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {AbstractTabComponent, TAB_COMPONENT_TEMPLATE} from '../AbstractTabComponent';

@Component({
    template: TAB_COMPONENT_TEMPLATE,
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['../AbstractTabComponent.css']
})
export class VitalsTabComponent extends AbstractTabComponent implements OnInit {

    constructor(public _store: Store<ApplicationState>) {
        super(_store);
    }

    ngOnInit(): void {
        this._store.dispatch(new UpdateActiveTabId(TabId.SINGLE_SUBJECT_VITALS_LINEPLOT));
    }
}
