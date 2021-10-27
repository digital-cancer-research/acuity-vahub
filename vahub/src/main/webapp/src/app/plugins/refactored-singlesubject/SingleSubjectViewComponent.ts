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
