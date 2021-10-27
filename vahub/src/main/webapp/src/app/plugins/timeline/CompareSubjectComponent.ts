import {Component, OnDestroy, OnInit} from '@angular/core';
import {TimelineId} from './store/ITimeline';

import {FilterEventService} from '../../filters/event/FilterEventService';
import {TrellisingDispatcher} from '../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {Subscription} from 'rxjs/Subscription';

@Component({
    selector: 'compare-subject',
    templateUrl: 'CompareSubjectComponent.html',
})
export class CompareSubjectComponent implements OnInit, OnDestroy {
    timelineId = TimelineId;
    private filterSubscription: Subscription;

    constructor(private filterEventService: FilterEventService,
                private trellisingDispatcher: TrellisingDispatcher) {
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
}
