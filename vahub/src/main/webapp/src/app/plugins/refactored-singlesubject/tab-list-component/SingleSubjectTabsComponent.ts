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

import {Component, Input} from '@angular/core';
import {UserPermissions} from '../../../security/UserPermissions';
import {DatasetViews} from '../../../security/DatasetViews';
import {StudyService} from '../../../common/StudyService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {TabRecords} from '../../../common/trellising/store/ITrellising';
import {PluginsService} from '../../PluginsService';
import {getTabId} from '../../../common/store/reducers/SharedStateReducer';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'ssv-tabs',
    templateUrl: 'SingleSubjectTabsComponent.html',
    styleUrls: ['./SingleSubjectTabsComponent.css']
})
export class SingleSubjectTabsComponent {
    tabRecords = new TabRecords();
    @Input() isDropdown: boolean;
    openedTabName$: Observable<string>;

    constructor(public userPermissions: UserPermissions,
                public datasetViews: DatasetViews,
                public sessionEventService: SessionEventService,
                private pluginsService: PluginsService,
                private _store: Store<ApplicationState>) {

        this.openedTabName$ = this._store.select(getTabId)
            .distinctUntilChanged()
            .map((tabId) => {
                if (!tabId) {
                    return '';
                }
                return this.tabRecords.get(tabId).name;
            });
    }

    ngAfterViewInit(): void {
        this.subscribeToTabChanges();
    }

    public isOngoing(): boolean {
        return StudyService.isOngoingStudy(this.sessionEventService.currentSelectedDatasets[0].type);
    }

    private subscribeToTabChanges(): void {
        this.openedTabName$.subscribe((tabName) => {
            setTimeout(() => {
                if (tabName) {
                    if (this.isDropdown) {
                        this.pluginsService.selectedTab.next(tabName);
                    } else {
                        this.pluginsService.selectedTab.next(null);
                    }
                }
            });
        });
    }

}
