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
import {every} from 'lodash';

import {DatasetViews} from '../../../security/module';
import {AbstractPluginComponent} from '../../AbstractPluginComponent';
import {TabName} from '../../../common/trellising/store/ITrellising';
import {PluginsService} from '../../PluginsService';
import {StudyService} from '../../../common/StudyService';
import {SessionEventService} from '../../../session/event/SessionEventService';

@Component({
    selector: 'aes-tabs',
    templateUrl: 'AEsTabsComponent.html'
})
export class AEsTabsComponent extends AbstractPluginComponent {
    @Input() isDropdown: boolean;

    constructor(public datasetViews: DatasetViews,
                private pluginsService: PluginsService,
                private sessionEventService: SessionEventService) {
        super();
    }

    tabClicked(tab: TabName): void {
        if (this.isDropdown) {
            this.pluginsService.selectedTab.next(tab);
        } else {
            this.pluginsService.selectedTab.next(null);
        }
    }

    isOngoing(): boolean {
        return every(this.sessionEventService.currentSelectedDatasets, dataset => StudyService.isOngoingStudy(dataset.type));
    }
}
