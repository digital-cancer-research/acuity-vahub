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

import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {AbstractPluginComponent} from '../AbstractPluginComponent';
import {CollapseTabsDirective} from '../../common/directives/CollapseTabsDirective';
import {PluginsService} from '../PluginsService';

@Component({
    templateUrl: 'AEsComponent.html'
})
export class AEsComponent extends AbstractPluginComponent implements AfterViewInit {
    @ViewChild(CollapseTabsDirective) collapseTabsDirective;

    dropdownOpen = false;

    constructor(public pluginsService: PluginsService) {
        super();
    }

    ngAfterViewInit(): void {
        this.collapseTabsDirective.hideOverflowedTabs();
        this.subscribeToTabChanges();
    }

    subscribeToTabChanges(): void {
        this.pluginsService.selectedTab.subscribe(() => {
            this.collapseTabsDirective.hideOverflowedTabs(true);
            this.dropdownOpen = false;
        });
    }

    toggleDropdown(): void {
        this.dropdownOpen = !this.dropdownOpen;
    }

    onResize(): void {
        this.collapseTabsDirective.hideOverflowedTabs(true);
    }

}
