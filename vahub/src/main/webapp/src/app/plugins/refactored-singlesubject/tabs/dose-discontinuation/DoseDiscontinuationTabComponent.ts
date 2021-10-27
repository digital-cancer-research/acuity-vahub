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
export class DoseDiscontinuationTabComponent extends AbstractTabComponent implements OnInit {

    constructor(public store: Store<ApplicationState>) {
        super(store);
    }

    ngOnInit(): void {
        this.store.dispatch(new UpdateActiveTabId(TabId.SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB));
    }
}
