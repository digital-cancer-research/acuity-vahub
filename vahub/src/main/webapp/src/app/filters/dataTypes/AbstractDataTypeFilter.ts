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

import {OnInit, OnDestroy, AfterViewInit, Output, EventEmitter} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';

import {AbstractFiltersModel} from './AbstractFiltersModel';
import {FilterEventService} from '../event/FilterEventService';
import {SessionEventService} from '../../session/module';

export abstract class AbstractDataTypeFilter implements OnInit, OnDestroy, AfterViewInit {

    @Output()
    public clearAll = new EventEmitter<void>();
    @Output()
    public exportFilters = new EventEmitter<MouseEvent>();

    protected populationFilterSubscription: Subscription;
    public filtersModel: AbstractFiltersModel;
    protected filterEventService: FilterEventService;
    protected sessionEventService: SessionEventService;
    protected subKey: string;

    ngAfterViewInit(): void {
        this.afterViewInit();
    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    protected afterViewInit(): void {
        if (this.sessionEventService.currentSelectedDatasets) {
            this.filtersModel.getFilters(true);
            console.log('Filter component ' + this.subKey + ' updating after view');
            this.filtersModel.firstTimeLoaded = true;
        }
    }

    protected onInit(): void {
        this.filtersModel.firstEventEmitted = false;
        // listen for pop filter changes and get the event filters based on the new pop filters data
        this.populationFilterSubscription = this.filterEventService.populationFilter.subscribe((popChanged: any) => {
            const isNotOnCohortEditorPage = window.location.hash.indexOf('cohort-editor') === -1;
            if (popChanged !== null && this.sessionEventService.currentSelectedDatasets && isNotOnCohortEditorPage) {
                this.filtersModel.getFilters(true, false, false, true);
            }
        });
        this.filterEventService.addSubscription(this.subKey, this.populationFilterSubscription);
    }

    protected onDestroy(): void {
        if (this.populationFilterSubscription) {
            this.populationFilterSubscription.unsubscribe();
        }
    }

    onClearAll(): void {
        this.clearAll.emit();
    }

    onExportFilters(event: MouseEvent): void {
        this.exportFilters.emit(event);
    }
}
