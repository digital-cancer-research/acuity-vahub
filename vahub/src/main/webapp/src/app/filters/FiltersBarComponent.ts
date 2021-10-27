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

import {Component, OnDestroy, AfterViewInit} from '@angular/core';
import {Router, NavigationStart, NavigationEnd} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {forEach} from 'lodash';

import {FilterEventService} from './event/FilterEventService';
import {FiltersBarService, FilterOptions} from './FiltersBarService';
import {BaseFilterItemModel} from './components/BaseFilterItemModel';
import {ModalAnswer, FilterId} from '../common/trellising';
import {AbstractFiltersModel} from './dataTypes/AbstractFiltersModel';
import {FiltersUtils} from './utils/FiltersUtils';
import {FiltersExportService} from './FiltersExportService';
import {SidePanelTab} from './IFilters';
import {SelectedFiltersModel} from './selectedFilters/SelectedFiltersModel';

@Component({
    selector: 'filters-bar',
    templateUrl: 'FiltersBarComponent.html',
    styleUrls: ['./filters.css']
})
export class FiltersBarComponent implements AfterViewInit, OnDestroy {
    options: FilterOptions;
    routerSubscription: Subscription;
    isVisible: boolean;
    clearAllModalIsVisible: boolean;
    clearAllMessage: string;
    clearAllTitle: string;
    clearedFilterName: string;
    currentTab: SidePanelTab;
    filterId = FilterId;

    sidePanelTab = SidePanelTab;

    constructor(private filtersBarService: FiltersBarService,
                private filtersExportService: FiltersExportService,
                private router: Router,
                private filterEventService: FilterEventService,
                private filtersUtils: FiltersUtils,
                private selectedFiltersModel: SelectedFiltersModel) {
        this.updateOptions();
        this.currentTab = SidePanelTab.POPULATION;
        this.clearAllMessage = 'If you want to continue then click  the "Proceed" button.<br/>' +
            'If you want to keep all of the applied filters then click the "Cancel" button.';
        this.clearAllTitle = 'Clear Filters';
        this.clearedFilterName = '';
    }

    ngAfterViewInit(): void {
        this.routerSubscription = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationStart) {
                const path = window.location.hash.split('/');
                if ((path.indexOf('timeline') !== -1 || path.indexOf('timeline-tab') !== -1) && this.currentTab === SidePanelTab.TRACKS) {
                    this.changeTab('population');
                }
                this.clearNotAppliedSelectedValues();

            }
            if (event instanceof NavigationEnd) {
                if ((event.url.indexOf('population-summary') !== -1 || event.url.indexOf('singlesubject') !== -1)
                    && this.currentTab === SidePanelTab.EVENTS) {
                    this.changeTab('population');
                }
            }
            this.updateOptions();
            if (!(this.options.showEventFilter || this.options.showPopulationFilter)) {
                this.isVisible = false;
            }
        });
        this.updateOptions();
    }

    updateOptions(): void {
        const [page, tab] = window.location.hash.slice(10).split('/');
        this.options = this.filtersBarService.getFilterOptions(page, tab);
        if (this.isCurrentTabMissing()) {
            this.changeTab(SidePanelTab.POPULATION);
        }
    }

    isCurrentTabMissing(): boolean {
        return (this.currentTab === SidePanelTab.EVENTS && this.options.showEventFilter)
            || (this.currentTab === SidePanelTab.SETTINGS && this.options.showSettings)
            || (this.currentTab === SidePanelTab.TRACKS && this.options.showTimelineFilter);
    }

    ngOnDestroy(): void {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
        this.clearAllFilters(ModalAnswer.YES, true);
    }

    clearNotAppliedSelectedValues(): void {
        const currentPageFilterModels = this.getCurrentPageFilters(window.location.hash);
        if (window.location.hash.toLowerCase().indexOf('population') === -1) {
            currentPageFilterModels.push(this.filtersBarService.populationFiltersModel);
        }
        currentPageFilterModels.forEach((filterModel: AbstractFiltersModel) => {
            filterModel.clearNotAppliedSelectedValues();
        });
    }

    changeTab(tabName: SidePanelTab): void {
        forEach(document.querySelectorAll('.tab-pane'), (node: Element) => {
            node.classList.remove('active');
        });
        (<Element>document.querySelector('.population-tab').parentNode).classList.remove('active');
        if (this.options.showEventFilter) {
            (<Element>document.querySelector('.events-tab').parentNode).classList.remove('active');
        }

        document.querySelector('.' + tabName).classList.add('active');
        (<Element>document.querySelector('.' + tabName + '-tab').parentNode).classList.add('active');

        if (!this.isVisible) {
            this.isVisible = true;
        }
        this.currentTab = tabName;
        this.filterEventService.setSidePanelTab(tabName);

        if (tabName === SidePanelTab.COHORT) {
            $('.export-clear-buttons').hide();
        } else {
            $('.export-clear-buttons').show();
        }
        $('.apply-clear-buttons').hide();
        $('.filter-item').removeClass('active');
        $('.filter-collection').hide();
    }

    toggleSidePanel(): void {
        this.isVisible = !this.isVisible;
        if (this.isVisible) {
            this.filterEventService.setSidePanelTab(this.currentTab);
        } else {
            this.filterEventService.setSidePanelTab('');
        }
    }

    isOnTimelinePage(): boolean {
        return window.location.hash.indexOf('timeline') > -1;
    }

    onExportFilters(event: MouseEvent): void {
        this.filtersExportService.exportFilters(event);
    }

    onClearAll(populationFilterClicked: boolean): void {
        console.log('onClearAll');
        if (populationFilterClicked) {
            this.clearedFilterName = 'Population Filters';
        } else {
            this.clearedFilterName = this.options.eventFiltersName;
        }
        this.clearAllModalIsVisible = true;
    }

    clearAllFilters(answer: ModalAnswer, clearAndDestroy = false): void {
        if (answer === ModalAnswer.YES || answer === ModalAnswer.NO) {
            let allFilterModels;
            if (answer === ModalAnswer.YES) {
                allFilterModels = this.filtersUtils.getAvailableEventFilterModels();
                allFilterModels.push(this.filtersBarService.populationFiltersModel);
            } else {
                allFilterModels = this.getCurrentPageFilters(this.clearedFilterName);
            }
            forEach(allFilterModels, (filterModel: AbstractFiltersModel) => {
                forEach(filterModel.itemsModels, (filter: BaseFilterItemModel) => {
                    filter.reset(clearAndDestroy);
                    filter.resetNumberOfSelectedFilters();
                });
                if (answer !== ModalAnswer.YES) {
                    filterModel.getFilters(true, false, true);
                }
            });

            if (answer === ModalAnswer.YES && !clearAndDestroy) {
                if (this.isOnTimelinePage()) {
                this.selectedFiltersModel.clearSelectedFilters();
                }
                this.filtersBarService.populationFiltersModel.getFilters(true);
            }
        }
        this.clearAllModalIsVisible = false;
    }

    getCurrentPageFilters(name: string): AbstractFiltersModel[] {
        if (name.toLowerCase().indexOf('population') !== -1) {
            return [this.filtersBarService.populationFiltersModel];
        } else {
            if (this.isOnTimelinePage()) {
                return this.filtersUtils.getAvailableTimelineEventFilterModels();
            } else {
                if (this.options.eventFiltersModel) {
                    return [this.options.eventFiltersModel];
                } else {
                    return [];
                }
            }
        }
    }
}
