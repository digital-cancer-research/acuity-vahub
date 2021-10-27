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

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import {Subscription} from 'rxjs/Subscription';
import {Observable} from 'rxjs/Observable';

import {ApplicationState} from '../../common/store/models/ApplicationState';
import {getIsSingleSubjectViewDataLoading} from './store/reducers/SingleSubjectViewReducer';
import {CollapseTabsDirective} from '../../common/directives/CollapseTabsDirective';
import {PluginsService} from '../PluginsService';
import {FilterEventService} from '../../filters/event/FilterEventService';
import {TrellisingDispatcher} from '../../common/trellising/store/dispatcher/TrellisingDispatcher';


/**
 * BEWARE: If you change this code, you must test the 'Jumpt to ACUITY' functionality from SENTRI
 */
@Component({
    templateUrl: 'SingleSubjectViewComponent.html',
    styleUrls: ['SingleSubjectViewComponent.css']
})
export class SingleSubjectViewComponent implements AfterViewInit, OnInit, OnDestroy {
    @ViewChild(CollapseTabsDirective) collapseTabsDirective;

    isLoading$: Observable<boolean>;
    dropdownOpen = false;
    private filterSubscription: Subscription;

    constructor(private _store: Store<ApplicationState>,
                public pluginsService: PluginsService,
                private filterEventService: FilterEventService,
                private trellisingDispatcher: TrellisingDispatcher
    ) {
        this.isLoading$ = this._store.select(getIsSingleSubjectViewDataLoading);
    }

    ngOnInit(): void {
        this.listenToPopulationFilterChange();
    }

    private listenToPopulationFilterChange(): void {
        this.filterSubscription = this.filterEventService.populationFilter.subscribe((changed) => {
            if (changed) {
                this.trellisingDispatcher.globalResetNotification();
            }
        });
    }

    ngOnDestroy(): void {
        this.filterSubscription.unsubscribe();
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
