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

import {AbstractFiltersModel} from './AbstractFiltersModel';
import {FilterHttpService} from '../http/FilterHttpService';
import {FilterEventService} from '../event/FilterEventService';
import {getServerPath} from '../../common/utils/Utils';
import {DatasetViews} from '../../security/DatasetViews';
import {PopulationFiltersModel} from './population/PopulationFiltersModel';

/**
 * Abstract class for all the event filters, ie aes, labs.
 *
 * Mainly implements a _getFiltersImpl
 */
export abstract class AbstractEventFiltersModel extends AbstractFiltersModel {

    constructor(protected populationFiltersModel: PopulationFiltersModel,
                protected filterHttpService: FilterHttpService,
                protected filterEventService: FilterEventService,
                protected datasetViews: DatasetViews) {
        super();
    }

    // TODO: change to const
    protected getFilterPath(): string {
        return 'filters';
    }

    protected _getFiltersImpl(manuallyApplied = false, reseted: boolean = false, triggeredByPopulation = false): void {
        this.loading = true;

        if (this.pendingRequest) {
            this.pendingRequest.unsubscribe();
        }

        // when we leave the view with event filters applied modal window appears
        // we want to emit this to notify deactivator that it needs to update event filter state
        if (reseted) {
            this.resetEventFilterEvent.next(null);
        }

        // emit the event to the rest of the app
        if (this.firstEventEmitted && !(this.isTimeline() && triggeredByPopulation)) {
            this.emitEvent(this.transformFiltersToServer(manuallyApplied, reseted));
        } else {
            this.firstEventEmitted = true;
        }

        this.makeFilterRequest(manuallyApplied);
    }

    protected makeFilterRequest(manuallyApplied: boolean): void {
        console.log('Sending ' + this.getName() + ' filters request');

        const filtersName = this.getName() + 'Filters';

        this.pendingRequest = this.filterHttpService.getEventFiltersObservable(
            getServerPath(this.getModulePath(), this.getFilterPath()),
            this.populationFiltersModel.transformFiltersToServer(false),
            this.transformFiltersToServer(manuallyApplied),
            filtersName
        )
            .subscribe(res => {
                console.log('Got ' + this.getName() + ' filters request');
                this.transformFiltersFromServer(res);
                if (this.firstTimeLoaded) {
                    this.hideEmptyFilters(this.datasetViews.getEmptyFilters(this.getName()));
                    this.firstTimeLoaded = false;
                }
                this.matchedItemsCount = <number>res.matchedItemsCount;
                this.filterEventService.setEventFilterEventCount(<number>res.matchedItemsCount);
                this.loading = false;
                this.filterEventService.dispatchNewEventFiltersWereApplied();
            });
    }
}
